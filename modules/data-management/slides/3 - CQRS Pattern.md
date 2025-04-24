# CQRS Pattern 

## Project structure

The implementation is based on the same microservices built with Spring Boot already seen for the SAGA pattern:

* ***order-service***: manages orders;
* ***payment-service***: manages payments and verifies credit cards;
* ***inventory-service***: manages the warehouse.

In this example, two databases (H2) will be used within the order-service microservice in order to
emphasize the separation between command-side and query-side data. The other microservices will, however, use only
one. Communication between microservices will take place synchronously through REST API, through the use of RestTemplate.
We will focus attention now on the order-service microservice.

### Databases
We begin the implementation by going to define the two DBs within the `application.yaml`:

```yaml
spring:
  datasource:
    command:
      url: jdbc:h2:mem:commanddb;DB_CLOSE_DELAY=-1;NON_KEYWORDS=order
      username: sa
      password:
      driver-class-name: org.h2.Driver
    query:
      url: jdbc:h2:mem:querydb;DB_CLOSE_DELAY=-1;NON_KEYWORDS=order
      username: sa
      password:
      driver-class-name: org.h2.Driver
```

At this point we create a configuration for each data source, associating each with its own repository and the same
reference entity (Order). Looking at, for example, `CommandDBConfig.java`:

```java
package com.nbicocchi.config;

@EnableTransactionManagement
@EnableJpaRepositories (
        basePackages = "com.nbicocchi.order.persistence.repository.command",
        entityManagerFactoryRef = "commandEntityManagerFactory",
        transactionManagerRef = "commandTransactionManager"
)
@Configuration
public class CommandDBConfig {
    @ConfigurationProperties(prefix = "spring.datasource.command")
    @Bean(name = "commandDataSource")
    public DataSource dataSource() { return DataSourceBuilder.create().build(); }

    @Bean(name = "commandEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("commandDataSource") DataSource commandDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(commandDataSource);
        em.setPackagesToScan("com.nbicocchi.order.persistence.model");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        return em;
    }

    @Bean(name = "commandTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("commandEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
```

### Controllers
To make the separation between the command part and the query part, we start by dividing the controllers:

* ***OrderCommandController***: receives queries that can create, modify or delete data;

```java
package com.nbicocchi.order.controller;

@AllArgsConstructor
@RestController
public class OrderCommandController {
private final OrderCommandService orderCommandService;

    @PostMapping(value = "/order")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderCommandService.createOrder(
                order.getProductId(),
                order.getCustomerId(),
                order.getCreditCardNumber()
        );
        return ResponseEntity.ok(createdOrder);
    }

    @DeleteMapping(value = "/order/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable String orderId) {
        orderCommandService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
```

* ***OrderQueryController***: receives requests that can read data.

```java
package com.nbicocchi.order.controller;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class OrderQueryController {
    private final OrderQueryService orderQueryService;

    @GetMapping(value = "/order", produces = "application/json")
    public ResponseEntity<List<Order>> returnAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderQueryService.findAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/order/{orderId}", produces = "application/json")
    public ResponseEntity<Order> returnOrderById(@PathVariable String orderId) {
        log.info("Fetching order with id {}", orderId);
        Order order = orderQueryService.findByOrderId(orderId);
        return ResponseEntity.ok(order);
    }
}
```

### Services
Each controller relies on a service dedicated to it:

* ***OrderCommandService***: handles requests from the command controller. Every time a WRITE is made
  on the commandDB, an event is published that contains just the order that was just created/deleted;

```java
package com.nbicocchi.order.service;

@Slf4j
@AllArgsConstructor
@Service
public class OrderCommandService {
    private final OrderCommandRepository orderCommandRepository;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public Order createOrder(String productId, String customerIds, String creditCardNumber) {
        Order order = new Order(productId, customerIds, creditCardNumber);

        log.info("Trying to contact payment with order ID " + order.getOrderId() + " and credit card number " + creditCardNumber);
        String paymentUrl = "http://payment-service:9001/payment?orderId=" + order.getOrderId()
                + "&creditCardNumber=" + creditCardNumber;
        Boolean paymentSuccess;
        try {
            paymentSuccess = restTemplate.postForObject(paymentUrl, null, Boolean.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Payment service not available: " + e.getMessage());
        }

        if(!Boolean.TRUE.equals(paymentSuccess)) {
            order.setStatus(OrderStatus.REJECTED);
            orderCommandRepository.save(order);
            eventPublisher.publishEvent(order);
            throw new IllegalArgumentException("Payment failed.");
        }

        log.info("Trying to contact inventory with product ID " + productId);
        String inventoryUrl = "http://inventory-service:9002/inventory/" + productId;
        Boolean inventoryValid;
        try {
            inventoryValid = restTemplate.exchange(inventoryUrl, HttpMethod.PUT, null, Boolean.class).getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Inventory service not available: " + e.getMessage());
        }

        if(!Boolean.TRUE.equals(inventoryValid)) {
            order.setStatus(OrderStatus.REJECTED);
            orderCommandRepository.save(order);
            eventPublisher.publishEvent(order);
            throw new IllegalArgumentException("Inventory not sufficient for order.");
        }

        order.setStatus(OrderStatus.APPROVED);
        eventPublisher.publishEvent(order);
        return orderCommandRepository.save(order);
    }

    public Order deleteOrder(String orderId) {
        orderCommandRepository.findByOrderId(orderId).ifPresent(orderCommandRepository::delete);
        return null;
    }
}
```

* ***OrderQueryService***: handles the query controller requests;

```java
package com.nbicocchi.order.service;

@AllArgsConstructor
@Service
public class OrderQueryService {
    private final OrderQueryRepository orderQueryRepository;

    public Order findByOrderId(String orderId) {
        return orderQueryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public List<Order> findAllOrders() { return (List<Order>) orderQueryRepository.findAll(); }
}
```

### Event Handler
To maintain consistency between the two databases, this component is used, which is responsible for consuming the events
published by the event publisher defined in `OrderCommandService` and performing the corresponding update in the queryDB.

```java
package com.nbicocchi.order.events;

@AllArgsConstructor
@Component
public class OrderEventHandler {
    private final OrderQueryRepository orderQueryRepository;

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        orderQueryRepository.save(order);
    }

    @EventListener
    public void handleOrderDeleted(OrderDeletedEvent event) {
        String orderId = event.getOrderId();
        orderQueryRepository.findByOrderId(orderId).ifPresent(orderQueryRepository::delete);
    }
}
```

### Examples of use
Let's build and test our system.

```bash
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```
The inventory microservice manages 3 products with ids: P-001, P-002, P-003. The warehouse contains 9, 0, and 1 instances
of each of them, respectively (see `inventory/runners/DataLoader`).

The payment microservice allows payments only for cards starting with 7777 (see payment/workers/PaymentWorkers)

We can submit a new order to the system by invoking the /order endpoint of the order microservice.

The following request succeeds because the credit card is valid and P-001 has enough instances.

```bash
curl --location --request POST 'http://localhost:9000/order' --header 'Content-Type: application/json' \
--data-raw '{
              "productId": "P-001",
              "customerId": "C-001",
              "creditCardNumber": "7777888899990000"
            }'
```

The following request fails because the credit card is invalid.

```bash
curl --location --request POST 'http://localhost:9000/order' --header 'Content-Type: application/json' \
--data-raw '{
              "productId": "P-001",
              "customerId": "C-001",
              "creditCardNumber": "6666888899990000"
            }'
```

The following request fails because P-002 not has enough instances.

```bash
curl --location --request POST 'http://localhost:9000/order' --header 'Content-Type: application/json' \
--data-raw '{
              "productId": "P-002",
              "customerId": "C-001",
              "creditCardNumber": "7777888899990000"
            }'
```

The following request allows us to see all orders both *APPROVED* and *DENIED*.

```bash
curl http://localhost:9000/order
```

The following request allows us to delete an order with a specific ID.

```bash
curl --location --request DELETE 'http://localhost:9000/order/<orderID>'
```



## Resources
* https://www.geeksforgeeks.org/cqrs-design-pattern-in-microservices/#challenges-of-cqrs-design-pattern-in-microservices
* https://www.vinsguru.com/cqrs-pattern/
* https://dev.to/jackynote/understanding-cqrs-pattern-pros-cons-and-a-spring-boot-example-3flb
* https://martinfowler.com/bliki/CQRS.html
* https://learn.microsoft.com/en-us/azure/architecture/patterns/cqrs

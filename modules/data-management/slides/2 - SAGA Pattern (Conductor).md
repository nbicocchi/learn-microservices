# SAGA Pattern (Conductor)

## Project structure

A common example of a microservices architecture is an e-commerce platform where users can make purchases. In such an architecture, each microservice is responsible for a specific task, working in coordination to ensure that data remains accurate and consistent across the system. This project implements a simplified e-commerce platform using three microservices built with Spring Boot, each serving a distinct role:

- **`order-service`**: Manages orders.
- **`payment-service`**: Manages payments and verifies credit cards.
- **`inventory-service`**: Manages the warehouse.

As an orchestrator, we’ve chosen [Orkes Conductor](https://play.orkes.io/), a modern and complex orchestrator that can be run either locally or in the cloud. Conductor provides an intuitive graphical interface, which allows us to define workflows, manage task invocations, and configure interactions between services. We will delve further into these capabilities as we proceed.

In our implementation, each microservice has its own dedicated database. For keeping things lightweight, all three services make use of an instance of the H2 local database. The key class, shared among all three services, is `Order`:

```java
package com.nbicocchi.order.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@Entity
public class Order {

   public enum OrderStatus {
      PENDING, APPROVED, REJECTED
   }

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @Column(unique = true, nullable = false, updatable = false)
   @EqualsAndHashCode.Include
   private String orderId = UUID.randomUUID().toString();
   private String productIds;
   private String customerId;
   private String creditCardNumber;
   private OrderStatus status = OrderStatus.PENDING;

   public Order(String productIds, String customerId, String creditCardNumber) {
      this.productIds = productIds;
      this.customerId = customerId;
      this.creditCardNumber = creditCardNumber;
   }
}

```

In summary, the architecture consists of three microservices, each with its own database to store product details, along with an orchestrator to manage interactions and ensure consistency across the platform.

## Docker configuration

Before delving into implementation details, reviewing the Docker container configuration will help clarify the architecture. The commented portion of the configuration file can be used for running **Conductor** locally as a Docker container.

```yaml
services:
   #conductor:
   #  image: orkesio/orkes-conductor-community-standalone:latest
   #  init: true
   #  ports:
   #    - "8080:8080"
   #    - "5000:5000"
   #  volumes:
   #    - redis:/redis
   #    - postgres:/pgdata
   #  healthcheck:
   #    test: [ "CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:8080" ]
   #    interval: 30s
   #    timeout: 10s
   #    retries: 5

   order:
      build: order-service
      mem_limit: 512m
      ports:
         - "9000:9000"
      environment:
         - SPRING_PROFILES_ACTIVE=docker
      #depends_on:
      #  conductor:
      #    condition: service_healthy

   payment:
      build: payment-service
      mem_limit: 512m
      ports:
         - "9001:9001"
      environment:
         - SPRING_PROFILES_ACTIVE=docker
      #depends_on:
      #  conductor:
      #    condition: service_healthy

   inventory:
      build: inventory-service
      mem_limit: 512m
      ports:
         - "9002:9002"
      environment:
         - SPRING_PROFILES_ACTIVE=docker
      #depends_on:
      #  conductor:
      #    condition: service_healthy

#volumes:
#  redis:
#  postgres:
```


## Conductor Overview

**Conductor** is a workflow orchestration framework developed by Netflix that facilitates the management of complex processes within microservices architectures. Conductor enables workflows to be defined as a sequence of independent tasks, each handled by different microservices, which promotes scalability, resilience, and centralized control. 

With support for multiple programming languages and integration with a wide array of technologies, Conductor is well-suited for automating and orchestrating distributed processes efficiently.

In our project, we need to add the necessary dependencies in each microservice’s `pom.xml` file to allow seamless interaction with Conductor and access to data:

```xml
<dependencies>
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>
   <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
   </dependency>
   <dependency>
      <groupId>io.orkes.conductor</groupId>
      <artifactId>orkes-conductor-client-spring</artifactId>
      <version>4.0.1</version>
   </dependency>
</dependencies>
```

## Workflow definition

In [our project](https://play.orkes.io/workflowDef/order-saga), designed to simulate an e-commerce platform, orders must pass certain checks prior to sale. For example, verifying the validity of a credit card number is crucial before completing a purchase. Essentially, a workflow in Conductor represents an activity diagram, structured as a sequence of tasks to be executed in order. Specifically, our workflow models the sale process and includes the following steps:

1. Creating a pending order (*Order* microservice).
2. Verifying the credit card number (*Payment* microservice)
3. Verifying the availability of each product within an order (*Inventory* microservice)
4. If both checks pass, the order moves from the *pending* to the *confirmed* state

By executing the workflow, we ensure consistent synchronization across databases. For example, if a product is purchased and the provided credit card number is not valid, the pending order is cancelled.


## Task definition and implementation

Each workflow consists of a set of tasks, and each task must be defined. To accomplish this, two main steps should be followed:

1. **Define Task Characteristics**: Use a JSON file to specify the attributes and configurations of each task.

2. **Implement Tasks in Java**: In the various microservices, implement the tasks by utilizing the [Worker](https://conductor-oss.github.io/conductor/devguide/how-tos/Workers/build-a-java-task-worker.html) interface from Conductor OSS.

Following these steps ensures that each task is correctly defined and implemented, allowing the workflow to function seamlessly.

### Task definition (JSON)

For each task, as with each workflow, it is essential to define not only the name but also any associated **metadata** (for example defining resiliency behaviours). These metadata typically adhere to a standard configuration and do require modifications. They can be also defined from the UI. Below an example of `payment-check` task in our workflow:

```json
{
   "createTime": 1732630264734,
   "updateTime": 1732630264734,
   "createdBy": "nbicocchi@unimore.it",
   "updatedBy": "nbicocchi@unimore.it",
   "name": "payment-check",
   "description": "",
   "retryCount": 3,
   "timeoutSeconds": 3600,
   "inputKeys": [],
   "outputKeys": [],
   "timeoutPolicy": "TIME_OUT_WF",
   "retryLogic": "FIXED",
   "retryDelaySeconds": 60,
   "responseTimeoutSeconds": 600,
   "concurrentExecLimit": 0,
   "inputTemplate": {},
   "rateLimitPerFrequency": 0,
   "rateLimitFrequencyInSeconds": 1,
   "ownerEmail": "nbicocchi@unimore.it",
   "pollTimeoutSeconds": 3600,
   "backoffScaleFactor": 1,
   "enforceSchema": false
}
```

### Task implementation (Java)
When it comes to defining the task in Java, we simply need to implement a class following this structure:

```java
package com.nbicocchi.payment.worker;

@AllArgsConstructor
@Component
@Slf4j
public class PaymentWorkers {
   PaymentRepository paymentRepository;

   /**
    * Note: Using this setting, up to 5 tasks will run in parallel, with tasks being polled every 200ms
    */
   @WorkerTask(value = "payment-check", threadCount = 1, pollingInterval = 200)
   public TaskResult paymentCheck(Order order) {
      log.info("Verifying {}...", order);
      Payment payment = new Payment(order.getOrderId(), order.getCreditCardNumber());
      if (payment.getCreditCardNumber().startsWith("7777")) {
         log.info("Verifying Order(valid)");
         payment.setSuccess(Boolean.TRUE);
         paymentRepository.save(payment);
         return new TaskResult(TaskResult.Result.PASS, "");
      }
      log.info("Verifying Order(not valid)");
      payment.setSuccess(Boolean.FALSE);
      paymentRepository.save(payment);
      return new TaskResult(TaskResult.Result.FAIL, "Invalid credit card number");
   }
}
```

Essentially, each Worker is associated with a Task, and the task's logic is implemented within the *@WorkerTask* methods. The example provided is the implementation of the `payment-check` task from our workflow.


## Trying out the ecosystem

Now that we have defined the workflow (both theoretically and practically), as well as the tasks and written the code to manage JPA `@Entity` classes, we can move on to see how to actually execute the workflow.

```sh
$ mvn clean package -Dmaven.test.skip=true
$ docker compose build
$ docker compose up --detach
```

The *inventory* microservice manages 3 products with ids: *P-001, P-002, P-003*. The warehouse contains 9, 0, and 1 instances of each of them, respectively (see inventory/runners/DataLoader).

The *payment* microservice allows payments only for cards starting with *7777* (see payment/workers/PaymentWorkers)

We can submit a new order to the system by invoking the */order* endpoint of the *order* microservice.

The following request succeeds because the credit card is valid and P-001 has enough instances. You can follow the SAGA by watching the logs of the three services.

```bash
curl -X POST http://localhost:9000/order \
     -H "Content-Type: application/json" \
     -d '{
    "productIds": "P-001",
    "customerId": "C-001",
    "creditCardNumber": "7777-1234-5678-0000"
        }'
```

The following request fails because the credit card is invalid.

```bash
curl -X POST http://localhost:9000/order \
     -H "Content-Type: application/json" \
     -d '{
    "productIds": "P-001",
    "customerId": "C-001",
    "creditCardNumber": "6666-1234-5678-0000"
        }'
```

The following request fails because P-002 not has enough instances.

```bash
curl -X POST http://localhost:9000/order \
     -H "Content-Type: application/json" \
     -d '{
    "productIds": "P-002",
    "customerId": "C-001",
    "creditCardNumber": "7777-1234-5678-0000"
        }'
```

## References
* [Saga Pattern with Conductor - Baeldung](https://www.baeldung.com/orkes-conductor-saga-pattern-spring-boot)
* [Conductor OSS site](https://conductor-oss.github.io/conductor/devguide/concepts/index.html)
* [Conductor OSS Documentation]()
* [Operators in Conductor](https://conductor-oss.github.io/conductor/documentation/configuration/workflowdef/operators/index.html)

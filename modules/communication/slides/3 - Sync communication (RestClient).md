# Communication styles (RESTClient)

## Key Principles of REST

**REST** is an architectural style that leverages the existing protocols of the web, specifically HTTP. It emphasizes stateless communication and a uniform interface for resource manipulation. The core HTTP methods used in RESTful communication are:

- **GET**: Retrieve information from the server.
- **POST**: Create a new resource on the server.
- **PUT**: Update an existing resource.
- **DELETE**: Remove a resource from the server.

1. **Statelessness**: Each request from a client to the server contains all the necessary information. The server does not store client context between requests.
2. **Client-Server Separation**: The client and server are independent entities that communicate over the network, allowing for changes on either side without affecting the other.
3. **Resource Identification**: Resources are identified using URIs (Uniform Resource Identifiers), which can be represented in various formats, such as JSON or XML.
4. **Uniform Interface**: RESTful APIs provide a uniform interface, which simplifies and decouples the architecture, making it easier for clients to interact with resources.

## Building RESTful Services

To create a RESTful service in Spring Boot, follow these steps (tools/code/product-service-h2):

1. **Define a Model Class**:
   Create a model class that represents the data structure of the resource. For example, a `Product` class:

```java
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Product {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @EqualsAndHashCode.Include
   private String uuid;
   private String name;
   private Double weight;

   public Product(String uuid, String name, Double weight) {
      this.uuid = uuid;
      this.name = name;
      this.weight = weight;
   }
}

```

2. **Create a REST Controller**:
   Define a REST controller that handles incoming HTTP requests and responds with the appropriate data.

```java
@RestController
@RequestMapping("/products")
public class ProductController {
   ProductService productService;

   public ProductController(ProductService productService) {
      this.productService = productService;
   }

   @GetMapping("/{uuid}")
   public Product findByUuid(@PathVariable String uuid) {
      return productService.findByUuid(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
   }

   @GetMapping
   public Iterable<Product> findAll() {
      return productService.findAll();
   }

   @PostMapping
   public Product create(@RequestBody Product product) {
      return productService.save(product);
   }

   @PutMapping("/{uuid}")
   public Product update(@PathVariable String uuid, @RequestBody Product product) {
      Optional<Product> optionalProject = productService.findByUuid(uuid);
      optionalProject.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      product.setId(optionalProject.get().getId());
      return productService.save(product);
   }

   @DeleteMapping("/{uuid}")
   public void delete(@PathVariable String uuid) {
      Optional<Product> optionalProject = productService.findByUuid(uuid);
      optionalProject.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      productService.delete(optionalProject.get());
   }
}
```

## Consuming RESTful Services with RestClient

In a microservices architecture, it is often necessary for services to consume APIs provided by other services. Spring Boot provides a simple way to achieve this through the `RestClient` class, which allows for HTTP requests to be made and handled effectively.

To consume a RESTful service in Spring Boot, follow these steps (code/sync-one-to-one):

1. **Use RestClient**:
   Use `RestClient` to make HTTP requests to other services. Here's an example of a service that consumes a REST API to fetch product data using a dedicated class named _ProductIntegration_:

```java
@RestController
@RequestMapping("/orders")
public class OrderController {
   OrderRepository orderRepository;
   ProductIntegration productIntegration;

   public OrderController(OrderRepository orderRepository, ProductIntegration productIntegration) {
      this.orderRepository = orderRepository;
      this.productIntegration = productIntegration;
   }

   @GetMapping(value = "")
   public Iterable<OrderDto> findAll() {
      Iterable<Order> orders = orderRepository.findAll();

      List<OrderDto> orderDtos = new ArrayList<>();
      for (Order order : orders) {
         OrderDto orderDto = new OrderDto(
                 order.getId(),
                 order.getUuid(),
                 order.getTimestamp(),
                 new HashSet<>()
         );

         for (ProductOrder productOrder : order.getProducts()) {
            orderDto.getProducts().add(productIntegration.findbyUuid(productOrder.getUuid()));
         }
         orderDtos.add(orderDto);
      }
      return orderDtos;
   }

   @GetMapping(value = "/{id}")
   public Order findById(@PathVariable Long id) {
      return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
   }
}
```

```java
@Component
public class ProductIntegration {
    String productServiceHost;
    int productServicePort;

    public ProductIntegration(
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort) {
        this.productServiceHost = productServiceHost;
        this.productServicePort = productServicePort;
    }

    public List<ProductDto> findAll() {
        String url = "http://" + productServiceHost + ":" + productServicePort + "/products";
        RestClient restClient = RestClient.builder().build();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public ProductDto findbyUuid(String uuid) {
        String url = "http://" + productServiceHost + ":" + productServicePort + "/products" + "/" + uuid;
        RestClient restClient = RestClient.builder().build();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
```

When consuming external APIs, it is important to handle errors properly. You can use try-catch blocks to manage exceptions that may occur during API calls.

```
try {
    User user = restClient.getForObject(url, User.class);
} catch (RestClientException e) {
    // Handle the error, e.g., log it or throw a custom exception
}
```

## DTOs
A **DTO (Data Transfer Object)** is a design pattern used in software engineering to transfer data between different parts of an application, often across network boundaries or between layers within the same application. The main purpose of a DTO is to encapsulate data and reduce the amount of information sent over the network by only containing necessary fields, and it’s commonly used in distributed systems and applications that follow the layered architecture (such as MVC or service-oriented architectures).

### Key Characteristics of DTOs
- **Encapsulation**: DTOs wrap data in a structure that hides complex entities or potentially sensitive information, only exposing fields relevant for the specific data transfer operation.
- **No Business Logic**: DTOs typically don’t contain business logic, as they serve only as containers for data. Their purpose is purely data transfer, so methods like getters and setters are usually the only ones included.
- **Serialization**: Since DTOs are often transferred over a network or between application boundaries, they are usually designed to be serializable (e.g., JSON or XML).

### When to Use DTOs
- **APIs and Microservices**: DTOs are commonly used in REST APIs and microservices. They provide a way to define the format and content of the data being exchanged without exposing internal models or database entities.
- **Reducing Data Load**: By selecting only relevant fields, DTOs can reduce the amount of data transmitted, especially useful in mobile applications or low-bandwidth networks.
- **Decoupling Layers**: In a layered architecture (e.g., separating data access, business logic, and presentation layers), DTOs help maintain separation by acting as an intermediary between the business and presentation layers.

Automatic mapping between entities and DTOs is a common requirement, as it simplifies the process of converting data between different layers of an application. In both Java and Python, libraries are available to facilitate this mapping, reducing boilerplate code and improving code readability.

### Entity-to-DTO Mapping
In Java, several libraries provide automatic mapping capabilities. Here are the most popular ones:

**MapStruct**: (Java) MapStruct is a powerful, compile-time, code-generating library that creates type-safe mappers between Java objects (e.g., entities and DTOs). It generates code at compile-time, so there's no runtime overhead, making it fast and efficient.
   
**ModelMapper**: (Java) ModelMapper is a more flexible, runtime-based library that provides a convention-based approach to object mapping. It can automatically map properties with similar names and is highly customizable.

**Marshmallow**: (Python) Marshmallow is a popular library for object serialization/deserialization in Python. It is typically used to convert objects to and from JSON, but it can also be used for DTO mappings.

**Pydantic**: (Python) Pydantic is primarily used for data validation and settings management, but it also serves as a great DTO library. It provides data validation and conversion between Python objects and JSON-compatible formats.

## Resources
* https://www.baeldung.com/spring-boot-restclient
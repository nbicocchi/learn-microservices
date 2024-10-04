# Communication styles (REST)

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

To create a RESTful service in Spring Boot, follow these steps (refer to shared-code/product-service-no-db):

1. **Create a New Spring Boot Project**:
   Use Spring Initializr to generate a new Spring Boot project with dependencies like `Spring Web`.

2. **Define a Model Class**:
   Create a model class that represents the data structure of the resource. For example, a `Product` class:

```java
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
   private Long id;
   private String name;
   private Double weight;
   
   // ...
}
```

3. **Create a REST Controller**:
   Define a REST controller that handles incoming HTTP requests and responds with the appropriate data.

```java
@RestController
@RequestMapping("/products")
public class ProductController {
   ProductService productService;

   public ProductController(ProductService productService) {
      this.productService = productService;
   }

   @GetMapping("/{id}")
   public Product findById(@PathVariable Long id) {
      return productService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
   }

   @GetMapping
   public Iterable<Product> findAll() {
      return productService.findAll();
   }

   @PostMapping
   public Product create(@RequestBody Product product) {
      return productService.save(product);
   }

   @PutMapping("/{id}")
   public Product create(@PathVariable Long id, @RequestBody Product product) {
      productService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      product.setId(id);
      return productService.save(product);
   }

   @DeleteMapping("/{id}")
   public void delete(@PathVariable Long id) {
      Optional<Product> optionalProject = productService.findById(id);
      optionalProject.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      productService.delete(optionalProject.get());
   }
}
```

## Consuming RESTful Services with RestClient

In a microservices architecture, it is often necessary for services to consume APIs provided by other services. Spring Boot provides a simple way to achieve this through the `RestClient`, which allows for HTTP requests to be made and handled effectively.

To consume a RESTful service in Spring Boot, follow these steps (refer to order-service-no-db):

1. **Create a New Spring Boot Project**:
   Use Spring Initializr to generate a new Spring Boot project with dependencies like `Spring Web`.

2. **Use RestClient**:
   Use `RestClient` to make HTTP requests to other services. Here's an example of a service that consumes a REST API to fetch user data:

```java
@RestController
@RequestMapping("/orders")
public class ConsumerController {
   RestClient restClient;
   String productServiceUrl;

   public ConsumerController(
           @Value("${app.product-service.host}") String productServiceHost,
           @Value("${app.product-service.port}") int productServicePort) {
      productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/products";
   }

   @GetMapping("/{id}")
   public Order findById(@PathVariable Long id) {
      RestClient restClient = RestClient.builder().build();
      List<Product> products = restClient.get()
              .uri(productServiceUrl)
              .retrieve()
              .body(new ParameterizedTypeReference<>() {});
      return new Order("order-x", LocalDate.now(), products);
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

## Docker configuration

```
services:
  product-service:
    image: product-service-no-db
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  order-service:
    build: order-service-no-db
    image: order-service-no-db
    mem_limit: 512m
    ports:
      - 8080:8080
    environment:
      - SPRING_PROFILES_ACTIVE=docker
```

## Resources
* https://www.baeldung.com/spring-boot-restclient
# GraphQL

Refer to the *product-service-graphql* example (labs/product-service-graphql):

The **product-service** manages product data. The model class is reported below:

```java
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull @EqualsAndHashCode.Include private String uuid;
    @NonNull private String name;
    @NonNull private Double weight;
}
```

---

## Dependencies

To use **GraphQL** with **Spring Boot**, add the following dependencies to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>

<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java-extended-scalars</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

These enable:

* GraphQL endpoint auto-configuration (`/graphql`)
* GraphQL Playground (via Altair or other clients)
* Integration with Spring WebMVC for HTTP transport

---

## GraphQL Schema

* **Defines the API contract**: types, queries, mutations, and inputs.
* Example schema for products:

```graphql
type Product {
    uuid: String!
    name: String!
    weight: Float!
}

type Query {
    allProducts: [Product!]!
    productByUuid(uuid: String!): Product
}

type Mutation {
    createProduct(product: ProductInput!): Product
    updateProduct(uuid: String!, product: ProductInput!): Product
    deleteProduct(uuid: String!): Boolean
}

input ProductInput {
    uuid: String!
    name: String!
    weight: Float!
}
```

* Strongly typed → clients know exactly what to send and expect.
* Allows **auto-generated documentation** and schema introspection.

---

## GraphQL Controller

The GraphQL controller handles incoming GraphQL queries and mutations. It supports two key queries and three mutations:

* `allProducts` → Returns all products.
* `productByUuid(uuid: String!)` → Returns a specific product.
* `createProduct(product: ProductInput!)` → Creates a new product.
* `updateProduct(uuid: String!, product: ProductInput!)` → Updates a product.
* `deleteProduct(uuid: String!)` → Deletes a product.

```java
@Controller
public class ProductGraphQLController {
    ProductService productService;

    public ProductGraphQLController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public Iterable<Product> allProducts() {
        return productService.findAll();
    }

    @QueryMapping
    public Product productByUuid(@Argument String uuid) {
        return productService.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @MutationMapping
    public Product createProduct(@Argument ProductInput product) {
        Product p = new Product(product.getUuid(), product.getName(), product.getWeight());
        return productService.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument String uuid, @Argument ProductInput product) {
        Product p = productService.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        p.setName(product.getName());
        p.setWeight(product.getWeight());
        return productService.save(p);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument String uuid) {
        Product p = productService.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productService.delete(p);
        return true;
    }

    public static class ProductInput {
        private String uuid;
        private String name;
        private Double weight;
        // getters and setters
    }
}
```

---

## GraphQL Queries

* Queries are **read-only operations**.
* Clients request **exactly the fields they need**.

Example: fetch all products

```graphql
query {
  allProducts {
    uuid
    name
    weight
  }
}
```

Example: fetch a single product by UUID

```graphql
query {
  productByUuid(uuid: "171f5df0-b213-4a40-8ae6-fe82239ab660") {
    uuid
    name
    weight
  }
}
```

---

## GraphQL Mutations

* Mutations are **write operations** (create, update, delete).
* They can return the modified object, enabling the client to update its UI immediately.

Example: create a product

```graphql
mutation {
  createProduct(product: {uuid: "new-001", name: "Tablet", weight: 1.1}) {
    uuid
    name
    weight
  }
}
```

Example: update a product

```graphql
mutation {
  updateProduct(
    uuid: "171f5df0-b213-4a40-8ae6-fe82239ab660",
    product: {
      uuid: "171f5df0-b213-4a40-8ae6-fe82239ab660",
      name: "Laptop X",
      weight: 2.5
    }
  ) {
    uuid
    name
    weight
  }
}
```

Example: delete a product

```graphql
mutation {
  deleteProduct(uuid: "new-001")
}
```

---

## Testing the API

You can test the GraphQL API in several ways:

* **Altair GraphQL Client (browser plugin)**
  Available for Chrome and Firefox, it provides an interactive interface to send queries and mutations to your Spring Boot GraphQL endpoint (default: `http://localhost:8080/graphql`).

* **GraphQL Playground / Postman**
  These tools can also send HTTP POST requests to `/graphql` with a GraphQL query body.

---

## Resources

* [Spring GraphQL Reference](https://docs.spring.io/spring-graphql/docs/current/reference/html/)
* [GraphQL Official Website](https://graphql.org/)
* [Altair GraphQL Client (browser plugin)](https://altair.sirmuel.design/)


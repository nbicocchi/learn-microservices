# GraphQL

## Theory

GraphQL is a query language for APIs (Application Programming Interfaces), developed by Facebook in 2012.

Unlike traditional REST APIs, where the client has to make multiple requests to obtain all the information it needs, GraphQL allows the client to specify exactly what data it wants to obtain and from where, in a single request.

In addition, GraphQL provides a strong typing system, which allows developers to clearly define the data structure and validate queries at compile time. This leads to better automatic API documentation and greater robustness in client and server code.

```
query {
  getPostById(id: "123") {
    id
    description
    user {
      id
      username
    }
    comments {
      id
      content
    }
    likesCount
    imagePath
  }
}
```

When this query is executed on the GraphQL server, a JSON object will be returned containing the required information about the post, or possibly an error if the post is not found or another problem occurs during the execution of the query. For example:

```json
{
  "data": {
    "post": {
      "id": "123",
      "description": "This is the post description.",
      "user": {
        "id": "456",
        "username": "john_doe"
      },
      "comments": [
        {
        "id": "789",
        "content": "This is the first comment."
        },
        {
        "id": "1011",
        "content": "This is the second comment."
        }
      ],
      "likesCount": "10",
      "imagePath": "posts/default.jpg"
    }
  }
}
```

In the above example, the values of all parameters were requested; in case it wants only some of them (e.g., just the name), the answer will be:

```json
{
  "data": {
    "post": {
      "description": "This is the post description."
    }
  }
}
```

making the query:

```
query {
  getPostById(id: "123") {
    description
  }
}
```

For the moment, we will focus on the basic concepts of GraphQL also by using examples with queries, and then we will move on to the technical aspects of its implementation.


### Queries & Mutations
In GraphQL, queries are used to retrieve data from the server, while mutations are used to modify or create data on the server. Both queries and mutations are defined in the GraphQL schema and can be executed by clients to interact with the server.

- **Query**: Used to retrieve data from the server; we have talked about it in the previous section.
- **Mutation**: Used to modify or create data on the server. 

An example can be:
    ```
    mutation {
        createPost(description: "This is a description", userId: "200", imagePath: "posts/default.jpg") {
            id
            description
            likescount
        }
    }
    ```
  Note the *Data mutations and after fetch* principle: after a mutation, the server returns the data that was modified or created. This allows the client to update its local cache or UI based on the server's response.

  If we don't want to return anything, we can use the keyword *void*:
    ```
    mutation {
        deletePost(id: "123") {
            void
        }
    }
    ```


### Aliases
In GraphQL, aliases are used to request the same field or fields multiple times within a single query, but with different names for each occurrence.
This is particularly useful when you want to retrieve similar data from a GraphQL server but need to differentiate between them in the response.

```
query GetProduct {
    alias0: getPostById(productId: 111) {
       id
       description
    }

    alias1: getPostById(productId: 112) {
       id
       description
    }
}
```
Here is the response:
```json
{
  "data": {
    "alias0": {
      "id": 111,
      "description": "post 111"
    },
    "alias1": {
      "id": 112,
      "description": "post 112"
    }
  }
}
```

### Fragments
Fragments in GraphQL are like reusable units of fields. They allow you to define a set of fields that you can include in multiple queries, mutations, or other fragments.

```
fragment PostFields on Post {
    id
    description
}
```
In this example, the fragment **`PostFields`** defines a set of fields that can be included in queries or mutations that require information about a post. The **`on Post`** part specifies that the fragment applies to objects of type **`Post`**.

```
query GetPost {
    getPostById(productId: 111) {
        ...PostFields
    }
  
}
```

### Operations
The operationName in GraphQL is an optional piece of metadata that you can include in your GraphQL requests. It's used to specify the name of the operation being performed within a multi-operation request.

In GraphQL, you can send multiple operations (queries, mutations, or subscriptions) in a single request separated by curly braces {}. This is particularly useful when you want to fetch or mutate multiple sets of data in a single round trip to the server.
Here's an example of a GraphQL request with multiple operations:
```To use the fragment in a query, you can include it like this:
query PleaseGetPost {
  getPostById(id: 111) {
    id
    description
  }
  getPostByUserId(userId: 200) {
    id
    likesCount
  }
}
```
Will be possible to see *operationName* in some places, like in the GraphQL debug interface:
```
--- logs here ---
    getPostById(id: 111) {
        id
        description
    }
    getPostByUserId(userId: 200) {
        id
        likesCount
    },
    operationName='PleaseGetProduct'
--- others logs here ---
```

### Variables
Variables in GraphQL allow you to parameterize your queries, mutations, or subscriptions, making them dynamic and reusable. Instead of hardcoding values directly into your GraphQL operations, you can use variables to pass values from the client to the server at runtime.

Here's how you can define and use variables in GraphQL:
1. Define the variable in the query or mutation operation.
    ```
        query GetPost($postId: ID!) {
            getPostById(id: $postId) {
                description
                likesCount
            }
        }
    ```
   You can define the default variable value by adding a colon and the default value after the type declaration:
   ```
       query GetPost($postId: ID = "123") {
         getPostById(id: $postId) {
           description
           likesCount
         }
       }
   ```

2. Pass the variable values when executing the operation.
  ```
    {
        "postId": "123"
    }
  ```
Using variables in GraphQL provides several benefits:
- **Dynamic Queries**: Variables allow you to construct dynamic queries based on user input or other runtime conditions.
- **Security**: Using variables helps protect against injection attacks, as values are passed separately from the query string.
- **Query Reusability**: By parameterizing your queries, mutations, or subscriptions, you can reuse them with different input values, improving code maintainability and reducing duplication.

### Directives
Directives in GraphQL are used to conditionally include or exclude fields or fragments in a query based on certain conditions. They provide a way to control the execution of a query and customize the response based on the client's requirements.
Here is an example of how you can use directives in a GraphQL query:
```
query GetPost($includeImage: Boolean!) {
  getPostById(id: "123") {
    description
    imagePath @include(if: $includeImage)
  }
}
```
In this query, the **`@include`** directive is used to conditionally include the **`imagePath`** field based on the value of the **`$includeImage`** variable. If the variable is **`true`**, the **`imagePath`** field will be included in the response; otherwise, it will be excluded.
```
{
    "$includeImage": true
}
```

### Introspection query
The introspection query is a special query in GraphQL that allows you to query the schema of a GraphQL server to retrieve information about the types, fields, and directives defined in the schema. This can be useful for exploring the capabilities of a GraphQL API, generating documentation, or building tools that work with GraphQL schemas.
Some application like Postman uses this query in advance to retrieve the schema of the API and to allow the user to compose queries and mutations in an intuitive way.

It is possible to do this query, by cURL for example, by sending the attached JSON body file *introspection_query.json* (omitted in this text for readability reasons because is very long) to the desired endpoint (e.g., *http://localhost:7001/graphql*) by the command:
```bash
curl --location --request POST 'http://localhost:7001/graphql' \
--header 'Content-Type: application/json' \
-d @introspection_query.json
```
The response is also omitted for brevity because can be really verbose.

Obviously, using this kind of method is a little bit cumbersome, so it is advisable to not use it.
This query will return a JSON object containing information about the schema of the GraphQL server, including the query type, mutation type, subscription type, types, and directives defined in the schema.

## Technical explanation

### Schema

The GraphQL schema defines the structure of the data available through a GraphQL API. This schema provides a clear map of the available data types and the relationships between them, enabling developers to understand how to interact with the API and what data can be requested and sent. In fact, it is possible to make a request in advance in order to know what objects and queries are available.

In the GraphQL schema, several data types are defined, including:

1. **Object Types**: They represent objects within the system. For example, an object type could represent a user, a post or any other entity in the system.
2. **Fields**: These are the properties of an object type. Each field has a name and a type. Fields can be scalar (strings, numbers, booleans, etc.) or they can be other object types.
3. **Arguments**: These are the parameters passed to fields to customise the result. For example, a query to obtain information about a user might require an argument such as the user's ID.
4. **Scalar types**: These are the primitive data types, such as strings, numbers, Booleans, etc.
5. **List of types**: Indicates an array of a specific type of data. For example, a list of posts.

Here is a simplified example of what a GraphQL schema might look like:

```graphql
type User {
    id: ID!
    username: String!
    email: String!
    password: String!
    avatarPath: String!
}

input UserInput {
    username: String!
    email: String!
    password: String!
}

type Query {
    getUserById(id: ID!): User
    getUserByUsername(username: String!): User
    getUserByEmail(email: String!): User
    getUsers: [User]!
}

type Mutation {
    createUser(username: String!, email: String!, password: String!, avatarPath:String!): User!
    deleteUser(id: ID!): Boolean
    updateUser(id: ID!, username: String, email: String, password: String, avatarPath: String): User
}
```

In this example:

- An object type was defined: User, which represents users in the system.
- Each object type has fields representing the properties of that object; with their respective types (integer, string, etc.). The "!" symbol in a GraphQL schema indicates that a field is mandatory, i.e. it must always have a value when returned by the GraphQL server. If a field has the "!" symbol, it means that it cannot be null and must be included in the query result. The usage of the square brackets around type show that the object returned is a List.
- The **`Input`** is a data type used to define the structure of input parameters for mutations. Mutations are operations that modify or update data in the GraphQL server, such as creating a new user or editing a post.
- The type **`Query`** defines the available read operations (queries), such as getUserById, which returns the details of the user having that specific *id*. It is similar to the GET method in REST.
- The **`Mutation`** in GraphQL are operations that allow data to be modified on the server. Whereas queries are used to read data, mutations allow data to be created, modified or deleted in the system; they take as input the parameters defined within the round brackets and return the values of the type defined after the symbol ":"; again the presence of the symbol "!" symbol means that after the operation is executed, it must return something other than *null*. Mutations are defined within the GraphQL schema just like queries, but are annotated with the type **`Mutation`** instead of **`Query`**.
  It works like the POST, PUT, PATCH and DELETE methods in REST.

### Responses

A special feature of GraphQL, unlike REST, is that the status code of the HTTP response is always 200, regardless of whether the request was successful or not. This is because GraphQL handles errors differently from traditional REST APIs.

Instead of using the HTTP status code to report errors, GraphQL returns a JSON object with an 'errors' key in the response body if errors occur during query processing. This object contains a list of errors giving details of the type of error and where it occurred.

For example, if a GraphQL query contains a syntax error, the server will respond with a status 200 and include an 'errors' object in the response to indicate the specific error:

- **Wrong query**:

    ```bash
    query GetUser {
        getUserById(id: 123 { # it lacks the ")" symbol after 123
            username
            email
        }
    }
    ```

- **Response**:

    ```bash
    {
        "errors": [
          {
            "message": "Invalid Syntax : offending token '{' at line 2 column 31"
          }
        ]
     }
    ```


On the other hand, if the query is processed correctly and there are no errors, the response will still be status 200 and will include the requested data in the JSON response.

Therefore, when working with GraphQL, it is important to examine the content of the JSON response to determine whether the request was successful or not, rather than relying solely on the HTTP status code.

**Exceptions handling**

It is possible to enhance the response of wrong query in order to make it more verbose (e.g. *notfoundexception* in the case of an object not found instead of a generic one). It can be done by adding a custom exception resolver class (extending *DataFetcherExceptionResolverAdapter*) like this:

```java
@Component
public class GraphQlCustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof NotFoundException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof InvalidInputException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof BadRequestException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
        else {
            return null;
        }
    }
}
```
where every exception returns a more verbose error. Obviously each exception will have to be declared separately (see attached code in the repository).


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

* **Postman**
  These tools can also send HTTP POST requests to `/graphql` with a GraphQL query body.

* **GraphiQL Interface (optional, built-in)**
  Spring Boot can expose a GraphiQL web interface at `/graphiql` (if enabled via `spring.graphql.graphiql.enabled=true`). It allows you to interactively explore your GraphQL schema, execute queries, and test mutations directly in the browser.

## Resources

* [Spring GraphQL Reference](https://docs.spring.io/spring-graphql/docs/current/reference/html/)
* [GraphQL Official Website](https://graphql.org/)
* [Altair GraphQL Client (browser plugin)](https://altair.sirmuel.design/)


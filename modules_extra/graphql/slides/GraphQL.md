# GraphQL

## Theory introduction

GraphQL is a query language for APIs (Application Programming Interfaces), developed by Facebook in 2012 and made open-source in 2015.

Unlike traditional REST APIs, where the client has to make multiple requests to obtain all the information it needs, GraphQL allows the client to specify exactly what data it wants to obtain and from where, in a single request.

With GraphQL, the client sends a query describing the structure of the data it wishes to receive, and the server responds with a JSON containing only the requested data, in the format requested by the client. This approach allows greater flexibility and efficiency, as it reduces network overhead and allows the client to obtain only the data it needs, without unnecessary information.

In addition, GraphQL provides a strong typing system, which allows developers to clearly define the data structure and validate queries at compile time. This leads to better automatic API documentation and greater robustness in client and server code.

Another advantage to using GraphQL is the lack of the need to provide for api versioning to allow, for example, legacy applications to run. This is because this query language only returns the data that’s explicitly requested, so new capabilities can be added via new types and new fields on those types without creating a breaking change.

For example, if you want to obtain information about an individual user, you can send the query:
```
query {
  user(id: "123") {
    id
    name
    email
    age
    posts {
      title
      body
    }
  }
}
```

In this query:

- **`query`** is the keyword indicating that a query is being performed.
- **`user`** is the name of the query endpoint, which may correspond to a function or a field defined in the GraphQL server.
- **`(id: "123")`** specifies the query arguments. In this case, we are querying the information of a user with a specific ID (in our example, "123").
- **`id`**, **`name`**, **`email`**, **`age`** are the required user fields.
- **`posts`** is a field that can be a list of objects, which may contain additional fields such as **`title`** and **`body`**, representing the user's posts.

When this query is executed on the GraphQL server, a JSON object will be returned containing the required information about the user, such as ID, name, email, age and posts, or possibly an error if the user is not found or another problem occurs during the execution of the query. For example:

```json
{
  "data": {
    "user": {
      "id": "123",
      "name": "Mario Rossi",
      "email": "mario@example.com",
      "age": 30,
      "posts": [
        {
          "title": "My first post",
          "body": "This is the body of my first post."
        },
        {
          "title": "My second post",
          "body": "This is the body of my second post."
        }
      ]
    }
  }
}
```

In this response:

- We have a JSON object with a key **`data`**, which contains the data required by the query.
- Within the object **`data`**, we have an object **`user`**, which contains information about the requested user.
- The object **`user`** contains the properties **`id`**, **`name`**, **`email`** and **`age`**, which match the information of the user specified in the query.
- The property **`posts`** is an array containing the user's post objects, each with fields **`title`** and **`body`**. In this case, we have two returned posts.

In the above example, the values of all parameters were requested; in case it wants only some of them (e.g., just the name), the answer will be:
```json
{
  "data": {
    "user": {
      "name": "Mario Rossi"
    }
  }
}
```
making the query:
```
query {
  user(id: "123") {
    name
  }
}
```
For the moment, we will focus on the basic concepts of GraphQL also by using examples with queries, and then we will move on to the technical aspects of its implementation.

### Basic concepts

#### Queries & Mutations
In GraphQL, queries are used to retrieve data from the server, while mutations are used to modify or create data on the server. Both queries and mutations are defined in the GraphQL schema and can be executed by clients to interact with the server.
The main difference between queries and mutations is that queries are read-only operations that do not modify the server's state, while mutations are write operations that can modify the server's state. 
The syntax for queries and mutations is similar, but they are distinguished by the operation keyword at the beginning of the request:
- **Query**: Used to retrieve data from the server; we have talked about it in the previous section.
- **Mutation**: Used to modify or create data on the server. An example can be:
    ```
    mutation {
        createUser(name: "Alice", email: "alice@mail.com") {
            id
            name
            email
        }
    }
    ```
    Note the *Data mutations and after fetch* principle: after a mutation, the server returns the data that was modified or created. This allows the client to update its local cache or UI based on the server's response.
    If we don't want to return anything, we can use the keyword *void*:
    ```
    mutation {
        deleteUser(id: "123") {
            void
        }
    }
    ```
    In this case, the server will delete the user with the specified ID and return nothing.

#### Aliases
In GraphQL, aliases are used to request the same field or fields multiple times within a single query, but with different names for each occurrence. 
This is particularly useful when you want to retrieve similar data from a GraphQL server but need to differentiate between them in the response.
Anyway,  if any part of a query fails to execute successfully, the entire query will result in an error. 
This is known as "all or nothing" behavior. So, when using aliases, it's important to ensure that each aliased field is valid and can be resolved successfully. Otherwise, the entire query will fail.
Just for example, if you want to retrieve information about two products with different IDs, you can use aliases to differentiate between them in the response:
```
query GetProduct {
    alias0: getProduct(productId: 111) {
        productId
        name
        serviceAddress
}

    alias1: getProduct(productId: 112) {
        productId
        name
        serviceAddress
    }
}
```
Here is the response:
```json
{
  "data": {
    "alias0": {
      "productId": 111,
      "name": "product 111"
    },
    "alias1": {
      "productId": 112,
      "name": "product 112"
    }
  }
}
```

#### Fragments
Fragments in GraphQL are like reusable units of fields. They allow you to define a set of fields that you can include in multiple queries, mutations, or other fragments.
Here's a breakdown of how fragments work in GraphQL:
```
fragment ProductFields on Product {
    productId
    name
}
```
In this example, the fragment **`ProductFields`** defines a set of fields that can be included in queries or mutations that require information about a product. The **`on Product`** part specifies that the fragment applies to objects of type **`Product`**.
To use the fragment in a query, you can include it like this:
```
query GetProduct {
  getProduct(productId: 111) {
    ...productFields
  }
}
```

#### Operations
The operationName in GraphQL is an optional piece of metadata that you can include in your GraphQL requests. It's used to specify the name of the operation being performed within a multi-operation request.

In GraphQL, you can send multiple operations (queries, mutations, or subscriptions) in a single request separated by curly braces {}. This is particularly useful when you want to fetch or mutate multiple sets of data in a single round trip to the server.
Here's an example of a GraphQL request with multiple operations:
```
query PleaseGetProduct {
  getProduct(productId: 111) {
    productId
    name
  }
}
```
Will be possible to see *operationName* in some places, like in the GraphQL debug interface:
```
--- logs here ---
    getProduct(productId: 112) {
      productId
      name
    }, 
    operationName='PleaseGetProduct'
--- others logs here ---
```

#### Variables
Variables in GraphQL allow you to parameterize your queries, mutations, or subscriptions, making them dynamic and reusable. Instead of hardcoding values directly into your GraphQL operations, you can use variables to pass values from the client to the server at runtime.

Here's how you can define and use variables in GraphQL:
1. Define the variable in the query or mutation operation.
  ```
  query GetProduct($productId: ID!) {
  product(id: $productId) {
    name
    price
  }
}
 ```
  You can define the default variable value by adding a colon and the default value after the type declaration:
  ```
    query GetProduct($productId: ID = "123") {
    product(id: $productId) {
        name
        price
        } 
    }
  ```

2. Pass the variable values when executing the operation.
  ```
  {
  "productId": "123"
}
  ```
Using variables in GraphQL provides several benefits:
- **Dynamic Queries**: Variables allow you to construct dynamic queries based on user input or other runtime conditions. 
- **Security**: Using variables helps protect against injection attacks, as values are passed separately from the query string. 
- **Query Reusability**: By parameterizing your queries, mutations, or subscriptions, you can reuse them with different input values, improving code maintainability and reducing duplication.

#### Directives
Directives in GraphQL are used to conditionally include or exclude fields or fragments in a query based on certain conditions. They provide a way to control the execution of a query and customize the response based on the client's requirements.
Here is an example of how you can use directives in a GraphQL query:
```
query GetProduct($includeServiceAddress: Boolean!) {
  product(id: "123") {
    name
    price @include(if: $includePrice)
  }
}
```
In this query, the **`@include`** directive is used to conditionally include the **`price`** field based on the value of the **`includePrice`** variable. If the variable is **`true`**, the **`price`** field will be included in the response; otherwise, it will be excluded.
```
{
    "$includePrice": true
}
```

#### Introspection query
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
type Product {
    productId: Int!
    name: String!
    weight: Int!
}

input ProductInput {
    productId: Int!
    name: String!
    weight: Int!
}

type Query {
    getProduct(productId: Int!): Product!
}

type Mutation {
    createProduct(input: ProductInput!): Product!
    deleteProduct(productId: Int!): Boolean
}
```

In this example:

- An object type was defined: Product, which represents products in the system.
- Each object type has fields representing the properties of that object; with their respective types (integer, string, etc.). The "!" symbol in a GraphQL schema indicates that a field is mandatory, i.e. it must always have a value when returned by the GraphQL server. If a field has the "!" symbol, it means that it cannot be null and must be included in the query result. The usage of the square brackets around type show that the object returned is a List.
- The **`Input`** is a data type used to define the structure of input parameters for mutations. Mutations are operations that modify or update data in the GraphQL server, such as creating a new user or editing a post.
- The type **`Query`** defines the available read operations (queries), such as getProduct, which returns the details of the product having that specific *productId*. It is similar to the GET method in REST.
- The **`Mutation`** in GraphQL are operations that allow data to be modified on the server. Whereas queries are used to read data, mutations allow data to be created, modified or deleted in the system; they take as input the parameters defined within the round brackets and return the values of the type defined after the symbol ":"; again the presence of the symbol "!" symbol means that after the operation is executed, it must return something other than *null*. Mutations are defined within the GraphQL schema just like queries, but are annotated with the type **`Mutation`** instead of **`Query`**.
It works like the POST, PUT, PATCH and DELETE methods in REST.

### Responses

A special feature of GraphQL, unlike REST, is that the status code of the HTTP response is always 200, regardless of whether the request was successful or not. This is because GraphQL handles errors differently from traditional REST APIs.

Instead of using the HTTP status code to report errors, GraphQL returns a JSON object with an 'errors' key in the response body if errors occur during query processing. This object contains a list of errors giving details of the type of error and where it occurred.

For example, if a GraphQL query contains a syntax error, the server will respond with a status 200 and include an 'errors' object in the response to indicate the specific error:

- **Wrong query**:

    ```bash
    query GetProduct {
        getProduct(productId: 123 { # it lacks the ")" symbol after 123
            productId
            name
            weight
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

### GraphQL vs REST
Here is a quick overview of the differences between GraphQL and REST API:

#### REST API
1. **Architecture**:
   - Based on resources identified by URLs. 
   - Uses standard HTTP methods like GET, POST, PUT, DELETE.

2. **Requests**:
   - Each endpoint is tied to a specific resource.
   - Responses are often fixed and can include unnecessary data.

3. **Scalability**:
   - Simpler to implement for straightforward APIs.
   - Can become complex with the increase in resources and relationships between them.

4. **Versioning**:
   - Often requires versioning of the API to manage changes (e.g., /v1/users, /v2/users).

5. **Caching**:
   - Excellent cache management due to standard HTTP methods.

#### GraphQL
1. **Architecture**:
    - Based on a single endpoint for all requests.
    - Allows defining exactly what data is required in the query.

2. **Requests**:
    - Clients can specify exactly what data they want, reducing unnecessary data.
    - More flexible, as a single query can request data from multiple resources with one call.

3. **Scalability**:
    - Can handle complex relationships between data without needing specific endpoints.
    - Requires more initial planning to define the data schema.

4. **Versioning**:
    - Does not require API versioning, as changes can be managed through the schema without breaking existing queries.
   
5. **Caching**:
    - Cache management is more complex, requiring specific techniques and tools (e.g., client-side persistence).

#### Key Differences
- **Flexibility**: GraphQL offers greater flexibility in data requests compared to REST, which has more rigid responses. 
- **Request Efficiency**: GraphQL can reduce the number of requests needed to obtain all the required data, while REST may require multiple API calls. 
- **Response Structure**: With GraphQL, responses are exactly what was requested, while REST often includes unnecessary fields. 
- **Implementation Complexity**: REST can be simpler to implement for basic APIs, while GraphQL may require more initial planning but offers advantages for more complex APIs.

In summary, GraphQL is often preferred for modern and complex applications that require flexible and efficient data interaction, while REST remains a solid choice for simpler APIs with a less dynamic data model.
Also, GraphQL is not a replacement for REST, but rather a complementary technology that can be used alongside REST APIs to provide more flexibility and efficiency in data retrieval.
So, the choice between GraphQL and REST depends on the specific requirements of the application and the complexity of the data model.

#### A simple comparison between GraphQL requests and REST requests
##### GET Request example
Here is a comparison between a GraphQL request and a REST request to obtain the same information:
- **GraphQL request**:
    ```graphql
    query GetProduct {
        getProduct(productId: 123) {
            productId
            name
            weight
        }
    }
    ```
  The response will be:
    ```json
    {
        "data": {
            "getProduct": {
                "productId": 123,
                "name": "Product Name",
                "weight": 100
            }
        }
    }
    ```
  
Additionally, GraphQL can help to avoid chattiness by allowing the client to request multiple resources in a single query. For example, the client could request information about multiple products in a single query, reducing the number of requests needed to obtain all the required data (we've talked about this in the first part of this markdown: *Aliases* and *Fragments*).

- **REST request**:
    ```bash
    GET /product/123
    ```
    The response will be:

    ```json
        {
            "productId": 123,
            "name": "Product Name",
            "weight": 100
        }
    ```

In the REST request, the client sends a GET request to the `/products/123` endpoint to retrieve information about the product with ID 123. The server responds with a JSON object containing all the product details.
Differently from the REST request, the GraphQL request allows the client to specify exactly what information it wants to retrieve about the product, including only the fields it needs, such as the product ID, name, weight, and service address.
For example, we could be request just a single field:

```graphql
    query GetProduct {
        getProduct(productId: 123) {
            name
        }
    }
```
  The response will be:

```json
    {
        "data": {
            "getProduct": {
                "name": "Product Name"
            }
        }
    }
```

To be noted that in the REST request we have to personalize the url in order to obtain the desired information, while in the GraphQL request we can obtain the same information just by changing the query.

##### POST Request example
Here is a comparison between a GraphQL request and a REST request to create a new product:
- **GraphQL request**:
    ```graphql
    mutation CreateProduct {
        createProduct(input: { productId: 123, name: "Product Name", weight: 100 }) {
            productId
            name
            weight
        }
    }
    ```
  The response will be:
    ```json
    {
        "data": {
            "createProduct": {
                "productId": 123,
                "name": "Product Name",
                "weight": 100
            }
        }
    }
    ```
- **REST request**:
    ```bash
    POST /product
    {
        "productId": 123,
        "name": "Product Name",
        "weight": 100
    }
    ```
    The response will be:

    ```json
    {
        "productId": 123,
        "name": "Product Name",
        "weight": 100
    }
    ```
In the REST request, the client sends a POST request to the `/product` endpoint with the product details in the request body to create a new product. The server responds with a JSON object containing the details of the newly created product.
In the GraphQL request, the client sends a mutation operation to create a new product with the specified details. The server responds with a JSON object containing the details of the newly created product (all of them or just a few).

##### DELETE Request example
Here is a comparison between a GraphQL request and a REST request to delete a product:
- **GraphQL request**:
    ```graphql
    mutation DeleteProduct {
        deleteProduct(productId: 123)
    }
    ```
  The response will be:
    ```json
    {
        "data": {
            "deleteProduct": true
        }
    }
    ```
- **REST request**:
    ```bash
    DELETE /products/123
    ```
    The response will be:

    ```json
    {
        "success": true
    }
    ```
In the REST request, the client sends a DELETE request to the `/products/123` endpoint to delete the product with ID 123. The server responds with a JSON object indicating the success of the deletion operation.
In the GraphQL request, the client sends a mutation operation to delete the product with the specified ID. The server responds with a JSON object indicating the success of the deletion operation.

Note that with graphql we always use the same endpoint using the same HTTP method (POST), unlike the REST API where a different method is used for each type of request: GET for query, POST for inserting, and DELETE to deleting.

##### "Chattiness reduction" 
In the case of a REST API, using our example, if we want to obtain information about a product and its reviews, we would have to make more than one request to the server: one to obtain the product information, one to obtain the reviews and another one to obtain the recommendations. 
This can lead to chattiness, where multiple requests are needed to obtain all the required data.
Indeed, to get all the information about a product, you might have to make several calls:
- One call to get the product details. 
- Another call to get the recommendations. 
- Another call to get the reviews. 

This involves multiple round-trips between the client and the server.
With GraphQL, you can make a single request to get all this information at once. Here's an example of a GraphQL query that requests all the fields defined in the ProductAggregate type:

```graphql
{
    productAggregate(productId: 1) {
        productId
        name
        weight
        reviews {
            reviewId
            reviewText
        }
    }
}
```
This query will return a JSON object containing all the requested fields for the product, recommendations and reviews in a single response. This reduces the number of round-trips between the client and the server, improving performance and reducing chattiness.
Also, *recommendations* and *reviews* are two different objects, so the query will return a JSON object with two different arrays, one for each object. We can also notice this in the appropriate schema.graphqls:
```graphql
type ProductAggregate {
    productId: Int!
    name: String!
    weight: Int!
    reviews: [Review]
}
```


## Code section: Spring boot with GraphQL

### Implementation

Spring Boot and GraphQL can be combined to create web services that use GraphQL as a query layer to access data. Spring Boot offers a number of libraries and tools that simplify the integration of GraphQL in Java applications.

In order to implement correctly, there are a few steps to follow:

1. Inclusion of the correct dependencies in Maven or Gradle.
2. The definition of the schema with the various types of objects, inputs, queries and mutations.
3. Enabling the graphql endpoint in the file *application.yml*.
4. The creation of the controller interface and its implementation

#### 1. Inclusion of addictions

**Maven**

Using Apache Maven, the file *pom.xml* must contain:
```xml
<dependencies>
    <!-- Other dependencies -->

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-graphql</artifactId>
    </dependency>
</dependencies>

```

#### 2. Schema definition.graphqls

In order to define the objects, we must create the file, with the extension `*.graphqls`, which must be placed in the path `src/main/java/resources/graphql`.

There shall be a single schema file within the folder; it is possible to split them in the case of defining different schemas for different applications that draw from the same *resources* folder by creating subfolders and defining this in the respective *application.yml (see later).*

#### 3. Enabling graphQL endpoints

It is then necessary to enable the endpoint by entering the correct entries in the file *application.yml*:

```yaml
spring:
  graphql:
    schema:
      locations: classpath*:graphql/**/
```

If desired, it is possible to declare a different classpath in order to place *.graphls files in different subdirectories for reasons of convenience. In that case:

```yaml
spring:
  graphql:
    schema:
      locations: classpath*:graphql/sub-folder/**/
```

In this case, the application will search for the schema file within the subfolder *product-service.*

By default, the endpoint will be reachable at the url `$ADDRESS:$PORT/graphql`. You can change this by adding in *application.yml*:

```yaml
spring:
       graphql:
               path: /api/projects/graphql
```

#### 4. Java interface definition and implementation

- **Controller Interface**:

```java
public interface ProductController {

    @QueryMapping
    public Product getProduct(@Argument int productId);

    @MutationMapping
    public Product createProduct(@Argument Product input);

    @MutationMapping
    public Boolean deleteProduct(@Argument int productId);
}
```

Worthy of note are the annotations *@QueryMapping*, which indicates that the query of the same name defined in the schema will refer to this method, *@MutationMapping* with regard to modification queries and finally *@Argument* which refers to the attributes passed by the query or mutation queries.

- **Interface  implementation**:

```java
@Controller
public class ProductControllerImpl implements ProductController { 
    /// CODE HERE
    @Override
    public Product getProduct(@Argument int productId) {
        // CODE HERE
        return response;
    }
}
```

### Requests
#### GraphiQL (for testing purpose)

Spring boot's GraphQL library provides a browser-accessible default endpoint for composing and testing queries and mutations. It can be enabled via *application.yml* file by adding this:
```yaml
spring:
    graphiql:
        path: /graphiql
        enabled: true
```
It can be changed via *path:* field.

Here is an example:

<img width="1499" alt="GraphiQL example" src="https://github.com/AlfaSierra92/GraphQL06/assets/4050967/536f7a24-09b5-46ba-9949-cff31c12803b">
It is advisable to use this mode only during development, as enabling this endpoint could potentially create security issues.

#### Postman

With Postman, this is very simple, as it is already set up for GraphQL queries. By putting in the correct url, it will automatically retrieve the schema of objects, queries and mutations and allow the various queries to be composed intuitively.

<img width="1087" alt="Postman example" src="https://github.com/AlfaSierra92/GraphQL06/assets/4050967/5f1a7185-b8e8-4723-916a-050d7bbe5a18">

#### cURL

With it, the composition of requests is more laborious as one has to compose requests by hand, and it is required to know the schema in advance (if not we have to use an introspection query before). Here are some examples of requests:

- **Query**:

    ```bash
    curl --location '127.0.0.1:7001/graphql' \
    --header 'Content-Type: application/json' \
    --data '{"query":"query GetProduct { getProduct(productId: 92) { productId name weight } }"}'
    ```

    and the response will be:
    <img width="860" alt="image" src="https://github.com/AlfaSierra92/GraphQL06/assets/4050967/dbc6cf7c-dac7-4925-bf78-d4c4d16ef27c">


- **Mutation (*Input query*)**:

    ```bash
    curl --location '127.0.0.1:7001/graphql' \
    --header 'Content-Type: application/json' \
    --data '{"query":"mutation { createProduct(input: { productId: 92, name: \"1111\", weight: 111 }) { productId name weight } }"}'
    ```
    and the response will be:
    <img width="1127" alt="image" src="https://github.com/AlfaSierra92/GraphQL06/assets/4050967/9788c2d1-98a8-4cb4-beb6-d058c7e4bc78">


The request itself is nothing more than a JSON object with the query or mutation to be executed; the keyword *query* at the beginning of the object is mandatory, regardless of the type of request.

#### By code

Since the response of a GraphQL query is nothing more than a json-formatted body, you can parse it as you have always done in the case of REST.
Here is an example of Java code to do query and json response parsing:
```java
@Override
public List<Review> getReviews(int productId) {
    try {
        String query = "query { getReviews(productId: " + productId + ") { reviewId productId author subject content } }";
        ResponseEntity<String> response = sendGraphQLRequest(reviewServiceUrl, query, new ParameterizedTypeReference<String>() {
        });

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());

        // Extracting values from JSON
        JsonNode reviewsNode = rootNode.path("data").path("getReviews");
        List<Review> reviews = new ArrayList<>();
        for (JsonNode reviewNode : reviewsNode) {
            int reviewId = reviewNode.path("reviewId").asInt();
            String author = reviewNode.path("author").asText();
            String subject = reviewNode.path("subject").asText();
            String content = reviewNode.path("content").asText();
            reviews.add(new Review(productId, reviewId, author, subject, content));
        }

        // Printing the extracted reviews
        LOG.debug("Received Reviews: {}", reviews);

        return reviews;
    } catch (HttpClientErrorException ex) {
        throw handleHttpClientException(ex);
    } catch (JsonMappingException e) {
        throw new RuntimeException(e);
    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    }
}

// Creating a http request, with the GraphQL query into the body
private <T> ResponseEntity<T> sendGraphQLRequest(String url, String query, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        String requestBody = "{\"query\":\"" + query + "\"}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
    }
```
For the complete code, see it in the repository (*GraphQL06* folder).

## How-to: try out the GraphQL endpoint
In this repository, there are four services that can be used to test the GraphQL endpoint: 
- **product-service**: for product retrieval, insertion and deletion with or without recommendations (if available);
- **review-service**: for review retrieval, insertion and deletion;

All of them work in a similar way, but with different objects, queries and mutations, obviously.
The GraphQL API specification can be retrieved by using a simple GraphQL client like Postman, cURL (by the *introspection* query) or by using the GraphiQL interface (if you want to do just a walkthrough with APIs).

To try out, you can clone the repository and build and run the project inside IntelliJ IDEA by launching the command `mvn run` in the project root folder.

You can also containerize with Docker and run the microservices by launching, consecutively, the commands (always in the project root folder):
1. `mvn clean` (useful in the case of a new build)
2. `mvn package` (in order to create the jar files)
3. `docker compose up --build -d` (to build and run the containers).

Then, you can make requests to the endpoint as described above in the **Request** section.

For Postman, cURL or similar, just remember to use the url 
1. [http://localhost:7001/graphql](http://localhost:7001/graphql) to access the *product-service* endpoint;
3. [http://localhost:7003/graphql](http://localhost:7003/graphql)  to access the *review-service* endpoint.

for the requests. 
If you want to use the GraphiQL interface, instead of external applications, you can access it by using the same urls but changing the endpoint name to *graphiql* (e.g., [http://localhost:7001/graphiql](http://localhost:7001/graphiql)).

To stop the services, just launch the command `docker compose down` in the project root folder.

## Resources and further details

- [GraphQL](https://graphql.org/)
- [Spring for GraphQL](https://spring.io/projects/spring-graphql)
- [Building a GraphQL service](https://spring.io/guides/gs/graphql-server)
- [Getting Started with GraphQL and Spring Boot](https://www.baeldung.com/spring-graphql)
- [GraphQL vs REST](https://aws.amazon.com/it/compare/the-difference-between-graphql-and-rest/)

# Synchronous communications

## Definitions and Know Issues

**Serialization** is the process of converting data structures or objects into a format that can be easily transmitted or stored. This format is often a byte stream or text, which can be later deserialized (converted back) into the original data structure or object.

**Spatial coupling** refers to the degree of dependency between different components or services in a system at a given point in time. A high degree of spatial coupling means that components are tightly connected, requiring direct knowledge of each other’s existence, interfaces, or locations. This can lead to reduced flexibility and increased maintenance complexity.

**Temporal coupling** occurs when components or services must be available and responsive at the same time to function correctly. This often happens in synchronous communication patterns, where one component must wait for another to process a request before proceeding. In cases where a chain of multiple services need to communicate, the **cumulative latency** can significantly degrade performance. **[unsolvable!]**

**API coupling** refers to the degree of dependency between a client and an API. A highly coupled API means that changes in the API can easily break the client, while a loosely coupled API provides more flexibility and resilience to changes.

**Over-fetching** occurs when an API returns more data than the client actually needs, leading to wasted bandwidth and increased processing time. This typically happens in REST APIs with fixed response structures, where a client cannot specify exactly which fields it requires.

**Under-fetching (aka chattiness)** occurs when a client requests data from an API but does not receive all the necessary information in a single response. As a result, the client must make additional requests to retrieve the missing data, leading to inefficiencies and increased latency.

**Thread pool exhaustion** clients waiting for a response from the server consumes system resources (threads, memory), which can be problematic in high-concurrency environments. **[unsolvable!]**

## REST (Representational State Transfer)

**Key Features:**

- **HTTP-based**: REST is built on top of HTTP/1.1, leveraging standard HTTP methods (GET, POST, PUT, DELETE) to interact with resources identified by URLs.
- **Stateless**: REST requests contain all the necessary information to process the request. No client state is stored on the server.
- **Text data**: REST uses **JSON** or **XML** for data serialization (readable but large and slow to serialize/de-serialize).

**Limitations of REST**:

- **Serialization**: text-based data results in larger payloads, slower parsing, degraded performance
- **Temporal coupling**
- **API coupling**
- **Over-fetching**
- **Under-fetching (chattiness)**
- **Thread pool exhaustion**


## gRPC (Google Remote Procedure Call)

![](images/rest-vs-grpc.webp)

**Key Features:**

- **Efficient Binary Protocol (Protobuf)**: gRPC uses **Protocol Buffers (Protobuf)** for data serialization (binary format).
- **HTTP/2-based Communication**: gRPC leverages [HTTP/2](https://www.cloudflare.com/learning/performance/http2-vs-http1.1/), enabling features like multiplexing, where multiple requests can be handled concurrently over a single connection, reducing latency and improving throughput.
- **Strongly Typed Contracts**: gRPC enforces strict typing through Protobuf definitions, which ensures that APIs are more robust and easier to maintain with backward/forward compatibility.

**How gRPC Solves REST Limitations**:

- **Serialization**: gRPC uses Protocol Buffers (Protobuf), a compact binary format, for data serialization instead of JSON or XML (commonly used in REST APIs). Binary formats are much more efficient in terms of size and speed because they take up less space and are faster to serialize and deserialize.
- **Under-fetching (chattiness)**: gRPC minimizes multiple round trips by leveraging HTTP/2 multiplexing, enabling efficient communication. This significantly reduces the network overhead typically found in REST-based systems.
- **API Coupling**: gRPC defines strict API contracts, ensuring strong typing and reducing the risk of breaking changes. With built-in support for both backward and forward compatibility, API evolution is smoother compared to REST, where changes in JSON payloads or specifications can more easily introduce incompatibilities.

**Limitations of gRPC**:

- **Complexity**: Protobuf introduces more complexity in terms of schema definitions and code generation.
- **Browser Support**: While gRPC is excellent for backend and service-to-service communication, it is not well-supported in browser environments, limiting its use for frontend applications.
- **Strict Typing**: While strict typing ensures robustness, it also introduces rigidity, as changes to the API require careful management of Protobuf contracts.

## GraphQL (Meta)

![](images/rest-vs-graphql.webp)

**Key Features:**

- **Single Endpoint**: Unlike REST, where different resources are exposed via multiple endpoints, GraphQL operates through a single endpoint for all queries, simplifying the API structure.
- **Flexible Queries**: GraphQL allows clients to request exactly the data they need (one or multiple entities), avoiding the over-fetching, under-fetching, and chattiness problems commonly encountered in REST APIs.
- **Schema-based**: GraphQL operates on a strong schema, which defines the types and structure of the API. This schema allows for introspection, enabling automatic API documentation and tooling.

**How GraphQL Solves REST Limitations**:

- **Over-fetching and under-fetching (chattiness)**: GraphQL allows clients to request only the fields they need, addressing REST’s issue of returning unnecessary data (over-fetching) or making multiple requests to retrieve the needed data (under-fetching).
- **API Evolution**: Unlike REST, which often requires versioning to handle changes, GraphQL’s flexible schema allows for non-breaking changes, such as adding new fields without impacting existing clients.

**Limitations of GraphQL**:

- **Complexity in Query Optimization**: While GraphQL gives clients flexibility, it also puts more responsibility on the server to optimize the queries. If not properly managed, complex or deeply nested queries can lead to performance bottlenecks on the server side.
- **Caching Challenges**: Caching in GraphQL is more complex than REST, as queries can be dynamic and granular. REST APIs can leverage HTTP-based caching more easily (based on endpoints), while GraphQL requires more sophisticated caching strategies.
- **Overhead in Complex Schemas**: For simple data models, GraphQL may introduce unnecessary complexity due to its schema-driven approach. The overhead of managing schemas and resolvers might not justify the benefits in all use cases.

## Summary

| **Feature**                        | **REST**                                      | **gRPC**                                     | **GraphQL**                                 |
|-------------------------------------|-----------------------------------------------|----------------------------------------------|---------------------------------------------|
| **Data Format**                     | JSON, XML                                     | Binary (Protocol Buffers - Protobuf)         | JSON                                        |
| **Transport Protocol**              | HTTP/1.1                                      | HTTP/2                                       | HTTP/1.1                                    |
| **Communication Style**             | Synchronous, request-response                 | Synchronous (with async support), streaming  | Synchronous, but supports subscriptions     |
| **Request-Response Type**           | Fixed response structures                     | RPC (Remote Procedure Call), predefined methods | Customizable queries by client             |
| **API Design**                      | Resource-oriented (uses URLs)                 | Function/method-oriented (RPC-style)         | Query-based (single endpoint)               |
| **Over-fetching/Under-fetching**    | Common, rigid response structures             | Reduced, due to HTTP/2                      | Flexible: clients request specific data     |
| **Performance**                     | Moderate (JSON/XML parsing)                   | High (binary Protobuf, low latency)          | Variable, based on query complexity         |
| **Browser Support**                 | Native support                                | Limited (requires gRPC-Web for browsers)     | Full browser support                        |
| **Ease of Use**                     | Widely adopted, simple                        | Complex (requires Protobuf schema management) | More complex query structure for clients    |
| **Use Case**                        | General web APIs, mobile applications         | Low-latency, high-performance internal service-to-service communication | Client-facing APIs with flexible data needs |

While **REST**, **gRPC**, and **GraphQL** offer distinct advantages in specific contexts, they all have limitations due to their synchronous nature. gRPC and GraphQL address many of REST’s inefficiencies, such as performance bottlenecks, over-fetching/under-fetching, and API evolution issues. However, the inherent challenges of synchronous communication often necessitate a shift toward **asynchronous communications**.
# Communication styles

In a monolithic application running on a single process, components invoke one another using language-level method or function calls. These can be strongly coupled if you're creating objects with code (for example, new ClassName()), or can be invoked in a decoupled way if you're using Dependency Injection by referencing abstractions rather than concrete object instances. Either way, the objects are running within the same process. 

**The biggest challenge when changing from a monolithic application to a microservices-based application lies in changing the communication mechanism. A direct conversion from in-process method calls into RPC calls to services will cause a chatty and not efficient communication that won't perform well in distributed environments.**

The challenges of designing distributed system properly are well enough known that there's even a canon known as the [Fallacies of distributed computing](https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing) that lists assumptions that developers often make when moving from monolithic to distributed designs.

The microservice community promotes the philosophy of **smart endpoints and dumb pipes**. The principle means that intelligence, such as business logic and processing, is handled at the service level (smart endpoints), while communication between services (dumb pipes) is kept simple, typically using lightweight protocols like HTTP, gRPC, or messaging systems like Kafka or RabbitMQ.

This approach avoids complex middleware (like ESBs in traditional SOA) by making the pipes solely responsible for message transport, not transformation or orchestration. The result is loose coupling, better scalability, resilience, and decentralized governance, as services can evolve independently without relying on a complex communication layer.

## Message formats

In the realm of distributed systems and microservices, communication protocols play a crucial role in determining the efficiency, performance, and flexibility of data exchange. Text-based REST protocols, which primarily use formats such as JSON and XML, are widely adopted for their simplicity and ease of use. In contrast, binary protocols like Protocol Buffers (Protobuf) and Apache Avro are gaining traction for their performance advantages and compact data representation. 

### Data Representation and Size

**Text-Based REST Protocols** commonly utilize JSON or XML for data serialization. While JSON is lightweight compared to XML, both formats are text-based, resulting in larger payload sizes compared to binary formats. Example: A JSON representation of a simple user object can be verbose, making it less efficient for network transmission:

  ```json
  {
      "name": "John Doe",
      "age": 30,
      "email": "john.doe@example.com"
  }
  ```

**Binary Protocols (Protobuf and Avro)** serialize data into a compact binary format, significantly reducing the size of transmitted messages. This efficiency is particularly beneficial for applications with limited bandwidth or high-volume data transfer. Example: The same user object in Protobuf can be serialized into a much smaller binary representation, which is not human-readable but offers substantial size savings.

### Performance and Speed

**Text-Based REST Protocols** involve parsing text, which is generally slower compared to binary formats. The overhead of handling text data can introduce latency, especially in high-throughput applications. Additionally, the increased size of JSON or XML payloads can lead to longer transmission times over the network, affecting overall performance.

**Binary Protocols (Protobuf and Avro)** are designed for speed. They employ efficient encoding and decoding algorithms that minimize processing time. As a result, both protocols typically provide faster serialization and deserialization compared to their text-based counterparts. The compact nature of binary formats also means that less data needs to be transmitted, further enhancing performance by reducing network latency.

### Schema Management and Compatibility

**Text-Based REST Protocols**: often lack a formal schema, which can lead to challenges in data integrity and versioning. While documentation tools like OpenAPI can provide some level of schema definition, they do not enforce compliance at the serialization level. This flexibility can introduce risks, such as unexpected data structures or changes that break existing clients, complicating the management of API versions over time.

**Binary Protocols (Protobuf and Avro)**: require a predefined schema, which helps enforce strong typing and data integrity. This schema is used to validate data during serialization and deserialization, reducing the likelihood of errors.

### Use Cases and Applications

**Text-Based REST Protocols** are widely used for web services, CRUD operations, and applications where human interaction is essential. Their simplicity and ease of integration make them suitable for scenarios requiring interoperability among diverse clients and platforms.

**Binary Protocols (Protobuf and Avro)** are well-suited for high-performance applications, microservices architectures, and big data processing environments. Their efficiency in data transmission and ability to handle large volumes of data make them ideal for scenarios such as IoT, real-time analytics, and data streaming.

### Summary

| Feature                        | Text-Based REST Protocols            | Binary Protocols (Protobuf/Avro)   |
|--------------------------------|--------------------------------------|-------------------------------------|
| **Data Representation**        | JSON/XML (text-based, larger size)  | Binary (compact, smaller size)      |
| **Performance and Speed**      | Slower serialization/deserialization | Faster due to efficient encoding     |
| **Schema Management**          | Flexible but lacks enforced schema   | Strongly typed, predefined schema    |
| **Compatibility**              | Potential issues with data integrity | Backward and forward compatibility    |
| **Interoperability**           | Human-readable and easy to debug     | Not human-readable, but tools available for representation |
| **Use Cases**                  | Web services, public APIs, CRUD ops  | High-performance applications, microservices, big data processing |
| **Client Integration**         | Simple integration with various clients | Requires handling of binary data for serialization/deserialization |
| **Network Efficiency**         | Higher latency due to larger payloads | Lower latency due to smaller payloads  |

Here’s an elaboration on the topic of client-service interaction styles, expanding on the dimensions and types of interactions you provided:


## Interaction Styles

Client-service interactions form the backbone of distributed systems, enabling communication between different components and facilitating various functionalities. These interactions can be categorized along two key dimensions: 
* **the relationship between the client and the service (one-to-one vs. one-to-many)**  
* **the nature of the response timing (synchronous vs. asynchronous)**

### Relationship between the client and the service

- **One-to-One**: In this interaction style, each client request is directed to a specific service, and the response comes from that same service. This approach is straightforward and simplifies the relationship between clients and services but can lead to tight coupling. **Example**: A user requests their account information from a banking service; the request is processed by the account service, which retrieves and sends the relevant data back.

- **One-to-Many**: In this model, a single client request can invoke multiple services. This is useful in scenarios where multiple services need to collaborate to fulfill a request. This approach allows for greater flexibility and scalability, as different services can be updated or replaced independently. **Example**: A request for a travel itinerary might be sent to a service that interacts with multiple services: a flight booking service, a hotel booking service, and a car rental service.

### Response timing

- **Synchronous**: In synchronous interactions, the client sends a request and waits for a response, often blocking further actions until the response is received. This model is intuitive but can lead to delays if the service takes time to process the request. **Example**: A client submits a form on a website and waits for the server to confirm the submission before continuing. This tight coupling can lead to performance bottlenecks if the service is slow to respond.

- **Asynchronous**: Asynchronous interactions allow the client to continue processing other tasks without waiting for a response. The client may receive the response later or be notified when the response is ready. This non-blocking approach improves responsiveness and user experience but can complicate error handling and state management. **Example**: A user uploads a large file to a cloud storage service. Instead of waiting for the upload to complete, the user can continue working, and the service notifies them when the upload is finished.

### Types of Interactions

**Request/Response (One-to-One, Synchronous)**: In this classic interaction style, a client sends a request to a service and waits for a direct response. The expectation is for a timely reply, which can lead to tight coupling between the client and the service. This pattern is common in traditional web applications and APIs where immediate feedback is required. **Example**: An API call to retrieve user details where the client waits for the service to return the data before proceeding.

**Asynchronous Request/Response (One-to-One, Asynchronous)**: In this interaction, a client sends a request but does not wait for an immediate response. Instead, it can continue processing other tasks. The service will eventually respond, but the client does not block while waiting. This pattern helps improve user experience by preventing UI freezes or delays. **Example**: A mobile app that sends a message to a server and receives a notification when the message is processed, allowing the user to continue using the app without interruption.

**Publish/Subscribe (One-to-Many, Asynchronous)**: This interaction model allows a client to publish messages to a topic or channel that can be consumed by multiple services. Interested services subscribe to these messages, which can be processed independently of the publisher. This decouples clients from services, enabling a more flexible architecture. **Example**: A news application publishes articles to a channel that multiple subscribers (e.g., different news feeds or alerts) can consume and display to users without direct knowledge of each other.

**Publish/Async Responses (One-to-Many, Asynchronous)**: **Description**: In this variation, a client publishes a request message to multiple services and waits for responses for a specified amount of time. This allows the client to receive inputs from various services while still not blocking its operation, enhancing its efficiency. **Example**: An e-commerce platform publishes a request for inventory availability from multiple suppliers and waits a short duration for responses, enabling it to aggregate and display the best options to users without unnecessary delays.

| Interaction Type                      | Client-Service Relationship | Response Timing   | Description                                                                                                                                                   | Example                                                                                                    |
|---------------------------------------|-----------------------------|--------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| **Request/Response**                  | One-to-One                  | Synchronous         | A client sends a request to a service and waits for an immediate response. This style often leads to tight coupling between the client and the service.     | A user requests their account information from a banking API and waits for the response.                  |
| **Asynchronous Request/Response**     | One-to-One                  | Asynchronous        | A client sends a request but does not wait for an immediate response, allowing other processing to continue. The service responds at a later time.         | A mobile app sends a message to a server and continues working while waiting for a notification of completion. |
| **Publish/Subscribe**                 | One-to-Many                 | Asynchronous        | A client publishes a message to a topic, which can be consumed by multiple interested services, promoting loose coupling.                                     | A news application publishes articles to a channel that multiple subscribers can consume.                 |
| **Publish/Async Responses**           | One-to-Many                 | Asynchronous        | A client publishes a request message and waits for responses from multiple services for a limited time, allowing for aggregated feedback.                     | An e-commerce platform requests inventory availability from multiple suppliers and collects responses within a specified time. |



## Synchronous communications

When a client uses request/response communication, it sends a request to a service, then the service processes the request and sends back a response. Request/response communication is especially well suited for querying data for a real-time UI (a live user interface) from client apps. 

Therefore, in a microservice architecture you’ll probably use this communication mechanism for most queries. When a client uses request/response communication, it assumes that the response will arrive in a short time, typically less than a second. 

A popular architectural style for request/response communication is REST. This approach is based on the HTTP protocol, embracing HTTP verbs like GET, POST, and PUT. REST is the most commonly used architectural communication approach when creating services. 

![](images/sync-comms-availability.avif)


## Issues of synchronous communications

### Chattiness
A common problem when designing a REST API is how to enable the client to retrieve multiple related objects in a single request. As shown above, imagine that a REST client wanted to retrieve an Order and the Order's Consumer. A pure REST API would require the client to make at least two requests, one for the Order and another for its Consumer. A more complex scenario would require even more round-trips and suffer from excessive latency.

One solution to this problem is for an API to allow the client to retrieve related resources when it gets a resource:

```
GET /orders/order-id-1345?expand=consumer
```

The query parameter specifies the related resources to return with the Order. This approach works well in many scenarios, but it’s often insufficient for more complex scenarios. It’s also potentially time-consuming to implement. **This has led to the increasing popularity of alternative (still synchronous) technologies such as [GraphQL](http://graphql.org)**.

### Availability
REST is an extremely popular IPC mechanism. You may be tempted to use it for inter-service communication. The problem with REST, though, is that it’s a synchronous protocol: an HTTP client must wait for the service to send a response. **Whenever services communicate using a synchronous protocol, the availability of the application is reduced**.

In the example above, the Order Service has a REST API for creating an Order. It invokes the Consumer Service and the Restaurant Service to validate the Order. Both of those services also have REST APIs.

Because these services use HTTP, they must all be simultaneously available to process the CreateOrder request. Mathematically speaking, **the availability of an operation is the product of the availability of the services that are invoked by that operation**. If the Order Service and the two services that it invokes are 99.5% available, the overall availability is 99.5 x 3 = 98.5%, which is significantly less. Each additional service that participates in handling a request further reduces availability.

This problem isn’t specific to REST-based communication. **Availability is reduced whenever a service can only respond to its client after receiving a response from another service**. If you want to maximize availability, you must minimize the amount of synchronous communication.

### Temporal coupling
Temporal coupling happens when a service - the caller - expects an instantaneous response from another - the callee - before it can resume processing. Since any delay in the response time of the callee would adversely affect the response time of the caller, the callee has to be always up and responsive. This situation usually happens when services use synchronous communication.

**Latency adds up**: The longer it takes for Consumer and Restaurant to prepare the data, the longer Order has to wait before responding to its clients. 

**Cascading failures is also another possibility**: If Consumer fails to respond, Orders will eventually time out and fail responding as well. If Consumer continues to be slow or unresponsive for a while, Orders might end up with a lot of open connections to Consumer, and eventually run out of memory, and fail!

### Code Flexibility
With the synchronous model, if we had another service that was required to fill an Order, we’d need to manually add another call from the Order service to the other service. This means a code change and redeployment.

**If we use the synchronous, request-response model, we start to see a web-like pattern of dependency between our services. The centers of these webs become our major points of failure within our application.**

## Asynchronous communications
When using messaging, services communicate by asynchronously exchanging messages:
* a messaging-based application typically uses a message broker, which acts as an intermediary between the services 
* a service client makes a request to a service by sending it a message. Because the communication is asynchronous, the client doesn't block waiting for a reply. Instead, the client is written assuming that the reply won’t be received immediately.

### Messages
A message consists of a header and a message body. The header is a collection of name-value pairs, metadata that describes the data being sent. The message body is the data being sent, in either text or binary format. There are several different kinds of messages:

* **Command** A message that’s the equivalent of an RPC request. It specifies the operation to invoke and its parameters.
* **Document** A generic message that contains only data. The receiver decides how to interpret it. The reply to a command is an example of a document message.
* **Event** A message indicating that something notable has occurred in the sender. An event is often a domain event, which represents a state change of a domain object such as an Order, or a Customer.

![](images/microservices-async-apis.avif)

### Message channels
Messages are exchanged over channels. A message channel is an abstraction of the messaging infrastructure. There are two kinds of channels:
* A **point-to-point channel** delivers a message to exactly one of the consumers that is reading from the channel. Services use point-to-point channels for the one-to-one interaction styles. For example, a command message is often sent over a point-to-point channel.
* A **publish-subscribe channel** delivers each message to all the attached consumers. Services use publish-subscribe channels for the one-to-many interaction styles. For example, an event message is usually sent over a publish-subscribe channel.
  
### Implementing the interaction styles using messaging

**One-way notifications**: Implementing one-way notifications is straightforward using asynchronous messaging. The client sends a message, typically a command message, to a point-to-point channel owned by the service. The service subscribes to the channel and processes the message. it doesn't send back a reply.

**Asynchronous request/response**: **Messaging is inherently asynchronous, so only provides asynchronous request/response**. With asynchronous request/response there is no expectation of an immediate reply: the client must tell the service where to send a reply message and must match reply messages to requests.

![](images/microservices-async-communications.avif)

**Publish/subscribe**: Messaging has built-in support for the publish/subscribe style of interaction. A client publishes a message to a publish-subscribe channel that is read by multiple consumers. 

![](images/microservices-publish-subscribe.avif)

The service that publishes the domain events owns a publish-subscribe channel, whose name is derived from the domain class. For example, the Order Service publishes Order events to an Order channel, and the Delivery Service publishes Delivery events to a Delivery channel. A service that’s interested in a particular domain object’s events only has to subscribe to the appropriate channel.

**Publish/async responses**: The publish/async responses interaction style is a higher-level style of interaction that’s implemented by combining elements of publish/subscribe and request/response. A client publishes a message that specifies a reply channel header to a publish-subscribe channel. A consumer writes a reply message containing a correlation id to the reply channel. The client gathers the responses by using the correlation id to match the reply messages with the request.

## Asynchronous messaging systems

![](images/brokerless-architecture.avif)

### Broker-based messaging
A message broker is an intermediary through which all messages flow. A sender writes the message to the message broker, and the message broker delivers it to the receiver.

There are many message brokers to chose from:
* RabbitMQ (https://www.rabbitmq.com)
* Apache Kafka (http://kafka.apache.org)
* ActiveMQ (http://activemq.apache.org)
* AWS Kinesis (https://aws.amazon.com/kinesis/)
* AWS SQS (https://aws.amazon.com/sqs/)

**Advantages of Broker Messaging Queues**
* **Loose coupling**: By using a message broker, services can communicate with each other without having direct dependencies on each other. This allows for increased flexibility and easier maintenance, as services can be added, removed, or modified without affecting other services.
* **Scalability**: A message broker can handle message distribution and load balancing, allowing for horizontal scaling of services. This means that as the number of services or the workload increases, the message broker can distribute the load across multiple instances of the service, ensuring efficient resource utilization.
* **Reliability**: A message broker can provide reliable message delivery by implementing features such as message persistence and guaranteed delivery. This ensures that messages are not lost in case of failures or network issues.
* **Centralized control**: A message broker acts as a central hub, allowing for easier management, monitoring, and administration of messages and communication between services.
* **Message transformation and routing**: Brokers often provide powerful routing capabilities, allowing messages to be transformed and directed to the appropriate destinations based on various criteria.

**Disadvantages of Broker Messaging Queues**
* **Single point of failure**: Since all messages pass through a central message broker if the broker fails, it can disrupt the entire messaging system.
* **Increased complexity**: Setting up and configuring a message broker can be more complex compared to brokerless messaging approaches.
* **Potential performance impact**: The additional overhead of routing messages through a broker can introduce some latency and potentially impact performance.

### Brokerless messaging
Brokerless messaging architectures are based on libraries allowing services to communicate with one another directly.

There are many message brokers to chose from:
* ZeroMQ (https://zeromq.org/)
* NanoMsg (https://nanomsg.org/)

**Advantages of Brokerless Messaging Queues**
* **Simplicity**: Brokerless messaging eliminates the need for a central message broker, reducing the complexity of the system.
* **Improved performance**: Without the need to route messages through a broker, brokerless messaging can offer lower latency and higher throughput, especially for local communication between services.
* **No single point of failure**: They eliminate the possibility of the message broker being a performance bottleneck or a single point of failure.

**Disadvantages of Brokerless Messaging Queues**
* **Spatial coupling**: Services need to know about each other’s locations and must therefore use one of the discovery mechanisms.
* **Time coupling**: Because both the sender and receiver of a message must be available while the message is being exchanged.
* **Scalability limitations**: Brokerless messaging may not scale as well as broker-based messaging for large-scale deployments, as the responsibility for message distribution and load balancing lies with individual services.
* **No built-in guaranteed delivery**: Brokerless messaging may require additional effort to implement reliable message delivery, as there is no inherent mechanism provided by a central broker.


## Issues of asynchronous communications
Message-based applications make it more difficult to reason through its business logic because its code is no longer processed in a linear fashion with a simple block request-response model.

### Message-handling semantics
Using messages in a microservice-based application requires more than understanding how to publish and consume messages. It requires that we understand how our application will behave based on the order in which messages are consumed and what happens if a message is processed out of order. For example, if we have strict requirements that all orders from a single customer must be processed in the order they are received, we’ll need to set up and structure our message handling differently than if every message can be consumed independently of one another.

*If a message fails, do we retry processing the error or do we let it fail? How do we handle future messages related to that customer if one of the customer’s messages fails?* These are important questions to think through.

### Message choreography
Using messages in microservices often means a mix of synchronous service calls and asynchronous service processing. The asynchronous nature of messages means they might not be received or processed in proximity to when the message is published or consumed. Having things like correlation IDs for tracking a user’s transactions across service invocations is critical to understanding and debugging what’s going on in our application.

Also, debugging message-based applications can involve wading through the logs of several different services, where user transactions can be executed out of order and at different times.

When choosing between synchronous REST communication and asynchronous messaging in a microservice system, it's important to weigh the trade-offs to determine the best approach for your use case. Here are the key trade-offs:

## Comparison summary

### Performance and Latency
- **Synchronous REST (REST, gRPC, GraphQL)**:
    - **Low Latency**: Direct, point-to-point communication allows for immediate request-response cycles.
    - **Blocking**: The caller must wait for the response before continuing, which can lead to higher latency under heavy loads or when one service is slow.
    - **Trade-off**: Faster in simple interactions but can become slower in high-latency scenarios or if there are multiple services involved in a single transaction.

- **Asynchronous Messaging (RabbitMQ, Kafka)**:
    - **Decoupled and Non-blocking**: The sender can send a message and continue processing without waiting for a response, improving performance in distributed systems.
    - **Higher Throughput**: Can handle high volumes of messages more efficiently, allowing services to process them at their own pace.
    - **Trade-off**: Latency is often higher for end-to-end interactions since responses are not immediate and processing can be delayed depending on the message queue length.

### Reliability and Fault Tolerance
- **Synchronous REST**:
    - **Tightly Coupled**: Both services must be available during the interaction. If the target service is down, the request will fail immediately.
    - **Less Resilient**: Requires robust error-handling mechanisms (e.g., retries, circuit breakers) to avoid cascading failures.
    - **Trade-off**: Greater risk of failures due to unavailability, timeouts, or network issues.

- **Asynchronous Messaging**:
    - **Decoupled Communication**: The sender and receiver don’t need to be available at the same time. Messages can be queued and processed when the consumer is available.
    - **Message Durability**: Queues like RabbitMQ and Kafka can store messages persistently, ensuring no messages are lost if services are temporarily unavailable.
    - **Trade-off**: While more resilient, processing delays and handling undelivered or dead-letter messages add complexity.

### Complexity and Simplicity
- **Synchronous REST**:
    - **Simplicity**: Easier to implement and understand, especially for small, direct service-to-service interactions.
    - **Tight Coupling**: Services become tightly coupled, as each service depends on the availability and responsiveness of the others.
    - **Trade-off**: Simpler but less scalable in complex or distributed systems with many microservices interacting with each other.

- **Asynchronous Messaging**:
    - **Higher Complexity**: Requires message brokers (like RabbitMQ or Kafka), message formats, queue management, message retries, dead-letter handling, and distributed transactions.
    - **Loose Coupling**: Services are more independent, allowing better scalability and resilience.
    - **Trade-off**: While more powerful and flexible, the system complexity increases with the need for message orchestration, monitoring, and failure handling.

### Scalability
- **Synchronous REST**:
    - **Limited Scalability**: Scaling synchronous services is more difficult because all components in the communication chain need to scale together to handle load spikes. A bottleneck in one service can affect the entire system.
    - **Vertical Scaling**: Often requires scaling vertically (i.e., adding more resources to individual services), which can be more expensive.
    - **Trade-off**: More challenging to scale horizontally, as each synchronous service must be available and performant for the entire interaction to succeed.

- **Asynchronous Messaging**:
    - **Better Scalability**: Allows services to process messages at their own pace, leading to easier horizontal scaling. Producers and consumers can scale independently.
    - **High Throughput**: Queues can handle high message volumes efficiently, and consumers can be added or removed as needed.
    - **Trade-off**: Easier to scale, but requires careful monitoring and scaling of message brokers to avoid bottlenecks.

### Data Consistency
- **Synchronous REST**:
    - **Easier to Ensure Consistency**: Since the request-response cycle is immediate, you can ensure that data is consistent after each transaction (strong consistency).
    - **Distributed Transactions**: For complex workflows, ensuring consistency across multiple services can be hard without distributed transactions, which adds complexity.
    - **Trade-off**: Better for use cases where data consistency is critical, but may introduce locking and delays in distributed systems.

- **Asynchronous Messaging**:
    - **Eventual Consistency**: In an asynchronous model, there is often a delay before all services are in sync. This can lead to temporary inconsistency, but eventual consistency is typically achieved.
    - **Out-of-Order Processing**: Messages may not always be processed in the order they were sent, which can introduce complexity when dealing with dependencies between operations.
    - **Trade-off**: Works well for scenarios where eventual consistency is acceptable, but requires careful design to handle scenarios where messages arrive out of order or late.

### Monitoring and Debugging
- **Synchronous REST**:
    - **Easier to Trace**: Because the communication follows a direct, request-response model, tracing and debugging interactions is simpler.
    - **Centralized Logging**: Logs are easier to follow, as each service logs its part of the transaction in a clear sequence.
    - **Trade-off**: Easier to monitor and debug, but as the system grows, tracing chains of synchronous calls across multiple services becomes more difficult.

- **Asynchronous Messaging**:
    - **Challenging to Trace**: Messages may pass through multiple queues and be processed at different times, making it harder to trace the complete flow of an interaction.
    - **Distributed Logs**: Log data can be scattered across different services, and monitoring tools are required to trace message flows and identify failures.
    - **Trade-off**: Requires more sophisticated tools (e.g., distributed tracing, monitoring) to track message flow, but provides better observability in large, distributed systems.

### Use Cases
- **Synchronous REST**:
  - **Best for Real-Time Interactions**: When immediate responses are needed (e.g., payment gateways, user authentication, or fetching user profiles).
  - **Small, Lightweight Services**: Suitable for simple, straightforward microservices that are tightly coupled.
  - **Trade-off**: Better for low-latency, real-time requirements but less resilient in complex distributed systems.

- **Asynchronous Messaging**:
  - **Best for Decoupled, High-Throughput Workflows**: Ideal for scenarios where tasks can be performed independently and in the background (e.g., order processing, event-driven architectures, notifications).
  - **Workload Distribution**: Effective when work needs to be distributed across multiple services or scaled horizontally.
  - **Trade-off**: Excellent for scalability and decoupling but not ideal for real-time, low-latency requirements.

## Resources
- Microservices Patterns (Chapter 3)
- Microservices with SpringBoot3 and SpringCloud (Chapter 7)
- [The Many Meanings of Event-Driven Architecture](https://www.youtube.com/watch?v=STKCRSUsyP0)
- https://softwaremill.com/data-serialization-tools-comparison-avro-vs-protobuf/


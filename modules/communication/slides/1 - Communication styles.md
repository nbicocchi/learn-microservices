# Communication styles

In a monolithic application running on a single process, components invoke one another using language-level method calls. These can be: 
* strongly coupled if you're creating objects with code (for example, new ClassName())
* decoupled if you're using Dependency Injection by referencing abstractions rather than concrete object instances. 

Either way, the objects are running within the same process. 

**The biggest challenge when changing from a monolithic application to a microservices-based application lies in changing the communication mechanism.** A direct conversion from in-process method calls into RPC calls to services will cause a chatty and not efficient communication that won't perform well in distributed environments.

![](images/monolithic-vs-distributed.webp)

The challenges of designing distributed system are so notorious that there's even a canon known as the [Fallacies of distributed computing](0%20-%20Fallacies%20of%20distributed%20computing.md) listing **wrong** assumptions that developers often make when moving from monolithic to distributed designs.

## Smart endpoints and dumb pipes
The microservice community promotes the philosophy of **smart endpoints and dumb pipes**. The principle means that intelligence, such as business logic and processing, is handled at the service level (smart endpoints), while communication between services (dumb pipes) is kept simple, typically using lightweight protocols like HTTP, gRPC, or messaging systems like Kafka or RabbitMQ.

This approach avoids complex middleware (like ESBs in traditional SOA) by making the pipes solely responsible for message transport, not transformation or orchestration. The result is loose coupling, better scalability, resilience, and decentralized governance, as services can evolve independently without relying on a complex communication layer.

![](images/dumb-pipes.webp)



## Taxonomy

Client-service interactions form the backbone of distributed systems, enabling communication between different components and facilitating various functionalities. These interactions can be categorized along two key dimensions:
* **the relationship between the client and the service (one-to-one vs. one-to-many)**
* **the nature of the response timing (synchronous vs. asynchronous)**

### Relationship between the client and the service

- **One-to-One**: In this interaction style, each client request is directed to a specific service, and the response comes from that same service. This approach is straightforward and simplifies the relationship between clients and services but can lead to tight coupling.
  - **Example**: A user requests their account information from a banking service; the request is processed by the account service, which retrieves and sends the relevant data back.

- **One-to-Many**: In this model, a single client request can invoke multiple services. This is useful in scenarios where multiple services need to collaborate to fulfill a request. This approach allows for greater flexibility and scalability, as different services can be updated or replaced independently. 
  - **Example**: A request for a travel itinerary might be sent to a service that interacts with multiple services: a flight booking service, a hotel booking service, and a car rental service.

### Response timing

- **Synchronous**: In synchronous interactions, the client sends a request and waits for a response, often blocking further actions until the response is received. This model is intuitive but can lead to delays if the service takes time to process the request. 
  - **Example**: A client submits a form on a website and waits for the server to confirm the submission before continuing. This tight coupling can lead to performance bottlenecks if the service is slow to respond.

- **Asynchronous**: Asynchronous interactions allow the client to continue processing other tasks without waiting for a response. The client may receive the response later or be notified when the response is ready. This non-blocking approach improves responsiveness and user experience but can complicate error handling and state management. 
  - **Example**: A user uploads a large file to a cloud storage service. Instead of waiting for the upload to complete, the user can continue working, and the service notifies them when the upload is finished.

## Types of interaction

![](images/communication-styles.webp)

**Request/Response (One-to-One, Synchronous)**: In this classic interaction style, a client sends a request to a service and waits for a direct response. The expectation is for a timely reply, which can lead to tight coupling between the client and the service. This pattern is common in traditional web applications and APIs where immediate feedback is required. 

**Asynchronous Request/Response (One-to-One, Asynchronous)**: In this interaction, a client sends a request but does not wait for an immediate response. Instead, it can continue processing other tasks. The service will eventually respond, but the client does not block while waiting. This pattern helps improve user experience by preventing UI freezes or delays. 

**Publish/Subscribe (One-to-Many, Asynchronous)**: This interaction model allows a client to publish messages to a topic or channel that can be consumed by multiple services. Interested services subscribe to these messages, which can be processed independently of the publisher. This decouples clients from services, enabling a more flexible architecture. 

**Publish/Async Responses (One-to-Many, Asynchronous)**: **Description**: In this variation, a client publishes a request message to multiple services and waits for responses for a specified amount of time. This allows the client to receive inputs from various services while still not blocking its operation, enhancing its efficiency. 

| Interaction Type                      | Client-Service Relationship | Response Timing   | Description                                                                                                                                                   | Example                                                                                                    |
|---------------------------------------|-----------------------------|--------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| **Request/Response**                  | One-to-One                  | Synchronous         | A client sends a request to a service and waits for an immediate response. This style often leads to tight coupling between the client and the service.     | A user requests their account information from a banking API and waits for the response.                  |
| **Asynchronous Request/Response**     | One-to-One                  | Asynchronous        | A client sends a request but does not wait for an immediate response, allowing other processing to continue. The service responds at a later time.         | A mobile app sends a message to a server and continues working while waiting for a notification of completion. |
| **Publish/Subscribe**                 | One-to-Many                 | Asynchronous        | A client publishes a message to a topic, which can be consumed by multiple interested services, promoting loose coupling.                                     | A news application publishes articles to a channel that multiple subscribers can consume.                 |
| **Publish/Async Responses**           | One-to-Many                 | Asynchronous        | A client publishes a request message and waits for responses from multiple services for a limited time, allowing for aggregated feedback.                     | An e-commerce platform requests inventory availability from multiple suppliers and collects responses within a specified time. |


## Resources
- Microservices Patterns (Chapter 3)
- Microservices with SpringBoot3 and SpringCloud (Chapter 7)


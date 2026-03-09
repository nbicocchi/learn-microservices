# Labs

# Questions

* In a **monolithic architecture**, entities like `Order`, `OrderLine`, and `Product` are often stored in the same database. Explain **how the management of data relationships changes** when moving to a **microservices architecture** with separate databases.

* What are DTOs, and what is their role in distributed communication?

* What are the **Fallacies of Distributed Computing**? Explain **why they are relevant** in designing distributed systems and provide **two concrete examples**.

* The fallacy **“The Network is Reliable”** is very common in distributed system development.
  a) Why is this assumption incorrect?
  b) Describe **two strategies** to mitigate problems caused by this fallacy.

* In the context of distributed systems, explain the meaning of:

  * **Spatial coupling**
  * **Temporal coupling**
  * **API coupling**

* Describe the problems of **over-fetching** and **under-fetching (chattiness)** in REST APIs. Explain how **GraphQL** can reduce both issues.

* Compare **REST**, **GraphQL**, and **gRPC** with respect to the following aspects:

  * serialization
  * API coupling
  * over-fetching
  * under-fetching

* Explain the principle **Smart Endpoints, Dumb Pipes** in microservices architecture. What are the advantages of this approach compared to an **Enterprise Service Bus (ESB)**?

* Describe the differences between the following service communication models:

  * **Synchronous Request/Response**
  * **Asynchronous Request/Response**
  * **Publish/Subscribe**
    Indicate **when each model is most appropriate**.

* Explain the problem of **duplicate POST requests** in distributed systems. Describe how **idempotency keys** ensure that a request is processed only once.

* Why is the fallacy **“Bandwidth is Infinite”** invalid in distributed systems? Explain the role of **protocol overhead** (TCP/IP, HTTP, serialization) in reducing the effectively available bandwidth.

* Explain the problem of **thread pool exhaustion** in clients using synchronous communications. Why can this issue become critical in **high-throughput systems**?

* Describe the role of **Protocol Buffers (Protobuf)** in **gRPC**. What are the main advantages compared to JSON serialization used in REST?

* Explain the concept of **schema evolution** in **Apache Avro**. How does the deserialization process work when the **writer schema** and **reader schema** are different?

* In the context of **GraphQL**, explain the difference between:

  * **query**
  * **mutation**
  * **alias**

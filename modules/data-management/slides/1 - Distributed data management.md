# Distributed data management

From its core principles and true context, a [microservice](https://www.baeldung.com/spring-microservices-guide)-based application is a distributed system. The overall system consists of multiple smaller services, and together these services provide the overall application functionality.

Although this architectural style provides numerous benefits, it has several limitations as well. One of the major problems in a microservice architecture is how to handle a [transaction that spans multiple services](https://www.baeldung.com/transactions-across-microservices).


## Database per Service Pattern

One of the benefits of a microservice architecture is the ability to choose the most suitable technology stack for each service. For example, we can use a relational database for Service A while opting for a NoSQL database for Service B.

This approach provides several advantages:
1) Each service can manage its domain data independently, using a data store that best fits its schema and data types.
2) Services can scale their data stores independently and remain insulated from failures in other services.

However, when a transaction spans multiple databases, maintaining **ACID** properties becomes a challenge. 

To address this, the **Saga pattern** offers an alternative by breaking a transaction into a sequence of smaller, compensatable steps. Each step represents a local transaction within a single service, and in case of failure, compensating actions ensure consistency.

Additionally, the **CQRS (Command Query Responsibility Segregation) pattern** can help manage complex data consistency requirements by separating write operations (commands) from read operations (queries), allowing for optimized data storage and retrieval strategies across services.


## CAP Theorem
The [CAP theorem](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/) states that a distributed system cannot simultaneously be:
* consistent
* available
* partition tolerant. 

For a formal description of the system and the three properties, please refer to [Gilbert and Lynch's paper](https://groups.csail.mit.edu/tds/papers/Gilbert/Brewer2.pdf).

**A Distributed System**

Let's consider a very simple distributed system. Our system is composed of two servers, G1 and G2. Both of these servers are keeping track of the same variable, v, whose value is initially v0. G1 and G2 can communicate with each other and can also communicate with external clients.

![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap1.svg)

A client can request to write and read from any server. When a server receives a request, it performs any computations it wants and then responds to the client. 

![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap2.svg)![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap3.svg)![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap4.svg)

And here is what a read looks like.

![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap5.svg)![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap6.svg)


**Consistency**

Here's how Gilbert and Lynch describe consistency.

> any read operation that begins after a write operation completes must return that value, or the result of a later write operation

In a consistent system, once a client writes a value to any server and gets a response, it expects to get that value (or a fresher value) back from any server it reads from.

Here is an example of an **inconsistent** system.

![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap7.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap8.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap9.svg)
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap10.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap11.svg)

Our client writes v1 to G1 and G1 acknowledges, but when it reads from G2, it gets stale data: v0.

On the other hand, here is an example of a **consistent** system.

![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap12.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap13.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap14.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap15.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap16.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap17.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap18.svg) 
![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap19.svg)

In this system, G1 replicates its value to G2 before sending an acknowledgement to the client. Thus, when the client reads from G2, it gets the most recent value of v: v1.

**Availability**

Here's how Gilbert and Lynch describe availability.

> every request received by a non-failing node in the system must result in a response

In an available system, if our client sends a request to a server and the server has not crashed, then the server must eventually respond to the client. The server is not allowed to ignore the client's requests.

**Partition Tolerance**

Here's how Gilbert and Lynch describe partitions.

> the network will be allowed to lose arbitrarily many messages sent from one node to another

This means that any messages G1 and G2 send to one another can be dropped. If all the messages were being dropped, then our system would look like this.

![](https://mwhittaker.github.io/blog/an_illustrated_proof_of_the_cap_theorem/assets/cap20.svg)

Our system has to be able to function correctly despite arbitrary network partitions in order to be partition tolerant.

**Key Insight**

* In a distributed system, network failures (partitions) are inevitable. This means that in practice, every system must be Partition Tolerant (P).

* As a result, the system must choose between Consistency (C) and Availability (A) when a partition occurs

![](images/cap-theorem.webp)

### CA Systems (Consistency and Availability)
- **Definition**: CA systems ensure **Consistency** and **Availability** but do not provide **Partition Tolerance**. CA systems are ideal for environments where network reliability is high, and both consistency and availability are critical.
- **Characteristics**:
    - Every request (read or write) gets a consistent response, meaning that all nodes in the system have the same data at the same time.
    - The system is available, meaning every request receives a response (even if it’s an error).
    - However, these systems assume a reliable network with minimal or no network partitions. In case of a network partition, the system may fail to provide both consistency and availability.
- **Examples**:
    - **Relational databases in a single data center** (e.g., MySQL, PostgreSQL).
    - **Centralized, tightly coupled distributed systems** within stable network environments.


### CP Systems (Consistency and Partition Tolerance)
- **Definition**: CP systems ensure **Consistency** and **Partition Tolerance** but do not guarantee **Availability** during network partitions. CP systems are suitable for applications where data accuracy and consistency are critical, even at the cost of availability during network issues.
- **Characteristics**:
    - They prioritize consistency, meaning all nodes have the same, up-to-date data.
    - They tolerate network partitions, meaning the system can still function even if parts of it cannot communicate with others.
    - If a network partition occurs, CP systems may sacrifice availability by blocking some requests to maintain consistency across nodes.
- **Examples**:
    - **Zookeeper**: A coordination and configuration management service that prioritizes consistency.
    - **HBase** and **Redis** (in specific configurations): Systems that prioritize strong consistency and may block requests if partitioned to ensure all nodes stay synchronized.

### AP Systems (Availability and Partition Tolerance)
- **Definition**: AP systems ensure **Availability** and **Partition Tolerance** but do not guarantee **Consistency**. AP systems are suitable for applications that require high availability and can tolerate temporary inconsistencies.
- **Characteristics**:
    - The system remains available during network partitions, meaning it can continue to serve requests even if parts of the system are isolated.
    - Partition tolerance ensures that the system can handle network failures without going offline.
    - Since consistency is not guaranteed, AP systems may allow different parts of the system to return different data during partitions, resulting in eventual consistency once the partition is resolved.
- **Examples**:
    - **Cassandra** and **DynamoDB**: NoSQL databases that prioritize availability and partition tolerance, offering eventual consistency.
    - **Couchbase** and **Riak**: AP databases that are often used in large-scale, distributed applications.

## Challenges of Distributed Transaction

We'll take an example of an e-commerce application that processes online orders and is implemented with microservice architecture.

There is a microservice to create the orders, one that processes the payment, another that updates the inventory and the last one that delivers the order. Each of these microservices performs a local transaction to implement the individual functionalities:

![distributed transaction](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/distributed-transaction.png)

This is an example of a distributed transaction as the transaction boundary crosses multiple services and databases.

To ensure a successful order processing service:
* all four microservices must complete the individual local transactions
* if any of the microservices fail to complete its local transaction, all the completed preceding transactions should roll back to ensure data integrity.


**Challenge 1: Maintaining ACID Properties**

Ensuring transaction correctness in a distributed system requires adherence to ACID principles:

- **Atomicity**: A transaction must be all-or-nothing—either all steps complete successfully, or none take effect.
- **Consistency**: The system must transition from one valid state to another, preserving data integrity.
- **Isolation**: Concurrent transactions should not interfere with each other; they must produce the same results as if executed sequentially.
- **Durability**: Once a transaction is committed, its changes must persist, even in the event of a system failure.

**Challenge 2: Managing Transaction Isolation Levels**

Transaction isolation defines how visible data modifications are when multiple services access the same data concurrently. For example, if a microservice updates a record while another service reads it, should the second service see the old or the new value? The choice of isolation level impacts consistency, performance, and potential anomalies like dirty reads, non-repeatable reads, or phantom reads.


## The Two-Phase Commit Pattern (2PC)

The Two-Phase Commit protocol (2PC) is a widely used pattern to implement distributed transactions. We can use this pattern in a microservice architecture to implement distributed transactions.

In a two-phase commit protocol there are two key components:
* the coordinator component that is responsible for controlling the transaction and contains the logic to manage the transaction.
* the participating nodes (e.g., the microservices) that run their local transactions.

As the name indicates, the two-phase commit protocol runs a distributed transaction in two phases:

1.  **Phase 1 (Prepare)** -- The coordinator asks the participating nodes whether they are ready to commit the transaction. The participants returned with a *yes* or *no*.
2.  **Phase 2 (Commit)** -- If all the participating nodes respond affirmatively in phase 1, the coordinator asks all of them to commit. If at least one node returns negative, the coordinator asks all participants to roll back their local transactions.

![two phase commit](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/two-phase-commit.png)


### Problems With 2PC

Although 2PC is useful to implement a distributed transaction, it has the following shortcomings:

* The coordinator node can become the single point of failure.
* All other services need to wait until the slowest service finishes its confirmation. So, the overall performance of the transaction is bound by the slowest service.
* The two-phase commit protocol is slow by design due to the chattiness and dependency on the coordinator. So, it can lead to scalability and performance issues in a microservice-based architecture involving multiple services.
* Two-phase commit protocol is not supported in NoSQL databases. Therefore, in a microservice architecture where one or more services use NoSQL databases, we can't apply a two-phase commit.

### Two-Phase Commit (2PC) and CAP Theorem

2PC aligns with CP in the CAP theorem, prioritizing Consistency and Partition Tolerance over Availability. It is suitable for systems where strong consistency is crucial and where network reliability is high.

* **Consistency (C)**: 2PC ensures that all participants in a transaction have a consistent state by coordinating a commit or rollback across all nodes. All nodes either commit the transaction or roll it back, achieving atomicity and ensuring data consistency at the expense of availability.

* **Availability (A)**: Because 2PC requires each participant to wait for all others to reach a decision, it is inherently blocking. In the event of a failure or timeout, nodes may be left in an uncertain state, which can limit availability. During a network partition, if the coordinator or any participant is unavailable, 2PC will block the transaction, reducing availability in favor of consistency.

* **Partition Tolerance (P)**: 2PC does not handle network partitions well, as the protocol relies on synchronous responses from all participants. A network failure during the transaction can leave nodes in a blocked or uncertain state. Consequently, 2PC does not prioritize partition tolerance and struggles to function effectively in environments where partitions are common.


## The Saga Pattern

The Saga pattern, [introduced in 1987 by Hector Garcia Molina & Kenneth Salem](https://www.cs.cornell.edu/andru/cs711/2002fa/reading/sagas.pdf), defines a saga as a sequence of transactions that can be interleaved with one another.

* A local transaction is the unit of work performed by a Saga participant.
* Every operation that is part of the Saga can be rolled back by a compensating transaction.
* The Saga pattern guarantees that either all operations complete successfully or the corresponding compensation transactions are run to undo the work previously completed.

In the Saga pattern, a compensating transaction must be *idempotent* and *retryable*. These two principles ensure that we can manage transactions without any manual intervention.

![saga pattern](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/saga-pattern.png)

### Saga Pattern and CAP Theorem

The Saga pattern aligns with **AP** in the CAP theorem, favoring **Availability** and **Partition Tolerance** while accepting **eventual consistency**. It is ideal for systems where high availability and partition tolerance are priorities, and where eventual consistency is acceptable, such as e-commerce order processing, travel bookings, and other long-running workflows.

- **Consistency (C)**: Sagas achieve **eventual consistency** rather than strong consistency. If a failure occurs during one of the steps, compensating transactions are executed to reverse the effects of previous steps. While this approach ensures that the system will reach a consistent state eventually, it does not guarantee immediate consistency across all nodes.

- **Availability (A)**: Sagas are non-blocking, allowing other parts of the system to proceed even if some steps are still being executed or a step fails. In a network partition, steps can continue on available nodes, improving the system's availability compared to 2PC.

- **Partition Tolerance (P)**: The Saga pattern is well-suited for partition tolerance since it does not require synchronous communication across all nodes. Steps in a Saga can be executed asynchronously, and nodes do not need to coordinate a global commit. This enables the system to handle partitions gracefully, with compensating actions mitigating inconsistencies after the partition is resolved.





### The Saga Execution Coordinator

The Saga Execution Coordinator is the central component to implement a Saga flow. It contains a Saga log that captures the sequence of events of a distributed transaction.

* For any failure, the SEC component inspects the Saga log to identify the impacted components and the sequence in which the compensating transactions should run.

* For any failure in the SEC component, it can read the Saga log once it's coming back up. It can then identify the transactions successfully rolled back, which ones are pending, and can take appropriate actions:

![saga execution](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/saga-execution.png)


### Saga Orchestration

In the Orchestration pattern, a single orchestrator is responsible for managing the overall transaction status. If any of the microservices encounter a failure, the orchestrator is responsible for invoking the necessary compensating transactions.

The following diagram demonstrates the successful Saga flow for the online order processing application:

![saga coreography](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/saga-coreography.png)

In the event of a failure, the microservice reports the failure to SEC, and it is the SEC's responsibility to invoke the relevant compensation transactions:

![saga coreography 2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/saga-coreography-2.png)

In this example, the Payment microservice reports a failure, and the SEC invokes the compensating transaction to unblock the seat. If the call to the compensating transaction fails, it is the SEC's responsibility to retry it until it is successfully completed. Recall that in Saga, a compensating transaction must be *idempotent* and *retryable*.

The Saga orchestration pattern is useful for brownfield microservice application development architecture. In other words, this pattern works when we already have a set of microservices and would like to implement the Saga pattern in the application. We need to define the appropriate compensating transactions to proceed with this pattern.

### Saga Choreography

In the Saga Choreography pattern, each microservice that is part of the transaction publishes an event that is processed by the next microservice. In the Saga, choreography flow is successful if all the microservices complete their local transaction, and none of the microservices reported any failure.

![saga orchestration](https://www.baeldung.com/wp-content/uploads/sites/4/2021/04/saga-orchestration.png)

The Choreography pattern works well when there are fewer participants in the transaction.


## The CQRS Pattern

### **Command Query Responsibility Segregation (CQRS)**

**CQRS** is an architectural pattern that separates the responsibilities of handling commands (write operations) and queries (read operations) within a software system. By decoupling these concerns, CQRS enhances **scalability, maintainability, and flexibility**.

In traditional application design, most systems interact with data using the **CRUD** model—performing **C**reate, **R**ead, **U**pdate, and **D**elete operations on a single datastore. However, CQRS introduces a different approach by **separating read operations (queries) from write operations (commands)**.

**When to Use CQRS**

CQRS is not a one-size-fits-all solution. Many applications work well with a straightforward CRUD model and don’t require additional complexity. However, CQRS is particularly useful in cases where:
- The **read and write workloads differ significantly**, allowing each to be scaled independently.
- The system requires **high performance**, as optimizations can be tailored separately for reads and writes.
- Different **data models** are needed for querying and updating, improving efficiency and reducing contention.

By decoupling commands from queries, CQRS enables better performance tuning, fault isolation, and system responsiveness—making it a strong candidate for complex, high-throughput applications.


![](images/cqrs-pattern.webp)

### Motivation

When designing **CRUD** applications, we typically create **entity classes** and corresponding **repository classes** to handle database operations. In this approach, we use the **same model classes** for both read and write operations. However, real-world applications often have **different requirements for reads and writes**. A model optimized for writes may not be efficient for reads.

#### Challenge: Differing Read and Write Requirements
Consider an application with the following **normalized tables**:
- `user`
- `product`
- `purchase_order`

Write operations, such as creating a new user, product, or order, are straightforward and involve inserting data directly into the relevant tables. However, **read operations** often require more than just retrieving raw records. For example:
- Fetching all **orders** placed by a specific user.
- Calculating **state-wise total sales**.
- Computing **product-wise total sales**.

These queries often involve **aggregations and complex joins** across multiple tables, which can significantly impact **read performance**. The more we normalize our data, the easier it becomes to write, but at the cost of making reads more complex and potentially slower.

**The Solution: Separate Models for Reads and Writes**
To optimize both performance and maintainability, we can separate **read models** from **write models**. This allows us to:
- **Optimize the write model** for quick and efficient inserts and updates.
- **Optimize the read model** for fast queries, using denormalized structures, caching, or precomputed views.

By applying patterns like **CQRS (Command Query Responsibility Segregation)**, we can ensure that our system handles both reads and writes efficiently without unnecessary trade-offs.


#### Challenge: Different traffic intensity between READ and WRITE operations

In most applications, **READ operations far outnumber WRITE operations**. This is especially true for **web-based applications**, which are typically **read-heavy**.
- **Social Media Platforms (Facebook, Twitter, etc.)**: Whether or not users post updates, they frequently **check feeds, browse profiles, and search content**—all of which are **read-heavy** operations. While new posts and interactions involve writes, the system processes far more reads than writes.
- **Flight-Booking Applications**: Only a small percentage of users **book tickets** (writes), whereas the majority repeatedly **search for flights, check availability, and compare prices** (reads). This creates a large disparity between **read and write workloads**.

To address this imbalance, we can **separate read and write concerns at the architectural level**. One approach is to have **dedicated microservices** for **READ** and **WRITE** operations.
- **Independent Scaling**: Read and write services can be scaled separately based on demand. For example, in a flight-booking application, the read service can be **scaled out** to handle heavy traffic, while the write service remains relatively smaller.
- **Performance Optimization**: Each service can use different data storage strategies—**denormalized views, caching, or read replicas** for fast queries, while the write service ensures data integrity.
- **Fault Isolation**: A failure in the write service (e.g., a database insert issue) does not impact read operations, improving system availability.

By leveraging **CQRS (Command Query Responsibility Segregation)**, applications can efficiently handle high read loads while maintaining a robust and scalable architecture.

### CQRS pattern and CAP theorem
**CQRS is inherently neither AP nor CP**, but its implementation can lean toward **AP** or **CP** depending on the consistency model used:

- **CQRS with CP**: Used in systems that require **immediate consistency**, ensuring that read and write operations are strictly synchronized.
- **CQRS with AP**: Common in **scalable, distributed systems** where reads are fast and based on an **eventually consistent** Read Model.

In practice, **most CQRS architectures favor AP** because the separation of read and write operations is often implemented using **asynchronous messaging and eventual consistency**, optimizing for **scalability and availability**.

## References
* [Orkes Conductor](https://www.orkes.io/what-is-conductor)
* [Eclipse MicroProfile LRA](https://github.com/eclipse/microprofile-lra)
* [Eventuate Tram Saga](https://eventuate.io/docs/manual/eventuate-tram/latest/getting-started-eventuate-tram-sagas.html)
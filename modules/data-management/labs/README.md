# Labs

## Lab 1: Orchestration-based Saga

In this exercise, three services collaborate to implement a **Saga Pattern** using Orkes Conductor. The services are:

1. **Order Service**: Accepts a POST request to create an order (`POST /order`), and starts the workflow.
2. **Payment Service**: Validates and processes the customer’s payment.
3. **Inventory Service**: Checks product availability and reserves the items in the warehouse.

```mermaid
flowchart TD
  ClientWrite -->|POST /order| OrderService
  OrderService -->|Start Workflow| OrkesConductor
  OrkesConductor -->|Confirm Order| OrderService
  OrkesConductor -->|Reject Order| OrderService
  OrkesConductor -->|Check Payment| PaymentService
  PaymentService -->|Payment Success| OrkesConductor
  PaymentService -->|Payment Failed| OrkesConductor
  OrkesConductor -->|Check Inventory| InventoryService
  InventoryService -->|Inventory Success| OrkesConductor
  InventoryService -->|Inventory Failed| OrkesConductor
  
```

## Lab 2: Choreography-based Saga

In this exercise, three services collaborate to implement a **Saga Pattern** using asynchronous messaging. The services are:

1. **Order Service**: Accepts a POST request to create an order (`POST /order`), and starts the workflow.
2. **Payment Service**: Validates and processes the customer’s payment.
3. **Inventory Service**: Checks product availability and reserves the items in the warehouse.

```mermaid
flowchart TD
  Client -->|POST /order| OrderService
  OrderService -->|order.created| PaymentService
  PaymentService -->|payment.ok| InventoryService
  PaymentService -->|payment.failed| OrderService
  InventoryService -->|inventory.ok| OrderService
  InventoryService -->|inventory.failed| OrderService
```

## Lab 3: CQRS

In this exercise, three services collaborate to implement a **Saga Pattern** using asynchronous messaging. In this example, the CQRS pattern is also applied to separate read and write responsibilities. The services are:

1. **Order Service**: Accepts a POST request to create an order (`POST /order`), and starts the workflow.
2. **Payment Service**: Validates and processes the customer’s payment.
3. **Inventory Service**: Checks product availability and reserves the items in the warehouse.

```mermaid
flowchart TD
  Client-Write -->|POST /order| OrderService-Write
  Client-Read -->|GET /order| OrderService-Read
  OrderService-Write -->|order.created| PaymentService
  OrderService-Write -->|order.confirmed| OrderService-Read
  PaymentService -->|payment.ok| InventoryService
  PaymentService -->|payment.failed| OrderService-Write
  InventoryService -->|inventory.ok| OrderService-Write
  InventoryService -->|inventory.failed| OrderService-Write
```



# Questions
1. What challenges arise in maintaining data consistency when using the one database per service pattern in microservices?
2. What is the Saga pattern, and why is it essential for managing distributed transactions? Describe the choreography and orchestration approaches.
3. What does the CAP theorem state, and how does it affect the design of distributed systems?
4. What role do compensating transactions play in the Saga pattern, and why must they be idempotent?
5. What is the two-phase commit protocol, and how does it ensure atomicity in distributed transactions?
6. What are the performance and scalability trade-offs of using two-phase commit in a distributed environment?
7. What is Conductor, and how does it help manage distributed workflows?
8. What challenges drive the adoption of the CQRS pattern in large-scale applications?
9. How does the CQRS pattern enable independent scaling and performance optimization for read and write operations?
10. How can a CQRS-based system align with either the CP or AP model under the CAP theorem?
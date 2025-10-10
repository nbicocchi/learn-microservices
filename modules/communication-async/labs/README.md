# Labs

## Lab 1: Basic RabbiMQ Communication

1. Implement a **provider-service** sending an *Event* to a message broker (*queue.messages* exchange) each second and logging its activity.
2. Implement a **consumer-service** connecting to the same message broker (*queue.messages* exchange) and logging the received events.

```java
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}

    @NonNull private Type type;
    @NonNull private K key;
    @NonNull private T data;
    private LocalDateTime timestamp = LocalDateTime.now();
}
```

---

## Lab 2: Scaling services with Asynchronous Communication

In this lab, you'll implement an asynchronous system for computing prime numbers based of three microservices:
- **proxy-service**: Exposes an endpoint to receive HTTP requests.
- **math-service**: Computes prime numbers (deploy three instances).
- **notification-service**: Receives the prime numbers computed by an instance of **math-service** and logs them.

1. Implement a **proxy-service** for consuming **math-service**:
   * `POST /primes {lowerbound, upperbound, email}` → communicates asynchronously with multiple instances of **math-service**.

2. Implement a **math-service**:
- Listens for incoming asynchronous events.
- Computes the requested prime numbers.
- Send a notification event to the **notification-service**.

2. Implement a **notification-service**:
- Listens for incoming **asynchronous events**.
- Logs the computed results.

---

# Questions
1. Comment on the key fallacies of distributed systems.
2. How does dependency injection contribute to the spread of the fallacies of distributed systems?
3. What are the four key communication styles in distributed systems?
4. What are the limitations of synchronous communication?
5. How do Protobuf and GraphQL mitigate the drawbacks of REST?
6. What are the trade-offs between using synchronous and asynchronous messaging in a distributed system?
7. What are the trade-offs between broker-based and brokerless asynchronous communication styles?
8. What are message exchanges and queues, and how do they support communication in microservice architectures?
9. How can you scale RabbitMQ consumers in a Spring Boot application to handle high message volumes? Describe the differences between consumer groups, partitions, and message routing.
10. What are DTOs, and what is their role in distributed communication?
11. What is the role of Spring Cloud Stream in terms of abstraction? What are the trade-offs of this approach compared to native libraries?  
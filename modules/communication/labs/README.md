# Labs

All exercises have to support both a development configuration (default profile) and a production configuration (docker profile).

## Lab 1: Basic REST Communication

1. Implement a **provider-service** returning a greeting message.
   * `GET /greet` → returns a greet message
   * `POST /setDelay {delay}` → set a wait delay before replying to `/greet`
2. Implement a **consumer-service**.
   * `GET /greet` → consumes `/greet` on the **provider-service** and returns the message
   * Logs the received message.
3. Use **prometheus** to show the changes in *peak threads* within both services after calling **consumer-service**/greet 100 times in parallel.

---

## Lab 2: Scaling with REST Communication

1. Implement a **math-service** for computing prime numbers.
    * `POST /primes {lowerbound, upperbound, email}` → returns the list of all prime numbers between `lowerbound` and `upperbound`.
2. Implement a **proxy-service** for consuming **math-service**.
   * `POST /primes {lowerbound, upperbound, email}` → returns the list of all prime numbers between `lowerbound` and `upperbound`.
3. After testing the two services by themselves, add a containerized nginx service acting as a load balancer. Refer to the `docker-compose.yml` file and `nginx` folder for properly running it.

---

## Lab 2: REST Communication for a Social Network

1. Implement a **post-service** for managing posts on a social network.
  - `GET /posts` → Returns all posts.
  - `GET /posts/{userUUID}` → Returns all posts created by a specific user.

   ```java
   class Post {
       Long id;
       String userUUID;
       LocalDateTime timestamp;
       String content;
   }
   ```  

2. Implement a **user-service**, exposing the following endpoints:
  - `GET /users` → Returns all users (only local details).
  - `GET /users/{userUUID}` → Returns local details and all posts of a specific user.

   ```java
   class User {
       Long id;
       String userUUID;
       String nickname;
       LocalDate birthDay;
   }
   ```  

3. Use DTOs to abstract internal details, such as database primary keys, in API responses.

---

## Lab 3: Basic RabbiMQ Communication

1. Implement a **Provider Service** (`provider-service`) sending an *Event* to a message broker (*queue.messages* exchange) each second and logging its activity.
2. Implement a **Consumer Service** (`consumer-service`) connecting to the same message broker and logging the received events. 
3. Deploy both services and the message broker within a Docker environment.


Here’s a more polished and structured version of your text:

---

## Lab 4: Asynchronous Math Service

In this lab, you'll implement an asynchronous system for computing prime numbers using two microservices:
- **Proxy Service (`proxy-service`)**: Exposes an endpoint to receive requests.
- **Math Service (`math-service`)**: Processes requests asynchronously and computes prime numbers.

1. **Implement the Proxy Service** (`proxy-service`)
Develop a **Proxy Service** that provides the following endpoint to accept prime number search requests:

```java
@PostMapping
public ProxyRequest searchPrimes(@RequestBody ProxyRequest request) {
    // Implementation goes here
}
```

Definition of `ProxyRequest`:

```java
public record ProxyRequest(Long lowerBound, Long upperBound, String email) {}
```

The **Proxy Service** communicates asynchronously with multiple instances of a **Math Service** (`math-service`), which is responsible for computing prime numbers.

To facilitate this communication, use the following **Event** class:

```java
public class Event<K, T> {
    private K key;
    private T data;
    private ZonedDateTime eventCreatedAt = ZonedDateTime.now();
}
```

2. **Implement the Math Service** (`math-service`)
Develop a **Math Service** that:
- Listens for incoming **asynchronous events**.
- Computes the requested prime numbers.
- Logs the computed results.


3. **Deploy Using Docker** To ensure scalability and reliability:
- Deploy all services using **Docker**.
- Run **three instances** of `math-service`, ensuring they share the same **consumer group** for load balancing.


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
# Labs

## Lab 1: Basic REST Communication

**Instructions:**
- Implement a service (*provider-service*) that exposes a REST endpoint (i.e., `/greet`) that returns a greeting message.
- Implement another service (*consumer-service*) that consumes the `/greet` endpoint every 2 seconds and displays the response in its logs.
- Use `RestClient` to make HTTP requests.

## Lab 2: REST Communication for a social network

**Instructions:**
- Implement a service (*post-service*) exposing endpoints for managing posts on a social network. In particular:
  - @Get /posts -> returning all posts
  - @Get /posts/{userid} -> returning all posts of a specific user

```java
class Post {
    Long id;
    String userUUID;
    String content;
    LocalDateTime timestamp;
}
```

- Implement a service (*user-service*) exposing endpoints for managing users of a social network. In particular:
  - @Get /users -> returning all users (only local details)
  - @Get /users/{userid} -> returning local details of the user and all its posts

```java
class User {
    Long id;
    String userUUID;
    String nickname;
    LocalDateTime birthDay;
}
```

- Both service must use DTOs for hiding implementation details (e.g., the primary key on the database). 
- Run the two services within a Docker environment.

## Lab 3: Asynchronous Communication with RabbitMQ
**Instructions:**
- Implement a service capable of receiving asynchronous events for solving math problems. 

```java
import java.time.LocalDateTime;

class Event {
    String type; // should be primes or fibonacci
    LocalDateTime timestamp;
    Long n;
}
```


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
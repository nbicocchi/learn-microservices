# Labs

## Lab 1: Basic REST Communication

1. Implement a **Provider Service** (*provider-service*) that exposes a REST endpoint (`/greet`) returning a greeting message.
2. Implement a **Consumer Service** (*consumer-service*) that:
  - Calls the `/greet` endpoint every 2 seconds (See @Scheduled annotation). 
  - Logs the received message.
3. Utilize `RestClient` to perform HTTP requests.

## Lab 2: REST Communication for a Social Network

1. Implement a **Post Service** (`post-service`), exposing the following endpoints:
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

2. Implement a **User Service** (`user-service`), exposing the following endpoints:
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
4. Deploy both services within a Docker environment.

## Lab 3: Asynchronous Math Service

1. Implement a **Math Service** (`math-service`)
  - Listens for asynchronous events containing math problems.
  - Computes either the `n`th Fibonacci or prime number based on the `type` field.
  - Logs the computed result.

   ```java
   class Event {
       String type; // "primes" or "fibonacci"
       LocalDateTime timestamp;
       Long n;
   }
   ```  

2. Implement a **Client Service** (`client-service`)
  - Generates random math events (`primes` or `fibonacci`).
  - Sends events asynchronously to a message broker (RabbitMQ).

3. Scenarios to Implement:
  - **Single Consumer**: One `client-service` communicates with one `math-service`.
  - **Load Balancing**: One `client-service` communicates with three replicas of `math-service`, where each instance processes all event types.
  - **Selective Routing**: One `client-service` communicates with two replicas of `math-service`, where each instance processes only one type of event (`primes` or `fibonacci`).

4. Deploy all services using Docker, ensuring proper RabbitMQ configuration for message routing.

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
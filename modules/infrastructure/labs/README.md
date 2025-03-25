# Labs

## Lab 1: Service Discovery with Eureka

1. Implement a **datetime-service** returning the current date and time (read from internal clock).
    * `GET /date` → returns the current date
    * `GET /time` → returns the current time
2. Implement a **datetime-composite-service** returning the current date and time (communicates with a pool of instances of **datetime-service** for getting the current date and time).
    * `GET /datetime` → returns the current date and time
3. Implement an architecture delivering the above service in which **datetime-composite-service** finds healthy instances of **datetime-service** using Eureka service discovery and client-side load balancing.
4. Implement an architecture delivering the above service in which **datetime-composite-service** finds healthy instances of **datetime-service** using nginx and server-side load balancing.

## Lab 2: Service Routing with Spring Cloud Gateway

1. Implement a **user-service** returning data about users:
    * `GET /` → returns data on all users.
    * `GET /{userUUID}` → returns data on the specified user.

    ```java
    public class UserModel {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NonNull @EqualsAndHashCode.Include private String userUUID;
        @NonNull private String nickname;
        @NonNull private LocalDate birthDate;
    }
    ```
2. Implement a **post-service** returning data about users' posts:
    * `GET /` → returns data on all posts.
    * `GET /{userUUID}` → returns data on posts of the specified user.

    ```java
    public class Post {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NonNull @EqualsAndHashCode.Include private String postUUID;
        @NonNull @EqualsAndHashCode.Include private String userUUID;
        @NonNull @EqualsAndHashCode.Include private LocalDateTime timestamp;
        @NonNull private String content;
    }
   ```
   
3. Implement a **comment-service** returning data about users' comments:
    * `GET /` → returns data on all comments.
    * `GET /{postUUID}` → returns data on comments of the specified post.

    ```java
    public class Comment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NonNull @EqualsAndHashCode.Include private String commentUUID;
        @NonNull @EqualsAndHashCode.Include private String postUUID;
        @NonNull @EqualsAndHashCode.Include private LocalDateTime timestamp;
        @NonNull private String content;
    }
    ```

4. Implement a BFF service returning data on a specific user. The service communicates with all three core services (**user**, **post**, **comment**) for returning a complete profile of the user.
   * `GET /{userUUID}` → returns data on the specified user.
5. Implement an architecture delivering the above service in which Spring Cloud Gateway is used (in tandem with Eureka) for consolidating the external API on a single interface.
6. Implement an architecture delivering the above service in which nginx (without Eureka which is not supported) is used for consolidating the external API on a single interface.



## Lab 3: Centralized Configuration
1. Implement a **datetime-service** returning the current date and time (read from internal clock).
   * `GET /date` → returns the current date
   * `GET /time` → returns the current time
2. Implement a **datetime-composite-service** returning the current date and time (communicates with a pool of instances of **datetime-service** for getting the current date and time).
   * `GET /datetime` → returns the current date and time
3. Implement an architecture delivering the above service in which **datetime-composite-service** finds healthy instances of **datetime-service** using Eureka service discovery and client-side load balancing.
4. Externalize the configuration of the two core services (i.e., **datetime**, **datetime-composite**) to a Git repository using Spring Config Server.

# Questions
1. What is service discovery, and why is it important in microservices architecture?
2. Explain the key differences between client-side and server-side load balancing.
3. Explain the key differences in using Spring Cloud Gateway or nginx as a server-side load balancer.
4. Explain the role of heartbeats (towards a Eureka node) in client-side load balancing.
5. What is an API Gateway? Is this pattern correlated with Server-side load balancing? How?
6. What is a cross-cutting concern and how it could be addressed with a gateway service? Is a shared library a viable alternative?
7. Discuss the differences and similarities between the API gateway and BFF pattern.
8. What are the advantages of using centralized configuration management in distributed architectures?
9. How does Spring Cloud Config Server work, and what are its main components?
10. What is the significance of using a Git repository for centralized configuration management?


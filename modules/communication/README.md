# Labs

## Lab 1: Implementing REST Communication in a Spring Boot Application
**Objective:** Create a simple Spring Boot application that communicates with another service using REST.

**Instructions:**
- Create a Spring Boot application that exposes a REST endpoint (e.g., `/greet`) that returns a greeting message.
- Implement another Spring Boot application that consumes the `/greet` endpoint and displays the response.
- Use `RestClient` to make HTTP requests from the consumer service to the provider service.
- Test the communication between the two services to verify that the greeting message is returned correctly.

**Expected Outcomes:**
- Students should be able to set up a Spring Boot application with REST communication.
- They will understand how to use `RestClient` to call external REST APIs and handle responses.

## Lab 2: Implementing Asynchronous Communication with RabbitMQ
**Objective:** Set up asynchronous communication between microservices using RabbitMQ.

**Instructions:**
- Create a Spring Boot application that produces messages to a RabbitMQ queue.
- Implement another Spring Boot application that consumes messages from that queue.
- Use `spring-cloud-stream` and `spring-cloud-starter-stream-rabbit` dependency to simplify RabbitMQ integration.
- Test the system by sending a message from the producer and verifying it is processed by the consumer.

**Expected Outcomes:**
- Students should understand how to implement asynchronous communication with RabbitMQ.
- They will learn how to configure RabbitMQ, publish messages, and consume them in Spring Boot applications.

### Lab 3: Combining Synchronous and Asynchronous Communication
**Objective:** Implement a microservice that uses both synchronous and asynchronous communication mechanisms.

**Instructions:**
- Create a Spring Boot service that consumes an external REST API using `RestClient` (synchronous communication) and processes the response.
- After processing, send a message to a RabbitMQ queue for further asynchronous processing by another service.
- The second Spring Boot service should listen to the RabbitMQ queue and print the processed message.
- Test the workflow to ensure the data flows correctly from the synchronous API call to the asynchronous message queue.

**Expected Outcomes:**
- Students will learn how to integrate both synchronous and asynchronous communication patterns in a single service.
- They will understand how to build more flexible and scalable microservices.

# Questions
1. How can you implement RESTful communication between two Spring Boot applications?
2. What are the advantages of using RabbitMQ for asynchronous communication between microservices?
3. How does GraphQL differ from REST when it comes to data retrieval in microservices?
4. Explain the use of Protocol Buffers (Protobuf) in microservices communication.
5. How does RabbitMQ ensure message delivery reliability in asynchronous communication?
6. What are message queues, and how do they support decoupled communication in microservice architectures?
7. What is the difference between a direct exchange and a fanout exchange in RabbitMQ, and how are they used?
8. How can you implement request-response communication patterns using RabbitMQ in a microservices architecture?
9. How can you scale RabbitMQ consumers in a Spring Boot application to handle high volumes of messages?
10. What are the trade-offs between using synchronous REST communication and asynchronous messaging in a microservice system?

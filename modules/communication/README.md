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
- Use the `spring-boot-starter-amqp` dependency to simplify RabbitMQ integration.
- Test the system by sending a message from the producer and verifying it is processed by the consumer.

**Expected Outcomes:**
- Students should understand how to implement asynchronous communication with RabbitMQ.
- They will learn how to configure RabbitMQ, publish messages, and consume them in Spring Boot applications.

## Lab 3: Using Kafka for Event-Driven Communication
**Objective:** Implement event-driven communication between microservices using Apache Kafka.

**Instructions:**
- Set up a Spring Boot application that produces events to a Kafka topic.
- Create another Spring Boot application that consumes events from the same Kafka topic.
- Use the `spring-kafka` library to handle Kafka integration and message serialization.
- Test the applications by producing an event and ensuring it is received and processed by the consumer.

**Expected Outcomes:**
- Students will learn how to leverage Apache Kafka for event-driven architectures.
- They will understand how to publish and subscribe to messages using Kafka topics in Spring Boot applications.

# Questions
1. What are the key differences between synchronous and asynchronous communication in microservices?
2. How can you implement RESTful communication between two Spring Boot applications?
3. Explain the role of `RestClient` in a Spring Boot application for REST communication.
4. What are the advantages of using RabbitMQ for asynchronous communication between microservices?
5. How can you configure a Spring Boot application to use RabbitMQ for message publishing and consumption?
6. What is Apache Kafka, and how does it facilitate event-driven communication in microservices?
7. Describe how to set up a Kafka producer and consumer in a Spring Boot application.
8. How does GraphQL differ from REST when it comes to data retrieval in microservices?
9. Explain the use of Protocol Buffers (Protobuf) in microservices communication.
10. What are the best practices for handling communication failures between microservices?

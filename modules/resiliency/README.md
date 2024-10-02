# Labs

## Lab 1: Implementing Circuit Breaker Pattern with Resilience4j
**Objective:** Set up a Circuit Breaker to handle service failures gracefully in a Spring Boot application using Resilience4j.

**Instructions:**
- Create a Spring Boot application that integrates Resilience4j by adding the `resilience4j-spring-boot2` dependency.
- Implement a RESTful service that simulates a delay or failure (e.g., throwing an exception) when a certain endpoint is hit.
- Apply the Circuit Breaker pattern to the service to prevent cascading failures when the service is down.
- Test the Circuit Breaker by repeatedly calling the endpoint and observing the behavior during failure and recovery.

**Expected Outcomes:**
- Students will understand how to implement the Circuit Breaker pattern using Resilience4j.
- They will be able to handle service failures gracefully and observe the effects of the Circuit Breaker in action.

## Lab 2: Implementing Retry Mechanism
**Objective:** Set up a retry mechanism to automatically retry failed requests in a Spring Boot application.

**Instructions:**
- Extend the Spring Boot application from Lab 1 to include a retry mechanism using Resilience4j's Retry feature.
- Configure the retry behavior (e.g., number of attempts, wait duration) for the RESTful service endpoint.
- Test the retry mechanism by simulating transient failures and ensuring that the application retries the request as configured.

**Expected Outcomes:**
- Students will learn how to implement a retry mechanism to handle transient failures.
- They will be able to configure and test the retry behavior effectively in a Spring Boot application.

## Lab 3: Bulkhead Pattern for Resource Isolation
**Objective:** Implement the Bulkhead pattern to isolate resources in a Spring Boot application using Resilience4j.

**Instructions:**
- Create a new Spring Boot service that simulates resource-intensive operations (e.g., long-running tasks).
- Use Resilience4j to implement the Bulkhead pattern, isolating resources for different parts of the application (e.g., separating HTTP calls from database calls).
- Test the Bulkhead pattern by simulating high load on one part of the application and verifying that it does not affect the other parts.

**Expected Outcomes:**
- Students will understand the Bulkhead pattern and its role in microservice resiliency.
- They will be able to configure and test the Bulkhead pattern to ensure resource isolation in a Spring Boot application.

# Questions
1. What is microservice resiliency, and why is it essential in distributed systems?
2. How does the Circuit Breaker pattern help improve resiliency in microservices?
3. Explain how Resilience4j implements the Circuit Breaker pattern in a Spring Boot application.
4. What are the key differences between the Circuit Breaker and Retry patterns?
5. Describe how to configure a retry mechanism using Resilience4j in a Spring Boot application.
6. What is the Bulkhead pattern, and how does it contribute to resource isolation?
7. How can you implement the Bulkhead pattern using Resilience4j in a Spring Boot application?
8. Discuss the significance of monitoring and metrics in ensuring microservice resiliency.
9. What role do timeouts play in improving the resiliency of microservices?
10. What are best practices for implementing resiliency patterns in Spring Boot applications?

# Labs

## Lab 1: Implementing Circuit Breaker Pattern with Resilience4j
**Objective:** Set up a Circuit Breaker to handle service failures gracefully in a Spring Boot application using Resilience4j.

**Instructions:**
- Create a Spring Boot application that integrates Resilience4j by adding the `resilience4j-spring-boot3` dependency.
- Implement a RESTful service that simulates a delay or failure (e.g., throwing an exception) when a certain endpoint is hit.
- Apply the Circuit Breaker pattern to the service to prevent cascading failures when the service is down.
- Test the Circuit Breaker by repeatedly calling the endpoint and observing the behavior during failure and recovery.

## Lab 2: Implementing Retry Mechanism
**Objective:** Set up a retry mechanism to automatically retry failed requests in a Spring Boot application.

**Instructions:**
- Extend the Spring Boot application from Lab 1 to include a retry mechanism using Resilience4j's Retry feature.
- Configure the retry behavior (e.g., number of attempts, wait duration) for the RESTful service endpoint.
- Test the retry mechanism by simulating transient failures and ensuring that the application retries the request as configured.

## Lab 3: Bulkhead Pattern for Resource Isolation
**Objective:** Implement the Bulkhead pattern to isolate resources in a Spring Boot application using Resilience4j.

**Instructions:**
- Create a new Spring Boot service that simulates resource-intensive operations (e.g., long-running tasks).
- Use Resilience4j to implement the Bulkhead pattern, isolating resources for different parts of the application (e.g., separating HTTP calls from database calls).
- Test the Bulkhead pattern by simulating high load on one part of the application and verifying that it does not affect the other parts.

# Questions
1. What is resiliency, and why is it essential in distributed systems?
2. How does the Circuit Breaker pattern help improve resiliency in microservices?
3. Explain how Resilience4j implements the Circuit Breaker pattern in a Spring Boot application.
4. What are the key differences between the Circuit Breaker, Retry, and Timeouts patterns?
5. Describe the *one thread per request* pattern and its key issues (i.e., thread pool/memory saturation). 
6. Which are the most promising alternatives to the *one thread per request* pattern? Highlight their key features and mutual differences.
7. What is the Bulkhead pattern, and how does it contribute to resource isolation?
8. Describe the difference between client-side and server-side resiliency patterns.
9. Describe pros and cons of *fixed-window*, *sliding-window*, *leaky bucket* policies for circuit breaker implementations.
10. What role do timeouts play in improving the resiliency of microservices? Describe best practices for setting timeout values.

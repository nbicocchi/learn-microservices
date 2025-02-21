# Labs

## Lab 1: Implementing Service Discovery with Eureka
**Objective:** Set up a service discovery mechanism using Netflix Eureka in a Spring Boot application.

**Instructions:**
- Create a Spring Boot application to act as the Eureka server using the `spring-cloud-starter-netflix-eureka-server` dependency.
- Develop another Spring Boot application that registers itself with the Eureka server.
- Configure the client application to discover other services via the Eureka server.
- Test the registration and discovery process by accessing the Eureka dashboard and verifying the registered services.
- Using replicas, show that the heartbeats mechanism allow the system to be resilient to failing services.

## Lab 2: Service Routing with Spring Cloud Gateway
**Objective:** Implement API Gateway functionality using Spring Cloud Gateway for routing requests to multiple microservices.

**Instructions:**
- Set up a Spring Boot application as an API Gateway using the `spring-cloud-starter-gateway` dependency.
- Configure routes in the `application.yml` file to direct incoming requests to appropriate backend services.
- Develop two simple microservices that the gateway will route requests to.
- Test the gateway by sending requests through it and verifying the responses from the backend services.

## Lab 3: Service Routing with Nginx
**Objective:** Implement API Gateway functionality using nginx for routing requests to multiple microservices.

**Instructions:**
- Configure routes in Nginx configuration file to direct incoming requests to appropriate backend services.
- Develop two simple microservices that the gateway will route requests to.
- Test the gateway by sending requests through it and verifying the responses from the backend services.

# Questions
1. What is service discovery, and why is it important in microservices architecture?
2. Explain the key differences between client-side and server-side load balancing.
3. Explain the role of heartbeats (towards a Eureka node) in client-side load balancing.
4. Explain the role of the Eureka server and its client in the service discovery process.
5. What is an API Gateway? Is this pattern correlated with Server-side load balancing? How?
6. What is a cross-cutting concern and how it could be addressed with a gateway service?
7. Discuss the differences and similarities between the API gateway and backend for frontends pattern.
8. What are the advantages of using centralized configuration management in microservices?
9. How does Spring Cloud Config Server work, and what are its main components?
10. What is the significance of using a Git repository for centralized configuration management?


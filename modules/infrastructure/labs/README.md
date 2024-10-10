# Labs

## Lab 1: Implementing Service Discovery with Eureka
**Objective:** Set up a service discovery mechanism using Netflix Eureka in a Spring Boot application.

**Instructions:**
- Create a Spring Boot application to act as the Eureka server using the `spring-cloud-starter-netflix-eureka-server` dependency.
- Develop another Spring Boot application that registers itself with the Eureka server.
- Configure the client application to discover other services via the Eureka server.
- Test the registration and discovery process by accessing the Eureka dashboard and verifying the registered services.

**Expected Outcomes:**
- Students will understand how to implement service discovery using Eureka.
- They will be able to set up both the Eureka server and client applications, ensuring successful registration and discovery.

## Lab 2: Service Routing with Zuul
**Objective:** Implement API Gateway functionality using Spring Cloud Zuul for routing requests to multiple microservices.

**Instructions:**
- Set up a Spring Boot application as a Zuul API Gateway using the `spring-cloud-starter-netflix-zuul` dependency.
- Configure routes in the `application.yml` file to direct incoming requests to appropriate backend services.
- Develop two simple microservices that the Zuul gateway will route requests to.
- Test the gateway by sending requests through Zuul and verifying the responses from the backend services.

**Expected Outcomes:**
- Students will learn how to implement API Gateway patterns with Zuul.
- They will understand how to configure service routes and use Zuul for routing requests to different services.

## Lab 3: Centralized Configuration with Spring Cloud Config
**Objective:** Set up centralized configuration management for microservices using Spring Cloud Config.

**Instructions:**
- Create a Spring Cloud Config Server using the `spring-cloud-config-server` dependency.
- Store configuration properties in a Git repository or local file system.
- Develop a Spring Boot microservice that retrieves its configuration from the Config Server.
- Test the configuration retrieval by changing the properties in the repository and verifying that the microservice reflects the changes without redeployment.

**Expected Outcomes:**
- Students will understand how to implement centralized configuration management with Spring Cloud Config.
- They will be able to set up a Config Server and connect microservices to it for dynamic configuration management.

# Questions
1. What is service discovery, and why is it important in microservices architecture?
2. Explain the key differences between client-side and server-side load balancing.
3. Explain the role of the Eureka server and its client in the service discovery process.
4. What is an API Gateway, and how does Zuul provide routing functionality for microservices?
5. Describe the process of configuring routes in a Zuul API Gateway.
6. What are the advantages of using centralized configuration management in microservices?
7. How does Spring Cloud Config Server work, and what are its main components?
8. Explain how microservices can retrieve configuration properties from the Spring Cloud Config Server.
9. What is the significance of using a Git repository for centralized configuration management?
10. Discuss the best practices for implementing service discovery, routing, and centralized configuration in Spring Boot applications.

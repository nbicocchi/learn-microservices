# Labs

## Lab 1: Managing Dependencies with Maven
**Objective:** Learn to use Maven for project management and dependency management in a Java application.

**Instructions:**
- Create a new Maven project using [Spring Initializr](https://start.spring.io/). 
- Add dependencies for Spring Boot (e.g., `spring-boot-starter-web`) and JUnit (e.g., `junit-jupiter`) to the `pom.xml` file.
- Create a simple RESTful web service that returns a greeting message at the `/greet` endpoint.
- Use Maven commands (`mvn clean`, `mvn install`) to build the project and ensure all dependencies are resolved correctly.
- Use Maven the show the dependency tree.

## Lab 2: Building a Spring Boot Application
**Objective:** Develop a Spring Boot application with various components and configurations.

**Instructions:**
- Create a new Spring Boot application using the [Spring Initializr](https://start.spring.io/), selecting appropriate dependencies (e.g., Spring Web, Spring Data JPA).
- Implement a simple RESTful API with CRUD operations for managing a resource (e.g., `User`).
- Configure the application properties (i.e., PostgreSQL database connection) in `application.yml`.
- Use Maven to build and run the Spring Boot application locally.
- Demonstrate how to build a Docker image using both the Jib Maven Plugin and `docker buildx` command.
- Run both the PostgreSQL and your microservice with `docker compose`. 

## Lab 3: Performance Testing with JMeter
**Objective:** Use JMeter to perform load testing on a Spring Boot application.

**Instructions:**
- Install JMeter and create a new test plan for the Spring Boot application developed in Lab 2.
- Configure thread groups to simulate multiple users accessing the RESTful API endpoints.
- Set up various samplers (e.g., HTTP Request) to test different endpoints and capture response times.
- Run the load test and analyze the results, focusing on response times, throughput, and error rates.

# Questions
1. What is Maven, and what role does it play in Java project management?
2. How do you define dependencies in a Maven `pom.xml` file, and what are the different scopes available?
3. Explain the lifecycle phases of a Maven build process and their significance.
4. What are the benefits of using Spring Boot for developing microservices?
5. How do you create a RESTful web service using Spring Boot, and what annotations are commonly used?
6. Describe the role of application properties in configuring a Spring Boot application and its profiles.
7. Describe are helpful in managing the different phases of software life, from development to production.
8. Describe how actuators are helpful in managing a microservice in production.
9. Describe the key features of the Spring Data library and more specifically of JPA.
10. What is JMeter, and how is it used for performance testing of web applications?
11. How can Dependency Injection and the Dependency Inversion Principle facilitate modularity in a monolithic application?
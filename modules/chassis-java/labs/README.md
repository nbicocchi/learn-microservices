# Labs

## Lab 1: Managing Dependencies with Maven
**Objective:** Learn to use Maven for project management and dependency management in a Java application.

**Instructions:**
- Create a new Maven project using [Spring Initializr](https://start.spring.io/). 
- Add `lombok`, `spring-boot-starter-web` and `spring-boot-starter-test` to the project dependencies.
- Create a REST controller that returns a greeting message at the `/greet` endpoint (GET).
- Use Maven commands (`mvn clean`, `mvn package`, `mvn dependency:tree`) to build the project and ensure all dependencies are resolved correctly.

## Lab 2: Building a Spring Application
**Objective:** Develop a Spring Boot application with various components and configurations.

**Instructions:**
- Create a new Spring Boot application using the [Spring Initializr](https://start.spring.io/), selecting appropriate dependencies (e.g., Spring Web, Spring Data JPA, etc).
- Configure `application.yml` and `pom.xml` to run the service on port 9000 and establish a connection with an H2 in-memory database.
- Implement a `/greet` endpoint returning the application name and version (fetched from application.yml).
- Implement a `/sleep` endpoint sleeping 100ms.
- Implement CRUD operations for managing the resource below. Each time a CRUD operation is requested the application should log the event at the `debug` level.

```java
public class User {
    Long id;
    String email;
    String name;
    String country;
}
```

- Use Maven to build and run the Spring Boot application locally.
- Use curl to test the REST endpoints.

## Lab 3: Performance Testing with JMeter
**Objective:** Use JMeter to perform load testing on a Spring Boot application.

**Instructions:**
- Install JMeter and create a new test plan for the Spring Boot application developed in Lab 2.
- Configure thread groups to simulate multiple users accessing the RESTful API endpoints and capture response times. Is there a noticeable difference between request involving H2 (e.g. `/users`), `/sleep`, and `/greet` ?

# Questions
1. What is Maven, and what role does it play in Java project management?
2. How are dependencies defined in a Maven `pom.xml` file? Provide an example of adding a dependency to a project.
3. Explain the concepts of lifecycles, phases, and goals in the Maven build process.
4. What is a fat JAR, and why is it beneficial for production use?
5. Is it advisable to run a project using `mvn spring-boot:run` in a production environment? Why or why not?
6. How does Spring Boot acquire configuration settings? Describe its hierarchical approach.
7. What is Project Lombok, and how does it simplify Java development?
8. What is inversion of control (IoC), and how does Spring implement it?
9. What is a bean in Spring? What are the two most common ways to define beans? Explain the purpose of the `@Primary` and `@Qualifier` annotations.
10. What is the role of application properties in configuring a Spring Boot application and its profiles? What is the key advantage of using properties instead of hardcoded configurations? How do these advantages align with the 12-Factor App methodology?
11. How does a logging system work, and how can log levels be used effectively during both development and production?
12. How do Spring Boot Actuators help in managing a microservice in a production environment?
13. What are the key features of Spring Data JPA?
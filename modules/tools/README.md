# Labs

## Lab 1: Managing Dependencies with Maven
**Objective:** Learn to use Maven for project management and dependency management in a Java application.

**Instructions:**
- Create a new Java project using Maven by generating a simple archetype (e.g., `maven-archetype-quickstart`).
- Add dependencies for Spring Boot (e.g., `spring-boot-starter-web`) and JUnit (e.g., `junit-jupiter`) to the `pom.xml` file.
- Create a simple RESTful web service using Spring Boot that returns a greeting message.
- Use Maven commands (`mvn clean`, `mvn install`) to build the project and ensure all dependencies are resolved correctly.

**Expected Outcomes:**
- Students will understand how to set up a Maven project and manage dependencies effectively.
- They will gain experience in building a Spring Boot application using Maven.

## Lab 2: Building a Spring Boot Application
**Objective:** Develop a Spring Boot application with various components and configurations.

**Instructions:**
- Create a new Spring Boot application using the Spring Initializr, selecting appropriate dependencies (e.g., Spring Web, Spring Data JPA).
- Implement a simple RESTful API with CRUD operations for managing a resource (e.g., `User`).
- Configure the application properties (e.g., database connection settings) in `application.yml`.
- Use Maven to build and run the Spring Boot application locally.

**Expected Outcomes:**
- Students will learn how to build and configure a Spring Boot application from scratch using Maven.
- They will gain hands-on experience with RESTful API development and data persistence.

## Lab 3: Performance Testing with JMeter
**Objective:** Use JMeter to perform load testing on a Spring Boot application.

**Instructions:**
- Install JMeter and create a new test plan for the Spring Boot application developed in Lab 2.
- Configure thread groups to simulate multiple users accessing the RESTful API endpoints.
- Set up various samplers (e.g., HTTP Request) to test different endpoints and capture response times.
- Run the load test and analyze the results, focusing on response times, throughput, and error rates.

**Expected Outcomes:**
- Students will understand how to use JMeter for performance testing of web applications.
- They will learn to analyze load testing results and identify performance bottlenecks.

# Questions
1. What is Maven, and what role does it play in Java project management?
2. How do you define dependencies in a Maven `pom.xml` file, and what are the different scopes available?
3. Explain the lifecycle phases of a Maven build process and their significance.
4. What are the benefits of using Spring Boot for developing Java applications?
5. How do you create a RESTful web service using Spring Boot, and what annotations are commonly used?
6. Describe the role of application properties in configuring a Spring Boot application.
7. What is JMeter, and how is it used for performance testing of web applications?
8. How do you create a test plan in JMeter, and what are its key components?
9. Discuss the importance of load testing and its impact on application performance.
10. What best practices should be followed when managing dependencies and building projects with Maven and Spring Boot?

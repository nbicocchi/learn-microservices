# Containerization

## Labs
Here’s the combined version of the three labs using **Java** for consistency:

### Lab 1: Writing a Dockerfile for a Java Environment
**Objective:** Create a Dockerfile that sets up a container with a Java environment.

**Instructions:**
- Write a Dockerfile that uses an official `openjdk` base image (e.g., `openjdk:17-jdk`).
- Add a simple Java application (`HelloWorld.java`) that prints "Hello, Docker!".
- Compile the Java file using `javac` within the Docker container.
- Run the compiled Java program inside the container.

**Expected Outcomes:**
- Students should be able to write and build a Dockerfile to create a Java environment.
- Verify the environment by running `java HelloWorld` inside the container to print the expected output.


### Lab 2: Writing a Docker Compose File for a Java Spring Boot Web Application and Database
**Objective:** Write a Docker Compose file to define two containers: one for a Java Spring Boot web application and another for a PostgreSQL database.

**Instructions:**
- Write a `docker-compose.yml` file that defines a Spring Boot web application (using `openjdk` image) and a PostgreSQL database.
- The Spring Boot application should connect to the PostgreSQL database and retrieve data (e.g., customer info).
- Define networking between the two containers and use volumes to persist PostgreSQL data.
- Use Docker Compose to start the containers and test the application.

**Expected Outcomes:**
- Students should be able to write a Docker Compose file to manage a multi-container application.
- They should connect the Spring Boot app to the database, query the database, and ensure data persistence using Docker volumes.

### Lab 3: Resource Limiting and Monitoring for Java Containers
**Objective:** Set CPU and memory limits on a Java container and monitor its usage.

**Instructions:**
- Create a Dockerfile that runs a Java application performing a resource-intensive task (e.g., calculating large Fibonacci numbers or processing large datasets).
- Write a Docker Compose file to run the container with CPU and memory limits using the `cpus` and `mem_limit` directives.
- Start the containers and use `docker stats` to monitor their resource usage and verify that the limits are applied.

**Expected Outcomes:**
- Students should understand how to define CPU and memory limits for Java containers.
- They will monitor resource consumption using `docker stats` and evaluate how limiting resources affects container performance.

## Relevant Questions
1. What are the main differences between bare-metal, virtual machines, and container-based deployments?
2. Can you define CPU and memory limits for a container? If so, how is it done?
3. Describe the life cycle of a container from creation to termination.
4. What are the key directives in a Dockerfile, and what roles do they play in container creation?
5. What are the essential directives in a Docker Compose file, and how do they contribute to multi-container applications?
6. Why are smaller container images preferable in CI/CD pipelines?
7. What is container orchestration, and why is it important in managing containerized applications?
8. How does Docker handle networking for containers, and what are the different networking modes available?
9. What is a Docker registry, and how does it facilitate the distribution and management of container images?
10. Explain the role of volumes in Docker and how they differ from bind mounts in managing data persistence.
# Labs

## Lab 1: Writing a Dockerfile for a Java Environment
**Objective:** Create a Dockerfile that sets up a Docker image containing a Java application.

**Instructions:**
- Write a Dockerfile that uses an official `openjdk` base image (e.g., `openjdk:21-jdk`).
- Add a simple Java application (built as a jar artifact) that prints "Hello, Docker!".
- Run the Java application whenever the container is started.
- Build another image producing the same output (prints "Hello, Docker!") but using Python.

## Lab 2: Writing a Docker Compose File for a Java Spring Boot Web Application and Database
**Objective:** Write a Docker Compose file to define two containers: one for a Java Spring Boot web application and another for a PostgreSQL database.

**Instructions:**
- Write a `docker-compose.yml` file that defines a Spring Boot web application and a PostgreSQL database.
- The Spring Boot application should connect to the PostgreSQL database and retrieve data (e.g., customer info).
- Define networking between the two containers (the application has to be exposed while PostgreSQL runs inside the Docker network) and use volumes to persist PostgreSQL data.
- Use Docker Compose to start the containers and test the application.

## Lab 3: Resource Limiting and Monitoring for Java Containers
**Objective:** Set CPU and memory limits on a Java container and monitor its usage.

**Instructions:**
- Write a Docker Compose file to run the Spring Boot application of Lab 2, with CPU and memory limits applied.
- Start the containers and use `docker stats` to monitor their resource usage and verify that the limits are applied.

# Questions
1. What are the main differences between bare-metal, virtual machines, and container-based deployments? Explain the concept of deployment density.
2. What is a Docker registry, and how does it facilitate the distribution and management of container images?
3. Describe the life cycle of a container from creation to termination.
4. Comment a (given) Dockerfile, and explain the key directives.
5. What are Jib and BuildPacks? Why are smaller images preferable?
6. Explain the role of externalized storage in Docker containers and in which ways it can be implemented.
7. Can you define CPU and memory limits for a container? Why is it useful?
8. Comment a (given) docker-compose.yml file, and explain the key directives.
9. What is container orchestration, and why is it important in managing distributed applications?

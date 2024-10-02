# Introduction to Docker

Docker is an open-source platform that enables developers to automate the deployment, scaling, and management of applications within lightweight, portable containers. This guarantees that applications run consistently across different environments, regardless of the underlying machine's customized settings. Consequently, developers can write code and test it in a container that behaves the same way on any machine, leading to fewer deployment issues.

## Docker Architecture
![](images/docker-architecture.avif)

The Docker architecture consists of several key components that work together to create and manage containers effectively:

- **Docker CLI**: The Command Line Interface (CLI) is the primary way users interact with Docker. It allows users to issue commands for managing Docker containers, images, networks, and volumes. The CLI serves as the user-friendly front end for developers to communicate with the underlying Docker Daemon.

- **Socket**: The socket acts as the communication bridge between the Docker CLI and the Docker Daemon. It enables the CLI commands to be transmitted to the Daemon, which then executes the requested actions.

- **Docker Daemon (Docker Engine)**: The core component of Docker that runs on the host machine. The Daemon is responsible for creating, running, and managing containers. It listens for Docker API requests and manages Docker objects such as images, containers, networks, and volumes.

- **Applications**: Applications that run inside containers are isolated from each other. Multiple applications can run concurrently within their containers, providing a robust environment for development and production.

- **Images**: Docker images are lightweight, standalone, and executable packages that include everything needed to run a piece of software, such as the application code, runtime, libraries, environment variables, and configuration files. Images are immutable and can be versioned.

- **Namespaced Process**: Containers run as namespaced processes, which means that they operate in isolated environments. Namespaces provide distinct views of system resources (such as process IDs, network access, user IDs, and file systems) for each container. This isolation ensures that containers do not interfere with one another or the host system.

## Key Elements
- **Dockerfile**: A Dockerfile is a script containing a series of commands to build a Docker image. It specifies the base image, the commands to install dependencies, the files to copy, environment variables to set, and the command to run the application. The Dockerfile serves as a blueprint for creating images.

- **docker-compose.yml**: Docker Compose is a tool that simplifies the management of multi-container Docker applications through a YAML configuration file. This file defines the services (containers) that comprise your application, including their configurations, such as images to use, ports to expose, network settings, and volume mounts. With Docker Compose, you can start and manage all services with a single terminal command, making it easier to replicate and manage complex applications across different environments.

## Terminal Commands (Bash)

The Docker CLI provides several commands for managing images and containers. Here are some of the most important commands:

### Managing Images

- **Build an Image**:
  ```bash
  docker build -t <image-name> <context>
  ```
  - **Purpose**: Creates a new Docker image from a Dockerfile located in the specified context (directory).
  - **Example**:
    ```bash
    docker build -t my_app .
    ```
    This command builds an image named `my_app` using the Dockerfile in the current directory.

- **Pull an Image**:
  ```bash
  docker pull <image-name>
  ```
  - **Purpose**: Downloads a specified image from a Docker registry, such as Docker Hub.
  - **Example**:
    ```bash
    docker pull ubuntu:20.04
    ```
    This command pulls the `ubuntu:20.04` image from Docker Hub.

### Managing Containers

- **Run a Container**:
  ```bash
  docker run <image-name>
  ```
  - **Purpose**: Creates and starts a container from the specified image.
  - **Example**:
    ```bash
    docker run -it ubuntu:20.04
    ```
    This command runs an interactive terminal in a new container based on the `ubuntu:20.04` image.

- **List Running Containers**:
  ```bash
  docker ps
  ```
  - **Purpose**: Displays a list of currently running containers.
  - **Example**:
    ```bash
    docker ps -a
    ```
    The `-a` option shows all containers, including those that are stopped.

- **Start and Stop a Container**:
  - **Start**:
    ```bash
    docker start <container-id>
    ```
    - **Purpose**: Starts a previously created (but stopped) container.

  - **Stop**:
    ```bash
    docker stop <container-id>
    ```
    - **Purpose**: Stops a running container.

- **Remove a Container**:
  ```bash
  docker rm <container-id>
  ```
  - **Purpose**: Deletes a specified container. The container must be stopped before it can be removed.

- **Execute a Command in a Running Container**:
  ```bash
  docker exec -it <container-id> bash
  ```
  - **Purpose**: Runs a specified command inside a running container. The `-it` flags enable an interactive terminal session.
  - **Example**:
    ```bash
    docker exec -it my_container bash
    ```
    This command opens an interactive Bash shell inside the container named `my_container`.

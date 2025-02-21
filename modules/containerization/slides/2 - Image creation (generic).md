# Image Creation

## Images and layers

Docker images are a core concept in containerization and form the foundation for deploying applications in containers. They are essentially immutable snapshots of an application and its environment, consisting of everything needed to run the application, including code, runtime, libraries, environment variables, configuration files, and dependencies.

Docker images are constructed in a **layered format**, where each layer represents a set of changes (or a filesystem delta). This layered structure provides efficiency, portability, and reuse. Layers can be inspected with specific tools such as [dive](https://github.com/wagoodman/dive).

**Base Layer**

Every Docker image begins with a **base layer**, which typically includes the operating system or minimal components necessary to run an application. For example, it could be a minimal **Alpine Linux** or **Ubuntu** image. Base layers are often pulled from public Docker registries like **Docker Hub**.

**Intermediate Layers**

Subsequent layers in the image add modifications, such as installing software packages, setting environment variables, or copying files. These layers correspond to the instructions in the Dockerfile that describe how to build the image. Each line or instruction in the Dockerfile (e.g., `RUN`, `COPY`, `ADD`, `EXPOSE`) creates a new layer.

For example, the following Dockerfile would create the following image layers:

1. **Base Layer**: The `ubuntu:20.04` image is the base.
2. **Intermediate Layer**: The result of running `apt-get update && apt-get install -y python3`.
3. **Intermediate Layer**: The result of copying the contents of the local directory to `/app`.
4. **Final Layer**: The layer resulting from setting the default command (`CMD ["python3", "/app/myapp.py"]`).

```dockerfile
FROM ubuntu:20.04
RUN apt-get update && apt-get install -y python3
COPY . /app
CMD ["python3", "/app/myapp.py"]
```

**Read-Only Layers**: Once built, all the layers in a Docker image are **read-only**. When the image is used to start a container, Docker combines these read-only layers into a unified view using a **Union File System** (like **OverlayFS**), allowing them to act as a single entity.

**Layer Caching**: Docker caches layers to make builds faster. If a Dockerfile instruction has not changed, Docker reuses the previously built layer, improving build performance.

**Layer Reuse**: Because layers are independent and reusable, multiple images can share common layers. For instance, if two images are built on the same base image (e.g., Ubuntu), they can reuse the base layer, reducing storage needs and speeding up deployment.

**Copy-on-Write in Containers**: When you run a Docker container from an image, Docker adds a **writable layer** on top of the read-only layers. Any changes made to the container, such as modifying files or writing logs, are stored in this top writable layer. However, this writable layer is ephemeral, and once the container is deleted, any changes are lost unless committed to a new image.





## Key commands

### `FROM`

The `FROM` instruction specifies the base image from which your image will be built. It’s usually the first line in any Dockerfile.

```dockerfile
FROM ubuntu:20.04
```

This command sets the base image to Ubuntu 20.04. The base image provides the environment for the application, and it is important to choose one that includes all necessary libraries and dependencies.

Here are the descriptions for **ARG** and **ENTRYPOINT** following the same format:

### `ARG`
 
The `ARG` instruction defines a variable that users can pass at build time to the Docker image with the `--build-arg` flag. ARG variables are only available during the build process and cannot be accessed after the image is built.

Example:
```dockerfile
ARG NODE_VERSION=14
```

This command defines a build-time variable `NODE_VERSION` with a default value of 14. You can override this value when building the image by using `--build-arg NODE_VERSION=16`.

### `COPY`

The `COPY` instruction copies files or directories from your local machine into the Docker image.

```dockerfile
COPY . /app
```

This command copies everything from the current directory on your local machine to the `/app` directory in the Docker image. It is commonly used to transfer application code and resources.

### `RUN`

The `RUN` instruction executes commands inside the image during the build process, typically used for installing software or setting up your environment.

```dockerfile
RUN apt-get update && apt-get install -y python3
```

This command updates the package list and installs Python 3 inside the image. Multiple commands can be combined into a single `RUN` instruction to reduce the number of layers in the image.

### `CMD`

The `CMD` instruction specifies the command that will run when the container starts. It is typically written in the exec form, which is an array format: `CMD ["executable", "param1", "param2"]`.

```dockerfile
CMD ["python3", "app.py"]
```

This command runs `app.py` using Python 3 when the container starts.

> **NOTE**: The key difference between `CMD` and `RUN` is the timing of the command execution. `RUN` is executed during the build time, while `CMD` is executed during container startup.
> **IMPORTANT**: There can only be one `CMD` instruction in a Dockerfile. If you list more than one `CMD`, only the last one takes effect.

### `CMD-SHELL`

The `CMD-SHELL` form of `CMD` allows you to specify a command in a single string format rather than an array. It runs the command in a shell, typically `/bin/sh -c` on Linux and `cmd /S /C` on Windows. This form is useful if you need shell features like variable substitution, piping, or chaining commands.

```dockerfile
CMD python3 app.py
```

In this example, the command is interpreted by the shell, so `python3 app.py` is executed within the container’s shell environment.

### `ENTRYPOINT`
The `ENTRYPOINT` instruction specifies the command that will run as the main process of the container. Unlike the `CMD` instruction, `ENTRYPOINT` commands are not overridden when the container is started with additional arguments. It is used to ensure the container runs the specified process by default.

Example:
```dockerfile
ENTRYPOINT ["python", "app.py"]
```

This command makes `python app.py` the default process to run whenever the container starts. Any arguments passed during container startup will be appended to the **ENTRYPOINT** command.

### `WORKDIR`

The `WORKDIR` instruction sets the working directory inside the image. All subsequent instructions will be executed from this directory.

```dockerfile
WORKDIR /app
```

This command sets the working directory to `/app`. If the directory does not exist, it will be created automatically.

### `EXPOSE`

The `EXPOSE` instruction tells Docker which port your application will use so that it can be accessed from outside the container.

```dockerfile
EXPOSE 8080
```

This exposes port 8080 for access. Note that `EXPOSE` does not publish the port; it merely indicates that the application will listen on that port.

### `ENV`

The `ENV` instruction sets environment variables in the container. These variables can be used by applications or scripts running inside the container.

```dockerfile
ENV ENVIRONMENT=production
```

### `USER`

The `USER` instruction sets the username or UID that will run the subsequent commands in the container. It is used to specify which user the processes inside the container will run as.

```dockerfile
USER appuser
```

This command sets the `ENVIRONMENT` variable to `production`. Environment variables can influence the behavior of applications within the container.

## Creating a Docker Image for a Simple Python Application

### Step 1: Write the Python Application

Create a file named `app.py` with the following content:

```python
# app.py
print("Hello, Docker!")
```

### Step 2: Create the Dockerfile

Create a `Dockerfile` in the same directory:

```Dockerfile
# Use an official Python runtime as a parent image
FROM python:3.9-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Run the command to execute the Python script
CMD ["python", "app.py"]
```

### Step 3: Build the Docker Image

Run the following command in your terminal:

```bash
docker buildx build -t python-app:latest .
```

### Step 4: Run the Docker Container

```bash
docker run python-app
```

**Expected Output**: `Hello, Docker!`


## Creating a Docker Image for a Simple Java Application

### Step 1: Write the Java Application

Create a file named `App.java` with the following content:

```java
// App.java
public class App {
    public static void main(String[] args) {
        System.out.println("Hello, Docker!");
    }
}
```

### Step 2: Create the Dockerfile

Create a `Dockerfile` in the same directory:

```Dockerfile
# OpenJDK runtime as a parent image
FROM eclipse-temurin:21

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Compile the Java program
RUN javac App.java

# Run the Java program
CMD ["java", "App"]
```

### Step 3: Build the Docker Image

```bash
docker buildx build -t java-app:latest .
```

### Step 4: Run the Docker Container

```bash
docker run java-app
```

**Expected Output**: `Hello, Docker!`


## Creating a Docker Image with the `ls` Command

### Step 1: Create the Dockerfile

Create a `Dockerfile`:

```Dockerfile
# Use an official Alpine Linux image as a parent image
FROM alpine:3.18

# Run the ls command
CMD ["ls", "-al"]
```

### Step 2: Build the Docker Image

```bash
docker buildx build -t ls-command:latest .
```

### Step 3: Run the Docker Container

```bash
docker run ls-command
```

```bash
docker run -it --entrypoint=/bin/sh ls-command
```

**Expected Output**: A detailed list of the root directory contents.


## Creating a Docker Image for a Flask Echo Server

In this section, we will create and containerize a simple Echo server using Flask, which echoes back any data sent to it.

### Step 1: Create a Dockerfile

Create a `Dockerfile` with the following content:

```Dockerfile
# Use the official Python image
FROM python:3.9-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the current directory contents into the container
COPY . .

# Install the required packages
RUN pip install -r requirements.txt

# Define the command to run the application
CMD ["python", "app.py"]
```

This Dockerfile does the following:
1. Uses a lightweight Python image.
2. Sets the working directory.
3. Copies the current directory contents into the container.
4. Installs dependencies from `requirements.txt`.
5. Runs `app.py` when the container starts.

### Step 2: Build the Docker Image

Build the Docker image:

```bash
docker buildx build -t echo-server-flask:latest .
```

### Step 3: Run the Docker Container

Run the container:

```bash
docker run -p 5000:5000 echo-server-flask
```

This command maps port 5000 on your host to port 5000 in the container, allowing you to access the Flask app.

### Step 4: Test the Echo Server

Test the Echo server using a tool like `curl` or Postman.

```bash
curl -X POST http://localhost:5000/echo -H "Content-Type: application/json" -d '{"message": "Hello, Echo Server!"}'
```

## Creating a Docker Image for a Spring Boot Echo Server

In this section, we will create and containerize a simple Echo server using Flask, which echoes back any data sent to it.

### Step 1: Create a Dockerfile

Create a `Dockerfile` with the following content:

```Dockerfile
FROM eclipse-temurin:21
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
```

This Dockerfile does the following:
1. Uses Eclipse Temurin image.
2. Sets the JAR_FILE local variable representing the application artifact.
3. Copies the application artifact into the container.
4. Runs `java -jar application.jar` when the container starts.

### Step 2: Build the Docker Image

Build the Docker image:

```bash
mvn clean package -Dmaven.test.skip=true
docker buildx build -t echo-server-java:latest .
```

### Step 3: Run the Docker Container

Run the container:

```bash
docker run -p 5000:5000 echo-server-java
```

This command maps port 5000 on your host to port 5000 in the container, allowing you to access the Flask app.

### Step 4: Test the Echo Server

Test the Echo server using a tool like `curl` or Postman.

```bash
curl -X POST http://localhost:5000/echo -H "Content-Type: application/json" -d '{"message": "Hello, Echo Server!"}'
```



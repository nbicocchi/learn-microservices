# Image Creation

A `Dockerfile` is a simple text file that contains a series of instructions on how to build a Docker image. Each instruction in a Dockerfile adds a layer to the image, creating a lightweight and reusable environment for running applications. This section introduces the essential commands used in Dockerfiles for building images.

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
FROM eclipse-temurin:21-jre-ubi9-minimal

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

### Step 1: Set Up the Flask Echo Server

Create a project directory:

```bash
mkdir flask-echo-server
cd flask-echo-server
```

### Step 2: Create a Python File

Create a Python file named `app.py` with the following content:

```python
from flask import Flask, request, jsonify

app = Flask(__name__)

# Route to echo the received data
@app.route('/echo', methods=['POST'])
def echo():
    data = request.json
    return jsonify({"echoed_data": data})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

This simple Flask application defines a POST endpoint (`/echo`). It expects JSON data and echoes back the same data.

### Step 3: Create a `requirements.txt` File

In the same directory, create a `requirements.txt` file to list the dependencies for the project. For this project, we need Flask:

```bash
Flask
```

### Step 4: Create a Dockerfile

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

# Expose port 5000 for the Flask app
EXPOSE 5000

# Define the command to run the application
CMD ["python", "app.py"]
```

This Dockerfile does the following:
1. Uses a lightweight Python image.
2. Sets the working directory.
3. Copies the current directory contents into the container.
4. Installs dependencies from `requirements.txt`.
5. Exposes port 5000 for Flask.
6. Runs `app.py` when the container starts.

### Step 5: Build the Docker Image

Build the Docker image:

```bash
docker buildx build -t flask-echo-server:latest .
```

### Step 6: Run the Docker Container

Run the container:

```bash
docker run -p 5000:5000 flask-echo-server
```

This command maps port 5000 on your host to port 5000 in the container, allowing you to access the Flask app.

### Step 7: Test the Echo Server

Test the Echo server using a tool like `curl` or Postman.

#### Using `curl`:

```bash
curl -X POST http://localhost:5000/echo -H "Content-Type: application/json" -d '{"message": "Hello, Echo Server!"}'
```

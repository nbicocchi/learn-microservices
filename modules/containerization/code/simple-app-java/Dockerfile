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
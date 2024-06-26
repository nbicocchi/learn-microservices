FROM openjdk:21-jdk
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "application.jar"]

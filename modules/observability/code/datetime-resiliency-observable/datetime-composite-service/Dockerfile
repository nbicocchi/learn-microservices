FROM eclipse-temurin:21
ARG JAR_FILE=target/*.jar
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar
COPY ${JAR_FILE} application.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/opentelemetry-javaagent.jar"
ENTRYPOINT ["java","-jar","/application.jar"]
FROM eclipse-temurin:latest
WORKDIR /application
ADD ./build/libs/*.jar app.jar

ENTRYPOINT ["java", "-cp", "/application/app.jar", "cloud.macca.aggregator.Main"]
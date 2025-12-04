# Properties

## Properties
In Spring Boot, `src/manin/resources/application.yml` configures the application settings. YAML (YAML Ain’t Markup Language) provides a structured and human-readable way of defining hierarchical configurations.

```yaml
server:
  port: 7000

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: user
    password: pass

logging:
  level:
    root: INFO
    com:
      example: DEBUG
```

## Common Application Properties

1. Server Configuration:
   ```yaml
   server:
     port: 7000
   ```

2. Datasource Configuration:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/mydb
       username: myuser
       password: mypass
   ```

3. Logging Configuration:
   ```yaml
   logging:
     level:
     root: INFO
     com:
       example: DEBUG
   ```

See [here](https://docs.spring.io/spring-boot/appendix/application-properties/index.html) the **long** list of common application properties.


## Custom Properties

Custom properties allow you to define application-specific settings that are loaded from external configuration files such as `application.yml`. These properties can then be injected into your beans using the `@Value` annotation. 

**This approach decouples configuration from code, making the application easier to maintain**.

### Defining Custom Properties

These properties can be anything that is required by your application, such as API keys, URLs, timeouts, or any other configuration data.

```yaml
server:
  port: 7000
  
spring:
  main:
    banner: "off"

app:
  name: My Application
  version: 1.0.0
  max-users: 1000
```

### Injecting Custom Properties with `@Value`

The `@Value` annotation allows you to inject the values of these custom properties directly into your beans.

```java
package com.nbicocchi.beans;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppService {
    private final String appName;
    private final String appVersion;
    private final Integer maxUsers;

    public AppService(
            @Value("${app.name}") String appName,
            @Value("${app.version}") String appVersion,
            @Value("${app.max-users}") Integer maxUsers) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.maxUsers = maxUsers;
    }

    @PostConstruct
    public void printAppDetails() {
        System.out.println("Application Name: " + appName);
        System.out.println("Version: " + appVersion);
        System.out.println("Max Users: " + maxUsers);
    }
}
```

The constructor of AppService takes three arguments: appName, appVersion, and maxUsers. These values are injected from the `application.yml` file using the `@Value` annotation.


## Profiles

**Profiles** provide a way to segregate parts of your application configuration and make it adaptable to different environments, such as **development**, **testing**, **staging**, and **production**. 


```yaml
server:
  port: 7000

app:
  name: My Application
  version: 1.0.0
  max-users: 1000

---
spring.config.activate.on-profile: docker
server:
  port: 8080

---
spring.config.activate.on-profile: no-banner
spring:
   main:
    banner-mode: off
```

In this file:
- The default profile (active if no other profile is activated) has basic configuration (`server.port`, `app.name`, `app.version`, `app.max-users`).
- When the **docker** profile is active, it overrides the `server.port` property.
- When the **no-banner** profile is active, it disables the startup banner.


## Activating Profiles

### `application.yml`
You can specify which profile is active by setting the `spring.profiles.active` property in the `application.yml` file.

```yaml
spring:
  profiles:
    active: docker
```

### Command-Line Arguments
You can activate a profile when starting your application by passing the `--spring.profiles.active` argument in the command line.

```bash
mvn clean package -Dmaven.skip.test=true

java -jar target/properties-0.0.1-SNAPSHOT.jar --spring.profiles.active=docker,no-banner
```

### Environment Variables
You can also set the `spring.profiles.active` property as an environment variable:

```bash
mvn clean package -Dmaven.skip.test=true

SPRING_PROFILES_ACTIVE=docker,no-banner java -jar target/properties-0.0.1-SNAPSHOT.jar
```

### Environment Variables (Docker Compose)

Define a docker-compose.yml file

```yaml
services:
   properties:
      build: .
      environment:
         - SPRING_PROFILES_ACTIVE=docker,no-banner
```

```bash
mvn clean package -Dmaven.test.skip=true
docker compose up --build -d
```

### Programmatically
You can also set the active profile programmatically by calling `setAdditionalProfiles()` in the `SpringApplication` object.

```java
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.setAdditionalProfiles("dev");
        app.run(args);
    }
}
```

## Resources
- [Properties with Spring and Spring Boot](https://www.baeldung.com/properties-with-spring)
- [Spring Profiles](https://www.baeldung.com/spring-profiles)
# Properties

## Properties
In Spring Boot, `src/manin/resources/application.yml` configures the application settings. YAML (YAML Ain’t Markup Language) provides a structured and human-readable way of defining hierarchical configurations compared to the previous key-value format of `application.properties`.

Here's an example of a basic `application.yml` file:

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

## Standard Properties

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

## Custom Properties

In **Spring Boot**, custom properties allow you to define application-specific settings that are loaded from external configuration files such as `application.yml`. These properties can then be injected into your Spring-managed beans using the `@Value` annotation. This approach decouples configuration from code, making the application more flexible and easier to maintain.

### Defining Custom Properties

You can define custom properties in `application.yml` files. These properties can be anything that is required by your application, such as API keys, URLs, timeouts, or any other configuration data.

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

The `@Value` annotation allows you to inject the values of these custom properties directly into fields in your Spring-managed components such as `@Service`, `@Controller`, `@Component`, etc.

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

* `@Value("${app.name}")`: Injects the value of the app.name property.
* `@Value("${app.version}")`: Injects the value of the app.version property.
* `@Value("${app.max-users}")`: Injects the value of the app.max-users property.

This approach uses constructor injection, which is a recommended practice in Spring as it makes the dependencies explicit and supports immutability (since fields are final).

## Profiles

In **Spring Boot**, **profiles** provide a way to segregate parts of your application configuration and make it adaptable to different environments, such as **development**, **testing**, **staging**, and **production**. By using profiles, you can define multiple configurations for different environments and switch between them easily, depending on the context in which the application is running.

* **Profile-specific properties**: You can define different property files for different profiles.
* **Profile activation**: Profiles can be activated via environment variables, command-line arguments, or within code.
* **Conditional Beans**: You can define beans that should only be loaded for specific profiles using the `@Profile` annotation.

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
- When the **no-banner** profile is active, it disables the Spring Boot startup banner.

If no profile is explicitly activated, Spring Boot uses the configuration defined in the main `application.yml` (default profile).


## Activating Profiles

Profiles can be activated in various ways:

### Via `application.yml`
You can specify which profile is active by setting the `spring.profiles.active` property in the `application.yml` file.

```yaml
spring:
  profiles:
    active: docker
```

### Via Command-Line Arguments
You can activate a profile when starting your application by passing the `--spring.profiles.active` argument in the command line.

```bash
$ mvn clean package
$ java -jar target/properties-0.0.1-SNAPSHOT.jar --spring.profiles.active=docker,no-banner
```

This will activate the **production** profile.

### Via Environment Variables
You can set the `spring.profiles.active` property as an environment variable:

```bash
$ mvn clean package
$ SPRING_PROFILES_ACTIVE=docker,no-banner java -jar target/properties-0.0.1-SNAPSHOT.jar --spring.profiles.active=docker,no-banner
```

### Via Environment Variables (Docker)

```bash
$ mvn clean package
$ docker buildx build -t $(basename $(pwd)) .
$ docker run -e SPRING_PROFILES_ACTIVE='docker,no-banner' properties
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
- [Spring Environment JavaDoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/Environment.html)
- [Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
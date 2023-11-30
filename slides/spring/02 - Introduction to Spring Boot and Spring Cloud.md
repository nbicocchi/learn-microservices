# Introduction to Spring Boot and Spring Cloud

Sources: https://www.youtube.com/watch?v=Nv2DERaMx-4&t=15663s

## Spring Framework
Spring has become the most popular development framework for building Java-based applications. 

Spring is based on the concept of **dependency injection**. A dependency injection framework allows you to more efficiently manage large Java projects by externalizing the relationship between objects within your application through convention (and annotations) rather than hardcoding those objects to “know” about each other. Spring sits as an intermediary between the different Java classes of your application and manages their dependencies. Spring essentially lets you assemble your code like a set of Lego bricks that snap together.

**Spring Boot is a re-envisioning of the Spring framework**. While it embraces core features of Spring, Spring Boot strips away many of the “enterprise” features found in Spring and instead delivers a framework geared toward Java-based, REST-oriented (Representational State Transfer) microservices. 

The key features of Spring Boot include the following:
* An embedded web server to avoid complexity in deploying the application: Tomcat (default), Jetty, or Undertow.
* A suggested configuration to start quickly with a project (starters).
* An automatic configuration for Spring functionally—whenever it’s possible.
* A wide range of features ready for production (such as metrics, security, status verification, externalized configuration, and more).

Using Spring Boot offers the following benefits for our microservices:
* Reduces development time and increases efficiency and productivity
* Offers an embedded HTTP server to run web applications
* Allows you to avoid writing a lot of boilerplate code
* Facilitates integration with the Spring Ecosystem (includes Spring Data, Spring Security, Spring Cloud, and more)
* Provides a set of various development plugins

### Spring Features
* Core technologies: dependency injection, events, resources, i18n, validation, data binding, type conversion, SpEL, AOP.
* Testing: mock objects, TestContext framework, Spring MVC Test, WebTestClient.
* Data Access: transactions, DAO support, JDBC, ORM, Marshalling XML.
* Spring MVC and Spring WebFlux web frameworks.
* Integration: remoting, JMS, JCA, JMX, email, tasks, scheduling, cache and observability.
* Languages: Kotlin, Groovy, dynamic languages.

https://spring.io/projects

### Spring Boot Features
* **Spring Boot uses Spring behind the scenes and makes it easier to use**
* Minimize the amount of manual configuration
* Perform auto-configuration based on props files and JAR classpath 
* Help to resolve dependency conflicts (Maven or Gradle)
* Provide an embedded HTTP server (Tomcat, Jetty, Undertow, ...)

https://docs.spring.io/spring-boot/docs/current/reference/html/index.html


### Spring Initializr

* Quickly create a starter Spring Boot project Select your dependencies
* Creates a Maven/Gradle project
* Import the project into your IDE
* Eclipse, IntelliJ, NetBeans etc ...

https://start.spring.io/

![](../microservices/images/spring-initializr.png)

### Spring Boot Starters
Dependency management is a critical aspects of any complex project. And doing this manually is less than ideal; the more time you spent on it the less time you have on the other important aspects of the project.

Spring Boot starters were built to address exactly this problem. Starter POMs are a set of convenient dependency descriptors that you can include in your application. You get a one-stop-shop for all the Spring and related technology that you need, without having to hunt through sample code and copy-paste loads of dependency descriptors.

Benefits of using Spring Boot starters:
* increase pom manageability
* production-ready, tested & supported dependency configurations
* decrease the overall configuration time for the project

https://www.baeldung.com/spring-boot-starters

### Spring Boot Dev Tools

Spring Boot gives us the ability to quickly setup and run services.

To enhance the development experience further, Spring released the spring-boot-devtools tool – as part of Spring Boot-1.3.

* Property defaults
* Automatic Restart
* Live Reload
* Global settings
* Remote applications

https://www.baeldung.com/spring-boot-devtools

## Maven
The key features of Maven are:
* simple project setup that follows best practices: Maven tries to avoid as much configuration as possible, by supplying project templates (named archetypes)
* dependency management: it includes automatic updating, downloading and validating the compatibility, as well as reporting the dependency closures (known also as transitive dependencies)
* isolation between project dependencies and plugins: with Maven, project dependencies are retrieved from the dependency repositories while any plugin’s dependencies are retrieved from the plugin repositories, resulting in fewer conflicts when plugins start to download additional dependencies
* central repository system: project dependencies can be loaded from the local file system or public repositories, such as Maven Central

https://www.baeldung.com/maven

https://12factor.net/dependencies

### Maven Standard Directory Structure

```
└───maven-project
    ├───pom.xml
    ├───README.txt
    ├───NOTICE.txt
    ├───LICENSE.txt
    └───src
        ├───main
        │   ├───java
        │   └───resources
        │       ├───application.properties
        │       ├───static
        │       └───templates
        ├───test
        │   ├───java
        │   └───resources
        ├───it
        ├───site
        └───assembly
``````

### Maven Wrapper Files
* No need to have Maven installed or present on your path
* If correct version of Maven is NOT found on your computer, automatically downloads correct version and runs Maven
* Two files are provided 
    * mvnw.cmd for MS Windows
    * mvnw.sh for Linux/Mac


### Maven pom.xml
A Project Object Model or POM is the fundamental unit of work in Maven. It is an XML file that contains information about the project and configuration details used by Maven to build the project. It contains default values for most projects. When executing a task or goal, Maven looks for the POM in the current directory. It reads the POM, gets the needed configuration information, then executes the goal.

Some of the configuration that can be specified in the POM are the project dependencies, the plugins or goals that can be executed, the build profiles, and so on. Other information such as the project version, description, developers, mailing lists and such can also be specified.

https://maven.apache.org/guides/introduction/introduction-to-the-pom.html

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.nbicocchi</groupId>
	<artifactId>demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>demo</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

    ...

</project>

```

## Gradle

https://www.baeldung.com/gradle

### build.gradle
```
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.nbicocchi'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### Key commands

```
$ ./gradlew clean
$ ./gradlew build
$ ./gradlew test
$ ./gradlew bootRun
$ ./gradlew bootJar
```

## Lombok
Java is a great language, but it can sometimes get too verbose for common tasks we have to do in our code or compliance with some framework practices. This often doesn’t bring any real value to the business side of our programs, and that’s where Lombok comes in to make us more productive.

The way it works is by plugging into our build process and auto-generating Java bytecode into our .class files as per a number of project annotations we introduce in our code.

Including it in our builds, in whichever system we’re using, is very straight forward. Project Lombok’s project page has detailed instructions on the specifics. 

```
<dependencies>
    ...
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.20</version>
        <scope>provided</scope>
    </dependency>
    ...
</dependencies>
```

https://projectlombok.org/features/

https://www.baeldung.com/intro-to-project-lombok

## Configuration

https://www.baeldung.com/properties-with-spring

https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

https://12factor.net/config

### Application Properties
A common practice in Spring Boot is using an external configuration to define our properties. This allows us to use the same application code in different environments. **We can use properties files, YAML files, environment variables and command-line arguments.**

By default, Spring Boot can access configurations set in an application.properties file, which uses a key-value format:

```
server.port=8181
spring.main.banner-mode=off
```

Within our values, we can use placeholders with the ${} syntax to refer to the contents of other keys, system properties, or environment variables:

```
app.name=MyApp
app.description=${app.name} is a Spring Boot application
```

If we have the same kind of properties with different values, we can represent the list structure with array indices:

```
app.servers[0].ip=127.0.0.1
app.servers[0].path=/path1
app.servers[1].ip=127.0.0.2
app.servers[1].path=/path2
app.servers[2].ip=127.0.0.3
app.servers[2].path=/path3
```

If we don’t want determinist property values, we can use RandomValuePropertySource to randomize the values of properties:

```
random.number=${random.int}
random.long=${random.long}
random.uuid=${random.uuid}
```

### YAML Properties
As well as Java properties files, we can also use YAML-based configuration files in our Spring Boot application. YAML is a convenient format for specifying hierarchical configuration data.

```
server:
  port: 8181

spring:
  main:
    banner-mode: off

app:
  name: MyApp
  description: ${app.name} is a Spring Boot application
  version: ${random.int}
  serial: ${random.uuid}
  servers[0].ip: 127.0.0.1
  servers[0].path: /path1
  servers[1].ip: 127.0.0.2
  servers[1].path: /path2
  servers[2].ip: 127.0.0.3
  servers[2].path: /path3
```

### Accessing properties with @Value
```
@Log
@Component
public class InitRunnerv2 implements CommandLineRunner {
    @Value("${app.name}")
    String name;
    @Value("${app.description}")
    String description;

    @Override
    public void run(String... args) throws Exception {
        log.info(name + " " + description);
    }
}
```

### Accessing properties with @ConfigurationProperties
```
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String name;
    private String description;
}
```

```
@Log
@Component
public class InitRunnerv1 implements CommandLineRunner {
    AppConfig appConfig;

    public InitRunnerv1(AppConfig pizzaConfig) {
        this.appConfig = pizzaConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(appConfig.getName() + " " + appConfig.getDescription());
    }
}
```

### Multiple Profiles
Spring Boot supports creating multi-document properties files. We can split a single physical file into multiple logical documents.

This allows us to define a document for each profile we need to declare, all in the same file:

```
app.msg=used-always-in-all-profiles
#---
spring.config.activate.on-profile=dev
app.msg=dev-profile
#---
spring.config.activate.on-profile=production
server.port=9000
app.msg=production-profile
```

As an alternative to having different profiles in the same file, we can store multiple profiles across different files. We achieve this by putting the name of the profile in the file name — for example, application-dev.yml or application-dev.properties.

https://www.baeldung.com/spring-profiles


### Properties files, environment variables and command-line arguments

Properties can be passed in via environment variables.

```
# Change the http server port to 7777 (instead of 8080)
$ SERVER_PORT=7777 ./gradlew bootRun

# Activates the dev profile
$ SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

Or via a JVM system parameter (-D option).

```
# Change the http server port to 7777 (instead of 8080)
java -Dserver.port=7777 -jar build/libs/configuration-0.0.1-SNAPSHOT.jar

# Activates the dev profile
java -Dspring.profiles.active=dev -jar build/libs/configuration-0.0.1-SNAPSHOT.jar
```

There is a precise hierarchy for overriding properties:
1. Command line
2. Environment variables
3. application.properties

```
SPRING_PROFILES_ACTIVE=production java -Dspring.profiles.active=dev -jar build/libs/configuration-0.0.1-SNAPSHOT.jar
```

https://12factor.net/codebase

https://12factor.net/build-release-run

## Dependency Injection

### Inversion of Control
Inversion of Control is a principle in software engineering which transfers the control of objects or portions of a program to a container or framework. In contrast with traditional programming, in which our custom code makes calls to a library, IoC enables a framework to take control of the flow of a program and make calls to our custom code. 

The advantages of this architecture are:

* decoupling the execution of a task from its implementation
* making it easier to switch between different implementations
* greater modularity of a program
* greater ease in testing a program by isolating a component or mocking its dependencies, and allowing components to communicate through contracts

We can achieve Inversion of Control through various mechanisms such as: Strategy design pattern, Service Locator pattern, Factory pattern, and Dependency Injection (DI).

### Dependency Injection
Dependency injection is a pattern we can use to implement IoC, where the control being inverted is setting an object’s dependencies. Connecting objects with other objects, or “injecting” objects into other objects, is done by an assembler rather than by the objects themselves.

Here’s how we would create an object dependency in traditional programming:

```
public class Mustang implements Car {
    private final Engine engine;
    private final Transmission transmission;

    public Mustang() {
        this.engine = new FordV8();
        this.transmission = new FordTransmission400CV();
    }

    @Override
    public String print() {
        return String.join(", ", engine.print(), transmission.print());
    }
}
```

By using DI, we can rewrite the example without specifying the implementation of the Item that we want:

```
public class Store {
    private Item item;

    public Store(Item item) {
        this.item = item;
    }
}
```

In the case of constructor-based dependency injection, the container will invoke a constructor with arguments each representing a dependency we want to set.

Spring resolves each argument primarily by type, followed by name of the attribute, and index for disambiguation. The *@Configuration* annotation indicates that the class is a source of bean definitions. We can also add it to multiple configuration classes.

```
@Configuration
public class AppConfig {

    @Bean
    public Item item() {
        return new ItemImpl();
    }
}
```

### @Component annotation
Spring has provided a few **specialized stereotype annotations: @Controller, @Service and @Repository**. They all provide the same function as **@Component**.

They all act the same because they are all composed annotations with @Component as a meta-annotation for each of them. They are like @Component aliases with specialized uses and meaning outside Spring auto-detection or dependency injection.

```
@Component
public class ItemImpl implements Item {

    @Override
    public String print() {
        return "This is a specific item!";
    }
}
```

https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring

https://www.baeldung.com/spring-component-annotation

### @Primary annotation
https://www.baeldung.com/spring-primary

### @Lazy annotation
https://www.baeldung.com/spring-lazy-annotation



## Database: Basics

```
Spring Data JPA ---> JPA
                      |
                      V
    Spring JDBC ---> JDBC
                      |
                      V
                   JDBC Driver
```

Add depencencies to pom.xml file

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

Configure a datasource in application.properties (H2)

```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
```

Configure a datasource in application.properties (PostGres)

```
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=changemeinprod!
spring.datasource.driver-class-name=org.postgresql.Driver
```

We can use Docker Compose to install a containerized version of a DBMS (PostGres)

```
version: '3.1'

services:

  db:
    image: postgres
    ports:
      - "5432:5432"
    restart: always
    environment:
      POSTGRES_PASSWORD: changemeinprod!
```

/src/main/resources/data.sql can be used to create schemas
```
DROP TABLE IF EXISTS "widgets";

DROP SEQUENCE IF EXISTS widgets_id_seq;
CREATE SEQUENCE widgets_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE TABLE "widgets" (
    "id" bigint DEFAULT nextval('widgets_id_seq') NOT NULL,
    "name" text,
    "purpose" text,
    CONSTRAINT "widgets_pkey" PRIMARY KEY ("id")
);
```

/src/main/resources/data.sql can be used to populate schemas
```
INSERT INTO widgets (id, name, purpose) VALUES
(1, 'Widget A', 'Used for testing purposes.'),
(2, 'Widget B', 'Designed for entertainment.'),
(3, 'Widget C', 'Enhances productivity.'),
(4, 'Widget D', 'Perfect for outdoor activities.'),
(5, 'Widget E', 'Improves overall well-being.');
```

https://www.baeldung.com/spring-boot-data-sql-and-schema-sql

## Database: DAOs with Spring JDBC
See code

## Database: Spring Data JPA
https://www.baeldung.com/spring-data-repositories
https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa



## Jackson & JSON
See code

## Building a REST API
See code

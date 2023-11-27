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

![](images/spring-initializr.png)

## Maven
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
<project>
  <modelVersion>4.0.0</modelVersion>
 
  <groupId>com.mycompany.app</groupId>
  <artifactId>my-app</artifactId>
  <version>1</version>
  
  <properties>
    <mavenVersion>3.0</mavenVersion>
  </properties>
 
  <dependencies>
    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-artifact</artifactId>
      <version>${mavenVersion}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-core</artifactId>
      <version>${mavenVersion}</version>
    </dependency>
  </dependencies>
</project>
```

## Spring Boot Starters
Dependency management is a critical aspects of any complex project. And doing this manually is less than ideal; the more time you spent on it the less time you have on the other important aspects of the project.

Spring Boot starters were built to address exactly this problem. Starter POMs are a set of convenient dependency descriptors that you can include in your application. You get a one-stop-shop for all the Spring and related technology that you need, without having to hunt through sample code and copy-paste loads of dependency descriptors.

Benefits of using Spring Boot starters:
* increase pom manageability
* production-ready, tested & supported dependency configurations
* decrease the overall configuration time for the project

The actual list of starters can be found [here](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-project/spring-boot-starters)

https://www.baeldung.com/spring-boot-starters


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
public class Store {
    private Item item;
 
    public Store() {
        item = new ItemImpl1();    
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

### Spring Stereotype Annotations
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

## Configuration

### Application Properties
A common practice in Spring Boot is using an external configuration to define our properties. This allows us to use the same application code in different environments. **We can use properties files, YAML files, environment variables and command-line arguments.**

By default, Spring Boot can access configurations set in an application.properties file, which uses a key-value format:

```
spring.datasource.url=jdbc:h2:dev
spring.datasource.username=SA
spring.datasource.password=password
```

### Properties files, environment variables and command-line arguments

Esempi di override

https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html


### Placeholders in Properties
Within our values, we can use placeholders with the ${} syntax to refer to the contents of other keys, system properties, or environment variables:

```
app.name=MyApp
app.description=${app.name} is a Spring Boot application
```

### List Structure
If we have the same kind of properties with different values, we can represent the list structure with array indices:

```
application.servers[0].ip=127.0.0.1
application.servers[0].path=/path1
application.servers[1].ip=127.0.0.2
application.servers[1].path=/path2
application.servers[2].ip=127.0.0.3
application.servers[2].path=/path3
```

### Multiple Profiles
Spring Boot supports creating multi-document properties files. We can split a single physical file into multiple logical documents.

This allows us to define a document for each profile we need to declare, all in the same file:

```
logging.file.name=myapplication.log
bael.property=defaultValue
#---
spring.config.activate.on-profile=dev
spring.datasource.password=password
spring.datasource.url=jdbc:h2:dev
spring.datasource.username=SA
bael.property=devValue
#---
spring.config.activate.on-profile=prod
spring.datasource.password=password
spring.datasource.url=jdbc:h2:prod
spring.datasource.username=prodUser
bael.property=prodValue
```

As an alternative to having different profiles in the same file, we can store multiple profiles across different files. We achieve this by putting the name of the profile in the file name — for example, application-dev.yml or application-dev.properties.


### YAML Format
As well as Java properties files, we can also use YAML-based configuration files in our Spring Boot application. YAML is a convenient format for specifying hierarchical configuration data.

```
spring:
    datasource:
        password: password
        url: jdbc:h2:dev
        username: SA
```

```
application:
    servers:
    -   ip: '127.0.0.1'
        path: '/path1'
    -   ip: '127.0.0.2'
        path: '/path2'
    -   ip: '127.0.0.3'
        path: '/path3'
```

```
logging:
  file:
    name: myapplication.log
bael:
  property: defaultValue
---
spring:
  config:
    activate:
      on-profile: staging
  datasource:
    password: 'password'
    url: jdbc:h2:staging
    username: SA
bael:
  property: stagingValue
```

### Value properties
```
public class PizzaApplication implements CommandLineRunner {
    @Value("${pizza.sauce}")
    String sauce;
    @Value("${pizza.topping}")
    String topping;
    @Value("${pizza.crust}")
    String crust;
    ...
```

```
# application.properties
pizza.sauce=bbq
pizza.topping=chicken
pizza.crust=stuffed
```

### Configuration properties
```
@Configuration
@ConfigurationProperties(prefix = "pizza")
public class PizzaConfig {
  private String sauce;
  private String topping;
  private String crust;
}
```

```
# application.properties
pizza.sauce=bbq
pizza.topping=chicken
pizza.crust=stuffed
```


## Databases Pt 1 -- Database Basics

## Databases Pt 2 -- DAOs with Spring JDBC

## Databases Pt 3 -- Spring Data JPA

## Jackson & JSON

## Building a REST API

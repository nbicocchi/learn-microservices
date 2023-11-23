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

https://spring.io/projects

* Core technologies: dependency injection, events, resources, i18n, validation, data binding, type conversion, SpEL, AOP.
* Testing: mock objects, TestContext framework, Spring MVC Test, WebTestClient.
* Data Access: transactions, DAO support, JDBC, ORM, Marshalling XML.
* Spring MVC and Spring WebFlux web frameworks.
* Integration: remoting, JMS, JCA, JMX, email, tasks, scheduling, cache and observability.
* Languages: Kotlin, Groovy, dynamic languages.

### Spring Boot
* **Spring Boot uses Spring behind the scenes and makes it easier to use**
* Minimize the amount of manual configuration
* Perform auto-configuration based on props files and JAR classpath 
* Help to resolve dependency conflicts (Maven or Gradle)
* Provide an embedded HTTP server (Tomcat, Jetty, Undertow, ...)

https://docs.spring.io/spring-boot/docs/current/reference/html/index.html

### Spring Initializr

https://start.spring.io/

* Quickly create a starter Spring Boot project Select your dependencies
* Creates a Maven/Gradle project
* Import the project into your IDE
* Eclipse, IntelliJ, NetBeans etc ...

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



### Application Properties
Various properties can be specified inside your application.properties file, inside your application.yaml file, or as command line switches. This appendix provides a list of common Spring Boot properties and references to the underlying classes that consume them.

```
# spring properties
server.port=8081

# custom properties
coach.name=Diego Armando
team.name=Boca Junior
```

https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

### Static content

### Templates

## Spring Boot Starters
Dependency management is a critical aspects of any complex project. And doing this manually is less than ideal; the more time you spent on it the less time you have on the other important aspects of the project.

Spring Boot starters were built to address exactly this problem. Starter POMs are a set of convenient dependency descriptors that you can include in your application. You get a one-stop-shop for all the Spring and related technology that you need, without having to hunt through sample code and copy-paste loads of dependency descriptors.

https://www.baeldung.com/spring-boot-starters


## Dependency Injection

## Configuration

## Databases Pt 1 -- Database Basics

## Databases Pt 2 -- DAOs with Spring JDBC

## Databases Pt 3 -- Spring Data JPA

## Jackson & JSON

## Building a REST API

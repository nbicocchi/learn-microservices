# Understanding the Full Spring Ecosystem

In this lesson, we'll go through the main modules that Spring offers and understand how they relate to each other to form the Spring ecosystem.

## The Spring Ecosystem

Due to the success of the framework, the Spring ecosystem grew over time.

This is now quite vast, as you can see on the [official site](https://spring.io/projects/spring-framework).

**Let's go through some of the most popular Spring modules.**

### Spring Core

-   contains the Core technologies: DI, events, validation, data binding, AOP
-   support for Testing
-   Spring Web
-   Data Access
-   can be used with several other programming languages: Kotlin, Groovy, dynamic languages

### Spring MVC

-   the Web MVC module
-   technically part of Spring Core, but worth discussing separately
-   Spring has both Spring MVC (for Servlet-stack web applications) and Spring WebFlux (for reactive-stack applications)

### Spring Persistence

-   also part of Spring Core
-   allows Data Access: transactions, DAO/Repository support, JDBC, ORM, Marshalling XML
-   contains the Spring Data project which reduces boilerplate code related to data access

### Spring Security

-   the most popular security framework when working with Java
-   “Learn Spring Security” - full course dedicated to it

### Spring Cloud

-   support for distributed system

### Spring Boot

-   provides an opinionated view of Spring development
-   has a very high adoption rate

### Others Spring Projects

-   Spring Batch
-   Spring Integration
-   Spring HATEOAS
-   Spring REST Docs
-   Spring AMQP
-   Spring Web Flow
-   Spring Web Services

In simple terms, **the framework has first class support for most technologies in the Java ecosystem**.

## Simplifying Spring Development with Boot

Of course, as the ecosystem grew it became more complex.

Over time, the Spring team made a few attempts to simplify Spring development according to the initial purpose, such as Spring Roo and the Groovy integration.

But eventually, **learning from what didn’t work, Spring Boot was born**. This project changed everything about developing with Spring by taking an opinionated view and providing sensible defaults.

We'll explore Boot in great detail further in the course. The goal in this lesson is to get a high-level picture of the Spring modules.

## Resources
- [The Spring Projects](https://spring.io/projects/spring-framework)
- [Spring Framework Reference](https://docs.spring.io/spring/docs/current/spring-framework-reference/overview.html)

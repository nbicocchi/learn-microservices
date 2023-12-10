# Understanding the Spring Ecosystem

In this lesson, we'll go through the main modules that Spring offers and understand how they relate to each other to form the Spring ecosystem.

## What is Spring?
Let's start with understanding what Spring actually is. Simply put, Spring is a [back-end](https://en.wikipedia.org/wiki/Front_and_back_ends) technology with a broad range of uses, the most common being the development of web applications.

Spring is also a very general term which can refer to the core of the framework, but most often, it refers to the whole family of Spring-related projects.

## The Popularity of Spring
The framework is also quite dominant in the Java ecosystem.

Let's have a look at just a quick search trend: [Spring Framework Google Search Trend](https://trends.google.com/trends/explore?date=all&q=%2Fm%2F0dhx5b)

![](images/spring-trends.png)

And given that Spring was originally built in response to the complexity of developing with [J2EE](https://www.oracle.com/technetwork/java/javaee/appmodel-135059.html) / Java EE, let's compare it with that: [Spring Framework - Java EE Google Search Trend Comparison](https://trends.google.com/trends/explore?date=all&q=%2Fm%2F0dhx5b,Java%20EE)

![](images/spring-vs-javaee-trends.png)

As the graph shows, Spring clearly overtakes its main competitor in popularity.

Another indication of Spring's popularity is the number of questions on StackOverflow for Spring compared to other related Java technologies:

-   [Spring Questions (148k)](https://stackoverflow.com/questions/tagged/spring)
-   [Hibernate Questions (77k)](https://stackoverflow.com/questions/tagged/hibernate)
-   [Maven Questions (66k)](https://stackoverflow.com/questions/tagged/maven)
-   [Tomcat Questions (37k)](https://stackoverflow.com/questions/tagged/tomcat)
-   [Servlets Questions (31k)](https://stackoverflow.com/questions/tagged/servlets)
-   [Java EE Questions (28k)](https://stackoverflow.com/questions/tagged/java-ee)
-   [Grails Questions (28k)](https://stackoverflow.com/questions/tagged/grails)

## Why Use Spring?
The early goal of Spring, and still a core guideline of the framework, is removing complexity, clutter and boilerplate code.

Basically, Spring aims to make building a system easier for developers.

This is a monumental task, and one that, solved correctly, can make a huge impact.

Spring is also not an all or nothing choice. You can actually pick and choose the parts of Spring that make sense for your system.

## Learning Spring
An interesting aspect of Spring is how much of a long-term investment learning Spring actually is, as its evolution is quite unique.

On the one hand, it's actively developed and always improving at the edges. But, at the core, it's highly highly stable.

In fact, it's so stable that learning the core of Spring 10 years ago would, with small exceptions, still be relevant today.

In this course, we'll focus on understanding the foundational concepts of Spring, starting with the definition of a bean and what dependency injection is. Then we'll work our way through the most commonly used Spring modules, including the core, persistence, web and security modules.

As we progress through the lessons, we'll dive deeper into each Spring aspect.

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
- [Spring Framework Reference](https://docs.spring.io/spring/docs/current/spring-framework-reference/overview.html)
- [The Spring Projects](https://spring.io/projects/spring-framework)
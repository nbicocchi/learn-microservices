# Introducing Spring Boot

In this lesson, we'll focus on understanding the Spring Boot module and the advantages of using Boot to develop Spring applications.

## What is Spring Boot?

Simply put, **Boot represents a layer of abstraction on top of the Spring framework**.

Boot is an extension of the Spring framework that comes with default configuration with an opinionated take on building web application with Spring.

Before Boot, a Spring application needed a lot of configuration just to get started. Using Boot eliminates a lot of [boilerplate code](https://en.wikipedia.org/wiki/Boilerplate_code).

**The goal is simplifying the development of a Spring application.** Compared to standard Spring, getting a simple application up-and-running with Boot is very quick.

However, the default configuration that Boot brings may not always be what we need. In this case, we can also add our own configuration. Boot will intelligently back off and allow the custom behavior we define to override the default one.

For example, we can keep the default web configuration and only override the persistence configuration.

## Spring and Spring Boot

A critical thing to understand as we’re starting out here is the relation between Spring and Boot.

**Boot lives on top of the Spring framework.** So it’s not a question of using one or the other.

Boot is, without a question, useful, and it does make things simple. But, in doing so, **it hides a lot of the internals of Spring**.

This makes it very easy to think you can ignore the internals, which is a risky way of developing an application.

In any complex application, you’ll need to go beyond Boot. At that point, understanding the core of Spring is critical. Like any abstraction, you can’t really use it well without actually understanding the underlying layer.

We need to understand how the core pieces of Spring actually work, and more importantly, what Spring Boot does on top of them.

The codebase of the course here will, naturally, make heavy use of Boot.

## Basic Features of Boot

Some of the core Boot features that allow the functionality we've discussed are:

-   Starters - can be seen as modules which encapsulate specific functionality (JPA, testing, web, mail, logging, etc)
-   Auto-configuration - the default Spring configuration that’s loaded under certain conditions
-   Dependency management - helps solve the issue of incompatible dependency versions

## Resources
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

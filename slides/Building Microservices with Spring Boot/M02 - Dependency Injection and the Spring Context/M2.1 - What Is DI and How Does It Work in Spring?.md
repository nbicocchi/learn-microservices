# What Is DI and How Does It Work in Spring?

In this lesson, we'll focus on understanding the concept of Dependency Injection (DI) and how this work in Spring.

## What is a Dependency?

Before we can talk about Dependency Injection, let’s first define what a dependency is.

Understanding this concept is critical not only for Spring, but for developing software in general.

Let’s focus on the Java ecosystem and put forward a simple definition:

_Class A has a dependency on Class B when it interacts with it in any way_

## Dependency Injection

The way Class A will get access to that Class B dependency is critical here.

One option is for Class A to take on the responsibility of instantiating B by itself:

```
public class A {
    private B bDependency;
  
    public A() {
        bDependency = new B();    
    }
}
```

Alternatively, that responsibility can be external, meaning the dependency comes from the outside:

```
public class A {
    private B bDependency;
    
    public A(B bDependency) {
        this.bDependency = bDependency;
    }
}
```

That is, in a nutshell, Dependency Injection (not to be confused with the the Dependency Inversion principle from SOLID).

**Injection is simply the process of injecting the dependency B in the object of type A.**

Now, since the instantiation of the B dependency is no longer done in A, that responsibility will now belong to the framework.

## Why DI

Separating the responsibility of instantiating a class from the logic in that class is a very useful concept.

This leads to **a more loosely coupled system and to a lot of flexibility in the design** of that system, as now the dependency can be decided (or swapped out) at runtime.

This can be very helpful in both application architecture, as well as testing, because DI make it easier to switch between different implementations of the dependency. For example, we can pass in a mock of a dependency rather than a full dependency object.

## Types of Injection

There are essentially three ways to inject a dependency:

-   via the raw field
-   via the constructor
-   via a setter

These all have their advantages and disadvantages, and we’ll look into each in detail throughout the course.

## The Spring IoC Container

Dependency Injection is a technique that’s part of the broader Inversion of Control (IoC) principle.

We won't go into detail here about IoC, but have a look at the resources section for more information on it.

Now that we understand the concepts and principles, let’s touch on what Spring does as well. Simply put, **Spring has the responsibility of creating and configuring the dependencies and injecting them where needed**.

We’ll discuss the Spring IOC Container further in the next lesson.

## Resources
- [Inversion of Control and Dependency Injection with Spring](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)
- [Inversion of Control Containers and the Dependency Injection pattern (Martin Fowler)](https://martinfowler.com/articles/injection.html)
- [Spring Dependency Injection Series](https://www.baeldung.com/spring-dependency-injection)

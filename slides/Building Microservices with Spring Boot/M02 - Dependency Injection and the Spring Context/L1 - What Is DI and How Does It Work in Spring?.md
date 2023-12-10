# What Is DI and How Does It Work in Spring?

In this lesson, we'll focus on understanding the concept of Dependency Injection (DI) and how this work in Spring.

## What Is Inversion of Control?

Inversion of Control is a principle in software engineering which transfers the control of objects or portions of a program to a container or framework. We most often use it in the context of object-oriented programming.

In contrast with traditional programming, in which our custom code makes calls to a library, IoC enables a framework to take control of the flow of a program and make calls to our custom code. To enable this, frameworks use abstractions with additional behavior built in. **If we want to add our own behavior, we need to extend the classes of the framework or plugin our own classes.**

The advantages of this architecture are:
* decoupling the execution of a task from its implementation
* making it easier to switch between different implementations
* greater modularity of a program
* greater ease in testing a program by isolating a component or mocking its dependencies, and allowing components to communicate through contracts

We can achieve Inversion of Control through various mechanisms such as: Strategy design pattern, Service Locator pattern, Factory pattern, and Dependency Injection (DI).


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
An IoC container is a common characteristic of frameworks that implement IoC.

In the Spring framework, the interface _ApplicationContext_ represents the IoC container. The Spring container is responsible for instantiating, configuring and assembling objects known as _beans_, as well as managing their life cycles.

The Spring framework provides several implementations of the _ApplicationContext_ interface: AnnotationConfigApplicationContext, _ClassPathXmlApplicationContext_ and _FileSystemXmlApplicationContext_ for standalone applications, and _WebApplicationContext_ for web applications.

In order to assemble beans, the container uses configuration metadata, which can be in the form of XML configuration or annotations.

Here’s one way to manually instantiate a container:

```
ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
```

And here’s an example of manually instantiating a container using _AnnotationConfigApplicationContext_:

```
AnnotationConfigApplicationContext annotationContext = new AnnotationConfigApplicationContext();
```

When you create an instance of _AnnotationConfigApplicationContext_ and provide it with one or more configuration classes, it scans these classes for the _@Bean_ annotations and other relevant annotations. It then initializes and manages the beans defined in these classes, setting up their dependencies and managing their lifecycle.

## Resources
- [Inversion of Control and Dependency Injection with Spring](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)
- [Inversion of Control Containers and the Dependency Injection pattern (Martin Fowler)](https://martinfowler.com/articles/injection.html)
- [Spring Dependency Injection Series](https://www.baeldung.com/spring-dependency-injection)

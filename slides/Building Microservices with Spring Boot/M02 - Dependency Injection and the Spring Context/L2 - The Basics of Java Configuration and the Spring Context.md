# The Basics of Java Configuration and the Spring Context

In this lesson, we'll discuss the main Spring IoC container - the Spring Application Context.

The relevant module for this lesson is: [basics-of-java-configuration-and-the-spring-context-end](https://github.com/nbicocchi/spring-boot-course/tree/module2/basics-of-java-configuration-and-the-spring-context-end)

## Spring Application Context

We previously discussed the Spring IOC Container - which, simply put, will create the objects, wire them together, configure them, and manage their complete lifecycle.

The framework comes with multiple container implementations. To keep things very simple at this early stage, we'll discuss the main one - the Spring Application Context.

**When the Spring application starts up, this Application** **Context** [**bootstrapps**](https://en.wikipedia.org/wiki/Bootstrapping) **and** **starts instantiating the objects called _beans_** in Spring terminology.

Let's start with a practical example of defining a bean manually. This will make understanding the concept of the Application Context more concrete.

## Contributing Beans to the Context

We can create beans in several ways that we'll discuss throughout the course.

But, for this example, we’ll use a simple and highly common option: a configuration class where we manually define a bean.

Let's create a new _PersistenceConfig_ class:

```
@Configuration
public class PersistenceConfig {

    @Bean
    public ProjectRepositoryImpl projectRepository() {
        return new ProjectRepositoryImpl();
    }
}
```

We've also defined a _projectRepository_ bean using the simple _@Bean_ annotation.

Before we go into more detail on this, let’s run/debug the application and understand how the Spring Application Context gets bootstrapped and how the bean we just defined is part of that process.

We’re going to set a [breakpoint](https://en.wikipedia.org/wiki/Breakpoint) in the bean definition here and debug. By doing this, we can see the bootstrapping process run, and, eventually, hit our configuration and create our bean.

Now that we’ve seen it working, let’s take a step back and understand what’s going on.

**The _@Configuration_ annotation indicates to Spring that this class needs to be processed by the container** because it will contribute bean definitions to it.

And, of course, the _@Bean_ annotation is one such actual bean definition. In our case, this is a bean named _projectRepository,_ as that’s the name of the method.

Now we've started to get some context around how to framework works and lay the groundwork for going deeper into beans and Dependency Injection.

## Boot Highlight

Let's briefly highlight the Spring Boot specific aspects used in this lesson.

By default, **Spring Boot loads all classes annotated with _@Bean_, _@Component_, _@Configuration_ etc that are located in the same package as the main class or in all sub-packages of this.** If we were using pure Spring, we'd have to specify these packages manually:

_AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();_

_appContext.scan("com.baeldung.ls");_

_appContext.refresh();_

In the next lessons, we consider this topic in more detail.

## Resources
- [Spring Application Context](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-basics)
- [Java-based Container Configuration](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-java)

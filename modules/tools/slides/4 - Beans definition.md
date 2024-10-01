# Defining Beans, Component Scanning and Bean Annotations

In this lesson, we'll discuss the main Spring IoC container - the Spring Application Context.

The relevant module for this lesson is: [defining-beans-component-scanning-and-bean-annotations-end](../code/learn-spring-m2/defining-beans-component-scanning-and-bean-annotations-end)

## Spring Application Context

One of the main features of the Spring framework is the IoC (Inversion of Control) container. The [Spring IoC container](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring) is responsible for managing the objects of an application. It uses dependency injection to achieve inversion of control.

The interfaces _[BeanFactory](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/BeanFactory.html)_ and _[ApplicationContext](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationContext.html)_ **represent the Spring IoC container**. Here, _BeanFactory_ is the root interface for accessing the Spring container. It provides basic functionalities for managing beans.

On the other hand, the _ApplicationContext_ is a sub-interface of the _BeanFactory_. It **provides** **more enterprise-specific functionalities**. The important features of _ApplicationContext_ are **resolving messages, supporting internationalization, publishing events, and application-layer specific contexts**. This is why we use it as the default Spring container.

**When the Spring application starts up, this Application** **Context** **starts instantiating the objects called _beans_** in Spring terminology.

## Contributing Beans to the Context
Let's start with a practical example of defining a bean manually. This will make understanding the concept of the Application Context more concrete. We can create beans in several ways that we'll discuss throughout the course. For this example, we’ll use a simple and highly common option: a configuration class where we manually define a bean.

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

Before we go into more detail on this, let’s run/debug the application and understand how the Spring Application Context gets bootstrapped and how the bean we just defined is part of that process.

We’re going to set a [breakpoint](https://en.wikipedia.org/wiki/Breakpoint) in the bean definition here and debug. By doing this, we can see the bootstrapping process run, and, eventually, hit our configuration and create our bean.

```
@Configuration
public class PersistenceConfig {

    @Bean
    public ProjectRepositoryImpl projectRepository() {
        // breakpoint here!
        return new ProjectRepositoryImpl();
    }
}
```

Now that we’ve seen it working, let’s take a step back and understand what’s going on.

**The _@Configuration_ annotation indicates to Spring that this class needs to be processed by the Spring Container** because it will contribute bean definitions to it. And, of course, the _@Bean_ annotation is one such actual bean definition. In our case, this is a bean named _projectRepository,_ as that’s the name of the method.

By default, **Spring Boot loads all classes annotated with _@Bean_, _@Component_, _@Configuration_ etc that are located in the same package as the main class or in all sub-packages of this.**

## Spring Component Scanning

At this point, we’re starting to understand the Spring application context and how we can add new beans to it using a _@Configuration_ class and the _@Bean_ annotation.

Now, let’s see how we can move from explicitly defining a bean to **letting the context discover that bean on its own**. This is called component scanning, and it's a different technique of achieving the same thing: adding beans into the context.

As it's common in Spring, this can be achieved with the help of annotations. More specifically, **the _@ComponentScan_ annotation**. Then, for the classes we want to define as beans, we can use stereotype annotations.

The simplest stereotype annotation we can use is _@Component._ Basically, during the bootstrapping process, **Spring will scan for any classes annotated with _@Component_ and will instantiate them as beans.**

Let’s open up the _ProjectRepositoryImpl_ and let’s annotate it:

```
@Component
public class ProjectRepositoryImpl implements IProjectRepository {
    // ...
}
```

We’re now expecting this to be picked up as a bean. We can add a breakpoint in the constructor here and start up the application, to verify that the breakpoint is hit. This way, we can see that component scanning is working as we expected.

Note that we never actually defined component scanning explicitly. This is because **_@SpringBootApplication_ is already using the annotation**. If we go to the _@SpringBootApplication_ definition (In Intellij: View > Jump to Source), we'll see that is includes the _@ComponentScan_ annotation.

By default, this scans classes in the same package or below. In our case, this is the _com.baeldung.ls_ package and all the packages below it. **If we want to scan a specific package, we can specify it using the _basePackages_ attribute.**

Now, we can move the _com.baeldung.ls.persistence_ package to _com.baeldung.other.persistence_. The project still works because _PersistenceConfig_ explicitely import _ProjectRepositoryImpl_ from a package which is not automatically scanned. Nevertheless, if we comment out the _@Configuration_ annotation the project stops working.

```
//@Configuration
public class PersistenceConfig {
    @Bean
    public ProjectRepositoryImpl projectRepository() {
        return new ProjectRepositoryImpl();
    }
}
```

Let’s use _@ComponentScan_ to scan the whole _com.baeldung_ package. In this way, even removing the _PersistenceConfig_ class, all beans can be found.

```
@SpringBootApplication
@ComponentScan(basePackages = "com.baeldung")
public class LsApp {

    public static void main(final String... args) {
        SpringApplication.run(LsApp.class, args);
    }
}
```

## Stereotype Annotations

Besides _@Component,_ there are, a few more stereotype annotations that aren’t actually that different. They use _@Component_ under the hood and just bring an extra layer of semantics on top.

For example, on the _ProjectRepositoryImpl_ we can replace _@Component_ with the _@Repository_ annotation:

```
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {
    // ...
}
```

Nothing changes technically, but this fits better the exact semantics of this particular bean, since it's actually a repository.

Similarly, we can use the _@Service_ annotation for the _ProjectServiceImpl_:

```
@Service
public class ProjectServiceImpl implements IProjectService {
    // ...
}
```

## Additional Annotations

### The _@Import_ Annotation

Spring allows us to modularise multiple configuration classes and **import one configuration into another using the _@Import_ annotation**:

```
@Import(PersistenceConfig.class)
public class AppConfig {
    //...
}
```

We can also import multiple configuration classes:

```
@Import({PersistenceConfig.class, ServiceConfig.class})
public class AppConfig {
    //...
}
```

The _@Import_ annotation here loads all the _@Bean_ definitions of _PersistenceConfig_ and _ServiceConfig_ into _AppConfig._

**Using the _@Import_ annotation is an efficient way of maintaining multiple configuration classes** and comes in handy when there are too many such configurations to manage.

### The _@Lazy_ Annotation

Now that we've understood how the beans are scanned, let’s see when these beans are initialized.

**By default the Container eagerly creates and configures all the singleton beans.** Generally this behaviour is desirable because errors around bean configurations are discovered immediately on application startup.

But whenever we don't want this to happen, **we can always prevent it by adding the _@Lazy_ annotation on our bean definitions**:

```
@Service
@Lazy
public class ProjectServiceImpl implements IProjectService {
	//...
}
```

In this example, we're telling the container to create the _ProjectServiceImpl_ bean instance only when it's first requested, rather than on startup.

Of course, we can also use the _@Lazy_ annotation alongside the _@Bean_ annotation.

### Meta-Annotations

We saw earlier how the _@Repository_ annotation internally uses the _@Component_ annotation. It’s worth mentioning here that the _@Component_ annotation is an example of a meta-annotation provided by Spring. **A meta-annotation is an annotation that can be applied to another annotation.** For example, the _Repository_ annotation is defined as:

```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
	//...
}

```
The _@Component_ here causes _@Repository_ to be treated in the same way as _@Component._

We can also combine multiple meta-annotations to create a ‘composed annotation’. This comes in handy when we want to group together the behaviors associated with multiple annotations into a single annotation.

## Resources
- [Spring Application Context](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-basics)
- [Java-based Container Configuration](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-java)
- [Spring Component Scanning](https://www.baeldung.com/spring-component-scanning)
- [@Component vs @Repository and @Service in Spring](https://www.baeldung.com/spring-component-repository-service)
- [Spring Bean Annotations](https://www.baeldung.com/spring-bean-annotations)


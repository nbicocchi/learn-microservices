# Defining Beans, Component Scanning and Bean Annotations

In this lesson, we'll focus on how to define beans in Spring and discover them using component scanning.

The relevant module you need to import when you're starting with this lesson is: [m2-defining-beans-component-scanning-and-bean-annotations-start](https://github.com/eugenp/learn-spring/tree/module2/m2-defining-beans-component-scanning-and-bean-annotations-start)

If you want have a look at the fully implemented lesson, as a reference, feel free to import: [m2-defining-beans-component-scanning-and-bean-annotations-end](https://github.com/eugenp/learn-spring/tree/module2/m2-defining-beans-component-scanning-and-bean-annotations-end)

## Spring Component Scanning

At this point, we’re starting to understand the Spring application context and how we can add new beans to it using a _@Configuration_ class and the _@Bean_ annotation.

Now, let’s see how we can move from explicitly defining a bean to **letting the context discover that bean on its own**.

This is called component scanning, and it's a different technique of achieving the same thing: adding beans into the context.

As it's common in Spring, this can be achieved with the help of annotations. More specifically, **the _@ComponentScan_ annotation**. Then, for the classes we want to define as beans, we can use stereotype annotations.

The simplest stereotype annotation we can use is _@Component._ Basically, during the bootstrapping process, **Spring will scan for any classes annotated with _@Component_ and will instantiate them as beans.**

Let’s open up the _ProjectRepositoryImpl_ and let’s annotate it:

```
@Component
public class ProjectRepositoryImpl implements IProjectRepository {
    // ...
}
```

We’re now expecting this to be picked up as a bean.

We can add a breakpoint in the constructor here and start up the application, to verify that the breakpoint is hit.

This way, we can see that component scanning is working as we expected.

Note that we never actually defined component scanning explicitly. This is because **_@SpringBootApplication_ is already using the annotation**.

If we go to the _@SpringBootApplication_ definition, we'll see that is includes the _@ComponentScan_ annotation.

By default, this scans classes in the same package or below. In our case, this is the _com.baeldung.ls_ package and all the packages below it.

**If we want to scan a specific package, we can specify it using the _basePackages_ attribute.**

Let’s add a new _PersistenceConfig_ class that uses _@ComponentScan_ to scan only the _com.baeldung.ls.persistence_ package:

```
@Configuration
@ComponentScan(basePackages= {"com.baeldung.ls.persistence"})
public class PersistenceConfig {
    // 
}
```

## Other Stereotype Annotations

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

## Advantages and Disadvantages

Finally, one quick note about this style of annotation-based Java configuration: as opposed to the more explicit configuration we explored earlier and even compared to the older-style XML-based configuration, this has the important **advantage of simplicity**.

However, wherever a process changes from explicit to implicit there’s also a disadvantage. And that is that **we’re losing the clean separation between the business logic (the beans) and the configuration of those beans**.

This is because the _@Component_ annotation used in this approach is added to the class definition, while the _@Bean_ annotation is used in a separate configuration class.

That’s typically an acceptable tradeoff, but it’s worth understanding.

## The _@Import_ annotation (extra)

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

## Lazily Initialized Beans (extra)

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

## Meta-Annotations (extra)

We saw earlier how the _@Repository_ annotation internally uses the _@Component_ annotation. It’s worth mentioning here that the _@Component_ annotation is an example of a meta-annotation provided by Spring.

**A meta-annotation is an annotation that can be applied to another annotation.**

For example, the _Repository_ annotation is defined as:

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
- [Spring Component Scanning](https://www.baeldung.com/spring-component-scanning)
- [@Component vs @Repository and @Service in Spring](https://www.baeldung.com/spring-component-repository-service)
- [Spring Bean Annotations](https://www.baeldung.com/spring-bean-annotations)

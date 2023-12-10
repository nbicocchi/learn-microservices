# The Spring Application Context

In this lesson, we'll learn about the **Spring ApplicationContext.**

The relevant module for this lesson is: [the-spring-application-context-end](https://github.com/nbicocchi/spring-boot-course/tree/module2/the-spring-application-context-end)

## What is the Application Context

The Application Context is a core part of the Spring Framework.

**It represents the IoC container and is responsible for instantiating, configuring and assembling beans.**

The container gets instructions on what objects to instantiate, configure and assemble by reading configuration metadata, which can be represented using XML or Java code.

## Obtaining a reference to the Context

We can reference the current running context in two ways:

1.  **Using the _@Autowired_ annotation - to autowire the current context**
2.  **Implementing the _ApplicationContextAware_ interface**

Using the _@Autowired_ annotation is self explanatory, so let’s see how to use the _ApplicationContextAware_ interface.

Let’s make our _ProjectServiceImpl_ class implement the _ApplicationContextAware_ interface:

```
@Service
public class ProjectServiceImpl implements IProjectService, ApplicationContextAware {
}
```

**The _ApplicationContextAware_ interface contains a _setApplicationContext()_ method that is called when the bean is initialized by Spring.**

This method gives us access to the current _ApplicationContext_.

Let’s override this method, add a logger and log the _ApplicationContext_ id:

```
public class ProjectServiceImpl implements IProjectService, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        LOG.info("CONTEXT WITH ID '{}' SET", applicationContext.getId());
    }
    // ...
}
```

Now, if we run the application, we can see in the logs that the context is indeed set:

```
CONTEXT WITH ID ‘application’ SET
```

**We already have a context here because when a Spring Boot app is run, an _ApplicationContext_ is configured and created automatically using the default configuration.**

The id of this context created by Spring is 'application'.

## Creating a new Context

**Spring provides us with various implementations of the _ApplicationContext_ interface, which we can use to create Application Contexts depending on the configuration method we use.**

For XML configuration we have the _ClassPathXmlApplicationContext_ and _FileSystemXmlApplicationContext_ classes.

For Groovy bean definition DSL we can use the _GenericGroovyApplicationContext_ class.

For Java-Based configuration, we have the _AnnotationConfigApplicationContext_ class

We also have the _GenericApplicationContext_ class which is a more flexible way of creating an Application Context, and many more.

**Let’s create a new context using the _AnnotationConfigApplicationContext_ class, since we're using Java-based configuration.**

We go to our _LsApp_ class, add a logger and create a new context by instantiating the _AnnotationConfigApplicationContext_ class in our _main_ method:

```
public class LsApp {
    private static final Logger LOG = LoggerFactory.getLogger(LsApp.class);

    public static void main(final String... args) {
        SpringApplication.run(LsApp.class, args);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        
    }
}
```

The context is now created, so let’s log the ID:

```
LOG.info("context created with id {}", ctx.getId());
```

Now if we run the program, we see in the logs that the context is created.

## Adding and Retrieving Beans from the Context

We have seen how to create a new context and how we can access the context from within a bean.

Now, let’s see how we can retrieve beans from the context.

**To retrieve beans from the context we can use the _getBean()_ method, which is defined in the** **_AbstractApplicationContext_ interface.**

Let’s retrieve the _projectService_ bean from the context we created.

But first, in the _LsApp,_ we will modify the context we created earlier because at the moment, the _BeanFactory_ in this context is empty; the context does not know where to scan for beans.

Let’s show it where to scan for beans. We can do this in two ways and we’ll use both in this example.

The first method is by **using constructor parameters. We can provide a String or String array as a parameter to the constructor which represents the packages that we want the context to scan for beans**:

```
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("com.baeldung.ls.persistence.repository");
```

The context will scan the _repository_ package for annotated classes.

Instead of the package, we can also provide:

1.  a _DefaultListBeanFactory_
2.  a single _@Component_ or _@Configuration_ class, or an array of such classes
3.  a list of Strings representing packages to scan for beans
    **The second method is exclusive to annotation-based application contexts and it's by using the _scan()_ method:**

```
ctx.scan("com.baeldung.ls.service");
```

The context will now also scan the _service_ package for beans.

Now to retrieve the bean we can use the _getBean()_ method of the _ApplicationContext_:

```
IProjectService projectService = ctx.getBean("projectServiceImpl", IProjectService.class);
```
Now we can use the _projectService_ bean to query for a _project_ and log it:

```
LOG.info("{}", projectService.findById(1L));
```

and when we restart the application, we see in our logs that the service is actually queried and the existing project is logged.

## Bean Lifecycle in the Application Context

Any Spring Bean supports the regular lifecycle callbacks and can use the _@PostConstruct_ and _@PreDestroy_ annotations.

**The method annotated with _@PostConstruct_ will be executed after the bean is instantiated.**

**The method annotated with _@PreDestroy_ will be executed when the context that contains the bean is destroyed.**

Let’s see this in an example.

We will be adding these lifecycle hooks to the _ProjectServiceImpl_.

In the _ProjectServiceImpl_, let’s create a method that will log some output when the bean is created:

```
@PostConstruct
public void created() {
    LOG.info("POST CONSTRUCT in ProjectServiceImpl");
}
```

Now, let’s create the method that will be called when the bean is destroyed and annotate it with _@PreDestroy_:

```
@PreDestroy
public void onDestroy() {
    LOG.info("PRE DESTROY in ProjectServiceImpl");
}
```
Let’s test it out. If we run the application we see in the logs that post-construct method is called after the context is created.

When we stop the application and the bean is destroyed, we see in the logs that pre-destroy is called before the application exits.

## Closing the Context

Now, let’s look at how to close a context by closing the context we created earlier.

In the _LsApp_ class, let's add a log to check if the context is active:

```
public class LsApp {

    public static void main(final String... args) {
        // ...

        LOG.info("Context active before close: {}", ctx.isActive());
    }
}
```

Now **to close the context all we have to do is call the _close()_ method of our _AnnotationConfigApplicationContext_ class** and the context will be closed.

To confirm this, let’s add a log to check if the context is active after calling the _close_ method:

```
public static void main(final String... args) {
    // ...

    LOG.info("Context active before close: {}", ctx.isActive());

    ctx.close();

    LOG.info("Context active after close: {}", ctx.isActive());
}
```

Now, if we run the program, we see in the logs that before closing the context the value will be _true,_ and after closing it, it will be _false_.

We also see that the lifecycle hook in the bean we created earlier is executed.

## Resources
- [Spring Framework Core](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html)
- [Java-based Container Configuration](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-java)
- [BeanFactory or ApplicationContext?](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#context-introduction-ctx-vs-beanfactory)

# Lifecycle and Scope of a Bean

In this lesson, we'll go through the basic concepts of the bean lifecycle in Spring.

The relevant module for this lesson is: [lifecycle-and-scope-of-a-bean-end](../code/learn-spring-m2/lifecycle-and-scope-of-a-bean-end)

## Bean Lifecycle Phases

Basically, **the lifecycle of a Spring bean consists of 3 phases:**

* initialization phase
* use phase
* destroy phase

**We focus on the _initialization_ and _destroy_ phases**, as they’re the most interesting ones from the point of view of dependency injection. Our goal here is to understand the lifecycle but also the [hooks](https://en.wikipedia.org/wiki/Hooking) in the framework connected to that lifecycle. The _use_ phase is a phase in which an application normally spends the most of its time, and it’s the point of interest for the business logic.

### Initialization Phase

This phase consists of two main stages:

* loading bean definitions
* instantiating beans

Let’s create a first bean in the _AppConfig_ class:

```
@Bean
public BeanA beanA() {
    return new BeanA();
}
```

Let’s look at first hook that Spring provides to customize the bean creation. **Spring will run methods annotated with _@PostConstruct_ only once, just after the initialization of the bean properties.** Let’s add this method to the _BeanA_ definition:

```
public class BeanA {
    private static Logger log = LoggerFactory.getLogger(BeanA.class);

    @PostConstruct
    public void post() {
        log.info("@PostConstruct method is called.");
    }
}
```

We can debug the application to make sure this gets called correctly.

Next, let’s explore a similar hook which works when we’re using the _@Bean_ annotation to define a bean.

First, let’s create our new bean - _BeanB_ and let’s provide it with a simple _initialize_ method:

```
public class BeanB {
    private static Logger log = LoggerFactory.getLogger(BeanB.class);

    public void initialize() {
        log.info("Custom initializer is called.");
    }

    @PostConstruct
    public void postConstruct() {
        log.info("@PostConstruct method is called.");
    }
}
```

Now let’s define this as a bean, **using the _@Bean_ annotation and add an optional param of the annotation called _initMethod_**_:_

```
@Bean(initMethod="initialize")
public BeanB beanB() {
    return new BeanB();
}
```

This is very useful when we want to keep configuration out of our domain logic because, notice, _BeanB_ is now entirely clean of any framework code.

### Destroy Phase

This is the final phase of an application. It’s activated when the application context becomes eligible for garbage collection. At this point, all the beans get out of scope of the application and become eligible for [garbage collection](https://en.wikipedia.org/wiki/Garbage_collection_(computer_science)). The destroy options are very similar to the initialization options we already explored.

First, **we have the _@PreDestroy_ annotation that we can add on a bean method, to indicate that method should be executed before the bean is destroyed.**

```
public class BeanC {
  
    private static Logger log = LoggerFactory.getLogger(BeanC.class);

    @PreDestroy
    public void preDestroy() {
        log.info("@PreDestroy method is called.");
    }

    public void destroy() {
        log.info("Custom destroy method is called.");
    }
}
```

Similarly, **we can use the _destroyMethod_ attribute of the _@Bean_ annotation** to specify the _destroy()_ method we added above:

```
@Bean(destroyMethod="destroy")
public BeanC beanC() {
    return new BeanC();
}
```

To activate this phase, let’s modify the main method:

```
public static void main(final String... args) {
    ConfigurableApplicationContext context = SpringApplication.run(LsApp.class, args);
    context.close();
}
```

## Bean Scopes

Basically, **Spring scopes allow us to control the life-cycle and visibility of beans**. When a bean is created, we can define its scope by means of the _@Scope_ annotation.

There are six types of bean scopes in Spring:
* **_singleton_** - with this scope, the container creates a single instance of a bean. All requests for such a bean will return the same object, which is cached. This is the default scope if no other scope is specified.
* **_prototype_** - with this scope, the container will create a new instance every time it’s requested.
* **_request_**_,_ **_session_**_,_ **_application_**_,_ **_websocket_** - these scopes are available only in a web-aware application context and are less often used in practice. 

Let's now consider the first two scopes. We'll use the _ProjectRepositoryImpl_ class to create a bean for each scope.

### Singleton Scope

We've seen how to define a bean in a class decorated with the _@Configuration_ annotation. Let's open _LsAppConfig.java_ and define a bean with the singleton scope:

```
@Bean
public IProjectRepository singletonBean() {
    return new ProjectRepositoryImpl();
}
```

**This bean has the _singleton_ scope, since this is the default scope in Spring if we don’t specify any other.**

We could also specify it explicitly using the _@Scope_ annotation:

```
@Bean
@Scope("singleton")
public IProjectRepository singletonBean() {
    return new ProjectRepositoryImpl();
}
```


### Prototype Scope

Now, let's define a bean with the scope prototype. To this end, we'll update the bean we've just created by changing the "singleton" scope and bean name into "prototype":

```
@Bean
@Scope("prototype")
public IProjectRepository prototypeBean() {
    return new ProjectRepositoryImpl();
}
```

### Understanding the Scopes

Now we'll demonstrate how these two scopes work. To this end, let’s create a single bean in _LsAppConfig_ class in a way that we have just seen:

```
@Bean
public IProjectRepository prototypeBean() {
    return new ProjectRepositoryImpl();
}
```

Note that, in order to avoid conflicts on bean injection, we need to comment out the _@Repository_ annotation in the _ProjectRepositoryImpl_ class.

Next, let's open the _ProjectServiceImpl_ class and inject a second instance of _ProjectRepositoryImpl:_

```
@Service
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private IProjectRepository projectRepository;

    @Autowired
    private IProjectRepository projectRepository2;
    
    // ...
    
}
```

In order to check how the beans are injected, in _ProjectServiceImpl_ we'll add a new method annotated with @_PostConstruct:_

```
@PostConstruct 
public void after() { 
    //
}
```

**Let's add a breakpoint in this method and start the application in debug mode.**

When the execution of the program stops at the breakpoint, we can see that _projectRepository_ and _projectRepository2_ actually refer to the same object in complete agreement with the definition of singleton.

If we now change the bean definition by specifying its scope as prototype:

```
@PostConstruct
public void after() {
}
```

And restart the application (in debug mode), we can see that those variables refer to different objects in complete agreement with the definition of prototype scope.

## Resources
- [Spring PostConstruct and PreDestroy Annotations](https://www.baeldung.com/spring-postconstruct-predestroy)
- [Spring Bean Scopes](https://www.baeldung.com/spring-bean-scopes)

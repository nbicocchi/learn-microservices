# Lifecycle of a Bean - Init and Destroy Hooks

In this lesson, we'll go through the basic concepts of the bean lifecycle in Spring.

The relevant module for this lesson is: [lifecycle-of-a-bean-end](https://github.com/nbicocchi/spring-boot-course/tree/module2/lifecycle-of-a-bean-end)

## Bean Lifecycle Phases

Basically, **the lifecycle of a Spring bean consists of 3 phases:**

1.  _initialization phase_
2.  _use phase_
3.  _destroy phase_

In this lesson, **we’ll focus on the _initialization_ and _destroy_ phases**, as they’re the most interesting ones from the point of view of dependency injection.

Our goal here is to understand not only the lifecycle, but also the [hooks](https://en.wikipedia.org/wiki/Hooking) in the framework connected to that lifecycle.

Of course, the _use_ phase is a phase in which an application normally spends the most of its time, and it’s the point of interest for the business logic.

## Initialization Phase

This phase consists of two main stages:

-   loading bean definitions
-   instantiating beans

Let’s create a first bean in the _AppConfig_ class:

```
@Bean
public BeanA beanA() {
    return new BeanA();
}
```

Let’s look at first hook that Spring provides to customize the bean creation. **Spring will run methods annotated with _@PostConstruct_ only once, just after the initialization of the bean properties.**

Let’s add this method to the _BeanA_ definition:

```
@PostConstruct
public void post() {
    // 
}
```

We can debug the application to make sure this gets called correctly.

Next, let’s explore a similar hook which works when we’re using the _@Bean_ annotation to define a bean.

First, let’s create our new bean - _BeanB_ and let’s provide it with a simple _initialize_ method:

```
public class BeanB {

    public void initialize() {
        log.info("Custom initializer is called.");
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

So far, we’ve seen how to customize the bean initialization using _@PostConstruct_ and _@Bean(initMethod=”..”)_. However, these are not the only possibilities. We’ll explore other options, like the _InitializingBean_ interface in other lessons.

The use phase is the phase in which normally an application lives the most of its time. The other lessons of the course consider this phase in full details. Therefore, let's go to the next phase.

## Destroy Phase

This is the final phase of an application. It’s activated when the application context becomes eligible for garbage collection.

At this point, all the beans get out of scope of the application and become eligible for [garbage collection](https://en.wikipedia.org/wiki/Garbage_collection_(computer_science)).

The destroy options are very similar to the initialization options we already explored.

First, **we have the _@PreDestroy_ annotation that we can add on a bean method, to indicate that method should be executed before the bean is destroyed:**

```
public class BeanC {
  
    private static Logger log = LoggerFactory.getLogger(BeanC.class);

    @PreDestroy
    public void preDestroy() {
        log.info("@PreDestroy annotated method is called.");
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

## Upgrade Notes

In Spring Boot 3.0, the transition from Java EE to Jakarta EE has been applied.

With this, several elements have migrated from _javax.\*_ packages to the corresponding _jakarta.\*_ ones. In fact, the import statements for the _@PostConstruct_ and _@PreDestroy_ annotations we introduce in this lesson would differ from the ones shown in the video with the newer version:

```
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
```

## Resources
- [Spring PostConstruct and PreDestroy Annotations](https://www.baeldung.com/spring-postconstruct-predestroy)

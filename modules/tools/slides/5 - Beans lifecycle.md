# Lifecycle and Scope of a Bean

## Bean Lifecycle Phases

Basically, **the lifecycle of a Spring bean consists of 3 phases:**

* initialization phase
* use phase
* destroy phase

**We focus on the _initialization_ and _destroy_ phases**, as they’re the most interesting ones from the point of view of dependency injection. Our goal here is to understand the lifecycle but also the [hooks](https://en.wikipedia.org/wiki/Hooking) in the framework connected to that lifecycle. 

### @Component

```java
@Component
public class BeanA {
    private static final Logger log = LoggerFactory.getLogger(BeanA.class);

    @PreDestroy
    public void preDestroy() {
        log.info("@PreDestroy method is called.");
    }

    @PostConstruct
    public void postConstruct() {
        log.info("@PostConstruct method is called.");
    }
}
```
### @Bean

```java
@Configuration
public class AppConfig {
    @Bean(initMethod="initialize", destroyMethod="destroy")
    public BeanB beanB() {
        return new BeanB();
    }
}
```

```java
public class BeanB {
    private static final Logger log = LoggerFactory.getLogger(BeanB.class);

    public void initialize() {
        log.info("Custom initialize() is called.");
    }

    public void destroy() {
        log.info("Custom destroy() is called.");
    }
}
```
## Bean Scopes

In Spring, **singleton** and **prototype** are two common bean scopes that define how beans are created and managed in the Spring container.

```java
@Configuration
public class AppConfig {
    @Bean(initMethod="initialize", destroyMethod="destroy")
    @Scope("singleton")
    public BeanB beanB() {
        return new BeanB();
    }

    @Bean(initMethod="initialize", destroyMethod="destroy")
    @Scope("prototype")
    public BeanC beanC() {
        return new BeanC();
    }
}
```

### Singleton Scope (default scope)
In the singleton scope, Spring creates **only one instance** of a bean per Spring container. This instance is shared across all parts of the application that request the bean. The bean is created when the Spring container is initialized (eager initialization by default), and it stays in memory throughout the entire lifecycle of the container.

### Prototype Scope
In the prototype scope, a **new instance** of the bean is created **every time** it is requested from the Spring container. Unlike singleton, the lifecycle of a prototype bean is short-lived. Once the bean is created and delivered by the container, the container does not manage the bean’s lifecycle any further (i.e., it won't handle destruction of the bean).

### Example

```java
@Component
public class BeanD {
    BeanB beanB1;
    BeanB beanB2;
    BeanC beanC1;
    BeanC beanC2;

    public BeanD(BeanB beanB1, BeanB beanB2, BeanC beanC1, BeanC beanC2) {
        this.beanB1 = beanB1;
        this.beanB2 = beanB2;
        this.beanC1 = beanC1;
        this.beanC2 = beanC2;
        // breakpoint here and debug
        // you will see that b1 and b2 are in fact references to the same object
        // while c1 and c2 are references to different objects
    }
}

```

## Resources
- [Spring PostConstruct and PreDestroy Annotations](https://www.baeldung.com/spring-postconstruct-predestroy)
- [Spring Bean Scopes](https://www.baeldung.com/spring-bean-scopes)

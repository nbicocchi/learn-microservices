# Defining Beans, Component Scanning and Bean Annotations

When the Spring application starts up, **Application Context** starts instantiating objects called _beans_ in Spring terminology to make them available to the ecosystem.

## Contributing Beans to the Context
We can create beans in several ways. 

The simplest stereotype annotation we can use is _@Component._ Basically, during the bootstrapping process, **Spring will scan for any classes annotated with _@Component_ and will instantiate them as beans.**

```java
@Component
public class ProductRepository {
    // ...
}
```

Another highly common option is a configuration class where we manually define a bean.

```java
@Configuration
public class PersistenceConfig {

    @Bean
    public ProductRepository productRepository() {
        return new ProductRepository();
    }
}
```

**The _@Configuration_ annotation indicates to Spring that this class needs to be processed by the Spring Container** because it will contribute bean definitions. 

By default, **Spring Boot loads all classes annotated with _@Component_, _@Configuration_ that are located in the same package as the main class (and sub-packages).**

## Stereotype Annotations
Besides _@Component_, there are a few more stereotype annotations that use _@Component_ under the hood and just bring an extra layer of semantics on top.

```java
@Repository
public class ProductRepository {
    // ...
}
```

```java
@Service
public class ProductService {
    // ...
}
```

```java
@RestController
@RequestMapping("/products")
public class ProductController {
    // ...
}
```

## Bean Lifecycle

Basically, **the lifecycle of a Spring bean consists of 3 phases:**

* initialization phase
* use phase
* destroy phase

**We focus on the _initialization_ and _destroy_ phases**, as they’re the most interesting ones from the point of view of dependency injection. 

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


## Resources
- [Spring Bean Scopes](https://www.baeldung.com/spring-bean-scopes)
- [Spring Component Scanning](https://www.baeldung.com/spring-component-scanning)
- [@Component vs @Repository and @Service in Spring](https://www.baeldung.com/spring-component-repository-service)
- [Spring Bean Annotations](https://www.baeldung.com/spring-bean-annotations)


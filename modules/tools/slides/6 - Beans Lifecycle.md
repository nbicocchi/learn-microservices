# Defining Beans, Component Scanning and Bean Annotations

When the Spring application starts up, **Application Context** starts instantiating objects called _beans_ in Spring terminology to make them available to the ecosystem.

## Contributing Beans to the Context
We can create beans in several ways. For this example, we’ll use a simple and highly common option: a configuration class where we manually define a bean.

Let's create a new _PersistenceConfig_ class:

```java
@Configuration
public class PersistenceConfig {

    @Bean
    public ProductRepository productRepository() {
        return new ProductRepository();
    }
}
```

**The _@Configuration_ annotation indicates to Spring that this class needs to be processed by the Spring Container** because it will contribute bean definitions to it. And, of course, the _@Bean_ annotation is one such actual bean definition. In our case, this is a bean named _productRepository,_ as that’s the name of the method.

By default, **Spring Boot loads all classes annotated with _@Bean_, _@Component_, _@Configuration_ etc that are located in the same package as the main class or in all sub-packages of this.** See the _@ComponentScan_ annotation options to extend the search to other packages.

## Spring Component Scanning
The simplest stereotype annotation we can use is _@Component._ Basically, during the bootstrapping process, **Spring will scan for any classes annotated with _@Component_ and will instantiate them as beans.**

```java
@Component
public class ProductRepository {
    // ...
}
```

Note that we never actually defined component scanning explicitly. This is because **_@SpringBootApplication_ is already using the annotation**. If we go to the _@SpringBootApplication_ definition (In Intellij: View > Jump to Source), we'll see that is includes the _@ComponentScan_ annotation.

## Stereotype Annotations
Besides _@Component_, there are, a few more stereotype annotations that aren’t actually that different. They use _@Component_ under the hood and just bring an extra layer of semantics on top.

For example, on the _ProductRepository_ we can replace _@Component_ with the _@Repository_ annotation:

```java
@Repository
public class ProductRepository {
    // ...
}
```

Nothing changes technically, but this fits better the exact semantics of this particular bean, since it's actually a repository. Similarly, we can use the _@Service_ and _@RestController_ annotation:

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
- [Spring Bean Scopes](https://www.baeldung.com/spring-bean-scopes)
- [Spring Application Context](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-basics)
- [Java-based Container Configuration](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-java)
- [Spring Component Scanning](https://www.baeldung.com/spring-component-scanning)
- [@Component vs @Repository and @Service in Spring](https://www.baeldung.com/spring-component-repository-service)
- [Spring Bean Annotations](https://www.baeldung.com/spring-bean-annotations)


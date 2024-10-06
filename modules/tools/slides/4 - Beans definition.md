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

By default, **Spring Boot loads all classes annotated with _@Bean_, _@Component_, _@Configuration_ etc that are located in the same package as the main class or in all sub-packages of this.**

## Spring Component Scanning

Now, let’s see how we can move from explicitly defining a bean to **letting the context discover that bean on its own**. As it's common in Spring, this can be achieved with the help of annotations. More specifically, **the _@ComponentScan_ annotation**. Then, for the classes we want to define as beans, we can use stereotype annotations.

The simplest stereotype annotation we can use is _@Component._ Basically, during the bootstrapping process, **Spring will scan for any classes annotated with _@Component_ and will instantiate them as beans.**

```java
@Component
public class ProductRepository {
    // ...
}
```

Note that we never actually defined component scanning explicitly. This is because **_@SpringBootApplication_ is already using the annotation**. If we go to the _@SpringBootApplication_ definition (In Intellij: View > Jump to Source), we'll see that is includes the _@ComponentScan_ annotation.

## Stereotype Annotations
Besides _@Component,_ there are, a few more stereotype annotations that aren’t actually that different. They use _@Component_ under the hood and just bring an extra layer of semantics on top.

For example, on the _ProductRepositoryImpl_ we can replace _@Component_ with the _@Repository_ annotation:

```java
@Repository
public class ProductRepository {
    // ...
}
```

Nothing changes technically, but this fits better the exact semantics of this particular bean, since it's actually a repository. Similarly, we can use the _@Service_ annotation for the _ProductService_:

```java
@Service
public class ProductService {
    // ...
}
```

## Resources
- [Spring Application Context](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-basics)
- [Java-based Container Configuration](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-java)
- [Spring Component Scanning](https://www.baeldung.com/spring-component-scanning)
- [@Component vs @Repository and @Service in Spring](https://www.baeldung.com/spring-component-repository-service)
- [Spring Bean Annotations](https://www.baeldung.com/spring-bean-annotations)


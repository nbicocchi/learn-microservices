# Dependency Injection

## What Is Inversion of Control?

**Inversion of Control is a principle in software engineering which transfers the control of portions of a program to an external container or framework**. We most often use it in the context of object-oriented programming.

In contrast with traditional OOP, in which our custom code makes calls to a library, IoC enables a framework to take control of the flow of a program and make calls to our custom code. To enable this, frameworks use abstractions with additional behavior built in. **If we want to add our own behavior, we need to extend the classes of the framework or plugin our own classes.**

We can achieve Inversion of Control through various mechanisms: 
* Strategy pattern
* Service Locator pattern
* Factory pattern
* **Dependency Injection pattern**

## The Spring IoC Container
The interface `org.springframework.context.ApplicationContext` represents the Spring IoC container which is **responsible for instantiating, configuring, and assembling beans**. It allows you to express the objects that compose your application and the rich interdependencies between such objects. Several implementations of the `ApplicationContext` interface are supplied out-of-the-box with Spring. 

The following diagram is a high-level view of how Spring works. Your application classes are combined with configuration metadata so that after the `ApplicationContext` is created and initialized, you have a fully configured and executable system or application.

![](images/container-magic.webp)

## Dependency Injection

_Class A has a dependency on Class B when it interacts with it in any way_

One option is for Class A to take on the responsibility of instantiating B by itself:

```java
public class A {
    private B bDependency;
  
    public A() {
        bDependency = new B();    
    }
}
```

Alternatively, that responsibility can be external, meaning the dependency comes from the outside:

```java
public class A {
    private B bDependency;
    
    public A(B bDependency) {
        this.bDependency = bDependency;
    }
}
```

**Injection is simply the process of injecting the dependency B in the object of type A.** Since the instantiation of the B dependency is no longer done in A, that responsibility will now belong to the framework. Separating the responsibility of instantiating a class from the logic in that class is a very useful concept:

* Leads to **a more loosely coupled system and adds flexibility** (dependencies can be swapped out at runtime).
* **Helpful in both application architecture, as well as testing, because DI make it easier to switch between different implementations of the dependency**. For example, we can pass in a mock of a dependency rather than a full dependency object.

## Dependency Injection Types

There are **three main ways to inject dependencies**:

* Constructor injection
* Setter injection
* Field injection

### Constructor Injection

In this case, we inject dependencies in a class via its constructor arguments (each argument represents a dependency that Spring will inject automatically).

Let’s take a look at our _ProductService_ where an object of _ProductRepository_ class is injected via a constructor argument:

```java
@Service
public class ProductService {
   private final ProductRepository productRepository;

   @Autowired
   public ProductService(ProductRepository productRepository) {
      this.productRepository = productRepository;
   }

   public Optional<Product> findByUuid(String uuid) {
      return productRepository.findByUuid(uuid);
   }

   public Iterable<Product> findAll() {
      return productRepository.findAll();
   }

   public Product save(Product product) {
      return productRepository.save(product);
   }

   public void delete(Product product) {
      productRepository.delete(product);
   }
}
```

Note that since we have a single constructor, the _@Autowired_ annotation is optional. If we define more than one constructor and we want one of them to inject dependencies on creating the bean, then we need to add _@Autowired_ on the required constructor.

### Setter Injection

In setter-based injection, **we inject dependencies using the setter methods** of the required dependencies declared as fields:
* **optional dependencies**
* **dependencies changes at runtime**

```java
@Service
public class ProductService {
    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }
}
```

### Field Injection

In field-based dependency injection **we inject dependencies using the _@Autowired_ annotation directly on fields**. This approach is [no longer recommended](https://www.baeldung.com/java-spring-field-injection-cons).

```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }
}
```


## The _@Qualifier_ and _@Primary_ Annotations

`@Qualifier` and `@Primary` are annotations used to handle the injection of dependencies when there are multiple beans of the same type, allowing more control over which specific bean is chosen by the framework.

`@Qualifier` is used to disambiguate between multiple beans of the same type. When Spring finds multiple beans that match a dependency, it throws an error unless you specify which one to use. By using `@Qualifier`, you can indicate the specific bean you want to inject.

Suppose you have two implementations of an interface `Vehicle`:

```java
public interface Vehicle {
    void start();
}

@Component
public class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car is starting");
    }
}

@Component
public class Bike implements Vehicle {
    @Override
    public void start() {
        System.out.println("Bike is starting");
    }
}
```

If you want to inject the `Car` bean into a class, you can use `@Qualifier`:

```java
@Component
public class Driver {
    private final Vehicle vehicle;

    @Autowired
    public Driver(@Qualifier("car") Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void drive() {
        vehicle.start();
    }
}
```

`@Primary` is used to specify which bean should be preferred when no `@Qualifier` is specified. If you have multiple beans of the same type and one is marked as `@Primary`, Spring will use that bean by default unless told otherwise.


```java
@Component
@Primary
public class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car is starting");
    }
}

@Component
public class Bike implements Vehicle {
    @Override
    public void start() {
        System.out.println("Bike is starting");
    }
}
```

Here, `Car` is marked with `@Primary`, so if you inject a `Vehicle` without specifying a `@Qualifier`, Spring will automatically choose the `Car` bean:

```java
@Component
public class Driver {
    private final Vehicle vehicle;

    @Autowired
    public Driver(Vehicle vehicle) { // No @Qualifier needed
        this.vehicle = vehicle;
    }

    public void drive() {
        vehicle.start();
    }
}
```

## General Best Practices for Dependency Injection

1. **Prefer constructor injection**:
    - Constructor injection makes the object’s dependencies clear and enforces immutability. It also makes unit testing easier since dependencies can be mocked and injected via the constructor.

2. **Handle optional dependencies with setter injection**:
    - Setter injection is ideal when some dependencies are optional. Constructor injection should be used for mandatory dependencies, while setter injection can be used for those that might not always be provided.

3. **Use `@Qualifier` and `@Primary` for multiple beans implementing the same interface**:
    - When you have multiple beans of the same type, use `@Qualifier` to specify which one to inject, or mark one of them with `@Primary` to make it the default.

4. **Avoid circular dependencies**:
    - Circular dependencies can cause issues with injection, especially with constructor injection. Spring will throw an error if it detects circular dependencies at runtime, so design your beans to avoid this.

## Circular Dependencies in Spring

A **circular dependency** occurs when two or more beans in a Spring application depend on each other, creating a cycle that Spring cannot resolve automatically. This situation typically arises when using **constructor-based dependency injection**, leading to a `BeanCurrentlyInCreationException`.

Consider the following two Spring beans:

```java
@Component
public class A {
    private final B b;

    @Autowired
    public A(B b) {
        this.b = b;
    }
}
```

```java
@Component
public class B {
    private final A a;

    @Autowired
    public B(A a) {
        this.a = a;
    }
}
```

Here, `A` depends on `B`, and `B` depends on `A`, creating a circular dependency.

### How Spring Handles Circular Dependencies

Spring can resolve circular dependencies **only if at least one of the dependencies is injected via a setter or field injection**, allowing the container to create one bean first and inject the other later.

**Setter Injection (Recommended)**

```java
@Component
public class A {
    private B b;

    @Autowired
    public void setB(B b) {
        this.b = b;
    }
}
```

```java
@Component
public class B {
    private A a;

    @Autowired
    public void setA(A a) {
        this.a = a;
    }
}
```

**`@Lazy` Annotation**

Another approach is marking one of the dependencies as `@Lazy`, delaying its initialization until it's actually needed.

```java
@Component
public class A {
    private final B b;

    @Autowired
    public A(@Lazy B b) {
        this.b = b;
    }
}
```

This tells Spring to inject `B` into `A` only when it is actually required, breaking the cycle.

## Resources
- [Inversion of Control Containers and the Dependency Injection pattern (Martin Fowler)](https://martinfowler.com/articles/injection.html)
- [Spring Dependency Injection Series](https://www.baeldung.com/spring-dependency-injection)



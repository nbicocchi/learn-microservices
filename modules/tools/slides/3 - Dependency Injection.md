# Dependency Injection

In this lesson, we'll focus on understanding the concept of Dependency Injection (DI) and how this work in Spring.

## What Is Inversion of Control?

**Inversion of Control is a principle in software engineering which transfers the control of objects or portions of a program to a container or framework**. We most often use it in the context of object-oriented programming.

In contrast with traditional programming, in which our custom code makes calls to a library, IoC enables a framework to take control of the flow of a program and make calls to our custom code. To enable this, frameworks use abstractions with additional behavior built in. **If we want to add our own behavior, we need to extend the classes of the framework or plugin our own classes.**

We can achieve Inversion of Control through various mechanisms such as: Strategy design pattern, Service Locator pattern, Factory pattern, and Dependency Injection (DI).

## The Spring IoC Container
The interface `org.springframework.context.ApplicationContext` represents the Spring IoC container which is **responsible for instantiating, configuring, and assembling beans**. It allows you to express the objects that compose your application and the rich interdependencies between such objects.

Several implementations of the `ApplicationContext` interface are supplied out-of-the-box with Spring. In standalone applications it is common to create an instance of [`ClassPathXmlApplicationContext`](http://static.springsource.org/spring/docs/current/api/org/springframework/context/support/ClassPathXmlApplicationContext.html) or [`FileSystemXmlApplicationContext`](http://static.springsource.org/spring/docs/current/api/org/springframework/context/support/FileSystemXmlApplicationContext.html).

**While XML has been the traditional format for defining configuration metadata you can instruct the container to use Java annotations or code** as the metadata format by providing a small amount of XML configuration to declaratively enable support for these additional metadata formats.

The following diagram is a high-level view of how Spring works. Your application classes are combined with configuration metadata so that after the `ApplicationContext` is created and initialized, you have a fully configured and executable system or application.

![](images/m2-container-magic.png)

## Dependency Injection

Before we can talk about Dependency Injection, let’s first define what a dependency is. Understanding this concept is critical not only for Spring, but for developing software in general. 

_Class A has a dependency on Class B when it interacts with it in any way_

One option is for Class A to take on the responsibility of instantiating B by itself:

```
public class A {
    private B bDependency;
  
    public A() {
        bDependency = new B();    
    }
}
```

Alternatively, that responsibility can be external, meaning the dependency comes from the outside:

```
public class A {
    private B bDependency;
    
    public A(B bDependency) {
        this.bDependency = bDependency;
    }
}
```

That is, in a nutshell, Dependency Injection (not to be confused with the the Dependency Inversion principle from SOLID).

**Injection is simply the process of injecting the dependency B in the object of type A.** Since the instantiation of the B dependency is no longer done in A, that responsibility will now belong to the framework. Separating the responsibility of instantiating a class from the logic in that class is a very useful concept:

* Leads to **a more loosely coupled system and to a lot of flexibility in the design** of that system, as now the dependency can be decided (or swapped out) at runtime.
* **Helpful in both application architecture, as well as testing, because DI make it easier to switch between different implementations of the dependency**. For example, we can pass in a mock of a dependency rather than a full dependency object.

## Dependency Injection Types

Now that we have a good understanding of how to create beans and make sure they get into the the Application Context, let’s have a look at defining relations between these beans. There are primarily **three ways to define or inject dependencies**:

* constructor injection
* setter injection
* field injection

### Constructor-Based DI

In constructor-based injection **we inject dependencies in a class via its constructor arguments**. Each constructor argument represents a dependency.

Spring will inject those dependencies in our class automatically.

Let’s take our _ProjectServiceImpl_ class as an example and inject _IProjectRepository_ in this class via a constructor argument:

```
@Service
public class ProjectServiceImpl implements IProjectService {

    private IProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImpl(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    
    // ...
}
```

The Spring container will fetch the _projectRepository_ bean for us and inject it in our service class.

Note that **since we have a single constructor, the _@Autowired_ annotation is optional. If we define more than one constructor and we want one of them to inject dependencies on creating the bean, then we need to add _@Autowired_ on the required constructor.**

Overall, constructor based dependency injection is clean and doesn't introduce any container specific classes, annotations or other dependencies.

### Setter-Based DI

In setter-based injection, **we inject dependencies using the setter methods** of the required dependencies declared as fields.

Let’s create a new service class implementation that will inject the repository dependency using setter injection:

```
@Service
public class ProjectServiceImplSetterInjection implements IProjectService {

    private IProjectRepository projectRepository;
    
    @Autowired
    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    
    // ...
}
```

The _setProjectRepository_ setter will be used to inject the appropriate _projectRepository_ bean in our class using the _@Autowired_ annotation added to the setter.

The container will inject the _projectRepository_ dependency after calling the service constructor.

### Field-Based DI

In field-based dependency injection **we inject dependencies using the _@Autowired_ annotation directly on fields**.

Let’s create a new service class implementation that will inject the repository dependency using _@Autowired_:

```
@Service
public class ProjectServiceImplAutowiring implements IProjectService {

    @Autowired
    private IProjectRepository projectRepository;
    
    // ...
}
```

This is all we need to do. The Spring container will fetch the appropriate bean _projectRepository_ and inject it in our service class.

The default way _@Autowired_ works is it matches the bean by-type, i.e. it will fetch the bean of type _IProjectRepository_ and inject it in our class.

### The _@Qualifier_ Annotation

Let's see what would happen if we had two repositories that implement our project repository interface.

We'll add a second implementation of the _ProjectRepository_ interface, similar to the first one, named _ProjectRepositoryImpl2_.

If we try to run the project now, the startup will fail, with a helpful error message, that shows us exactly what the issue is:

_"Parameter 0 of constructor in com.baeldung.ls.service.impl.ProjectServiceImpl required a single bean, but 2 were found."_

The framework also gives us a suggestion for how to fix the problem: either making one of the beans primary, or using the _@Qualifier_ annotation.

If there are multiple beans of a type, then we can control the specific bean that we have to inject by using the additional _@Qualifier_ annotation and give it the specific bean name to inject:

```
@Service
public class ProjectServiceImpl implements IProjectService {

    private IProjectRepository projectRepository;

    public ProjectServiceImpl(@Qualifier("projectRepositoryImpl2") IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    
    //...
}
```

Note that we're using the bean name: "_projectRepositoryImpl2_" even though we haven't defined this name explicitly.

This is because **Spring will generate a default name for each bean, based on the class name**, if we don't define one.

This name is simply the name of the class, starting with a lowercase. For example, for our _ProjectRepositoryImpl2_ class, the container will create a bean called “_projectRepositoryImpl2_”.

We’ve also learned how to define beans using the _@Bean_ annotation on factory methods. In this case, the method name becomes the bean name.

### The _@Primary_ Annotation

Another way to fix the issue above is to add the _@Primary_ annotation to one of our repository implementations.

**We use _@Primary_ to give higher preference to a bean when there are multiple beans of the same type.**

If we remove the _@Qualifier_ annotation from all our service implementations, and mark the _ProjectRepositoryImpl2_ class with _@Primary_, this bean will be injected into the services.

### Advantages and Disadvantages of the Injection Methods

There's no "absolute rule" here - all three injection methods are viable, but there is a simple rule of thumb you can follow:

-   use Constructor injection for all required dependencies
-   use Setter injection for optional dependencies, if any

**Constructor injection is slightly more verbose, but it has an important advantage over the others - it leads to more testable code**. The reason is simply that, with constructor injection, all the collaborators of the object are defined explicitly and need to be passed in during instantiation.  The other two methods make collaborators easy to miss.

**While field-based injection appears to be a very convenient way to inject dependencies into private fields, it should be avoided because it makes the dependent class tightly coupled to the Spring container for injecting the dependency.**

Let's see this with an example:

```
@Service
public class ProjectServiceImplAutowiring implements IProjectService {
    @Autowired
    private IProjectRepository projectRepository;
//..
}
```

In this case, if we want to instantiate _ProjectServiceImplAutowiring_ outside of Spring, we won’t be able to inject the dependency _IProjectRepository_ as we are heavily dependent on the Spring container to do it for us. By comparison, when using constructor or setter-based injection, we can use the constructor or the setter method to manually inject the dependency.

**This is a very common issue we face when we have to write a plain JUnit test for our class.**

Consider the example below, where we’re testing the _ProjectServiceImpl_ class:

```
public class ProjectServiceImplTest {

    private ProjectServiceImpl projectServiceImpl = new ProjectServiceImpl(new ProjectRepositoryImpl());

    @Test
    public void givenNewProject_thenSavedSuccess() {
        Project newProject = new Project("First Project", LocalDate.now());

        assertNotNull(projectServiceImpl.save(newProject));
    }
}
```

Since we’re using constructor based injection in _ProjectServiceImpl,_ we’re able to manually inject the instance of _ProjectRepositoryImpl_ into _ProjectServiceImpl_ and easily test our code.

However, if we had to write a similar test for _ProjectServiceImplAutowiring,_ where we're using field based injection, there is no easy way to inject _ProjectRepositoryImpl,_ hence making our code untestable without using the Spring container.

## Resources
- [Inversion of Control and Dependency Injection with Spring](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)
- [Inversion of Control Containers and the Dependency Injection pattern (Martin Fowler)](https://martinfowler.com/articles/injection.html)
- [Spring Dependency Injection Series](https://www.baeldung.com/spring-dependency-injection)
- [Guide to Spring @Autowired](https://www.baeldung.com/spring-autowire)



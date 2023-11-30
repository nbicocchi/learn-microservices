# Simple Wiring and Injection

In this lesson, we'll have a look at how to wire beans together using Dependency Injection.

The relevant module you need to import when you're starting with this lesson is: [m2-simple-wiring-and-injection-start](https://github.com/eugenp/learn-spring/tree/module2/m2-simple-wiring-and-injection-start)

If you want have a look at the fully implemented lesson, as a reference, feel free to import: [m2-simple-wiring-and-injection-end](https://github.com/eugenp/learn-spring/tree/module2/m2-simple-wiring-and-injection-end)

## Dependency Injection

Now that we have a good understanding of how to create beans and make sure they get into the the Application Context, let’s have a look at defining relations between these beans

We talked about this in a general way when we first introduced DI. Let’s make that more concrete and see how we can set up a dependency between two beans.

The container injects the dependencies for us, but we have to define those dependencies.

There are primarily **three ways to define or inject dependencies**:

* constructor injection
* setter injection 
* field injection

## Constructor-Based DI

In constructor-based injection **we inject dependencies in a class via its constructor arguments**. Each constructor argument represents a dependency.

Spring will inject those dependencies in our class automatically.

Let’s take our _ProjectServiceImpl_ class as an example and inject _IProjectRepository_ in this class via a constructor argument:

```
@Service
public class ProjectServiceImpl implements IProjectService {

    private IProjectRepository projectRepository;

    public ProjectServiceImpl(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    
    // ...
}
```

The Spring container will fetch the _projectRepository_ bean for us and inject it in our service class.

Note that **since we have a single constructor, the _@Autowired_ annotation is optional**.

If we define more than one constructor and we want one of them to inject dependencies on creating the bean, then we need to add _@Autowired_ on the required constructor.

Overall, constructor based dependency injection is clean and doesn't introduce any container specific classes, annotations or other dependencies.

## Setter-Based DI

In setter-based injection, **we inject dependencies using the setter methods** of the required dependencies declared as fields.

Let’s create a new service class implementation that will inject the repository dependency using setter injection:

```
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

Next, let’s define the service in our configuration class _AppConfig:_

```
@Configuration
public class LsAppConfig {

    @Bean
    public IProjectService projectServiceImplSetterInjection() {
        return new ProjectServiceImplSetterInjection();
    }
}
```

The container will inject the _projectRepository_ dependency after calling the service constructor.

## Field-Based DI

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

Next, we'll see what happens if we’re trying to inject a bean by interface and we have multiple candidate beans that implement that interface.


## _@Qualifier_

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

## Advantages and Disadvantages of the Injection Methods

There's no "absolute rule" here - all three injection methods are viable, but there is a simple rule of thumb you can follow:

-   use Constructor injection for all required dependencies
-   use Setter injection for optional dependencies, if any

Constructor injection is slightly more verbose, but it has an important advantage over the others - it leads to more testable code.

The reason is simply that, with constructor injection, all the collaborators of the object are defined explicitly and need to be passed in during instantiation.

The other two methods make collaborators easy to miss.

## The _@Primary_ Annotation

Another way to fix the issue above is to add the _@Primary_ annotation to one of our repository implementations.

**We use _@Primary_ to give higher preference to a bean when there are multiple beans of the same type.**

If we remove the _@Qualifier_ annotation from all our service implementations, and mark the _ProjectRepositoryImpl2_ class with _@Primary_, this bean will be injected into the services.

## Disadvantages of Field-based Dependency Injection (extra)

While field-based injection appears to be a very convenient way to inject dependencies into private fields, it should be avoided because **it makes the dependent class tightly coupled to the Spring container** for injecting the dependency.

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

# Resources
- [Intro to Inversion of Control and Dependency Injection with Spring](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)
- [Guide to Spring @Autowired](https://www.baeldung.com/spring-autowire)

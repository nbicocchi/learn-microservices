# Debugging and Solving Wiring Exceptions

Previously, we looked at how to create beans and use them in other Spring-managed components. However, problems can arise trying to wire beans, especially when we first start developing with Spring, or when we have to deal with a complex application context.

In this lesson, we’ll look at three common wiring exceptions and analyze how we can solve them. Namely, we’ll examine:

-   _NoSuchBeanDefinitionException_ \- this is thrown whenever we try to use a bean that Spring doesn't find in the application context
-   _NoUniqueBeanDefinitionException_ \- this happens when there are multiple beans matching the one we want to wire in
-   _BeanCurrentlyInCreationException_ \- this is an exception that usually appears when there is a circular dependency in the context

The relevant module for this lesson is: [debugging-and-solving-wiring-exceptions-end](https://github.com/nbicocchi/spring-boot-course/tree/module2/debugging-and-solving-wiring-exceptions-end)

## Problem 1: Spring Cannot Find the Requested Bean

First, we’ll explore what happens **when we try to use a bean that isn’t defined in the Spring application context.**

**This usually happens when we forget to add the @Component annotation or one of its specializations to a class, or the @Bean annotation to a configuration method.**

To simulate this, we’ll temporarily remove the _@Repository_ annotation from our _ProjectRepositoryImpl_ class:

```
public class ProjectRepositoryImpl implements IProjectRepository {
    // …
}
```

Now let’s launch the application, and see the outcome in the logs:

```
ERROR 3482 — [           main] o.s.b.d.LoggingFailureAnalysisReporter   : 
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of constructor in com.baeldung.ls.service.impl.ProjectServiceImpl
  required a bean of type 'com.baeldung.ls.persistence.repository.IProjectRepository' that could not be found.

The injection point has the following annotations:
    - @org.springframework.beans.factory.annotation.Qualifier(value="projectRepositoryImpl")

Action:

Consider defining a bean of type 'com.baeldung.ls.persistence.repository.IProjectRepository' in your configuration.
```

As we can see, **the framework translates the exception that’s thrown to a more friendly and useful message**. If we set up a breakpoint in the “report” method of the class that’s logging this message, _LoggingFailureAnalysisReporter_, and launch the application once again in debug mode, we'll be able to verify that the exception is in fact a _NoSuchBeanDefinitionException_:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/b0Kgu0yTSiHONyzmyCQu)

We can also appreciate that if DEBUG level logs are enabled for this class, then the exception would also be included in the output.

Furthermore, the suggested action in the printed error message gives us a hint on how to solve the problem. In this case, adding the _@Repository_ annotation back to our implementing class will do the trick:

```
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {
    // …
}
```

**Another reason why Spring might not find the requested bean is if we don’t scan the package where the bean is defined**. For instance, we might erroneously set up a _@ComponentScan_ annotation to scan just the service package in our main class:

```
@SpringBootApplication
@ComponentScan("com.baeldung.ls.service")
public class LsApp {

    public static void main(final String... args) {
        SpringApplication.run(LsApp.class, args);
    }
}
```

Running the application again at this point will fail in a similar way.

Of course, in this case, the solution is to either start the scanning from a root package in the project so that everything is included or if we have a more complicated setup, to make sure all packages containing beans that we intend Spring to manage to be explicitly included in our configuration:

```
@SpringBootApplication
@ComponentScan({"com.baeldung.ls.service", "com.baeldung.ls.persistence.repository"})
public class LsApp {
    // …
}
```

Naturally, there could be other causes why a bean isn’t included in the context, such as if it’s loaded based on a condition, or based on the active profile.

## Problem 2: Conflicting Bean Definitions

A second problem, and one we briefly touched on in a previous lecture, is **when two or more beans match the one we try to wire in**.

**One way this problem can arise is to have two beans that implement the same interface. If we wire the interface into a class, Spring won’t know which of the implementations to use.**

Let’s force this error by adding a second repository to our project that also implements IProjectRepository in this way:

```
@Repository
public class ProjectRepositoryBImpl implements IProjectRepository {

    @Override
    public Optional<Project> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Project save(Project project) {
        return null;
    }
}
```

Now let's try to launch our project and inspect the exception that’s triggered. This time the _LoggingFailureAnalysisReporter_ shows the following:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/X6Y0WdW9Smep2S6fuKax)

Looking at the logs, we can see further information on the cause and how to solve it:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of constructor in com.baeldung.ls.service.impl.ProjectServiceImpl
  required a single bean, but 2 were found: [...]
```

The error message from Spring is clear, and there’s also a very straightforward suggested solution:

```
Action:

Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans,
  or using @Qualifier to identify the bean that should be consumed
```

**Therefore, one solution is to mark one of our repositories as _@Primary_ if it’s the one we want to default to in case of a conflict.** For example:

```
@Repository
@Primary
public class ProjectRepositoryImpl implements IProjectRepository {
    // …
}
```

**Another option is to leave the responsibility of deciding between the two implementations to the class that wants to use them, instead of defining a default.**

**For this, we can add the @Qualifier annotation** to our _ProjectServiceImpl_ constructor:

```
@Service
public class ProjectServiceImpl implements IProjectService {

    private IProjectRepository projectRepository;

    public ProjectServiceImpl(@Qualifier("projectRepositoryImpl") IProjectRepository  projectRepository) {
        this.projectRepository = projectRepository;
    }
    // ...
}
```

Remember that beans are referred to using the class name with the first letter in lowercase.

## Problem 3: Circular Dependency

A third common wiring exception arises **when there’s a circular dependency in the context**. That is, where bean A depends on bean B, which in turn depends on bean A to be initialized.

Spring tries to decide the order in which to create beans by analyzing how they’re dependent, and if they both depend on each other, Spring cannot decide which should be created first.

Both beans need to wait for the other to finish first, and since that doesn’t work, a _BeanCurrentlyInCreationException_ is thrown.

Let’s simulate this scenario by adding two _Service_ classes:

```
@Service
public class ProjectServiceA {
    
    private ProjectServiceB projectServiceB;

    public ProjectServiceA(ProjectServiceB projectServiceB) {
        this.projectServiceB = projectServiceB;
    }

}

@Service
public class ProjectServiceB {

    private ProjectServiceA projectServiceA;

    public ProjectServiceB(ProjectServiceA projectServiceA) {
        this.projectServiceA = projectServiceA;
    }
}
```

Now let’s run the application and check the exception in the _LoggingFailureAnalysisReporter_:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/c8m3eaAToaSUZiXk8v89)

And the printed message in the console:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

The dependencies of some of the beans in the application context form a cycle:

┌─────┐
|  projectServiceA defined in file [.../com/baeldung/ls/serviceProjectServiceA.class]
↑ 	↓
|  projectServiceB defined in file [.../com/baeldung/ls/service/ProjectServiceB.class]
```

For this problem, it’s more difficult to suggest a standard solution because **it’s often caused by architecture and design flaws**. This is also what Spring points to in their suggested action:

```
Action:

Relying upon circular references is discouraged and they are prohibited by default.
  Update your application to remove the dependency cycle between beans.
  As a last resort, it may be possible to break the cycle automatically by setting spring.main.allow-circular-references to true.
```

Sometimes, in cases like this, a larger rewrite or code restructuring is needed. For example, we could extract code sections, or add a new bean C that both beans A and B can depend on.

Of course, redesigning is not always a possibility, especially in the short term, but luckily for us, there are other options that can help us patch the issue in a more straightforward manner. If we enable the _spring.main.allow-circular-references_ property as suggested in the logs, the framework will attempt to automatically break the cycle, but even then there might be cases in which this is not possible. In fact, our example is one such case.

An important point here is that this problem arises when Spring is attempting to create the two interdependent beans at the same time. But if we hold off on fully injecting one bean into the other until it’s needed, then it doesn’t interfere with context loading during startup. **One way to tell Spring to not fully initialize the dependent bean during startup, and instead create a proxy, is to use the _@Lazy_ annotation.**

```
@Service
public class ProjectServiceA {

    private ProjectServiceB projectServiceB;

    public ProjectServiceA(@Lazy ProjectServiceB projectServiceB) {
        this.projectServiceB = projectServiceB;
    }
}
```

Using _@Lazy_ tells Spring to only fully create the injected bean when it’s needed, and this means _ProjectServiceA_ doesn’t need to wait for _ProjectServiceB_ to be created.

As we can see in all three cases, even though the message presented by the framework is useful to figure out the cause of the error in the Spring context, we still need to understand how the framework works and what mechanisms it offers in order to come up with the solution that best suits our needs. This is what we want to achieve in this course.

As a matter of fact, in this lesson, we put into practice several of the concepts we’ve explored in previous lessons to be able to resolve the issues and start the application successfully.

## Resources
- [NoSuchBeanDefinitionException](https://www.baeldung.com/spring-nosuchbeandefinitionexception)
- [Circular Dependencies in Spring](https://www.baeldung.com/circular-dependencies-in-spring)
- [Spring Primary Annotation](https://www.baeldung.com/spring-primary)

# Exploring the Scopes of Spring Beans

In this lesson, we'll consider the different scopes of beans in Spring.

The relevant module you need to import when you're starting with this lesson is: [scopes-of-spring-beans-start](https://github.com/eugenp/learn-spring/tree/module2/scopes-of-spring-beans-start)

If you want have a look at the fully implemented lesson, as a reference, feel free to import: [scopes-of-spring-beans-end](https://github.com/eugenp/learn-spring/tree/module2/scopes-of-spring-beans-end)

## Scopes of Beans

Basically, **Spring scopes allow us to control the life-cycle and visibility of beans**. When a bean is created, we can define its scope by means of the _@Scope_ annotation.

There are six types of bean scopes in Spring:

-   **_singleton_** - with this scope, the container creates a single instance of a bean. All requests for such a bean will return the same object, which is cached. This is the default scope if no other scope is specified.
-   **_prototype_** - with this scope, the container will create a new instance every time it’s requested.
-   **_request_**_,_ **_session_**_,_ **_application_**_,_ **_websocket_** - these scopes are available only in a web-aware application context and are less often used in practice. We’re have a look at these in a later lesson when we talk about web applications.

Let's now consider the first two scopes. We'll use the _ProjectRepositoryImpl_ class to create a bean for each scope.

## Singleton Scope

In the previous lessons, we've seen how to define a bean in a class decorated with the _@Configuration_ annotation. Let's open _LsAppConfig.java_ and define a bean with the singleton scope:

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

The 2 declarations above are equivalent.

Instead of typing the String value for the scope, we can also use a built-in constant:

```
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
```

## Prototype Scope

Now, let's define a bean with the scope prototype. To this end, we'll update the bean we've just created by changing the "singleton" scope and bean name into "prototype":

```
@Bean
@Scope("prototype")
public IProjectRepository prototypeBean() {
    return new ProjectRepositoryImpl();
}
```

## Understanding the Scopes

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

JAVA

`@PostConstruct public void after() { }`

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
- [Spring Bean Scopes](https://www.baeldung.com/spring-bean-scopes)

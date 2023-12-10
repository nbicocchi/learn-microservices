# @Resource and @Inject

In this lesson, we'll discuss the possibility to wire the beans into the application context using the _@Resource_ and _@Inject_ annotations.

The relevant module for this lesson is: [spring-wiring-resource-and-inject-end](https://github.com/nbicocchi/spring-boot-course/tree/module2/spring-wiring-resource-and-inject-end)

## Wiring Annotations

In addition to the standard Spring annotations for injecting dependencies, Spring also supports the _@Resource_ and _@Inject_ annotations introduced to Java EE in JSR-250 and JSR-330 correspondingly. Similar to the _@Autowired_ annotation, these ones also serve to inject the beans into the application context.

Let's consider both of them in more detail and let's see some examples of their use.

## _@Resource_

According to the JSR-250 specificaction, **the _@Resource_ annotation serves to identify the target component by its unique name**. This annotation can be used at the field and method level, but not at the constructor level.

In order to demonstrate the usage of this annotation, let's create a class _ProjectServiceResourceImpl_ that implements the _IProjectService_ interface:

```
@Service
public class ProjectServiceResourceImpl implements IProjectService {

    // ...

    @Resource
    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        LOG.info("wired projectRepository instance: {}", projectRepository);
    }
}
```

As we can easily see, the implementation is very similar to the existing _ProjectServiceImpl_ class. The only difference is how we've injected the _IProjectRepository_ bean by means of the _@Resource_ annotation. Note that **the annotation is applied to the setter method.** Therefore, we don't need the constructor in order to inject the dependency.

In order to make sure that the dependency is injected, let's add a breakpoint inside the setter method and start up the application in debug mode. We can see that while starting, the application gets paused inside the setter method and the _projectRepository_ gets instantiated as expected. When we resume the flow, we can see the application context is constructed without errors which means that the injection is successful.

In case there are multiple beans of the same type present, we can **specify the bean that we need to inject by providing the _name_ attribute of the _@Resource_ annotation**:

```
@Resource(name = "projectRepositoryImpl")
public void setProjectRepository(IProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
    LOG.info("wired projectRepository instance: {}", projectRepository);
}
```

## _@Inject_

Like the _@Autowired_ annotation, **the _@Inject_ annotation can be used at the field and method level as well as at the constructor level**.

In order to be able to use it, we should include the corresponding artifact in our _pom.xml_:

```
<dependency>
    <groupId>javax.inject</groupId>
    <artifactId>javax.inject</artifactId>
    <version>1</version>
</dependency>
```

Now, let's illustrate how we can use this annotation. Similar to what we did for the _@Resource_ annotation example, we create class a class _ProjectServiceInjectImpl_ that implements _IProjectService_ interface:

```
@Service
public class ProjectServiceInjectImpl implements IProjectService {
    
    // ...

    @Inject
    public ProjectServiceInjectImpl(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        LOG.info("wired projectRepository instance: {}", projectRepository);
    }

    // ...
}
```

Notice that **in this case, we've annotated the constructor**.

In order to make sure that everything works correctly, let's add a breakpoint in the constructor and start the application in debug mode. We can see that the application gets paused at the breakpoint and an _ProjectRepositoryImpl_ gets injected into the constructor. When we resume the execution, we see that the application context gets created without errors.

In case there are multiple beans of the same type in the context, **we can avoid ambiguity by adding the _@Named_ annotation** from the _javax.inject.Named_ package that specifies the name of the bean we want to inject:

```
@Inject
@Named("projectRepositoryImpl")
public ProjectServiceInjectImpl(IProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
    LOG.info("wired projectRepository instance: {}", projectRepository);
}
```

## Resources
- [Using JSR 330 Standard Annotations](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-standard-annotations)
- [Injection with @Resource](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-resource-annotation)

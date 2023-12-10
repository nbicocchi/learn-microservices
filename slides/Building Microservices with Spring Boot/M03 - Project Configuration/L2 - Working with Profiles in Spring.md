# Working with Profiles in Spring

In this lesson, we'll learn how to use Spring profiles for creating beans that are only enabled when a specific profile is activated.

The relevant module for this lesson is: [working-with-profiles-end](https://github.com/nbicocchi/spring-boot-course/tree/module3/working-with-profiles-end)

## Creating Profile-Specific Beans

Let’s take an example of maintaining two implementations of the repository layer in our app: for the dev and actual production environments.

A common use case for this is having a dev repository layer implementation that will perform all activity [in-memory](https://en.wikipedia.org/wiki/In-memory_database), while the production implementation can use a persistent database.

Using Spring profiles we can create these different beans and activate them accordingly on different environments.

**Any bean can be restricted to a specific profile by annotating the class with _@Profile_ annotation and specifying the profile name.**

Let’s create our bean for the dev environment; we’ll add the _@Profile_ annotation directly on our _ProjectRepositoryImpl_ class:

```
@Profile("dev")
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    @Override
    public Optional<Project> findById(Long id) {
        LOG.info("Retrieving Project using ProjectRepositoryImpl");
        return projects.stream().filter(p -> p.getId() == id).findFirst();
    }

   // other methods
}
```

This _ProjectRepositoryImpl_ bean will only be loaded when the “_dev_” profile will be active.

Similarly, let’s create our bean for the Production environment:

```
@Profile("prod")
@Repository
public class ProjectRepositoryDBBasedImpl implements IProjectRepository {

    @Override
    public Optional<Project> findById(Long id) {
        LOG.info("Retrieving Project using ProjectRepositoryDBBasedImpl");
        return projects.stream().filter(p -> p.getId() == id).findFirst();
    }
	
    // other methods
}
```

Note that we're not using actual implementations here; instead we've added logs to differentiate the two implementations.

**It's worth noting that if a bean is not annotated with _@Profile_, it will be enabled with all profiles.**

## The Default Profile

First, let’s set a breakpoint in both of our repositories. Then we'll debug the application and check the logs:

_No active profile set, falling back to default profiles: default_

We can see that the a default profile actually called “default” is activated. However, the breakpoints didn't fire and the application failed to start.

This is because it was not able to find any _projectRepository_ bean since both of them are disabled now.

## Activating Profiles

A specific profile can be activated programmatically, through configuration or by using system parameters.

**Activating a specific profile using a property is the simplest and most popular way, so we'll use this method in our example as well.**

In our default _application.properties_ file, let's add the property:

```
spring.profiles.active=dev
```

If we debug the app now, we'll notice that we hit the right breakpoint as the right bean is now active.

Also, the log message has changed:

_The following profiles are active: dev_

This confirms us that our profile was correctly activated.

We can also activate multiple profiles by using comma-separated list of profiles as the property value:

```
spring.profiles.active=dev,qa
```

## Using _@Profile_ with _@Bean_ (extra)

In addition to using the _@Profile_ annotation with all the stereotype annotations like _@Repository_, we can also use it with the _@Bean_ annotation.

The example configuration below activates either of the _beanA_ objects based on the active profile:

```
@Configuration
public class AppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Bean("beanA")
    @Profile("prod")
    BeanA prodBeanA() {
        LOG.info("Returning prodBeanA for prod profile");
        return new BeanA("prod");
    }

    @Bean("beanA")
    @Profile("dev")
    BeanA devBeanA() {
        LOG.info("Returning devBeanA for dev profile");
        return new BeanA("dev");
    }
}
```

## Profiles and the Spring _Environment_ (extra)

The _Environment_ interface is an abstraction integrated in the container, which can be used to get the application’s properties and the profile information.

Let's see how we can use it to determine the active and default profiles:

```
@Configuration
public class AppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private Environment environment;

    @PostConstruct
    void post() {
         LOG.info("Active Profiles: {}", environment.getActiveProfiles());
         LOG.info("Default Profiles: {}", environment.getDefaultProfiles());	 	
    }
}
```

## Resources
- [Spring Profiles](https://www.baeldung.com/spring-profiles)

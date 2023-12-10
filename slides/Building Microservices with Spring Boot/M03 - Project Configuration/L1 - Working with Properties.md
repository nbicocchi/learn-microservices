# Working with Properties

In this lesson, we'll have a look at working with properties in Spring and Spring Boot.

The relevant module for this lesson is: [working-with-properties-end](https://github.com/nbicocchi/spring-boot-course/tree/module3/working-with-properties-end)

## Configuration Using Properties
By now we have a basic understanding that we can create configurable Spring Boot applications using its support for flexible externalized configuration.

In our examples, we'll focus on using a properties file for configuration, as it's the most common format.

However, other formats like [yaml](https://en.wikipedia.org/wiki/YAML) files, environment variables and command lines arguments can also be used to configure our app.

Configuration using properties files helps in changing the behaviour of an application through a properties file.

We can change and control the value of fields in a class using properties files.

The entries in properties file are defined as key-value pairs, for e.g.:

*propertyName=propertyValue*

Then, these value can be injected in our class fields:

*@Value("${propertyName}")*

## Defining Properties
To understand all of this, we'll use an example of a repository which, before saving a new *Project *object*,* will create an *internalId* by adding a prefix and a suffix to user given *id.*

Let's understand how to create properties that we want to inject in our class.

By default, Spring Boot reads properties from the *application.properties* file placed in the *src/main/resources* folder of a Maven app.

We'll create the *application.properties* file, and add the properties:

```
project.prefix=PRO 
project.suffix=123
```

## Using the *@Value* Annotation to Inject Properties
To read the values of *project.prefix *and *project.suffix *from the properties file, we'll declare the fields in our class that should hold these values and annotate them with the *@Value* annotation.

Let's declare the fields that should hold the values of prefix and suffix in our *ProjectRepositoryImpl* class:

```
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    @Value("${project.prefix}")
    private String prefix;

    @Value("${project.suffix}")
    private Integer suffix;
    
    // ...
}
```
We can inject different types of properties, such as: *String, boolean, int, float*, *Date, Collections, Map.*

Note that here we've used *prefix* as a *String* type and *suffix* as an *Integer*.

## Using the Properties

Let's also add the logic of creating a new *id*:

```
private void updateInternalId(Project project) {
    LOG.info("Prepending Prefix " + prefix);
    LOG.info("Appending Suffix " + suffix);

    project.setInternalId(prefix + "-" + project.getId() + "-" + suffix);

    LOG.info("Generated internal id " + project.getInternalId());
}
```

And that's it; the Spring container will automatically inject the values of these properties in our fields by reading them from the properties file.

Let's test our properties injection, by creating a new *Project *object and see if we are able to generate the internal id by appending the *prefix *and *suffix.*

We'll modify our *LsApp* class to create a new *Project *object and save it:

```
@SpringBootApplication
public class LsApp {

    @Autowired
    private IProjectService projectService;
    
    @PostConstruct
    public void postConstruct() {
        Project project = new Project("My First Project", LocalDate.now());
        projectService.save(project);
    }
    
    // ...
}
```

Note: you'll notice the Project constructor signature differs from the one used in the video. That's because we improved its logic in the code to autogenerate the id incrementally (which better simulates the usual database behavior).

Now, when we run our app, we'll see the logs:

```
Prepending Prefix PRO
Appending Suffix 123
Generated internal id PRO-1-123
```

This shows that our properties were correctly injected.

## Boot Highlight

Let's briefly highlight the Spring Boot specific aspects used in this lesson.

As we've said above, by default, Spring Boot loads the properties from file *application.properties*. If we're creating a pure Spring application, we would have to explicitly indicate the file from which the properties should be loaded. We can do this by adding the following annotation:

*@PropertySource("classpath:application.properties")*

to any file subject to the component scan.

## Spring *Environment* (extra)
The [*Environment*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/Environment.html) interface represents "the environment in which the current application is running".

It contains information about the application's properties and profiles.

We can use the *Environment* API to search properties that are available in the application's environment:

```
@Configuration
public class AppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);
    
    @Autowired
    private Environment environment;
    
    @PostConstruct
    private void postConstruct(){
        LOG.info("project suffix: {}", environment.getProperty("project.suffix"));    
    }
}
```

In this example, the *Environment *object performs a search over a set of *PropertySource* objects to find the value of *"project.suffix".*

## Spring Property Sources (extra)

*The PropertySource class *is a simple abstraction of any source of key value pairs in a standalone Spring application.

The *Environment* object is configured with 2 default *PropertySource* objects:

-   the JVM system properties - *System.getProperties()*
-   the system environment variables -  *System.getenv()*

In a web application, the *Environment *contains some additional property sources, such as: *ServletConfig* parameters and *ServletContext* parameters.

A Spring Boot application is configured with even more property sources; for example

-   *application.properties* and yaml variants
-   *application-{profile}.properties *

These are only a few of a multitude of other property sources that Spring supports, implementing a well-thought ordering to allow sensible overriding.

The [official documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config) lists all these property sources.

## Resources
- [Properties with Spring and Spring Boot](https://www.baeldung.com/properties-with-spring)
- [Spring Environment JavaDoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/Environment.html)
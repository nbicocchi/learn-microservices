# Working with Properties

## Configuration Using Properties
By now we have a basic understanding that we can create configurable Spring Boot applications using its support for flexible externalized configuration.

In our examples, we'll focus on using a properties file for configuration, as it's the most common format. However, other formats like [yaml](https://en.wikipedia.org/wiki/YAML) files, environment variables and command lines arguments can also be used to configure our app.

Configuration using properties files helps in changing the behaviour of an application through a properties file. We can change and control the value of fields in a class using properties files. The entries in properties file are defined as key-value pairs, for e.g.:

```
propertyName=propertyValue
```

Then, these value can be injected in our class fields:

```
@Value("${propertyName}")
```

To understand all of this, we'll use an example of a repository which, before saving a new *Project* object, will create an *internalId* by adding a prefix and a suffix to user given *id.*

By default, Spring Boot reads properties from the *application.properties* file placed in the *src/main/resources* folder of a Maven app.

We'll create the *application.properties* file, and add the properties:

```
project.prefix=PRO 
project.suffix=123
```

## Using the @Value Annotation to Inject Properties
To read the values of *project.prefix* and *project.suffix* from the properties file, we'll declare the fields in our class that should hold these values and annotate them with the *@Value* annotation.

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
We can inject different types of properties, such as: *String, boolean, int, float*, *Date, Collections, Map.* Note that here we've used *prefix* as a *String* type and *suffix* as an *Integer*.

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

Let's test our properties injection, by creating a new *Project* object and see if we are able to generate the internal id by appending the *prefix* and *suffix*.

We'll modify our *LsApp* class to create a new *Project* object and save it:

```
@SpringBootApplication
public class LsApp implements ApplicationRunner {
    public static final RandomGenerator RND = RandomGenerator.getDefault();

    IProjectRepository projectRepository;

    public LsApp(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(LsApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        projectRepository.save(new Project("P1", LocalDate.now()));
        projectRepository.save(new Project("P2", LocalDate.now()));
        projectRepository.save(new Project("P3", LocalDate.now()));
    }
}

```

Now, when we run our app, we'll see the logs:

```
Prepending Prefix PRO
Appending Suffix 123
Generated internal id PRO-1-123
```

This shows that our properties were correctly injected.

## Spring Environment
The [*Environment*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/Environment.html) interface represents *the environment in which the current application is running*. **It contains information about the application's properties and profiles**. We can use the *Environment* API to search properties that are available in the application's environment:

```
@Configuration
public class AppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);
    
    @Autowired
    private Environment environment;
    
    @PostConstruct
    private void postConstruct(){
        LOG.info("project prefix: {}", environment.getProperty("project.prefix"));
        LOG.info("project suffix: {}", environment.getProperty("project.suffix"));  
    }
}
```

In this example, the *Environment* object performs a search over a set of *PropertySource* objects to find the value of *"project.suffix".*

The *Environment* object is configured with 2 default *PropertySource* (an abstraction of any source of key value pairs) objects:

-   the JVM system properties - *System.getProperties()*
-   the system environment variables -  *System.getenv()*

A Spring Boot application is configured with even more property sources; for example

-   *application.properties* and yaml variants
-   *application-{profile}.properties*

These are only a few of a multitude of other property sources that Spring supports, implementing a well-thought ordering to allow sensible overriding. The [official documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config) lists all these property sources.

## Spring Boot Default Properties

At this point, we know Spring Boot helps an application get up and running really quickly by providing a default configuration. We’ve also learned that the framework supports externalizing the configuration of our application using properties.

Well, in order to be flexible and able to back off and allow for the definition of custom behavior, **Spring Boot itself bases most of its autoconfiguration aspects on application properties that we can define or override ourselves.**

### Default Boot Properties: Port Number

With this file being empty for the moment, let’s launch our application and check the console output. Among various messages, we can easily find the following one:

_\[main\] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat initialized with port(s):_ **_8080_** _(http)_

Spring Boot is the one managing the embedded servlet, and therefore, the one configuring the exposed port, using 8080 even though we haven’t specified this in our application. **This demonstrates that, out of the box, Spring Boot provides a reasonable set of default configurations.** One of them is the Tomcat server port number, and it establishes a property on top of that configuration to allow us to modify the behavior without having to meddle with the application codebase. Let’s change this value to be 8081.

To this end, we should specify the new value of the server port number in the _application.properties_ file. Now we come across a problem here. How do we know the exact name that Boot expects the property to have? Well, as you would expect at this point, **Boot offers clear documentation on this, exhaustively listing all the properties it supports to customize its default behavior.**

We’ve included a link in the Resources section of this lesson to explore these Common Application Properties in detail.

We can see the properties are conveniently presented into categories. In this case, we can navigate to the Server Properties, where we’ll find the _server.port_ property introduced:

![](images/m3-common-application-properties.png)

As we can appreciate, **the documentation shows not only the properties we can use, but also a description of its intention and the value represented by the default behavior.**

So let’s set it in our _application.properties_ file:

```
server.port=8081
```

If we restart the application, we’ll be able to see that it now binds the service to the new port number:

_\[main\] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat initialized with port(s):_ **_8081_** _(http)_

### Default Boot Properties: Banner

Now let’s see another example of a default behavior we can customize with properties, Boot’s start-up banner.

You’ve probably noticed in the console the ASCII art banner shown when the application starts, similar to the following one:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (<version>)
```

Even this behavior can be customized with a simple property. Let’s completely disable it in our application:

```
spring.main.banner-mode=off
```

We can also change the default banner with:

```
spring.banner.location=banner.txt
```

You can find plenty of [Online Spring Boot Banner Generator](https://devops.datenkollektiv.de/banner.txt/index.html)s.

### Common Application Properties

It’s worth mentioning that **Boot also makes the metadata of the properties it defines available.** Of course, **it’s up to the IDE or plugin to provide support for these features.** For example, Spring Tool Suite (STS) provides such support.

Even if we count on this helpful tool, we might have difficulties finding the right entry among the large list of supported properties.

Of course, it’s hardly possible to remember all of them either, so we encourage you to become familiar with the official Common Application Properties document and keep it handy, as you’ll most likely use it on several occasions. Remember, you can find the corresponding link in the Resources section below.

## Working with @PropertySource

As we already know, in a Spring Boot application the default property file is _application.properties._

With the _@PropertySource_ annotation we can define additional properties files. This will help us structure our properties better, and can be used in any configuration class.

In order to see how it works, **we'll create a custom property file called _additional.properties_ in the resource folder** (_src/main/resources):_

```
additional.info=Additional Info
```

**And introduce this property source in our _AppConfig_ class by using the _@PropertySource_ annotation:**

```
@PropertySource("classpath:additional.properties")
@Configuration
public class AppConfig {
  
    // ...
    
}
```

With this configuration, we can inject the newly defined property in our beans using the _@Value_ annotation.

To demonstrate that the value has been injected, we'll log its value:

```
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {
    public static final Logger LOG = LoggerFactory.getLogger(ProjectRepositoryImpl.class);

    @Value("${project.prefix}")
    private String prefix;

    @Value("${project.suffix}")
    private Integer suffix;

    @Value("${additional.info}")
    private String additional;

    private final List<Project> projects = new ArrayList<>();

    ...

    private void updateInternalId(Project project) {
        LOG.info("Additional Info " + additional);
        LOG.info("Prepending Prefix " + prefix);
        LOG.info("Appending Suffix " + suffix);

        project.setInternalID(prefix + "-" + project.getId() + "-" + suffix);

        LOG.info("Generated internal id " + project.getInternalID());
    }
}
```

## Resources
- [Properties with Spring and Spring Boot](https://www.baeldung.com/properties-with-spring)
- [Spring Environment JavaDoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/Environment.html)
- [Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
# Logging in a Spring Boot Project

In this lesson, we'll learn about [logging](https://en.wikipedia.org/wiki/Log_file) in a Spring Boot project, including how to configure logging levels and other logging configurations.

The relevant module for this lesson is: [logging-in-a-spring-boot-project-end](../code/learn-spring-m2/working-with-logs-end)

## Logging Dependency

Even though logging might look like just a matter of recording some text to a destination, there’s much more to it than that. A robust logging approach includes:

* formatting log messages
* dealing with concurrent access to log files
* writing to alternate destinations, depending on log level or other criteria
* configuring all of this, and without having to alter the code

When you adopt a logging library or framework, you get all of the above—and more—in a nicely packaged, ready-to-use unit, and most of the time it’s free.

When we add the _spring-boot-starter_ dependency in a Boot project, **the _spring-boot-starter-logging_ is already included transitively**. This contains all the dependencies needed for logging.

We can verify this by checking the dependency tree in our IDE, or by running the command _mvn dependency:tree_ in the project location.

Let's have a look at the output of the command:

```
[INFO] com.baeldung:logging-in-a-spring-boot-project-end:jar:0.1.0-SNAPSHOT
[INFO] \- org.springframework.boot:spring-boot-starter-web:jar:3.0.5:compile
[INFO]    +- org.springframework.boot:spring-boot-starter:jar:3.0.5:compile
[INFO]    |  +- org.springframework.boot:spring-boot:jar:3.0.5:compile
[INFO]    |  +- org.springframework.boot:spring-boot-autoconfigure:jar:3.0.5:compile
[INFO]    |  +- org.springframework.boot:spring-boot-starter-logging:jar:3.0.5:compile
[INFO]    |  |  +- ch.qos.logback:logback-classic:jar:1.4.6:compile
[INFO]    |  |  |  +- ch.qos.logback:logback-core:jar:1.4.6:compile
[INFO]    |  |  |  \- org.slf4j:slf4j-api:jar:2.0.7:compile
[INFO]    |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.19.0:compile
[INFO]    |  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.19.0:compile
[INFO]    |  |  \- org.slf4j:jul-to-slf4j:jar:2.0.7:compile
...
```

We can see that Spring Boot uses [Logback](https://logback.qos.ch/) and [slf4j](https://www.slf4j.org/) as the default logging library.

## Default Logging Configuration

Spring Boot includes a default configuration for Logback with a pre-configured message pattern.

Adding log statements is fairly straightforward.

Let’s declare the log in _ProjectServiceImpl_:

```
private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);
```

Next, let’s add log statements in our service methods:

```
@Override
public Optional<Project> findById(Long id) {
    LOG.info("Project Service >> Finding Project By Id {}", id);
    return projectRepository.findById(id);
}

@Override
public Project save(Project project) {
    LOG.info("Project Service >> Saving Project", project);
    return projectRepository.save(project);
}
```

Next, modify our main class for saving some projects using an instance of IProjectService:

```
@SpringBootApplication
public class LsApp implements ApplicationRunner {
    IProjectService projectService;

    public LsApp(IProjectService projectService) {
        this.projectService = projectService;
    }

    public static void main(final String... args) {
        SpringApplication.run(LsApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        projectService.save(new Project("P1", LocalDate.now()));
        projectService.save(new Project("P2", LocalDate.now()));
        projectService.save(new Project("P3", LocalDate.now()));
    }
}
```


Let’s boot our app and check the logs:

```
2024-02-19T16:17:52.891+01:00  INFO 21997 --- [           main] c.b.ls.service.impl.ProjectServiceImpl   : Project Service >> Saving Project
2024-02-19T16:17:52.891+01:00  INFO 21997 --- [           main] c.b.ls.service.impl.ProjectServiceImpl   : Project Service >> Saving Project
```

We can see here the default configuration uses patterns and ANSI colors, to make the output more readable.

Now that we’ve seen how logging works out of the box, let’s see how we can configure its behavior.

## Configuring Log Levels

The default logging ROOT level is INFO, which is why our info-level log statements showed up in the log.

Of course, we can tune that level. The way we can configure logging is simply via the Boot _application.properties_ file. The ROOT logging level can be configured from the property:

```
logging.level.root=WARN
```

If we boot the app again, we'll no longer see our logs printed. In fact, all the printed logs were at INFO level. Therefore, once we changed it to WARN, we’ll not see any logs.

## Configuring Log Levels for Packages

We can configure different logging levels for packages, **using the property: _logging.level.packagename_**_._

For example, while we want the overall logging to only show WARN info, let’s say we want the logging coming out of Spring to be at the INFO level. This gives us a bit more visibility into what the framework is doing.

We need to simply add the property:

```
logging.level.org.springframework=INFO
```

If we run the app now, we'll see the Spring logs.

Finally, let’s say we want the logging level in our own application to be a little bit more verbose, to closely follow our own logic:

```
logging.level.com.baeldung.ls=DEBUG
```

Notice how we can tune packages individually based on how much we want to see in the log.

As our app is simple at this stage, the log is clean and readable even if we don't tune it. But, as soon as there’s more complexity the log becomes unwieldy very quickly if it’s not carefully and intelligently tuned. Let’s now raise the log level of our own package to INFO:

```
logging.level.com.baeldung.ls=INFO
```

If we run the app now, the log levels will no longer be part of the output.

## Other Common Logging Configurations

We can use the _application.properties file_ directly for other common logging configurations.

For example, to **print log statements in a log file**, we can do:

_logging.file=app.log_

Or, if we want to **change the date-format of logs**:

_logging.pattern.dateformat=yyyy-MM-dd_

You can keep exploring these config options by using the autocomplete in the _application.properties._

## Logging Best Practices

```
"levels": [
    "OFF",
    "ERROR",
    "WARN",
    "INFO",
    "DEBUG",
    "TRACE"
],
```

* **ERROR**: Any error/exception that is or might be critical. Our Logger automatically sends an email for each such message on our servers (usage: `logger.error("message");` )
* **WARN**: Any message that might warn us of potential problems, e.g. when a user tried to log in with wrong credentials - which might indicate an attack if that happens often or in short periods of time (usage: `logger.warn("message");` )
* **INFO**: Anything that we want to know when looking at the log files, e.g. when a scheduled job started/ended (usage: `logger.info("message");` )
* **DEBUG**: As the name says, debug messages that we only rarely turn on. (usage: `logger.debug("message");` )

The beauty of this is that if you set the log level to WARN, info and debug messages have next to no performance impact. If you need to get additional information from a production system you just can lower the level to INFO or DEBUG for a short period of time (since you'd get much more log entries which make your log files bigger and harder to read). 

**Adjusting log levels etc. can normally be done at runtime.**



## Resources
- [Logging in Spring Boot](https://www.baeldung.com/spring-boot-logging)
- [Introduction to SLF4J](https://www.baeldung.com/slf4j-with-log4j2-logback)
- [Introduction to Java Logging](https://www.baeldung.com/java-logging-intro)
- [Spring Reference - Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html)

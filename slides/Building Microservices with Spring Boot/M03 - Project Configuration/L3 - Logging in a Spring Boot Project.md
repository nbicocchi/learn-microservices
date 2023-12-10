# Logging in a Spring Boot Project

In this lesson, we'll learn about [logging](https://en.wikipedia.org/wiki/Log_file) in a Spring Boot project, including how to configure logging levels and other logging configurations.

The relevant module for this lesson is: [logging-in-a-spring-boot-project-end](https://github.com/nbicocchi/spring-boot-course/tree/module3/logging-in-a-spring-boot-project-end)

## Logging Dependency

When we add the _spring-boot-starter_ dependency in a Boot project, **the _spring-boot-starter-logging_ is already included transitively**. This contains all the dependencies needed for logging.

We can verify this by checking the dependency tree in our IDE, or by running the command _mvn dependency:tree_ in the project location.

Let's have a look at the output of the command:

```
[INFO] com.baeldung:logging-in-a-spring-boot-project-lesson-start:jar:0.1.0-SNAPSHOT
[INFO] \- org.springframework.boot:spring-boot-starter:jar:2.1.3.RELEASE:compile
[INFO]    +- org.springframework.boot:spring-boot-starter-logging:jar:2.1.3.RELEASE:compile
[INFO]    |  +- ch.qos.logback:logback-classic:jar:1.2.3:compile
[INFO]    |  |  +- ch.qos.logback:logback-core:jar:1.2.3:compile
[INFO]    |  |  \- org.slf4j:slf4j-api:jar:1.7.25:compile
[INFO]    |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.11.2:compile
[INFO]    |  |  \- org.apache.logging.log4j:log4j-api:jar:2.11.2:compile
[INFO]    |  \- org.slf4j:jul-to-slf4j:jar:1.7.25:compile
...
```

**We can see that Spring Boot uses** [**Logback**](https://logback.qos.ch/) **as the default logging library.**

## Default Logging Configuration

Spring Boot includes a default configuration for Logback with a pre-configured message pattern.

Adding log statements is fairly straightforward.

Let’s declare the log in _ProjectServiceImpl_:

```
private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);
```

Note that we're using [SLF4J](https://www.slf4j.org/) classes to make it easier to change the logging implementation should we need to at a later point.

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

Let’s boot our app and check the logs:

```
INFO 19504 --- [           main] c.b.ls.service.impl.ProjectServiceImpl   : Project Service >> Saving Project
INFO 19504 --- [           main] c.b.ls.service.impl.ProjectServiceImpl   : Project Service >> Finding Project By Id 1
```

We can see here the default configuration uses patterns and ANSI colors, to make the output more readable.

Now that we’ve seen how logging works out of the box, let’s see how we can configure its behavior.

## Configuring Log Levels

The default logging ROOT level is INFO, which is why our info-level log statements showed up in the log.

Of course, we can tune that level.

The way we can configure logging is simply via the Boot _application.properties_ file.

The ROOT logging level can be configured from the property:

```
logging.level.root=WARN
```

If we boot the app again, we'll no longer see our logs printed.

In fact, all the printed logs were at INFO level. Therefore, once we changed it to WARN, we’ll not see any logs.

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

As our app is simple at this stage, the log is clean and readable even if we don't tune it.

But, as soon as there’s more complexity the log becomes unwieldy very quickly if it’s not carefully and intelligently tuned.

Let’s now raise the log level of our own package to INFO:

```
logging.level.com.baeldung.ls=INFO
```

If we run the app now, the log levels will no longer part of the output.

## Other Common Logging Configurations

We can use the _application.properties file_ directly for other common logging configurations.

For example, to **print log statements in a log file**, we can do:

_logging.file=app.log_

Or, if we want to **change the date-format of logs**:

_logging.pattern.dateformat=yyyy-MM-dd_

You can keep exploring these config options by using the autocomplete in the _application.properties._

## Upgrade Notes

Since Spring Boot 2.2, _logging.file_ has been deprecated in favor of _logging.file.name_:

_logging.file.name=app.log_

## Resources
- [Logging in Spring Boot](https://www.baeldung.com/spring-boot-logging)
- [Spring Reference - Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html)

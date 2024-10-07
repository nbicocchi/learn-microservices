# Logging in a Spring Boot Project

## Logging Dependency

A robust logging approach includes:

* formatting log messages
* dealing with concurrent access to log files
* writing to alternate destinations, depending on log level or other criteria
* configuring all of this, and without having to alter the code

When you adopt a logging library or framework, you get all of the above—and more—in a nicely packaged, ready-to-use unit, and most of the time it’s free.

When we add the _spring-boot-starter_ dependency in a Boot project, **the _spring-boot-starter-logging_ is already included transitively**. This contains all the dependencies needed for logging.

We can verify this by checking the dependency tree in our IDE, or by running the command _mvn dependency:tree_ in the project location.

Let's have a look at the output of the command:

```
[INFO]    |  +- org.springframework.boot:spring-boot-starter-logging:jar:3.0.5:compile
[INFO]    |  |  +- ch.qos.logback:logback-classic:jar:1.4.6:compile
[INFO]    |  |  |  +- ch.qos.logback:logback-core:jar:1.4.6:compile
[INFO]    |  |  |  \- org.slf4j:slf4j-api:jar:2.0.7:compile
[INFO]    |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.19.0:compile
[INFO]    |  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.19.0:compile
[INFO]    |  |  \- org.slf4j:jul-to-slf4j:jar:2.0.7:compile
```

We can see that Spring Boot uses [Logback](https://logback.qos.ch/) and [slf4j](https://www.slf4j.org/) as the default logging library.

## Default Logging Configuration

Spring Boot includes a default configuration for Logback with a pre-configured message pattern. Adding log statements in our services is fairly straightforward:

```java
@RestController
public class DateTimeController {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeController.class);

    @GetMapping("/time")
    public String getTime() {
        String response = LocalTime.now().toString();
        LOG.info("getTime() invoked, returning {}", response);
        return response;
    }

    @GetMapping("/date")
    public String getDate() {
        String response = LocalDate.now().toString();
        LOG.info("getDate() invoked, returning {}", response);
        return response;
    }
}
```

Let’s boot our app and check the logs:

```
2024-10-07T11:26:50.422+02:00  INFO 103111 --- [nio-7000-exec-1] com.nbicocchi.DateTimeController         : getTime() invoked, returning 11:26:50.422503308
2024-10-07T11:26:55.027+02:00  INFO 103111 --- [nio-7000-exec-3] com.nbicocchi.DateTimeController         : getDate() invoked, returning 2024-10-07
```

Now that we’ve seen how logging works out of the box, let’s see how we can configure its behavior.

## Configuring Log Levels

The default logging ROOT level is INFO, which is why our info-level log statements showed up in the log.

Of course, we can tune that level. The way we can configure logging is simply via the Boot _application.yml_ file. The ROOT logging level can be configured from the property:

```
logging:
  level:
    root: WARN
```

If we boot the app again, we'll no longer see our logs printed. In fact, all the printed logs were at INFO level. Therefore, once we changed it to WARN, we’ll not see any logs.

## Configuring Log Levels for Packages

We can configure different logging levels for packages, **using the property: _logging.level.packagename_**_._

For example, while we want the overall logging to only show WARN info, let’s say we want the logging coming out of Spring to be at the INFO level. This gives us a bit more visibility into what the framework is doing.

We need to simply add the property:

```
logging:
  level:
    root: WARN
    org.springframework: INFO
```

If we run the app now, we'll see the Spring logs.

Finally, let’s say we want the logging level in our own application to be a little bit more verbose, to closely follow our own logic:

```
logging:
  level:
    root: WARN
    org.springframework: INFO
    com.nbicocchi: DEBUG
```

Notice how we can tune packages individually based on how much we want to see in the log.

As our app is simple at this stage, the log is clean and readable even if we don't tune it. But, as soon as there’s more complexity the log becomes unwieldy very quickly if it’s not carefully tuned.

## Other Common Logging Configurations

We can use the _application.properties file_ directly for other common logging configurations.

For example, to **print log statements in a log file**, we can use the `logging.file.name` property. Or, if we want to **change the date-format of logs** we can use `logging.pattern.dateformat`.

```
logging:
  level:
    root: WARN
    org.springframework: INFO
    com.nbicocchi: DEBUG
  file:
    name: app.log
  pattern:
    dateformat: yyyy-MM-dd
```


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

## Resources
- [Logging in Spring Boot](https://www.baeldung.com/spring-boot-logging)
- [Introduction to SLF4J](https://www.baeldung.com/slf4j-with-log4j2-logback)
- [Introduction to Java Logging](https://www.baeldung.com/java-logging-intro)
- [Spring Reference - Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html)

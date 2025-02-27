# Logging in a Spring Boot Project

## Logging Dependency

A robust logging approach includes:

* **formatting** log messages
* dealing with **concurrent access to log files**
* writing to **alternate destinations**, depending on log level or other criteria
* **configuring** all of this, and **without having to alter the code**

When you adopt a logging library or framework, you get all of the above in a nicely packaged, ready-to-use unit, and most of the time it’s free.

When we add the _spring-boot-starter_ dependency, **the _spring-boot-starter-logging_ is already included transitively**. This contains all the dependencies needed for logging.

We can verify this by checking the dependency tree in our IDE, or by running _mvn dependency:tree_.

```
[INFO] +- org.springframework.boot:spring-boot-starter-web:jar:3.3.4:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:3.3.4:compile
[INFO] |  |  +- org.springframework.boot:spring-boot:jar:3.3.4:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-autoconfigure:jar:3.3.4:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-starter-logging:jar:3.3.4:compile
[INFO] |  |  |  +- ch.qos.logback:logback-classic:jar:1.5.8:compile
[INFO] |  |  |  |  \- ch.qos.logback:logback-core:jar:1.5.8:compile
[INFO] |  |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.23.1:compile
[INFO] |  |  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.23.1:compile
[INFO] |  |  |  \- org.slf4j:jul-to-slf4j:jar:2.0.16:compile
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

With Lombok, it can be further simplified to:

```java
@Log4j2
@RestController
public class DateTimeController {
    @GetMapping("/time")
    public String getTime() {
        String response = LocalTime.now().toString();
        log.info("getTime() invoked, returning {}", response);
        return response;
    }

    @GetMapping("/date")
    public String getDate() {
        String response = LocalDate.now().toString();
        log.info("getDate() invoked, returning {}", response);
        return response;
    }
}
```

Let’s boot our app and check the logs:

```bash
curl -X GET http://localhost:7000/time  
```

```bash 
curl -X GET http://localhost:7000/date
```

```
2025-02-21T14:59:41.121+01:00  INFO 60378 --- [nio-7000-exec-1] com.nbicocchi.DateTimeController         : getTime() invoked, returning 14:59:41.121709906
2025-02-21T14:59:47.398+01:00  INFO 60378 --- [nio-7000-exec-2] com.nbicocchi.DateTimeController         : getDate() invoked, returning 2025-02-21
```

## Importance of Log Levels

- Control the verbosity of logs
- Help in debugging and monitoring
- Improve system observability

Each log entry is assigned a level through specific method calls:
- `log.info("Application started")` → INFO level
- `log.warn("Potential issue detected")` → WARN level
- `log.error("An error occurred!")` → ERROR level


| Level     | Description                                                |
|-----------|------------------------------------------------------------|
| **TRACE** | Most detailed logs, used for debugging at a granular level |
| **DEBUG** | Debugging information, useful for development              |
| **INFO**  | General system events and important runtime information    |
| **WARN**  | Potential issues that may not cause failures               |
| **ERROR** | Errors that need attention but do not stop the system      |
| **FATAL** | Critical errors that may cause system shutdown             |


## Configuring Log Levels

**The logging system filters messages based on the configured log level.**

**Root**

The default logging ROOT level is INFO, which is why our info-level log statements showed up in the log.

Of course, we can tune that level. The way we can configure logging is simply via the Boot _application.yml_ file. The ROOT logging level can be configured from the property:

```yaml
logging:
  level:
    root: WARN
```

If we boot the app again, we'll no longer see our logs printed. In fact, all the printed logs were at INFO level. Therefore, once we changed it to WARN, we’ll not see any logs.

**Packages**

We can configure different logging levels for packages, **using the property: _logging.level.packagename_**_._

For example, while we want the overall logging to only show WARN info, let’s say we want the logging coming out of Spring to be at the INFO level. This gives us a bit more visibility into what the framework is doing.

We need to simply add the property:

```yaml
logging:
  level:
    root: WARN
    org.springframework: INFO
```

If we run the app now, we'll see the Spring logs.

Finally, let’s say we want the logging level in our own application to be a little bit more verbose, to closely follow our own logic:

```yaml
logging:
  level:
    root: WARN
    org.springframework: INFO
    com.nbicocchi: DEBUG
```

Notice how we can tune packages individually based on how much we want to see in the log.

The beauty of this is that if you set the log level to WARN, info and debug messages have next to no performance impact. If you need to get additional information from a production system you just can lower the level to INFO or DEBUG for a short period of time (since you'd get much more log entries which make your log files bigger and harder to read).

## Other Common Logging Configurations

We can also use the _application.yml_ file for other logging configurations.

For example, to **print log statements in a log file**, we can use the `logging.file.name` property. Or, if we want to **change the date-format of logs** we can use `logging.pattern.dateformat`.

```yaml
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


## Resources
- [Logging in Spring Boot](https://www.baeldung.com/spring-boot-logging)
- [Introduction to SLF4J](https://www.baeldung.com/slf4j-with-log4j2-logback)
- [Introduction to Java Logging](https://www.baeldung.com/java-logging-intro)

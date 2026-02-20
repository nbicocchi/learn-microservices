# Spring Boot Logging

---

## Dependencies

* `spring-boot-starter` **includes `spring-boot-starter-logging` by default**
* Uses **Logback** + **SLF4J** under the hood
* Features:

    * Formatted logs
    * Concurrent file access
    * Multiple destinations
    * Configurable log levels **without changing code**

---

## Log Levels

* Control **verbosity** → helps with **debugging** and **monitoring**

| Level | Purpose                           |
| ----- | --------------------------------- |
| TRACE | Most detailed, granular debugging |
| DEBUG | Development/debugging information |
| INFO  | General runtime/system events     |
| WARN  | Potential issues                  |
| ERROR | Errors requiring attention        |
| FATAL | Critical errors, may stop system  |

**Example usage:**

```java
log.info("Application started");
log.warn("Potential issue detected");
log.error("An error occurred!");
```

---

## Configuring Log Levels

### 1️⃣ Root Level (Default: INFO)

```yaml
logging:
  level:
    root: WARN
```

### 2️⃣ Package-Specific Levels

```yaml
logging:
  level:
    root: WARN
    org.springframework: INFO
    com.nbicocchi: DEBUG
```

* Use **higher levels** (WARN/ERROR) to reduce logging overhead
* Temporarily lower levels (DEBUG/TRACE) for **more verbose logs** in production

---

## Other Configurations

### Log to a File

```yaml
logging:
  file:
    name: app.log
```

### Change Log Date Format

```yaml
logging:
  pattern:
    dateformat: yyyy-MM-dd
```

---

## Key Takeaways

* Spring Boot **simplifies logging** with built-in starter
* **SLF4J + Logback** provides a flexible logging API
* **Log levels** and **configuration via YAML** allow fine-grained control
* **Production vs Development** → adjust verbosity with minimal code changes

---

## Resources

* [Spring Boot Logging](https://www.baeldung.com/spring-boot-logging)
* [SLF4J Introduction](https://www.baeldung.com/slf4j-with-log4j2-logback)
* [Java Logging Introduction](https://www.baeldung.com/java-logging-intro)


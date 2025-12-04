# Spring Boot Logging 

### **Dependencies**

* `spring-boot-starter` includes `spring-boot-starter-logging` by default.
* Uses **Logback** and **SLF4J**.
* Supports formatted logs, concurrent file access, multiple destinations, and configurable levels without changing code.

### **Log Levels**

Control verbosity and help with debugging/monitoring:

| Level | Purpose                           |
| ----- | --------------------------------- |
| TRACE | Most detailed, granular debugging |
| DEBUG | Development/debugging info        |
| INFO  | General runtime/system events     |
| WARN  | Potential issues                  |
| ERROR | Errors needing attention          |
| FATAL | Critical errors, may stop system  |

Example usage:

```java
log.info("Application started");
log.warn("Potential issue detected");
log.error("An error occurred!");
```

### **Configuring Levels**

* **Root level**: default is `INFO`, configurable in `application.yml`:

```yaml
logging:
  level:
    root: WARN
```

* **Package-specific levels**:

```yaml
logging:
  level:
    root: WARN
    org.springframework: INFO
    com.nbicocchi: DEBUG
```

* Setting higher levels (WARN/ERROR) minimizes performance impact; levels can be lowered temporarily for more verbose logging in production.

### **Other Configurations**

* Log to a file:

```yaml
logging:
  file:
    name: app.log
```

* Change log date format:

```yaml
logging:
  pattern:
    dateformat: yyyy-MM-dd
```

### **Resources**

* [Spring Boot Logging](https://www.baeldung.com/spring-boot-logging)
* [SLF4J Introduction](https://www.baeldung.com/slf4j-with-log4j2-logback)
* [Java Logging Intro](https://www.baeldung.com/java-logging-intro)

# Actuators

Simply put, **actuators are monitoring tools**. They bring production ready features into our app with very low effort. Actuators provide various endpoints (documented [here](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints)) mainly exposed via HTTP which help in monitoring and, to some extent, managing our application. 

**The best way to enable actuators is to add the _spring-boot-starter-actuator_ dependency**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## GET operations

Let’s go to _http://localhost:8080/actuator_ and view a list of available endpoints. 

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/actuator",
      "templated": false
    },
    "health": {
      "href": "http://localhost:8080/actuator/health",
      "templated": false
    },
    "health-path": {
      "href": "http://localhost:8080/actuator/health/{*path}",
      "templated": true
    }
  }
}
```

We can modify the list of available endpoints with the following property:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: loggers,info,health
```

We can activate all endpoints with:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

### _/health_ endpoint

Let’s run the application and have a look at the _/health_ endpoint first. We can open up the browser and access this at: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

Naturally, the health endpoint shows information about the health of the application:

```json
{"status":"UP"}
```

We can add details with:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: ALWAYS
```

### _/metrics_ endpoint
The metrics endpoint publishes information about OS and JVM as well as application-level metrics. Once enabled, we get information such as memory, heap, processors, threads, classes loaded, classes unloaded, and thread pools, along with some HTTP metrics as well.

```json
{
  "names": [
    "application.ready.time",
    "application.started.time",
    "disk.free",
    "disk.total",
    "executor.active",
    "executor.completed",
    "executor.pool.core",
    "executor.pool.max",
    "executor.pool.size",
    "executor.queue.remaining",
    "executor.queued",
    "http.server.requests",
    "http.server.requests.active",
    "jvm.buffer.count",
    "jvm.buffer.memory.used",
    "jvm.buffer.total.capacity",
    "jvm.classes.loaded",
    "jvm.classes.unloaded",
    "jvm.compilation.time",
    "jvm.gc.concurrent.phase.time",
    "jvm.gc.live.data.size",
    "jvm.gc.max.data.size",
    "jvm.gc.memory.allocated",
    "jvm.gc.memory.promoted",
    "jvm.gc.overhead",
    "jvm.gc.pause",
    "jvm.info",
    "jvm.memory.committed",
    "jvm.memory.max",
    "jvm.memory.usage.after.gc",
    "jvm.memory.used",
    "jvm.threads.daemon",
    "jvm.threads.live",
    "jvm.threads.peak",
    "jvm.threads.started",
    "jvm.threads.states",
    "logback.events",
    "process.cpu.time",
    "process.cpu.usage",
    "process.files.max",
    "process.files.open",
    "process.start.time",
    "process.uptime",
    "system.cpu.count",
    "system.cpu.usage",
    "system.load.average.1m",
    "tomcat.sessions.active.current",
    "tomcat.sessions.active.max",
    "tomcat.sessions.alive.max",
    "tomcat.sessions.created",
    "tomcat.sessions.expired",
    "tomcat.sessions.rejected"
  ]
}
```

Each element has its own page with details. For example we can see the uptime of our service with:
[http://localhost:8080/actuator/metrics/process.uptime](http://localhost:8080/actuator/metrics/process.uptime)

### _/beans_ endpoint

```text
...
"dateTimeController": {
    "aliases": [],
    "scope": "singleton",
    "type": "com.nbicocchi.DateTimeController",
    "resource": "file [/home/nicola/IdeaProjects/learn-microservices/modules/tools/code/actuators/target/classes/com/nbicocchi/DateTimeController.class]",
    "dependencies": []
},
...
```

### _/loggers_ endpoint

**Spring Boot Actuator also exposes a _/loggers_ endpoint which allows us to view and configure logging levels of our app at runtime.**

```
{ //...
    "loggers": {
        "ROOT": {
            "configuredLevel": "INFO",
            "effectiveLevel": "INFO"
        },
        "com": {
            "configuredLevel": null,
            "effectiveLevel": "INFO"
        },
        "com.baeldung": {
            "configuredLevel": null,
            "effectiveLevel": "INFO"
        },
        "com.baeldung.ls": {
            "configuredLevel": null,
            "effectiveLevel": "INFO"
        },
        "com.baeldung.ls.LsApp": {
            "configuredLevel": null,
            "effectiveLevel": "INFO"
        }
    }
  //...    
}
```

## POST operations

If you want to update/set the environment property while the application is running, you have to set:

```
management:
  endpoint:
    env:
      post:
        enabled: true
```

**For example, we can also configure the logging level for individual loggers at runtime by hitting the POST _/loggers/{{logger}}_ request.**

For example, we can set the ROOT logging level to TRACE in our app by hitting the endpoint:

```
$ curl -X POST http://localhost:8080/actuator/loggers/ROOT -H "Content-Type: application/json" -d '{ "configuredLevel": "TRACE" }'
```

This can be very useful when troubleshooting issues without having to restart our app.


## Resources
- [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)
- [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [Spring Boot Actuator: Production-ready features](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready)
- [Auto-configured HealthIndicators](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-health-indicators)
- [Custom Information in Spring Boot Info Endpoint](https://www.baeldung.com/spring-boot-info-actuator-custom)

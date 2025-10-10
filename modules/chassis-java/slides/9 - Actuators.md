# Actuators

**Actuators are monitoring tools**. They bring production-ready features into our app with very low effort. Actuators provide various endpoints (documented [here](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints)) which help in monitoring and, to some extent, managing our application. 

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

The /actuator/health endpoint provides an overview of the application's health status. Key Features:
* Returns a basic status (`UP`, `DOWN`, or `UNKNOWN`).
* Can include detailed health indicators (e.g., database, disk space, external services).
* Supports custom health checks via HealthIndicator beans.

Let’s run the application and have a look at the _/health_ endpoint first. We can open up the browser and access this at: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

```json
{"status":"UP"}
```

We can add details with:

```yaml
management:
  endpoint:
    health:
      show-details: ALWAYS
```

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499032023040,
        "free": 373250428928,
        "threshold": 10485760,
        "path": "/home/nicola/IdeaProjects/learn-microservices/.",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### _/metrics_ endpoint
The metrics endpoint publishes information about OS and JVM as well as application-level metrics. Once enabled, we get information such as memory, heap, processors, threads, classes loaded, classes unloaded, and thread pools, along with some HTTP metrics as well.

[http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics).

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

```json
{
  "name": "process.uptime",
  "description": "The uptime of the Java virtual machine",
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "VALUE",
      "value": 160.103
    }
  ],
  "availableTags": []
}
```

### _/beans_ endpoint

The `/actuator/beans` endpoint in Spring Boot Actuator provides detailed information about all beans registered in the application context. Key Features:
- Lists all beans managed by Spring.
- Shows their **names, types, dependencies, and source configurations**.
- Helps in debugging **bean wiring issues** and understanding the application context structure.

[http://localhost:8080/actuator/beans](http://localhost:8080/actuator/beans).

```yaml
"dateTimeController": {
    "aliases": [],
    "scope": "singleton",
    "type": "com.nbicocchi.DateTimeController",
    "resource": "file [/home/nicola/IdeaProjects/learn-microservices/modules/tools/code/actuators/target/classes/com/nbicocchi/DateTimeController.class]",
    "dependencies": []
}
```

### _/loggers_ endpoint

The `/actuator/loggers` endpoint in Spring Boot Actuator provides detailed information and control over the application's logging configuration. Key Features:
- Displays the **current logging levels** for all loggers.
- Allows **runtime modification** of logging levels.

[http://localhost:8080/actuator/loggers](http://localhost:8080/actuator/loggers).


```json
{ 
  //...
  "loggers": {
      "ROOT": {
          "configuredLevel": "INFO",
          "effectiveLevel": "INFO"
      },
      "com": {
          "configuredLevel": null,
          "effectiveLevel": "INFO"
      },
      "com.nbicocchi": {
          "configuredLevel": null,
          "effectiveLevel": "INFO"
      },
      "com.nbicocchi.ls": {
          "configuredLevel": null,
          "effectiveLevel": "INFO"
      },
      "com.nbicocchi.ls.LsApp": {
          "configuredLevel": null,
          "effectiveLevel": "INFO"
      }
  }
  //...
}
```

## POST operations

If you want to update/set the environment property while the application is running, you have to set:

```yaml
management:
  endpoint:
    env:
      post:
        enabled: true
```

**For example, we can also configure the logging level for individual loggers at runtime by hitting the POST `/loggers/{{logger}}` request.**

For example, we can set the ROOT logging level to TRACE in our app by hitting the endpoint:

```bash
curl -X POST http://localhost:8080/actuator/loggers/ROOT -H "Content-Type: application/json" -d '{ "configuredLevel": "TRACE" }'
```

This can be very useful when troubleshooting issues without having to restart our app.


## Resources

- [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)
- [Custom Information in Spring Boot Info Endpoint](https://www.baeldung.com/spring-boot-info-actuator-custom)

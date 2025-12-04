# Actuators

**Actuators are monitoring tools**. They bring production-ready features into our app with very low effort. Actuators provide various endpoints (documented [here](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints)) which help in monitoring and, to some extent, managing our application. 

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

[http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).


### _/metrics_ endpoint
The metrics endpoint publishes information about OS and JVM as well as application-level metrics. Once enabled, we get information such as memory, heap, processors, threads, classes loaded, classes unloaded, and thread pools, along with some HTTP metrics as well.

[http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics).


### _/beans_ endpoint

The `/actuator/beans` endpoint in Spring Boot Actuator provides detailed information about all beans registered in the application context. 

[http://localhost:8080/actuator/beans](http://localhost:8080/actuator/beans).


### _/loggers_ endpoint

The `/actuator/loggers` endpoint in Spring Boot Actuator provides detailed information and control over the application's logging configuration. Key Features:

[http://localhost:8080/actuator/loggers](http://localhost:8080/actuator/loggers).


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
curl -X POST "http://localhost:8080/actuator/loggers/ROOT" \
  -H "Content-Type: application/json" \
  -d '{
        "configuredLevel": "TRACE"
      }'
```

## Resources

- [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)
- [Custom Information in Spring Boot Info Endpoint](https://www.baeldung.com/spring-boot-info-actuator-custom)

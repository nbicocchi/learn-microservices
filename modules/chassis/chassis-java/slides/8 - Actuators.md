# Spring Boot Actuators

**Actuators are production-ready monitoring and management tools** that can be easily added to a Spring Boot application. They provide endpoints to monitor, manage, and gather metrics from your app.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

---

## 🔹 GET Operations

* Visit **`http://localhost:8080/actuator`** to see available endpoints:

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

* Control which endpoints are exposed via `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: loggers,info,health
```

* To **expose all endpoints**:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

---

### 🔹 /health Endpoint

* Provides an **overview of application health**
* Returns statuses: `UP`, `DOWN`, or `UNKNOWN`
* Can include **detailed health indicators** (database, disk space, external services)
* Supports **custom health checks** via `HealthIndicator` beans

[View Health Endpoint →](http://localhost:8080/actuator/health)

---

### 🔹 /metrics Endpoint

* Publishes **OS, JVM, and application-level metrics**
* Example data: memory, heap, processors, threads, classes loaded/unloaded, thread pools, HTTP metrics

[View Metrics Endpoint →](http://localhost:8080/actuator/metrics)

---

### 🔹 /beans Endpoint

* Provides **detailed information about all beans** in the Spring context

[View Beans Endpoint →](http://localhost:8080/actuator/beans)

---

### 🔹 /loggers Endpoint

* Shows **current and configured logging levels**
* Allows **runtime updates** to logging configuration

[View Loggers Endpoint →](http://localhost:8080/actuator/loggers)

---

## 🔹 POST Operations

* Enable POST for runtime environment changes:

```yaml
management:
  endpoint:
    env:
      post:
        enabled: true
```

* Example: Update ROOT logging level to TRACE at runtime:

```bash
curl -X POST "http://localhost:8080/actuator/loggers/ROOT" \
  -H "Content-Type: application/json" \
  -d '{
        "configuredLevel": "TRACE"
      }'
```

* Allows **dynamic debugging and tuning** without restarting the app

---

## Key Takeaways

* Actuators bring **production-ready monitoring** with minimal effort
* Endpoints provide **health, metrics, beans, loggers**, and more
* Supports **GET for monitoring** and **POST for runtime configuration**

---

## Resources

* [Spring Boot Actuator Guide](https://www.baeldung.com/spring-boot-actuators)
* [Custom Info in Info Endpoint](https://www.baeldung.com/spring-boot-info-actuator-custom)


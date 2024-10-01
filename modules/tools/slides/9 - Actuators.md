# Actuators in Boot

In this lesson, we'll focus on a core feature of Spring Boot: **actuators**.

The relevant module for this lesson is: [working-with-actuators-end](../code/learn-spring-m2/working-with-actuators-end)

## Actuators

Simply put, **actuators are monitoring tools**. They bring production ready features into our app for very low effort.

More specifically, they provide various endpoints mainly exposed via HTTP which basically help in monitoring and, to some extent, managing our application.

Through actuators, Spring Boot provides these built-in endpoints focused on auditing, health checks, and displaying metrics information.

**The best way to enable actuators is to add the _spring-boot-starter-actuator_ dependency**:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Boot provides a number of built-in endpoints, well documented in the [official reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints).

Let’s go to _http://localhost:8080/actuator_ and view a list of available endpoints. We can modify the list of available endpoints with the following property:

```
management.endpoints.web.exposure.include=loggers,info, health
```

We should see:

```
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
        },
        "info": {
            "href": "http://localhost:8080/actuator/info",
            "templated": false
        },
        "loggers": {
            "href": "http://localhost:8080/actuator/loggers",
            "templated": false
        },
        "loggers-name": {
            "href": "http://localhost:8080/actuator/loggers/{name}",
            "templated": true
        }
    }
}
```

### The _/health_ Endpoint

Let’s run the application and have a look at the _/health_ endpoint first. We can open up the browser and access this at: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

Naturally, the health endpoint shows information about the health of the application:

```
{"status":"UP"}
```

We can add details with:

```
management.endpoint.health.show-details=ALWAYS
```

```
{
    "status": "UP",
    "components": {
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 499898105856,
                "free": 332536348672,
                "threshold": 10485760,
                "path": "/Users/nicola/IdeaProjects/learn-spring-boot/.",
                "exists": true
            }
        },
        "ping": {
            "status": "UP"
        }
    }
}
```

### The _/info_ Endpoint

Let’s hit the /info endpoint: [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)

This endpoint shows information about our application. At this point, we haven’t configured or defined any, so **the endpoint by default, won’t contain any data**:

```
{}
```

Of course, the format is JSON as we expect. Populating the response of this endpoint is quite easy, as everything is configurable via properties. **Let’s add some info about our app name and description:**

```
info.application.name = Actuator info
info.application.description= A demo Spring project with information
info.organization = How to do in Java
```

Now, when we hit the _info_ endpoint, we’ll see:

```
{
    "application": {
        "name": "Actuator info",
        "description": "A demo Spring project with information"
    },
    "organization": "How to do in Java",
}
```

### The _/loggers_ Endpoint

**Spring Boot Actuator also exposes a _/loggers_ endpoint which allows us to view and configure logging levels of our app at runtime.**

On enabling, we can hit the _/actuator/loggers_ endpoint and view the entire list of loggers and their configuration.

Since we have changed the actuator context path to _/monitoring_ in our app, we will be accessing the loggers at: [http://localhost:8080/actuator/loggers](http://localhost:8080/actuator/loggers):

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

## POST to Spring Boot Actuator

If you want to update/set the environment property while the application is running, you have to set:

```
management.endpoint.env.post.enabled=true
```

**For example, we can also configure the logging level for individual loggers at runtime by hitting the POST _/loggers/{{logger}}_ request.**

For example, we can set the ROOT logging level to DEBUG in our app by hitting the endpoint:

```
$ curl -X POST http://localhost:8080/actuator/loggers/ROOT -H "Content-Type: application/json" -d '{ "configuredLevel": "TRACE" }'
```

This can be very useful when troubleshooting issues without having to restart our app.

## Custom Health Indicator

First, let's revisit the default output of the health actuator. If we run the application and hit the _/actuator/health_ endpoint, we get the following output:

```
{
    "status": "UP"
}
```

To display more information, we need to configure the Health actuator. To this end, let's add to the _application.properties_ file the following line:

```
management.endpoint.health.show-details=ALWAYS
```

Now, if we restart the application and hit _/actuator/health_, we can see the following output:

```
{
    "status": "UP",
    "details": {
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 107373129728,
                "free": 52361125888,
                "threshold": 10485760
            }
        }
    }
}
```

This output now contains more information with respect to the default one. When creating a custom health indicator, we'll include even more details.

### Creating a Custom Health Indicator

To create a custom health indicator, **we need to implement the _HealthIndicator_ interface**. In this component, we should override the _health()_ method that returns a _Health_ instance built according to our needs.

Let's say we want to know the state of our database. If the database is up, then we'll return the status UP using _Health.up()_. Otherwise, we return the status DOWN using _Health.down()_. In this case we'd like to give as well a brief explanation of the reason why the database is down.

As mentioned, we start by creating a new class called _DBHealthIndicator_ that implements the _HealthIndicator_ interface. Then, **we override the _health()_ method according to our logic**:

```
package com.baeldung.ls.actuate.health;

@Component
public class DBHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        if (isDbUp()) {
            return Health.up()
                .build();
        } else {
            return Health.down()
                .withDetail("Error Code", 503)
                .build();
        }
    }

    private boolean isDbUp() {
        return false;
    }
}
```

The implementation of the _isDbUp()_ method can vary but for the current simplified implementation it always returns _false_.

Let's boot our application again and hit the _/actuator/health_ endpoint:

```
{
    "status": "DOWN",
    "details": {
        "DB": {
            "status": "DOWN",
            "details": {
                "Error Code": 503
            }
        },
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 107373129728,
                "free": 59248070656,
                "threshold": 10485760
            }
        }
    }
}
```

**Now, we see the output of our _DBHealthIndicator_ in the node _DB_ under _details_.**

Note that by default, Spring Boot chooses a name for the node based on the name of _HealthIndicator_ bean: it removes the suffix _HealthIndicator_ from the bean name. In our case, the bean name is _DBHealthIndicator,_ hence, the node name becomes _DB_.

### Health Indicator Status Types

By default, there are four status types that a health indicator can return:

- UP
- DOWN
- OUT\_OF\_SERVICE
- UNKNOWN

We can also return a custom status from our custom health indicator.

The severity order can be specified in _application.properties_:

```
management.endpoint.health.status.order=down,out-of-service,unknown,up
```

### Auto Configured Health Indicators

**Spring Boot provides out of the box health indicators for common external services like database, message queues, cache service etc.** that our app may connect to.

You can find a link to the complete list of auto configured health indicators in the Resources section.

These indicators are enabled by default, if our app is connecting to these services. We can disable them all by setting:

```
management.health.defaults.enabled=false
```


## Resources
- [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)
- [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [Spring Boot Actuator: Production-ready features](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready)
- [Auto-configured HealthIndicators](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-health-indicators)
- [Custom Information in Spring Boot Info Endpoint](https://www.baeldung.com/spring-boot-info-actuator-custom)

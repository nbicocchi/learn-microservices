# Actuators in Boot

In this lesson, we'll focus on a core feature of Spring Boot - actuators.

The relevant module you need to import when you're starting with this lesson is: [m4-actuators-in-boot-start](https://github.com/eugenp/learn-spring/tree/module4/m4-actuators-in-boot-start)

If you want have a look at the fully implemented lesson, as a reference, feel free to import: [m4-actuators-in-boot-end](https://github.com/eugenp/learn-spring/tree/module4/m4-actuators-in-boot-end)

## Actuators

Simply put, **actuators are monitoring tools**. They bring production ready features into our app for very low effort.

More specifically, they provide various endpoints mainly exposed via HTTP (but also [JMX](https://docs.oracle.com/javase/tutorial/jmx/overview/index.html)) which basically help in monitoring and, to some extent, managing our application.

## Actuators in Spring Boot

Through actuators, Spring Boot provides these built-in endpoints focused on auditing, health checks, and displaying metrics information.

**The best way to enable actuators is to add the _spring-boot-starter-actuator_ dependency**:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Boot provides a number of built-in endpoints, well documented in the [official reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints).

Out of these, two endpoints are enabled by default: the _/health_ and _/info_ endpoints.

## The _/health_ Endpoint

Let’s run the application and have a look at the _/health_ endpoint first. We can open up the browser and access this at: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

Naturally, the health endpoint shows information about the health of the application:

_{"status":"UP"}_

The default output is, of course, the bare minimum - just enough to know our application is up and running.

## The _/info_ Endpoint

Let’s hit the /info endpoint: [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)

This endpoint shows information about our application. At this point, we haven’t configured or defined any, so **the endpoint by default, won’t contain any data**:

_{}_

Of course, the format is JSON as we expect.

Populating the response of this endpoint is quite easy, as everything is configurable via properties.

**Let’s add some info about our app name and description:**

```
info.lsapp.name=Learn Spring Application 
info.lsapp.description=Learn Spring Application Developed With Spring Boot 2`
```

Now, when we hit the _info_ endpoint, we’ll see:

```
{
    "lsapp": {
        "name": "Learn Spring Application",
        "description": "Learn Spring Application Developed With Spring Boot 2"
    }
}
```

Next, let’s do some simple configuration of the actuators.

## Actuators Context Path

By default, all actuators are available at _/actuators/{endpoint\_name}_ path. This can be easily configured via properties.

**Let’s open up _application.properties_ and change the base path of all actuators:**

```
management.endpoints.web.base-path=/monitoring
```

Now all endpoints will be available at: _/monitoring/{endpoint\_name}._

We can further change paths of specific actuators also. For example, if we want to make our _info_ actuator available at _/monitoring/information,_ we’ll do:

```
management.endpoints.web.path-mapping.info=/information
```

This will make our info available at _/monitoring/information._

## Boot Highlight

Actuators are a purely Spring Boot specific functionality that we can use out-of-the-box. We can reproduce this functionality in a plain Spring application by designing and implementing similar APIs.

## The _/loggers_ Endpoint (extra)

**Spring Boot Actuator also exposes a _/loggers_ endpoint which allows us to view and configure logging levels of our app at runtime.**

Since this endpoint is not enabled by default, we can easily enable it by including it in the list of enabled endpoints in the _application.properties_:

```
management.endpoints.web.exposure.include=loggers,info, health
```

On enabling, we can hit the _/actuator/loggers_ endpoint and view the entire list of loggers and their configuration.

Since we have changed the actuator context path to _/monitoring_ in our app, we will be accessing the loggers at: [http://localhost:8080/monitoring/loggers](http://localhost:8080/monitoring/loggers):

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

**We can also configure the logging level for individual loggers at runtime by hitting the POST _/loggers/{{logger}}_ request.**

For example, we can set the ROOT logging level to DEBUG in our app by hitting the endpoint:

_POST http://localhost:8080/monitoring/loggers/ROOT_

with the payload:

```
{
    "configuredLevel": "DEBUG"
}
```

Now our app will start logging at debug level.

This can be very useful when troubleshooting issues without having to restart our app.

## Upgrade Notes

Note that since Spring Boot 2.5, the actuator's "_info_" HTTP endpoint is not enabled by default anymore. That means we now have to explicitly include it in the list of enabled endpoints property (_management.endpoints.web.exposure.include_) as we've shown in this lesson if we want to use it.

Additionally, since version 2.6 the "_info_" endpoint doesn't retrieve the "_info._" prefixed properties from the Spring Environment by default (as the _info.lsapp.name_ and _info.lsapp.description_ ones we've defined above). To enable this, we have to add the following application property in _application.properties_ file:

```
management.info.env.enabled=true`
```

## Resources
- [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)
- [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [Spring Boot Actuator: Production-ready features](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready)

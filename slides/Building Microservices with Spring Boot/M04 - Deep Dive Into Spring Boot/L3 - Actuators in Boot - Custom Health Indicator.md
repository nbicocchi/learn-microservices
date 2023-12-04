# Actuators in Boot - Custom Health Indicator

In this lesson, we'll keep on investigating Spring Boot actuators and **we'll create our first** **custom actuator that will extend the basic functionality of the _/health_ actuator**.

The relevant module you need to import when you're starting with this lesson is: [actuator-custom-health-indicator-start](https://github.com/eugenp/learn-spring/tree/module4/actuator-custom-health-indicator-start)

If you want have a look at the fully implemented lesson, as a reference, feel free to import: [actuator-custom-health-indicator-end](https://github.com/eugenp/learn-spring/tree/module4/actuator-custom-health-indicator-end)

## Default Output of the Health Actuator

Previously, we learned about basic actuators provided by Spring Boot out of the box. We can also easily create custom ones that will display additional information about the state of the system.

To achieve this, **the Spring Boot Actuator module provides extension points that allows us to implement our own functionality**.

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

## Creating a Custom Health Indicator

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

## Health Indicator Status Types (extra)

By default, there are four status types that a health indicator can return:

-   UP
-   DOWN
-   OUT\_OF\_SERVICE
-   UNKNOWN

We can also return a custom status from our custom health indicator.

The severity order can be specified in _application.properties_:

```
management.endpoint.health.status.order=down,out-of-service,unknown,up
```

## Auto Configured Health Indicators (extra)

**Spring Boot provides out of the box health indicators for common external services like database, message queues, cache service etc.** that our app may connect to.

You can find a link to the complete list of auto configured health indicators in the Resources section.

These indicators are enabled by default, if our app is connecting to these services.

**We can disable them all by setting the _management.health.defaults.enabled_ property to false.**

## Resources
- [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)
- [Spring Boot Actuator: Production-ready features](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready)
- [Auto-configured HealthIndicators](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-health-indicators)


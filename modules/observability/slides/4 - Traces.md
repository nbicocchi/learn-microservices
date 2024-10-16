# Distributed Tracing

## Micrometer Tracing and Zipkin
To understand what is going on in a distributed system such as a system landscape of cooperating
microservices, it is crucial to be able to track and visualize how requests and messages flow between microservices when processing an external call to the system landscape.

[Micrometer Tracing](https://github.com/micrometer-metrics) is used to collect trace information, propagate trace contexts (for example, trace and span IDs) in calls to other microservices and export the trace information into trace analysis tools like Zipkin. Micrometer
supports auto-configuration of tracers based on [OpenTelemetry](https://opentelemetry.io/) or
[OpenZipkin Brave](https://github.com/openzipkin/brave).

By default, trace headers are propagated between microservices using [W3C trace context headers](https://www.w3.org/TR/trace-context/). A sample W3C trace context traceparent header looks like this:

```
traceparent:"00-2425f26083814f66c985c717a761e810-fbec8704028cfb20-01"
```

The value of the traceparent header contains four parts, separated by a -:

* 00 indicates the version used. Will always be 00 using the current specification.
* 2425f26083814f66c985c717a761e810 is the trace ID.
* fbec8704028cfb20 is the span ID.
* 01 indicates various flags. Will always be 01 using the current specification.

[Zipkin](http://zipkin.io) is a distributed tracing system that Micrometer Tracing can send tracing data to for storage and visualization. Zipkin comes with native support for storing trace information either in memory, or in a database such as Apache Cassandra, Elasticsearch, or MySQL. Added to this, a number of extensions are available. For details, refer to https://zipkin.io/pages/extensions_choices.html. 

The infrastructure for handling distributed tracing information in Micrometer Tracing and Zipkin is
originally based on Google Dapper (https://ai.google/research/pubs/pub36356). **The tracing information from a complete workflow is called a trace tree, and sub-parts of the tree, such
as the basic units of work, are called spans. A correlation ID is called TraceId, and a span is identified by its own unique SpanId, along with the TraceId of the trace tree it belongs to.**

![](images/distributed-tracing-example.avif)

From the preceding screenshot, we can see that an HTTP GET request is sent to the gateway service which subsequently engages in a sequence of security-related activities.

## Maven dependencies
To enable distributed tracing, add to *ALL* services within our ecosystem the *micrometer-tracing-bridge-otel*, and *opentelemetry-exporter-zipkin* dependencies.

```
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing-bridge-otel</artifactId>
    </dependency>
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-zipkin</artifactId>
    </dependency>
```

## Configuration

As above, *ALL* services within our ecosystem can be configured to send traces to Zipkin as described below:

```
management.zipkin.tracing.endpoint: http://localhost:9411/api/v2/spans
management.tracing.sampling.probability: 1.0
logging.pattern.level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
```

This specifies:
* that traces will be sent to Zipkin at http://localhost:9411/api/v2/spans
* that all traces are sent to Zipkin (the default is 0.1 or 10 percent)
* an alternative log format which includes *traceID* and *spanID*

With the above log format, the log output will look like:

```
2024-05-18T19:18:44.399+02:00  INFO [composite-service,d1f4e74e0a6cd241c68ceaae73d1e50a,1579ffdacf5fcfe2] ...
```

Where: 
* product-composite is the name of the microservice
* d1f4e74e0a6cd241c68ceaae73d1e50a is the trace ID
* 1579ffdacf5fcfe2 is the span ID.

### Gateway service configuration
In order to expose Zipkin via our gateway service (this is particularly useful when the ecosystem is run inside a Docker environment), we can update its routing rules. Using the following configuration Zipkin is available at: http://localhost:8080/zipkin/web.

```
spring.cloud.gateway.routes:
- id: composite-service
  uri: lb://composite-service
  predicates:
    - Path=/time/**
- id: zipkin-web-start
  uri: http://${app.zipkin-server}:9411
  predicates:
    - Path=/zipkin/web
      filters:
    - SetPath=/
- id: zipkin-web-other
  uri: http://${app.zipkin-server}:9411
  predicates:
    - Path=/zipkin/**
```

## Services code
The current versions of Spring Boot, Project Reactor, and Micrometer Tracing do not yet work together perfectly. Therefore, a couple of workarounds have been applied to the source code for reactive clients. The problems are mainly related to the complexity of propagating trace contexts (for example, trace and span IDs) between different threads involved in reactive asynchronous processing, specifically if parts of the processing involve imperative synchronous processing.

To address many of the challenges with context propagation, we can turn on automatic context propagation by calling the method Hooks.enableAutomaticContextPropagation() in a reactive client’s
main() method. For the *time* service, it looks like this:

```java
@SpringBootApplication
public class TimeServiceApplication {
	public static void main(String[] args) {
		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(TimeServiceApplication.class, args);
	}
}
```

However, for the *gateway* and *composite* services, one problem remains. To ensure that a *WebClient* instance is correctly instrumented for observation, for example, to be able to propagate the current trace and span IDs as headers in an outgoing request, the *WebClient.Builder* instance is expected to be injected using auto-wiring. Unfortunately, when using Eureka for service discovery, the *WebClient.Builder* instance is recommended to be created as a bean annotated with @LoadBalanced as:

```java
@Bean
@LoadBalanced
public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
}
```

So, there is a conflict in how to create a WebClient instance when used with both Eureka and Micrometer Tracing. To resolve this conflict, the @LoadBalanced bean can be replaced by a load-balancer-aware exchange-filter function, ReactorLoadBalancerExchangeFilterFunction. An exchange-filter function can be set on an auto-wired WebClient.Builder instance like:

```java
@SpringBootApplication
public class GatewayApplication {
	@Autowired
	private ReactorLoadBalancerExchangeFilterFunction lbFunction;

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder.filter(lbFunction).build();
	}

	public static void main(String[] args) {
		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(GatewayApplication.class, args);
	}
}
```

## Docker configuration

```
services:
  zipkin:
    image: openzipkin/zipkin:3.0.5
    restart: always
    mem_limit: 512m
    environment:
      - STORAGE_TYPE=mem

  eureka:
    build: eureka-server
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  gateway:
    build: gateway-service-end
    mem_limit: 512m
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  composite:
    build: composite-service-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  time:
    build: time-service-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker
```

Here are the explanations for the preceding code:
* Zipkin is included into the ecosystem.
* The environment variable _STORAGE_TYPE_ specifies the storage backend for Zipkin. In this case, traces are kept in memory.

## Trying out distributed tracing
TODO

## Resources
* https://opentelemetry.io/
# Service Routing

In microservice architectures, usually comes a point where we’ll need to ensure that critical *cross-cutting concerns* such as security, logging, and tracking users across multiple service calls occur. **We want these functionalities to be consistently enforced across all services** without the need for each team to build their own solution. 

## Implementing cross-cutting concerns with a shared library
While it’s possible to use a common library for embedding these capabilities into service, doing so has several (negative) implications:
* **Consistency:** It’s challenging to implement these capabilities in each service consistently. 
* **Bug Proness:** Pushing the responsibilities to implement cross-cutting concerns like security and logging down to the individual teams greatly increases the odds that someone will not implement them properly or will forget to do them. 
* **Flexibility:** It’s possible to create a hard dependency across all our services. The more capabilities we build into a common framework shared across all our services, the more difficult it is to change or add behavior in our common code without having to recompile and redeploy all our services. Suddenly an upgrade of core capabilities built into a shared library becomes a long migration process.

## Implementing cross-cutting concerns with a gateway service
To solve these issues, we need to abstract these cross-cutting concerns into a service that can sit independently and act as a filter and router for all the microservice calls in our architecture. We call this service a *gateway*. Clients no longer directly call a microservice. Instead, all calls are routed through the service gateway, which acts as a single *Policy Enforcement Point (PEP)*, and are then routed to a final destination.

The use of a centralized *PEP* means that cross-cutting service concerns can be carried out in a single place without the individual development teams having to implement those concerns. Examples of cross-cutting concerns that can be implemented in a service gateway:
* **Static routing** A service gateway places all service calls behind a single URL and API route. This simplifies development as we only have to know about one service endpoint for all of our services.
* **Dynamic routing** A service gateway can inspect incoming service requests and, based on the data from the incoming request, perform intelligent routing for the service caller. For instance, customers participating in a beta program might have all calls to a service routed to a specific cluster of services that are running a different version of code.
* **Authentication and authorization** Because all service calls route through a service gateway, the service gateway is a natural place to check whether the callers of a service have authenticated themselves.
* **Metric collection and logging** A service gateway can be used to collect metrics and log information as a service call passes through it. You can also use the service gateway to confirm that critical pieces of information are in place for user requests, thereby ensuring that logging is uniform. 

## Spring Cloud Gateway

Spring Cloud Gateway aims to provide a simple, yet effective way to route to APIs and provide cross-cutting concerns to them such as: security, monitoring/metrics, and resiliency.

### Maven dependencies
To include a Cloud Gateway service in our ecosystem, create an empty service and add the *spring-cloud-starter-gateway*, *spring-cloud-starter-netflix-eureka-client*, *spring-boot-starter-actuator* dependencies.

```
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-gateway</artifactId>
		</dependency>
		...
	</dependencies>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
```

### Configuration

The following configuration:
* configure Spring Cloud Gateway as an Eureka client.
* configure Spring Boot Actuator for development usage.
* configure log levels so that we can see log messages from interesting parts of the internal processing in Spring Cloud Gateway.


```
server.port: 8081
spring.application.name: gateway-service
app.eureka-server: localhost

management.endpoint.gateway.enabled: true
management.endpoint.health.show-details: "ALWAYS"
management.endpoints.web.exposure.include: "*"

eureka:
  client:
    serviceUrl:
      defaultZone: http://${app.eureka-server}:8761/eureka/
    initialInstanceInfoReplicationIntervalSeconds: 5
    registryFetchIntervalSeconds: 5
  instance:
    leaseRenewalIntervalInSeconds: 5
    leaseExpirationDurationInSeconds: 5

logging:
  level:
    root: INFO
    org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator: INFO
    org.springframework.cloud.gateway: TRACE

---
spring.config.activate.on-profile: docker
server.port: 8080
app.eureka-server: eureka
```

### Routing rules
When it comes to configuring Spring Cloud Gateway, the most important thing is setting up the routing rules. Setting up routing rules can be done in two ways:
* programmatically, using a Java DSL
* by configuration

Using a Java DSL to set up routing rules programmatically can be useful in cases where the rules are stored in external storage, such as a database, or are given at runtime, for example, via a RESTful API or a message sent to the gateway.

In more static use cases, it is convenient to declare the routes in the configuration file. **Separating the routing rules from the Java code makes it possible to update the routing rules without having to deploy a new version of the microservice**.

A route is defined by the following:
* **ID**, the name of the route
* **Destination URI**, which describes where to send a request
* **Predicates**, which select a route based on information in the incoming HTTP request
* **Filters**, which can modify both the request and/or the response

![](images/gateway-predicate-filters.avif)

Clients make requests to Spring Cloud Gateway. If the Gateway Handler Mapping determines that a request matches a route, it is sent to the Gateway Web Handler. This handler runs the request through a filter chain that is specific to the request.

### Routing requests to the composite-service API

```
spring.cloud.gateway.routes:
  - id: composite-service
    uri: lb://composite-service
    predicates:
      - Path=/datetime/**
```

Some points to note from the preceding code:
* **id**: composite-service: The name of the route is composite-service.
* **uri**: lb://composite-service: If the route is selected by its predicates, the request will be routed to the service that is named composite-service in the discovery service. The protocol *lb://* is used to direct Spring Cloud Gateway to use the client-side load balancer to look up the destination in the discovery service.
* **predicates**: Path=/datetime/** is used to specify what requests this route should match. ** matches zero or more elements in the path.

### Routing requests to the date and time services APIs

```
spring.cloud.gateway.routes:
  - id: time-service
    uri: lb://time-service
    predicates:
      - Path=/time/**
  - id: date-service
    uri: lb://date-service
    predicates:
      - Path=/date/**
```

Despite this practice should be avoided in production, it could be useful during development and testing to expose individual services through the gateway. With the above configuration the time and date services are exposed via the */time* and */date* endpoints.

### Routing requests to the Eureka server’s web page
```
spring.cloud.gateway.routes:
  - id: eureka-web-start
    uri: http://${app.eureka-server}:8761
    predicates:
      - Path=/eureka/web
    filters:
      - SetPath=/
  - id: eureka-web-other
    uri: http://${app.eureka-server}:8761
    predicates:
      - Path=/eureka/**
```

Eureka exposes both an API and a web page for its clients. In this case, requests sent to the edge server with the path starting with /eureka/web/ should be handled as a call to the Eureka web page and routed to http://${app.eureka-server}:8761.

The web page will also load several web resources, such as .js, .css, and .png files. These requests will be routed to http://${app. eureka-server}:8761/eureka.

### Routing requests with predicates and filters
To learn a bit more about the routing capabilities in Spring Cloud Gateway, we will try out host-based routing, where Spring Cloud Gateway uses the hostname of the incoming request to determine where to route the request. We will use a website for testing HTTP codes: http://httpstat.us/.

A call to http://httpstat.us/${CODE} returns a response with the ${CODE} HTTP code and a response body, also containing the HTTP code, and a corresponding descriptive text. For example:

```
$ curl http://httpstat.us/200 -i  

HTTP/1.1 200 OK
Content-Length: 6
Content-Type: text/plain
Date: Sun, 12 May 2024 15:39:57 GMT
Server: Kestrel
Set-Cookie: ARRAffinity=984be3d3ba9615669dc57bb8932bb241f00dc260a93772a8c4fefb6be05aa32c;Path=/;HttpOnly;Domain=httpstat.us
Request-Context: appId=cid-v1:3548b0f5-7f75-492f-82bb-b6eb0e864e53

200 OK%     
```

Assume that we want to route calls to http://${hostname}:8080/headerrouting as follows:
* Calls from the *i.feel.lucky* host should return *200 OK*
* Calls from the *im.a.teapot* host should return *418 I'm a teapot*
* Calls from all other hostnames should return *501 Not Implemented*

To implement these routing rules in Spring Cloud Gateway, we can use the Host route predicate to select requests with specific hostnames, and the SetPath filter to set the desired HTTP code in the request path. This can be done as follows:

```
spring.cloud.gateway.routes:
    - id: host_route_200
      uri: http://httpstat.us
      predicates:
      - Host=i.feel.lucky
      - Path=/headerrouting/**
      filters:
      - SetPath=/200
    - id: host_route_418
      uri: http://httpstat.us
      predicates:
      - Host=im.a.teapot
      - Path=/headerrouting/**
      filters:
      - SetPath=/418
    - id: host_route_501
      uri: http://httpstat.us
      predicates:
      - Path=/headerrouting/**
      filters:
      - SetPath=/501
```

You can now test the configuration with the following commands:
```
$ curl http://localhost:8080/headerrouting -H "Host: i.feel.lucky:8080"
200 OK

$ curl http://localhost:8080/headerrouting -H "Host: im.a.teapot:8080"
418 I'm a teapot

$ curl http://localhost:8080/headerrouting
501 Not Implemented
```

Refer to the official documentation for the full list of [predicates](https://cloud.spring.io/spring-cloud-gateway/reference/html/#gateway-request-predicates-factories) and [filters](https://cloud.spring.io/spring-cloud-gateway/reference/html/#gatewayfilter-factories).

If we want to see the routes managed by the Gateway server, we can list the routes via the *actuator/gateway/routes* endpoint on the Gateway server. This will return a:

```
curl http://localhost:8080/actuator/gateway/routes | jq

[
  {
    "predicate": "Paths: [/datetime/**], match trailing slash: true",
    "route_id": "composite-service",
    "filters": [],
    "uri": "lb://composite-service",
    "order": 0
  },
  {
    "predicate": "Paths: [/time/**], match trailing slash: true",
    "route_id": "time-service",
    "filters": [],
    "uri": "lb://time-service",
    "order": 0
  },
  {
    "predicate": "Paths: [/date/**], match trailing slash: true",
    "route_id": "date-service",
    "filters": [],
    "uri": "lb://date-service",
    "order": 0
  },
  ...
```

### Gateway Code
With an edge server in place, external health check requests also have to go through the edge server.
Therefore, the edge server has to be equipped with a composite health check that checks the status of all microservices.

```java
@Configuration
public class HealthCheckConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);
    private WebClient webClient;

    @Autowired
    public HealthCheckConfiguration(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Bean
    ReactiveHealthContributor healthcheckMicroservices() {
        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();

        registry.put("time", () -> getHealth("http://TIME-SERVICE"));
        registry.put("date", () -> getHealth("http://DATE-SERVICE"));
        registry.put("composite", () -> getHealth("http://COMPOSITE-SERVICE"));

        return CompositeReactiveHealthContributor.fromMap(registry);
    }

    private Mono<Health> getHealth(String baseUrl) {
        String url = baseUrl + "/actuator/health";
        LOG.debug("Setting up a call to the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log(LOG.getName(), FINE);
    }
}
```

The main application class, GatewayApplication, declares a _WebClient.Builder_ bean to be used by the implementation of the health indicator, as follows:

```java
@SpringBootApplication
public class GatewayApplication {

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}
```

### Docker configuration
Add a _Dockerfile_ to containerize the service and edit the _docker-compose.yml_ file to include the service within your ecosystem.

```
  gateway:
    build: gateway-service-end
    mem_limit: 512m
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
```

## Resources
- Spring Microservices in Action (Chapter 8)
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 10)
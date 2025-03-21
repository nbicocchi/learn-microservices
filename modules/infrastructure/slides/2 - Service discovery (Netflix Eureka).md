# Service Discovery (Netflix Eureka)

Netflix Eureka is a highly configurable discovery server that can be set up for a number of different use cases, and it provides robust, resilient, and fault-tolerant runtime features.

The Eureka server **does not have a backend store**:
* service have to send heartbeats to keep their registrations up to date (can be done in memory);
* clients also have an in-memory cache of Eureka registrations (they do not have to go to the registry for every request to a service).

## Netflix Eureka server

### Maven dependencies

To include Netflix Eureka in our project, we need to add the *spring-cloud-starter-netflix-eureka-server* dependency as shown below.

```
    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2024.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
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

### Server code
Add the _@EnableEurekaServer_ annotation to the application main class.

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```

### Configuration
By default, every Eureka server is also a Eureka client and requires (at least one) service URL to locate a peer. If you do not provide it, the service runs and works, but it fills your logs with a lot of noise about not being able to register with the peer.

In standalone mode, you might prefer to switch off the client side behavior so that it does not keep trying and failing to reach its peers. The following example shows how to switch off the client-side behavior:

```yaml
server:
  port: 8761
  
eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    waitTimeInMsWhenSyncEmpty: 0
    response-cache-update-interval-ms: 5000
```

- **`eureka.instance.hostname: localhost`**: 
  - Specifies the hostname of the Eureka server. In this case, it is set to `localhost`, which means the Eureka server is running locally (commonly for development purposes).
- **`registerWithEureka: false`**: 
  - Indicates that this Eureka instance will not register itself as a client. This is common for standalone Eureka servers.
- **`fetchRegistry: false`**:
  - Disables the Eureka server from fetching the service registry from other Eureka servers. This is used when this instance is the only Eureka server and there is no need to synchronize with other servers.
- **`serviceUrl.defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/`**: 
  - Specifies the URL where other services can register with the Eureka server. This URL is dynamically constructed using the hostname (`localhost`) and the port (`8761`), creating a service URL of `http://localhost:8761/eureka/`.
- **`waitTimeInMsWhenSyncEmpty: 0`**: 
  - Configures the server not to wait if the service registry is empty when synchronizing. This setting is typically used to speed up the initial startup process.
- **`response-cache-update-interval-ms: 5000`**: 
  - Defines the interval (in milliseconds) at which the Eureka server updates its response cache. This is set to 5 seconds, ensuring that the cache is regularly refreshed, improving performance when responding to requests for service registry information.

Refer to [this guide](https://cloud.spring.io/spring-cloud-netflix/reference/html/) for more details on the configuration. After startup, we can connect to the Spring Eureka Dashboard at http://localhost:8761/. There are no registered clients!

![](images/eureka-dashboard-empty.webp)

## Netflix Eureka clients

### Maven dependencies
We only need to bring in a dependency to connect to a discovery server called _spring-cloud-starter-netflix-eureka-client_.

```
    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2024.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
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

### Client code
Spring Cloud comes with an abstraction allowing clients to make requests (to registered services) through a client-side load balancer. The standard HTTP client, _RestClient_, can be configured to use a load balanced implementation. This function can be enabled by adding the _@LoadBalanced_ annotation to a _@Bean_ declaration that returns a _RestClient_.

```java
@SpringBootApplication
public class CompositeApplication {

   @Bean
   @LoadBalanced
   public RestClient.Builder balancedRestClientBuilder() {
      return RestClient.builder();
   }

   public static void main(String[] args) {
      SpringApplication.run(CompositeApplication.class, args);
   }
}
```

To actually make a request to a registered service, it is enough to **mention the services by name as reported in the Spring Eureka Dashboard**. In our example, the *datetime-composite-service* connects to a set of *datetime-service* replicas.

```java
@GetMapping(value = "/datetime")
public LocalDateTimeWithTimestamp dateTime() {
   String urlTime = "http://DATETIME-SERVICE/time";
   String urlDate = "http://DATETIME-SERVICE/date";

   LOG.info("Calling time API on URL: {}", urlTime);
   LocalTime localTime = restClient.get()
           .uri(urlTime)
           .retrieve()
           .body(LocalTime.class);

   LOG.info("Calling time API on URL: {}", urlDate);
   LocalDate localDate = restClient.get()
           .uri(urlDate)
           .retrieve()
           .body(LocalDate.class);

   return new LocalDateTimeWithTimestamp(localDate, localTime, LocalDateTime.now());
}
```

### Configuration
Client microservices have the following configuration:

```yaml
server.port: 8080
spring.application.name: composite-service

eureka:
   client:
      serviceUrl:
         defaultZone: http://localhost:8761/eureka/
      initialInstanceInfoReplicationIntervalSeconds: 5
      registryFetchIntervalSeconds: 5
   instance:
      leaseRenewalIntervalInSeconds: 5
      leaseExpirationDurationInSeconds: 5

---
spring.config.activate.on-profile: docker
server.port: 8080
eureka.client.serviceUrl.defaultZone: http://eureka:8761/eureka/


```

* `spring.application.name: service-name`:
  - Defines the name of the Spring Boot application. This name will be used to identify the service when it registers with Eureka.
* `eureka.client.serviceUrl.defaultZone`:
  - Defines the URL of the Eureka server that this service will use to register itself and communicate with. Here, it is pointing to a local Eureka server running on `http://localhost:8761/eureka/`.
* `initialInstanceInfoReplicationIntervalSeconds: 5`:
  - Specifies how frequently (in seconds) the initial instance information is replicated to the Eureka server. In this case, it will replicate every 5 seconds.
* `registryFetchIntervalSeconds: 5`:
  - Defines how often (in seconds) the service fetches registry information from the Eureka server. It will fetch the updated registry every 5 seconds to get a fresh list of available services.
* `leaseRenewalIntervalInSeconds: 5`:
  - Sets the interval (in seconds) at which this service renews its lease with the Eureka server. The service will send heartbeat signals every 5 seconds to indicate it is still healthy and available.
* `leaseExpirationDurationInSeconds: 5`:
   - Defines how long the Eureka server will wait (in seconds) before expiring the service instance if it does not receive a lease renewal (heartbeat). If no heartbeat is received within 5 seconds, the instance is considered expired.

## Docker configuration

```yaml
services:
   eureka:
      build: eureka-service
      ports:
         - "8761:8761"

   datetime-composite:
      build: datetime-composite-service
      ports:
         - "8080:8080"
      environment:
         - SPRING_PROFILES_ACTIVE=docker

   datetime:
      build: datetime-service
      environment:
         - SPRING_PROFILES_ACTIVE=docker
      deploy:
         mode: replicated
         replicas: 3
```

Once everything is set up, we can run the example project with:

```bash
export COMPOSE_FILE="docker-compose-cslb.yml"
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

You can also test the server-side version with:

```bash
export COMPOSE_FILE="docker-compose-sslb.yml"
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

## Resources
- Spring Microservices in Action (Chapter 6)
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 9)

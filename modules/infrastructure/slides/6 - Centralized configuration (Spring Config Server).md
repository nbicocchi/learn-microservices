# Centralized Configuration (Spring Config Server)

## Spring Cloud Configuration Server

When it comes to setting up a config server, there are a number of options to consider:
* **Selecting a storage type** for the configuration repository
* **Deciding on the initial client connection**, either to the config server or to the discovery server
* **Securing the configuration**, both against unauthorized access to the API and by avoiding storing sensitive information in plain text in the configuration repository

### Storage type
Spring Cloud Config server supports the storing of configuration files in a number of different backends:
* Git repository
* Local filesystem
* JDBC database
* [HashiCorp Vault](https://www.vaultproject.io/)

See the [reference documentation](https://docs.spring.io/spring-cloud-config/reference/server.html) for the full list.

### Initial client connection
- **Config Server First:** The client first connects to the Config Server to retrieve its configuration and then registers with the Discovery Server. This approach allows storing the Discovery Server's configuration in the Config Server but creates a single point of failure since Spring Config Server is not distributed.

- **Discovery Server First:** The client first connects to the Discovery Server to find an available Config Server instance, reducing the risk of a single point of failure. This approach requires setting `spring.cloud.config.discovery.enabled=true` (default is false) but adds an extra network round trip during startup.

See the [reference documentation](https://docs.spring.io/spring-cloud-config/reference/client.html#discovery-first-bootstrap) for more details.

### The Config Server API
The config server exposes a REST API that can be used by its clients to retrieve their configuration. We will use the following endpoints in the API:

* _/{microservice}/{profile}_: Returns the configuration for the specified microservice and the specified Spring profile. 
* _/encrypt_ and _/decrypt_: Endpoints for encrypting and decrypting sensitive information. These must be locked down before being used in production.
* _/actuator_: The standard actuator endpoint exposed by all microservices. These should be used with care and locked down before being used in production.

## Setting up Spring Cloud Config Server

### Maven dependencies

```text
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2024.0.0</spring-cloud.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
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

### Configuration

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/nbicocchi/learn-microservices-config
          skipSslValidation: true
          timeout: 4

encrypt:
  key: ${CONFIG_SERVER_ENCRYPT_KEY}

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

server:
  port: 8080

eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka:8761/eureka/
```

### Server code

```java
@SpringBootApplication
@EnableConfigServer
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

### Docker configuration

```yaml
  config:
    build: config-service
    ports:
      - "8888:8888"
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
      - ENCRYPT_KEY=${CONFIG_SERVER_ENCRYPT_KEY}
    healthcheck:
      test: [ "CMD-SHELL", "curl -f http://localhost:8888/actuator/health" ]
      interval: 10s
      timeout: 5s
      retries: 5
    depends_on:
      eureka:
        condition: service_healthy
```

The values of the preceding environment variables, marked in the Docker Compose file with ${...}, are fetched from the `.env` file:

```
CONFIG_SERVER_ENCRYPT_KEY=ninna-nanna-ninna-0h
SPRING_PROFILES_ACTIVE=docker
CONFIG_SERVER_HOST=config
CONFIG_SERVER_PORT=8888
```

These environmental variables can be injected in IntelliJ Configurations using third party plugins such as [EnvFile](https://github.com/ashald/EnvFile).

### Config Repository

After moving the configuration files from each client’s source code to the [configuration repository](https://github.com/nbicocchi/learn-microservices-config), we will have some common configuration in many of the configuration files:
* the common parts have to be placed in a common configuration file _application.yml_
* this file is shared by all clients

```
config-repo/
├── application.yml
├── datetime-composite-service.yml
├── datetime-service.yml
└── gateway-service.yml
```

The most of these files are simple and similar to each because all common parts have been included in _application.yml_. 

```text
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
    initialInstanceInfoReplicationIntervalSeconds: 5
    registryFetchIntervalSeconds: 5
  instance:
    leaseRenewalIntervalInSeconds: 5
    leaseExpirationDurationInSeconds: 5

management:
  endpoints:
    web:
      exposure:
        include: health,info,env,refresh

---
spring.config.activate.on-profile: docker
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka:8761/eureka/
```

Below you can see the content of _datetime-composite-service.yml_.

```text
server.port: 9000
spring.application.name: datetime-composite-service

---
spring.config.activate.on-profile: docker
server.port: 8080
```



### Trying out Spring Cloud Config Server

**Configuration retrieval** Configurations can be retrieved using the */service/profile* endpoint exposed by the configuration server. For example, you can use the following command to retrieve the _datetime-service_ configuration for the docker profile. You can test all the other combinations by changing the name either of the *service* or of the *profile*.

```bash
curl http://localhost:8888/datetime-service/docker | jq
```

```json
{
  "name": "datetime-service",
  "profiles": [
    "docker"
  ],
  "label": null,
  "version": "f0713f611970dc1b8264e9511d75550651200fc9",
  "state": "",
  "propertySources": [
    {
      "name": "https://github.com/nbicocchi/learn-microservices-config/Config resource 'file [/tmp/config-repo-2718930897293658586/datetime-service.yml' via location '' (document #1)",
      "source": {
        "spring.config.activate.on-profile": "docker",
        "server.port": 8080
      }
    },
    {
      "name": "https://github.com/nbicocchi/learn-microservices-config/Config resource 'file [/tmp/config-repo-2718930897293658586/datetime-service.yml' via location '' (document #0)",
      "source": {
        "server.port": 9001,
        "spring.application.name": "datetime-service",
        "app.default.zone": "US/Eastern"
      }
    },
    {
      "name": "https://github.com/nbicocchi/learn-microservices-config/Config resource 'file [/tmp/config-repo-2718930897293658586/application.yml' via location '' (document #1)",
      "source": {
        "spring.config.activate.on-profile": "docker",
        "eureka.client.serviceUrl.defaultZone": "http://eureka:8761/eureka/"
      }
    },
    {
      "name": "https://github.com/nbicocchi/learn-microservices-config/Config resource 'file [/tmp/config-repo-2718930897293658586/application.yml' via location '' (document #0)",
      "source": {
        "eureka.client.serviceUrl.defaultZone": "http://localhost:8761/eureka/",
        "eureka.client.initialInstanceInfoReplicationIntervalSeconds": 5,
        "eureka.client.registryFetchIntervalSeconds": 5,
        "eureka.instance.leaseRenewalIntervalInSeconds": 5,
        "eureka.instance.leaseExpirationDurationInSeconds": 5,
        "management.endpoints.web.exposure.include": "health,info,env,refresh"
      }
    }
  ]
}
```

The property sources are returned in priority order; if a property is specified in multiple property sources, the first property in the response takes precedence.

### Securing the configuration
To protect configurations, Spring Cloud Config Server incorporates several security mechanisms and best practices:

* Use TLS for encrypting data in transit.
* **Secure sensitive properties using encryption mechanisms (symmetric or asymmetric).**
* Leverage OAuth 2.0 or other strong authentication mechanisms to protect access to configuration data.
* Regularly rotate encryption keys and credentials.
* Limit access to sensitive configurations by enforcing RBAC.
* Use a private and secured repository for storing configuration files.
* Regularly audit and monitor configuration access through logs and monitoring tools.

As described above, sensitive data can be encrypted and decrypted using the /encrypt and /decrypt endpoints exposed by the config server.

```bash
curl http://localhost:8888/encrypt -d my-super-secure-password
```

```
b5bceeba6c1f03f807e286b1352aceac4002e27f01bbb4384aa8399a11c8f9853c37b073cdda13b2445043f241de8759%   
```

```bash
curl http://localhost:8888/decrypt -d b5bceeba6c1f03f807e286b1352aceac4002e27f01bbb4384aa8399a11c8f9853c37b073cdda13b2445043f241de8759
```

```
my-super-secure-password%    
```

If you want to use an encrypted value in a configuration file, you need to prefix it with {cipher} and wrap it in. For example, to store the encrypted version of 'my-super-secure-password', add the following line in a YAML-based configuration file:
```
secret-password: '{cipher}b5bceeba6c1f03f807e286b1352aceac4002e27f01bbb4384aa8399a11c8f9853c37b073cdda13b2445043f241de8759'
```

When the config server detects values in the format '{cipher}...', it tries to decrypt them using its encryption key before sending them to a client.

## Setting up Spring Cloud Config Clients

### Maven dependencies

```text
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2024.0.0</spring-cloud.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config</artifactId>
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

### Updating the configuration

To update the configuration across all services, we must call the `/actuator/refresh` endpoint on each service. This can be achieved by using a simple shell script that iterates over the list of services and triggers the refresh. 

```bash
#!/bin/bash

# List of service URLs (replace with actual service URLs)
SERVICES=("http://service1:8080" "http://service2:8080" "http://service3:8080")

# Loop through each service and trigger the /actuator/refresh endpoint
for SERVICE in "${SERVICES[@]}"
do
  echo "Refreshing config for $SERVICE"
  if curl -X POST "$SERVICE/actuator/refresh" -s -o /dev/null
    echo "✅ Config refreshed for $SERVICE"
  else
    echo "❌ Failed to refresh config for $SERVICE"
  fi
done
```

However, if we have replicated services or services are hidden behind a gateway, it becomes more complex, as the configuration must be refreshed on all instances of each service. In this case, we can implement a cross-cutting concern directly into the API gateway. By leveraging the Discovery Client and RestClient, the gateway can dynamically discover all replicas of a service and send a `/actuator/refresh` request to each instance. This approach ensures that configuration updates are propagated consistently across all replicas without the need for manually handling each service instance.

```java
@Service
public class ServiceRefresher {
    private final ReactiveDiscoveryClient discoveryClient;
    private final WebClient webClient;

    public ServiceRefresher(ReactiveDiscoveryClient discoveryClient, WebClient.Builder webClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.webClient = webClientBuilder.build();
    }

    public Mono<Void> refreshAllServices() {
        return discoveryClient.getServices() // Fetch all registered services reactively
                .flatMap(discoveryClient::getInstances) // Get instances for each service
                .flatMap(this::refreshInstance) // Call /actuator/refresh for each instance
                .then(); // Return Mono<Void>
    }

    private Mono<Void> refreshInstance(ServiceInstance instance) {
        String url = instance.getUri().toString() + "/actuator/refresh";
        System.out.println("Refreshing: " + url);

        return webClient.post()
                .uri(url)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> System.out.println("✅ Refreshed: " + url))
                .doOnError(e -> System.err.println("❌ Failed to refresh " + url + " - " + e.getMessage()))
                .onErrorResume(e -> Mono.empty()); // Avoid breaking the chain if one request fails
    }
}
```

## Resources
- Spring Microservices in Action (Chapter 5)
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 12)



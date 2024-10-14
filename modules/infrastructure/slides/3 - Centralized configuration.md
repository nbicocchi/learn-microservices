# Centralized Configuration

## Introduction

**Completely separating the configuration information from the application code allows developers and operations teams to make changes to their configurations without going through a recompile process**. It also introduces complexity, because now developers have another artifact to manage and deploy with the application.

**Many developers turn to property files** to store their configuration information. Configuring your application in these files becomes a simple task, so easy that most developers never do more than placing their configuration file under source control.

**This approach might work with a small number of applications, but it quickly falls apart when dealing with hundreds of microservices**. It might also represent a security concern. To avoid this issues, developers should follow these key principles:

* **Segregate**: We need to completely separate the service configuration information from the actual physical deployment of a service. In fact, application configuration shouldn’t be deployed with the service instance. Instead, configuration information should either be passed as environment variables to the starting service or read from a centralized repository when the service starts.
* **Abstract**: We also need to abstract access to configuration data behind a service interface. Instead of writing code that directly reads the service repository, whether file-based or a JDBC database, we should use a REST-based JSON service to retrieve the application’s configuration data.
* **Centralize**: Because a cloud-based application might literally have hundreds of services, it’s critical to minimize the number of different repositories used to hold configuration data. Centralize your application configuration into as few repositories as possible.
* **Harden**: Because your application configuration information is going to be completely segregated from your deployed service and centralized, it’s critical that the solution you utilize and implement be highly available and redundant.

## Configuration management architecture

![](images/centralized-configuration.avif)

1. When a microservice instance comes up, it calls a service endpoint to read its configuration information, which is specific to the environment it’s operating in. The connection information for the configuration management (connection credentials, service endpoint, ...) is passed into to the microservice as it starts.
2. The actual configuration resides in a repository. Based on the implementation of your configuration repository, you can choose different ways to hold your configuration data. This can include files under source control, relational databases, key-value data stores, etc. 
3. The actual management of the application configuration data occurs independently of how the application is deployed. Changes to configuration management are typically handled through the build and deployment pipeline, where modifications can be tagged with version information and deployed
through the different environments (development, staging, production, and so forth). 
4. When the configuration management changes, the services that use that application configuration data must be notified of the alteration and refresh their copy of the application data.


## Implementation choices

[Spring Cloud Configuration Server](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/) - Offers a general configuration management solution with different backends.

* **Non-distributed key-value store**
* Can use multiple backends for storing configuration data including filesystem, Consul, Git, Redis etc.

[etcd](https://github.com/etcd-io/etcd) - Written in Go, used as Kubernetes' backing store for all cluster data. Used for service discovery and key-value management. Uses the raft protocol (https://raft.github.io/, https://thesecretlivesofdata.com/raft/) for its distributed computing model.

* Very fast and scalable
* Easy to use and set up

[Apache Zookeeper](https://zookeeper.apache.org/) - Offers distributed locking capabilities. Often used as a configuration management solution for accessing key-value data.

* Oldest, most battle-tested of the solutions 
* Most complex to use. Can be used for configuration management, but consider only if you’re already using it in other pieces of your architecture



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
**By default, a client connects first to the config server to retrieve its configuration**. Based on the configuration, it connects to the discovery server, to register itself. With this approach, it is possible to store the configuration of the discovery server in the config server. **One concern with connecting to the config server first is that the config server can become a single point of failure (i.e., Spring Config Server is not a distributed application!).**

It is also possible the other way around, that is, the client first connects to the discovery server to find a config server instance and then connects to the config server to get its configuration. If the clients connect first to a discovery server there can be multiple config server instances registered so that a single point of failure can be avoided.

If you prefer to use the discovery server to locate the config server, you can do so by setting _spring.cloud.config.discovery.enabled=true_ (the default is false). The net result of doing so is that client applications only need the appropriate discovery configuration. For example, you need to define the Eureka server address (_eureka.client.serviceUrl.defaultZone_). The price for using this option is an extra network round trip on startup, to locate the service registration. The benefit is that, as long as the discovery server is a fixed point, the config server can change its coordinates.

See the [reference documentation](https://docs.spring.io/spring-cloud-config/reference/client.html#discovery-first-bootstrap) for more details.

### Securing the configuration
To protect configurations, Spring Cloud Config Server incorporates several security mechanisms and best practices:

* Use TLS for encrypting data in transit. 
* Secure sensitive properties using encryption mechanisms (symmetric or asymmetric). 
* Leverage OAuth 2.0 or other strong authentication mechanisms to protect access to configuration data. 
* Regularly rotate encryption keys and credentials. 
* Limit access to sensitive configurations by enforcing RBAC. 
* Use a private and secured repository for storing configuration files. 
* Regularly audit and monitor configuration access through logs and monitoring tools.

### The Config Server API
The config server exposes a REST API that can be used by its clients to retrieve their configuration. We will use the following endpoints in the API:

* _/{microservice}/{profile}_: Returns the configuration for the specified microservice and the specified Spring profile. 
* _/encrypt_ and _/decrypt_: Endpoints for encrypting and decrypting sensitive information. These must be locked down before being used in production.
* _/actuator_: The standard actuator endpoint exposed by all microservices. These should be used with care and locked down before being used in production.

## Setting up Spring Cloud Config Server

### Maven dependencies

```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>
	</dependencies>
```

### Configuration

```yaml
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

### Server code

```java
@SpringBootApplication
@EnableConfigServer
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(App.class, args);
        String repoLocation = ctx.getEnvironment().getProperty("spring.cloud.config.server.native.searchLocations");
        LOG.info("Serving configurations from folder: {}", repoLocation);
    }
}
```

### Docker configuration

```yaml
  config:
    build: config-service
    mem_limit: 512m
    ports:
      - 8888:8888
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - ENCRYPT_KEY=${CONFIG_SERVER_ENCRYPT_KEY}
    healthcheck:
      test: "curl -f localhost:8888/actuator/health"
      interval: 5s
      timeout: 5s
      retries: 20
    depends_on:
      eureka:
        condition: service_healthy
```

The values of the preceding environment variables, marked in the Docker Compose file with ${...}, are fetched from the `.env` file:

```
CONFIG_SERVER_ENCRYPT_KEY=ninna-nanna-ninna-0h
CONFIG_SERVER_USR=user
CONFIG_SERVER_PWD=secret
```

These environmental variables can be injected in IntelliJ Configurations using third party plugins such as [EnvFile](https://github.com/ashald/EnvFile).

### Config Repository

After moving the configuration files from each client’s source code to the [configuration repository](https://github.com/nbicocchi/learn-microservices-config), we will have some common configuration in many of the configuration files, for example, for the configuration of actuator endpoints and how to connect to Eureka.
* the common parts have to be placed in a common configuration file _application.yml_
* this file is shared by all clients

The configuration repository can be found in _/config-repo_.

```
config-repo/
├── application.yml
├── datetime-composite-service.yml
├── datetime-service.yml
└── gateway-service.yml
```

The most of these files are simple and similar to each because all common parts have been included in _application.yml_. Below you can see the content of _datetime-service.yml_.

```
server.port: 9001
spring.application.name: datetime-service

---
spring.config.activate.on-profile: docker
server.port: 8080
```



### Trying out Spring Cloud Config Server

**Configuration retrieval** Configurations can be retrieved using the */service/profile* endpoint exposed by the configuration server. For example, you can use the following command to retrieve the _datetime-service_ configuration for the docker profile. You can test all the other combinations by changing the name either of the *service* or of the *profile*.

```bash
$ curl http://localhost:8888/datetime-service/docker | jq

{
  "name": "time-service",
  "profiles": [
    "docker"
  ],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [
    {
      "name": "Config resource 'file [/Users/nicola/IdeaProjects/learn-spring-boot/code/learn-spring-m6/spring-cloud-config-end/config-repo/time-service.yml]' via location 'file:/Users/nicola/IdeaProjects/learn-spring-boot/code/learn-spring-m6/spring-cloud-config-end/config-repo/' (document #1)",
      "source": {
        "spring.config.activate.on-profile": "docker",
        "server.port": 8080
      }
    },
    ...
```

The response contains properties from a number of property sources, one per property file and Spring profile that matched the API request. The property sources are returned in priority order; if a property is specified in multiple property sources, the first property in the response takes precedence.

**Encryption** Information can be encrypted and decrypted using the /encrypt and /decrypt endpoints exposed by the config server.

```
curl http://localhost:8888/encrypt -d my-super-secure-password
4d28a7cb6eb9976dbeae0eb1cc0cb05672f01789140394fe4d93069d3622ab10a25ba9cf2b4ae3fcbc566dfb6cf13403%   
```

```
curl http://localhost:8888/decrypt -d 4d28a7cb6eb9976dbeae0eb1cc0cb05672f01789140394fe4d93069d3622ab10a25ba9cf2b4ae3fcbc566dfb6cf13403
my-super-secure-password%    
```

If you want to use an encrypted value in a configuration file, you need to prefix it with {cipher} and wrap it in ''. For example, to store the encrypted version of 'my-super-secure-password', add the following line in a YAML-based configuration file:
```
secret-password: '{cipher}4d28a7cb6eb9976dbeae0eb1cc0cb05672f01789140394fe4d93069d3622ab10a25ba9cf2b4ae3fcbc566dfb6cf13403'
```

When the config server detects values in the format '{cipher}...', it tries to decrypt them using its encryption key before sending them to a client.

## Setting up Spring Cloud Config Clients

### Maven dependencies

```xml
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
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
	</dependencies>
```

### Configuration
* Move the configuration file, _application.yml_, to the config repository and rename it with the name of the client as specified by the property _spring.application.name_.
* Add a new _application.yml_ file to the _src/main/resources_ folder. This file will be used to hold the configuration required to connect to the config server.

```yaml
spring:
  application:
    name: datetime-service
  config:
    import: optional:configserver:http://${CONFIG_SERVER_HOST}:${CONFIG_SERVER_PORT}
  cloud.config:
    failFast: true
    retry:
      initialInterval: 3000
      multiplier: 1.3
      maxInterval: 10000
      maxAttempts: 20
```

This configuration will make the client do the following:
* Use environment variables to define the location of the config server
* Try to reconnect to the config server during startup up to 20 times, if required. If the connection attempt fails, the client will initially wait for 3 seconds before trying to reconnect. The wait time for subsequent retries will increase by a factor of 1.3. The maximum wait time between connection attempts will be 10 seconds. If the client can’t connect to the config server after 20 attempts, the startup sequence will fail

This configuration is generally good for resilience against temporary connectivity problems with the config server. It is especially useful when the whole landscape of microservices and its config server are started up at once, for example, when using the docker-compose up command. In this scenario, many of the clients will be trying to connect to the config server before it is ready, and the retry logic will make the clients connect to the config server successfully once it is up and running.

### Updating the configuration

TBD

```bash
$ curl -X POST HOST:PORT/actuator/refresh -d {} -H "Content-Type: application/json"
```

## Resources
- Spring Microservices in Action (Chapter 5)
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 12)



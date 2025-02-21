# Apache Zookeeper Centralized Configuration

Zookeeper offers a hierarchical namespace where clients can store various types of data, including configuration data. Spring Cloud Zookeeper Config serves as an alternative to the Config Server and Client.

By default, configuration is stored in the /config namespace. Multiple PropertySource instances are generated based on the application's name and active profiles. This setup mirrors the Spring Cloud Config's approach to resolving properties.

## Apache Zookeeper services

By adding the dependency org.springframework.cloud:spring-cloud-starter-zookeeper-config, you enable the autoconfiguration that sets up Spring Cloud Zookeeper Config.

### Maven dependencies

```
    <properties>
            <java.version>21</java.version>
            <spring-cloud.version>2023.0.2</spring-cloud.version>
    </properties>
    
    <dependencies>
            
            ...
            
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-zookeeper-config</artifactId>
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

### Service code

In a Java Spring Boot application using Zookeeper, the @EnableDiscoveryClient annotation enables service registration and discovery. It allows the application to automatically register itself with Zookeeper as a service and discover other services that are registered in the same distributed environment.

```
@SpringBootApplication
@EnableDiscoveryClient
public class TimeServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(TimeServiceApplication.class, args);
	}

}
```

To verify the correct reading of configuration information we can create a controller

```
@RestController
public class TimeController {

    @Value("${configuration}")
    private String configuration;

    @GetMapping("/configuration")
    public String getMessage() {

        return this.configuration;
    }
}
```

### configuration

In this section we will configure the application. It is possible to try the application locally with the file application-local.properties or on docker with application-docker.properties.

#### application.yml file

```
server:
    port: 8080

spring:
    application:
        name: time-service

    cloud:
        zookeeper:
            connect-string: localhost:2181
            discovery:
                enabled: true
                root: /services
        config:
            enabled: true
            root: /config

    config:
        import: "optional:zookeeper:"


---


server:
    port: 8080

spring:
    profiles:
        active: docker

    application:
        name: time-service

    cloud:
        zookeeper:
            connect-string: zookeeper:2181
            discovery:
                enabled: true
                root: /services
            config:
                enabled: true
                root: /config

    config:
        import: "optional:zookeeper:"

```

service configuration file local profile:

* server:port=8080: The application uses port 8080 to accept HTTP requests.
* spring:application:name=time-service: application name
* spring:cloud:zookeeper:connect-string=localhost:2181: Connects to Zookeeper located at localhost on port 2181.
* spring:cloud:zookeeper:discovery:enabled=true: Enables service discovery via Zookeeper.
* spring:cloud:zookeeper:discovery:root=/services: Services are registered in Zookeeper under the path /services.
* spring:cloud:zookeeper:config:enabled=true: Enables centralized configuration via Zookeeper.
* spring:cloud:zookeeper:config:root=/config: Configurations are stored in Zookeeper under the path /config.
* spring:config:import="optional:zookeeper": This indicates that if the specified source (in this case, ZooKeeper) is not available or cannot be reached, the application will not fail and will continue to use the local or default configurations.

service configuration file docker profile:

* server:port=8080: The application uses port 8080 to accept HTTP requests.
* spring:application:name=time-service: application name
* spring:cloud:zookeeper:connect-string=zookeeper:2181: Connects to Zookeeper located at zookeeper on port 2181.
* spring:cloud:zookeeper:discovery:enabled=true: Enables service discovery via Zookeeper.
* spring:cloud:zookeeper:discovery:root=/services: Services are registered in Zookeeper under the path /services.
* spring:cloud:zookeeper:config:enabled=true: Enables centralized configuration via Zookeeper.
* spring:cloud:zookeeper:config:root=/config: Configurations are stored in Zookeeper under the path /config.
* spring:config:import="optional:zookeeper": This indicates that if the specified source (in this case, ZooKeeper) is not available or cannot be reached, the application will not fail and will continue to use the local or default configurations.

### Docker configuration

To containerize the service add a Dockerfile and a docker-compose file.

#### Dockerfile

```
FROM eclipse-temurin:21-jdk as builder
WORKDIR extracted
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21-jdk
WORKDIR application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

#### docker-compose file

```
  zookeeper:
    image: zookeeper:3.7
    container_name: zookeeper
    ports:
      - "2181:2181"
    healthcheck:
      test: [ "CMD", "zkServer.sh", "status" ]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  config-service:
    build:
      context: ./config-service
      dockerfile: Dockerfile
    image: config-service
    ports:
      - "8083:8083"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_CLOUD_ZOOKEEPER_CONNECT_STRING: zookeeper:2181
    depends_on:
      zookeeper:
        condition: service_healthy
    healthcheck:
      test: [ "CMD-SHELL", "curl -f http://localhost:8083/health || exit 1" ]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  time-service:
    build:
      context: ./time-service
      dockerfile: Dockerfile
    image: time-service
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_CLOUD_ZOOKEEPER_CONNECT_STRING: zookeeper:2181
    depends_on:
      config-service:
        condition: service_healthy
```

## Configuration on Zookeeper

Now, we have to add the configuration file on Zookeeper for our service. In this way, our services can start and take the configuration information.

To do this, we have to populate the /config node on Zookeeper.

One way to do this is implemented by config-service and it is composed by the following steps:
1. connectToZookeeper: allow us to connect with Zookeeper
   * **Note**: CountDownLatch is used to wait that the connection with zookeeper is established
2. createOrUpdateNode: allow us to create the configuration nodes for our services:
   * first we create the /config node, then the other node for each service
   * **Note**: in zookeeper.create, with CreateMode we can specify the type of our zknode
3. close: allow us to close the connection with Zookeeper

Then, we have to ensure that our services (for example time-service), starts after that config-service has populate the /config node on Zookeeper.
To do this, we can implement a HealthCheckController.
The HealthCheckController verify if zookeeper-config-service has populate the /config node in Zookeeper by the following steps:
1. connect with Zookeeper
2. check if the nodes /config for our services exists
3. if the nodes exists, the Healtcheck return "OK"
4. if the nodes do not exists, the Healthcheck return "NOT_READY"
5. **note**: the healtcheck has to be implemented in the docker-compose file too

Finally, we can run the project:

```
docker-compose up --build
```

## Test

To ensure that the /config node is populated, we can use zkCli.sh:

```
[zk: localhost:2181(CONNECTED) 1] ls /
[config, zookeeper]
[zk: localhost:2181(CONNECTED) 2] get /config/time-service/configuration
I am time-service
```

You can open a browser and search:

```
localhost:8080/configuration
```

Finally, to verify the Healthcheck you can search:

```
http://localhost:8083/health
```

# Resources

- https://zookeeper.apache.org/
- https://cwiki.apache.org/confluence/display/ZOOKEEPER/Index
- https://spring.io/projects/spring-cloud-zookeeper

# Apache Zookeeper Centralized Configuration

Zookeeper offers a hierarchical namespace where clients can store various types of data, including configuration data. Spring Cloud Zookeeper Config serves as an alternative to the Config Server and Client.

By default, configuration is stored in the /config namespace. Multiple PropertySource instances are generated based on the application's name and active profiles. This setup mirrors the Spring Cloud Config's approach to resolving properties.

## Apache Zookeeper services

By adding the dependency org.springframework.cloud:spring-cloud-starter-zookeeper-config, you enable the auto-configuration that sets up Spring Cloud Zookeeper Config.

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

#### application.properties file

In this file you can decide if you want to try the application locally or on docker.

```
#spring.profiles.active=local
spring.profiles.active=docker
```

#### application-local.properties file

service configuration file:

* server.port=8080: The application uses port 8080 to accept HTTP requests.
* spring.application.name=time-service: application name
* spring.cloud.zookeeper.connect-string=localhost:2181: Connects to Zookeeper located at localhost on port 2181.
* spring.cloud.zookeeper.discovery.enabled=true: Enables service discovery via Zookeeper.
* spring.cloud.zookeeper.discovery.root=/services: Services are registered in Zookeeper under the path /services.
* spring.cloud.zookeeper.config.enabled=true: Enables centralized configuration via Zookeeper.
* spring.cloud.zookeeper.config.root=/config: Configurations are stored in Zookeeper under the path /config.
* spring.config.import=optional:zookeeper: This indicates that if the specified source (in this case, ZooKeeper) is not available or cannot be reached, the application will not fail and will continue to use the local or default configurations.

```
server.port=8080

spring.application.name=time-service

spring.cloud.zookeeper.connect-string=localhost:2181
spring.cloud.zookeeper.discovery.enabled=true
spring.cloud.zookeeper.discovery.root=/services

spring.cloud.zookeeper.config.enabled=true
spring.cloud.zookeeper.config.root=/config

spring.config.import=optional:zookeeper:
```

#### application-docker.properties file

service configuration file:

* server.port=8080: The application uses port 8080 to accept HTTP requests.
* spring.application.name=time-service: application name
* spring.cloud.zookeeper.connect-string=zookeeper:2181: Connects to Zookeeper located at zookeeeper on port 2181.
* spring.cloud.zookeeper.discovery.enabled=true: Enables service discovery via Zookeeper.
* spring.cloud.zookeeper.discovery.root=/services: Services are registered in Zookeeper under the path /services.
* spring.cloud.zookeeper.config.enabled=true: Enables centralized configuration via Zookeeper.
* spring.cloud.zookeeper.config.root=/config: Configurations are stored in Zookeeper under the path /config.
* spring.config.import=optional:zookeeper: This indicates that if the specified source (in this case, ZooKeeper) is not available or cannot be reached, the application will not fail and will continue to use the local or default configurations.

```
server.port=8080

spring.application.name=time-service

spring.cloud.zookeeper.connect-string=zookeeper:2181
spring.cloud.zookeeper.discovery.enabled=true
spring.cloud.zookeeper.discovery.root=/services

spring.cloud.zookeeper.config.enabled=true
spring.cloud.zookeeper.config.root=/config

spring.config.import=optional:zookeeper:
```

### Docker configuration

To containerize the service add a Dockerfile and a docker-compose file.

#### Dockerfile

```
FROM openjdk:21-jdk
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "application.jar"]
```

#### docker-compose file

```
  zookeeper:
    image: zookeeper:3.7
    container_name: zookeeper
    ports:
      - "2181:2181"

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
      - zookeeper
```

## Configuration on Zookeeper

Now, we have to add the configuration file on Zookeeper for our service.

To do this, we have to start Zookeeper:

```
docker-compose up -d zookeeper
```

Then we have to connect to the ZooKeeper instance using the ZooKeeper command line interface (CLI):

```
docker exec -it zookeeper zkCli.sh -server localhost:2181
```

Now in ZooKeeper, create a root node for configurations:

```
create /config
```

This command creates a znode (ZooKeeper node) /config to store configurations for various services.

Finally, create a node under /config specifically for time-service and set a configuration value:

```
create /config/time-service
create /config/time-service/configuration "I am time-service"
```

Verify that the configuration has been set correctly:

```
get /config/time-service/configuration
```

**Note**: in this example we have manually configured our service via zkCli. It is possible to automate the configuration by, for example, creating a Java script using the [Zookeeper API](Overview%20of%20Apache%20Zookeeper.md#api).

For example, if we want to create a zkNode, set a value and read it, we can use the Zookeeper API like this:
```
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

public class ZookeeperConfig {

    ...
    
    if (zk.exists(path, false) == null) {
                zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                zk.setData(path, data, zk.exists(path, false).getVersion());
            }
    
            byte[] readData = zk.getData(path, false, null);
            
    
    ...

}
```

## Run the project

Once everything in set up, we can run the example project with:

```
mvn clean package
docker-compose up --build
```

## Test

You can open a browser and search:

```
localhost:8080/configuration
```

# Resources

- https://zookeeper.apache.org/
- https://cwiki.apache.org/confluence/display/ZOOKEEPER/Index
- https://spring.io/projects/spring-cloud-zookeeper

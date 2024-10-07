# Image creation (Java)

## Building an Image (Dockerfile)

The most standard approach to convert the fat jars produced by Maven and Spring Boot into Docker images, is to create a *Dockerfile* that looks like this:

```dockerfile
FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
```

First, create your jar artifact inside the *target/* directory.

```bash
$ cd product-service-no-db
$ mvn clean package
```

Then, create a docker image and submit it to your local daemon. The following command gives to the image the same name of the folder containing the *Dockerfile*.

```bash
$ docker buildx build -t $(basename $(pwd)) .
```

**If we change something in our application, repackage it and rebuild the image we'll be able to see the build is using the cached layers except for the application layer:**

```bash
$ docker images                              
REPOSITORY              TAG       IMAGE ID       CREATED         SIZE
product-service-no-db   latest    9883188cab8e   4 seconds ago   478MB
...
```

Now let’s run our image. We will bind the port 8080 in the container to 8080 in our host machine:

```bash
$ docker run -e SPRING_PROFILES_ACTIVE=docker -p 8080:8080 product-service-no-db
```

This is what we can infer from the command:
* docker run: The docker run command will start the container and display log output in the Terminal. The Terminal will be locked as long as the container runs.
* -e SPRING_PROFILES_ACTIVE=docker sets the corresponding environment variable inside the container
* -p 8080:8080 option maps port 8080 in the container to port 8080 in the Docker host, which makes it possible to call it from the outside.

Now let's list our running containers:

```bash
docker ps                                                                     
CONTAINER ID   IMAGE                   COMMAND                  CREATED         STATUS         PORTS                    NAMES
f5c2c01a895a   product-service-no-db   "java -cp @/app/jib-…"   7 seconds ago   Up 7 seconds   0.0.0.0:8080->8080/tcp   youthful_dijkstra
```

Let's verify that there are no errors, and let’s hit our endpoint:

[http://localhost:8080/products](http://localhost:8080/products)

## Building an Image (Spring Boot Maven Plugin)

Spring Boot natively allows to build our application into a Docker image:

* integration with layertools, to help us inspect and extract the layers in our jar
* integration with [Cloud Native Buildpacks](https://buildpacks.io/), which help us build the actual Open Container Initiative (OCI) image

### Building an Image From the Command Line

Add the Spring Boot plugin to the *pom.xml* configuration file.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

Create your jar artifact inside the *target/* directory.

```bash
$ mvn clean package
```

Then, create a docker image and submit it to your local daemon.

```bash
$ mvn spring-boot:build-image
```

After the build is done, we can list our Docker images in the terminal to verify the images have been created as expected:

```bash
$ docker images
REPOSITORY                            TAG              IMAGE ID       CREATED        SIZE
paketobuildpacks/run-jammy-base       latest           def0c6bb7994   8 days ago     104MB
paketobuildpacks/builder-jammy-base   latest           72fa30f71c5f   44 years ago   1.43GB
product-service-no-db                 0.0.1-SNAPSHOT   526fa2f45c60   44 years ago   361MB
```

The image size is substantially reduced when compared to plain docker, but the process is quite *slow*.

### Building an Image as Part of the Artifact Build Process

We can of course **add the execution of this particular _spring-boot:build-image_ goal to the _spring-boot-maven-plugin_** setup to build the image each time we build the project artifact:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <image>
                    <name>product-service-no-db:${project.version}</name>
                </image>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>build-image</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Let’s build the image this time by triggering the Maven build process from the command line. All Spring Boot plugin executions are configured to run in the _package_ phase by default, so adding the _build-image_ to the executions will run this goal automatically in this same phase:

```bash
$ mvn package
```

**This will now package our application into a JAR file and build the Docker image as well.**

## Building an Image (Jib Maven Plugin)

[Jib](https://github.com/GoogleContainerTools/jib) is an open-source Java tool maintained by Google for building Docker images of Java applications. It simplifies containerization since with it, **we don’t need to write a _Dockerfile_.

Google publishes Jib as both a Maven and a Gradle plugin. **This saves us separate docker build/push commands and simplifies adding this to a CI pipeline.**

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.google.cloud.tools</groupId>
            <artifactId>jib-maven-plugin</artifactId>
            <version>3.4.3</version>
        </plugin>
    </plugins>
</build>
```

With this change, we can create images with:

```bash
# (pushes to DockerHub)
$ mvn compile jib:build 
```

```bash
# (pushes to local docker daemon)
$ mvn compile jib:dockerBuild
```

We can list our Docker images in the terminal to verify the images have been created as expected:

```bash
$ docker images
REPOSITORY                            TAG              IMAGE ID       CREATED        SIZE
paketobuildpacks/run-jammy-base       latest           def0c6bb7994   8 days ago     104MB
paketobuildpacks/builder-jammy-base   latest           72fa30f71c5f   44 years ago   1.43GB
product-service-no-db                 latest           4dd75cdb8aa6   54 years ago   316MB

```

_Jib_ created a smaller image in a much shorter time!


### Customizing Docker Aspects

By default, **Jib makes a number of reasonable guesses about what we want**, like the FROM and the ENTRYPOINT. Let’s make a couple of changes to our application that are more specific to our needs.

```xml
<configuration>
    <container>
        <ports>
            <port>8082</port>
        </ports>
    </container>
</configuration>
```

```xml
<configuration>
    <from>
        <image>openjdk:alpine</image>
    </from>
</configuration>
```

```xml
<configuration>
    <to>
        <image>product-service-no-db</image>
    </to>
</configuration>
```

We configure tags, volumes, and [several other Docker directives](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#extended-usage) in the same way. Jib supports numerous Java runtime configurations, too:

-   _jvmFlags_ is for indicating what startup flags to pass to the JVM.
-   _mainClass_ is for indicating the main class, which **Jib will attempt to infer automatically by default.**
-   _args_ is where we’d specify the program arguments passed to the _main_ method.

Of course, make sure to check out Jib’s documentation to see all the [configuration properties available](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin).

## Resources 
- [Docker Overview](https://docs.docker.com/get-started/overview/)
- [Docker - Getting Started guide](https://docs.docker.com/get-started/)
- [Creating Docker Images with Spring Boot](https://www.baeldung.com/spring-boot-docker-images)
- [Spring Boot Maven Plugin - Layered Jars](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#repackage-layers)

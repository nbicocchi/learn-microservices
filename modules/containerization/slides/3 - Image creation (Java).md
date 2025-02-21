# Image creation (Java)

## Building an Image (Dockerfile)

The most standard approach to convert the fat jars produced by Maven and Spring Boot into Docker images, is to create a *Dockerfile* that looks like this:

```dockerfile
FROM eclipse-temurin:21
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
<build>
    <plugin>
        <groupId>com.google.cloud.tools</groupId>
        <artifactId>jib-maven-plugin</artifactId>
        <version>3.4.3</version>
        <configuration>
            <from>
                <image>eclipse-temurin:21-jdk-alpine@sha256:sha256:c63d8669d87e16bcee66c0379d1deedf844152da449ad48f2c8bd73a3705d36b</image>
            </from>
            <to>
                <image>product-service-no-db</image>
            </to>
            <container>
                <mainClass>com.nbicocchi.product.App</mainClass>
            </container>
        </configuration>
    </plugin>
</build>
```

We configure tags, volumes, and [several other Docker directives](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#extended-usage) in the same way. Jib supports numerous Java runtime configurations, too:

-   _jvmFlags_ is for indicating what startup flags to pass to the JVM.
-   _mainClass_ is for indicating the main class, which **Jib will attempt to infer automatically by default.**
-   _args_ is where we’d specify the program arguments passed to the _main_ method.

Of course, make sure to check out Jib’s documentation to see all the [configuration properties available](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin).

## Optimizing Docker images

The image size can have a significant impact on your performance either as a developer or as an organization. Especially when you are working in large projects with multiple services, the size of the images can be quite large, and this could cost you a lot of money and time.

* **Disk space**: You are wasting disk space in your docker registry and in your production servers.
* **Slower builds**: The larger the image, the longer it takes to build and push the image.
* **Security**: The larger the image, the larger dependencies you have and the more attack surface you have.
* **Bandwidth**: The larger the image, the more Bandwidth consumption you have when pulling and pushing the image from and to the registry.

### Choosing the right base image

Using eclipse-temurin:21, the final image is 480M 

```dockerfile
FROM eclipse-temurin:21
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
```

Using openjdk:21-jdk-slim, the final image is 470M

```dockerfile
FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
```

Using eclipse-temurin:21-jdk-alpine, the final image is 400M

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
```

### Build your own image using jlink and multi-stage dockerfile

`jlink` is a tool that can be used to create a custom runtime image that contains only the modules that are needed to run your application.

```dockerfile
# First stage, build the custom JRE
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /optimized-jdk-21

# Second stage, Use the custom JRE and build the app image
FROM alpine:latest
ENV JAVA_HOME=/opt/jdk/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-builder /optimized-jdk-21 $JAVA_HOME

ARG APPLICATION_USER=spring
RUN addgroup --system $APPLICATION_USER &&  adduser --system $APPLICATION_USER --ingroup $APPLICATION_USER
COPY --chown=$APPLICATION_USER:$APPLICATION_USER target/*.jar /application.jar
USER $APPLICATION_USER

ENTRYPOINT [ "java", "-jar", "/application.jar" ]
```

We have two stages, the first stage is used to build a custom JRE image using jlink and the second stage is used to package the application in a slim alpine image.

* In the first stage, we used the eclipse-temurin:17-jdk-alpine image to build a custom JRE image using jlink. Then we run jlink to build a small JRE image that contains all the modules by using --add-modules ALL-MODULE-PATH that are needed to run the application.

* In the second stage, we used the alpine image (which is a quite small 3Mb) to package our application) as base image, we then took the custom JRE from the first stage and use it as our JAVA_HOME.

* The rest of the Dockerfile is the same as the previous one, just copying artifacts and setting the entrypoint using a custom user (not root).

**Using this approach, the final image is 170MB.**

## Resources 
- [Docker Overview](https://docs.docker.com/get-started/overview/)
- [Docker - Getting Started guide](https://docs.docker.com/get-started/)
- [Creating Docker Images with Spring Boot](https://www.baeldung.com/spring-boot-docker-images)
- [Spring Boot Maven Plugin - Layered Jars](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#repackage-layers)

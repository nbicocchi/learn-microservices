# Spring Boot with Docker

Welcome to a new lesson out of Learn Spring. In this lesson, we are going to learn about **the Spring Boot Docker support**.

Note that we won’t be covering what Docker is nor its basic concepts here. If you don’t have experience using this platform, then I strongly recommend having a look at the Docker links in the Resources section before moving on, as they will help you understand all the base Docker features.

The relevant module for this lesson is: [spring-boot-with-docker-end](https://github.com/nbicocchi/spring-boot-course/tree/module8/spring-boot-with-docker-end)

## The Spring Boot Plugin

If you have some experience with Docker, you know we can use a Dockerfile to build our Spring Boot application into a Docker image.

To do this efficiently, **we would have to split our application's jar and indicate which parts of the application will go into each image layer** for Docker to cache them properly, aiming to have less frequently changing layers above more frequently changing layers.

We can do this manually but depending on the size of the project it can get hard to maintain.

So to make this easier for us, Spring Boot added some features to **the Spring Boot Plugin on version 2.3 that help us with the process of building our application into a Docker image:**

-   layertools, to help us inspect and extract the layers in our jar
-   integration with Cloud Native Buildpacks, which help us build the actual Open Container Initiative (OCI) image

Let’s now see this in action.

## Building an Image From the Command Line

Before we start, please note that to build an image Docker must be installed in your system locally.

We'll be using our simple Projects REST service to package the application as a Docker image using the Spring Boot Maven plugin.

Note: make sure to run _mvn clean install_ first to download the plugin.

We can do this in two ways. **The first method is using the _spring-boot:build-image_ plugin goal directly in the terminal:**

```
$ mvn spring-boot:build-image
```

**We can use this if we only want to build the image quickly without modifying anything in our project configuration.**

If we explore the log entries we'll be able to see that the task is executed as expected:

```
[INFO] Building image 'docker.io/library/spring-boot-with-docker:0.1.0-SNAPSHOT'
```

As you can see, now the image is being built using the artifact id as the name (_spring-boot-with-docker_) and tagged with the corresponding artifact version (_0.1.0-SNAPSHOT_).

Naturally, we can customize these and other aspects by using the second approach to build the image. Let's see what that is.

## Building an Image as Part of the Artifact Build Process

We can of course **add the execution of this particular _spring-boot:build-image_ goal to the _spring-boot-maven-plugin_** setup so as to build the image each time we build the project artifact:

```
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <image>
                    <name>com.baeldung/project-api:${project.version}</name>
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

**Here we are also indicating how the image should be named (_com.baeldung/project-api_) and we're using XML variables to set the appropriate version tag.**

Let’s build the image this time by triggering the Maven build process from the command line. All Spring Boot plugin executions are configured to run in the _package_ phase by default, so adding the _build-image_ to the executions will run this goal automatically in this same phase:

```
$ mvn package
```

**This will now package our application into a JAR file and build the Docker image as well.**

## Exploring and Running the Images

After the build is done, we can list our Docker images in the terminal to verify the images have been created as expected:

```
$ docker images
spring-boot-with-docker-start      0.1.0-SNAPSHOT          c54fe8fa3ca3        40 years ago        245MB
com.baeldung/project-api           0.1.0-SNAPSHOT          8d185564c8d0        40 years ago        245MB
```

Now let’s run our image. We will name the container _project-api_ and bind the port 8080 in the container to 8080 in our host machine:

```
$ docker container run -d --name project-api -p 8080:8080 com.baeldung/project-api:0.1.0-SNAPSHOT
```

Now let's list our running containers:

```
$ docker container ps CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES e367d10649da com.baeldung/project-api:0.1.0-SNAPSHOT "/cnb/process/web" 55 seconds ago Up 49 seconds 0.0.0.0:8080->8080/tcp project-api
```

And check the application logs:

```
$ docker logs project-api
```

Let's verify that there are no errors, and let’s hit our endpoint:

[http://localhost:8080/projects](http://localhost:8080/projects)

As you can see, it works and we get our response as expected.

Let's go a little bit further now and explore what the plugin is doing under the hood.

## Layered Jars

As we indicated, **a Docker image is made up of layers. Each layer represents a certain instruction written in a Dockerfile.** This layered approach is useful because layers from one image can be reused in other images.

The Spring Boot team has made it easier to split our application into sensible layers to adapt efficiently to this approach.

Since version 2.4.0, **this feature is enabled by default**, so there is nothing special we have to do in our project to obtain a layered jar.

For previous versions, however, we have to enable this configuration explicitly in our _spring-boot-maven-plugin_ setup:

```
<configuration>
    <layers>
        <enabled>true</enabled>
    </layers>
</configuration>
```

By default, Spring defines four layers after we package the JAR. **We can inspect these layers using the _layertools_ mechanism** added by the Spring team:

```
$ java -Djarmode=layertools -jar target/spring-boot-with-docker-start-0.1.0-SNAPSHOT.jar list
dependencies
spring-boot-loader
snapshot-dependencies
application
```

As we can see, the command lists the four abovementioned layers:

-   _dependencies:_ contains all the application dependencies
-   _spring-boot-loader:_ for the Spring Boot loader classes
-   _snapshot-dependencies:_ contains snapshot dependencies that might change more often than normal dependencies
-   _application:_ contains our code

## Using the Layered Jar in a Dockerfile

**We can use this same _layertools_ instrument to extract and make use of the layers when we build the Docker image.**

For instance, let's have a look at the Dockerfile I created (which can be found in the code repo):

```
FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

Let’s go through the main instructions. The first line indicates we’re using _openjdk_ as builder.

Note: we are using a feature in Docker called multi-stage builds, please note you can only do this in Docker v17+.

Doing this will keep the size of our build down because we can make builders copy its content to other stages and then discard them on the final stage.

As you can see, we’re using _layertools_ to extract the layers we just saw. Then we run our actual build accessing the extracted content in the next stage.

Finally, we are using the Spring _JarLauncher_ to execute the Spring Boot application.

Let’s now build the image from the project root directory using the Docker CLI:

```
$ docker build -f docker/Dockerfile . --tag com.baeldung/project-api
```

Now our image is built, and we can test it:

```
$ docker container run --name project-api -p 8080:8080 com.baeldung/project-api:latest
```

Note: remember to end the _project-api_ container we started earlier first, otherwise this container won't be able to use port 8080.

As you can see, it works.

**If we change something in our application, repackage it and rebuild the image we'll be able to see the build is using the cached layers except for the application layer:**

```
$ mvn clean package
...
$ docker build -f docker/Dockerfile . --tag com.baeldung/project-api2
...
Step 8/12 : COPY --from=builder application/dependencies/ ./
 ---> Using cache
 ---> d65f759258a2
Step 9/12 : COPY --from=builder application/spring-boot-loader/ ./
 ---> Using cache
 ---> d96dc51846be
Step 10/12 : COPY --from=builder application/snapshot-dependencies/ ./
 ---> Using cache
 ---> 5b749d9b6d50
Step 11/12 : COPY --from=builder application/application/ ./
 ---> 948e098fa982
...
```

## The Jib Plugin

Google has an open source tool called [Jib](https://github.com/GoogleContainerTools/jib) that is relatively new but quite interesting for a number of reasons. Probably the most interesting thing is that you do not need docker to run it. Jib builds the image by using the same standard output as you get from `docker build` but does not use `docker` unless you ask it to, so it works in environments where docker is not installed (common in build servers). You also do not need a `Dockerfile` (it would be ignored anyway) or anything in your `pom.xml` to get an image built in Maven (Gradle would require you to at least install the plugin in `build.gradle`).

Another interesting feature of Jib is that it is opinionated about layers, and it optimizes them in a slightly different way than the multi-layer `Dockerfile` created above. As in the fat JAR, Jib separates local application resources from dependencies, but it goes a step further and also puts snapshot dependencies into a separate layer, since they are more likely to change. There are configuration options for customizing the layout further.

The following example works with Maven without changing the `pom.xml`:

```
$ mvn com.google.cloud.tools:jib-maven-plugin:build -Dimage=myorg/myapp
```

To run that command, you need to have permission to push to Dockerhub under the `myorg` repository prefix. If you have authenticated with `docker` on the command line, that works from your local `~/.docker` configuration. You can also set up a Maven “server” authentication in your `~/.m2/settings.xml` (the `id` of the repository is significant):

```
<server>
  <id>registry.hub.docker.com</id>
  <username>myorg</username>
  <password>...</password>
</server>
```

## Resources
- [Docker Overview](https://docs.docker.com/get-started/overview/)
- [Docker - Getting Started guide](https://docs.docker.com/get-started/)
- [Creating Docker Images with Spring Boot](https://www.baeldung.com/spring-boot-docker-images)
- [Spring Boot Maven Plugin - Layered Jars](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#repackage-layers)

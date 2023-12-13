# Dockerizing Java Apps using Jib

In this tutorial, we’ll take a look at Jib and how it simplifies containerization of Java applications.

We’ll take a simple Spring Boot application and build its Docker image using Jib. And then we’ll also publish the image to a remote registry.

## Introduction to Jib

_Jib_ is an open-source Java tool maintained by Google for building Docker images of Java applications. It simplifies containerization since with it, **we don’t need to write a _dockerfile._**

And actually, **we don’t even have to have _docker_ installed** to create and publish the docker images ourselves.

Google publishes Jib as both a Maven and a Gradle plugin. This is nice because it means that Jib will catch any changes we make to our application each time we build. **This saves us separate docker build/push commands and simplifies adding this to a CI pipeline.**

There are a couple of other tools out there, too, like Spotify’s [docker-maven-plugin](https://github.com/spotify/docker-maven-plugin) and [dockerfile-maven](https://github.com/spotify/dockerfile-maven) plugins, though the former is now deprecated and the latter requires a _dockerfile_.

## A Simple Greeting App

Let’s take a simple spring-boot application and dockerize it using Jib. It’ll expose a simple GET endpoint:

```bash
http://localhost:8080/greeting
```

Which we can do quite simply with a Spring MVC controller:

```java
@RestController
public class GreetingController {

    private static final String template = "Hello Docker, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name",
        defaultValue="World") String name) {

        return new Greeting(counter.incrementAndGet(),
          String.format(template, name));
    }
}
```

## Preparing the Deployment

We’ll also need to set ourselves up locally to authenticate with the Docker repository we want to deploy to.

For this example, we’ll **provide our DockerHub credentials to _.m2/settings.xml_**:

```
<servers>
    <server>
        <id>registry.hub.docker.com</id>
        <username><DockerHub Username></username>
        <password><DockerHub Password></password>
    </server>
</servers>
```

There are other ways to provide the credentials, too. **The recommended way by Google is to use helper tools, which can store the credentials in an encrypted format in the file system. In this example, we could have used [docker-credential-helpers](https://github.com/docker/docker-credential-helpers#available-programs)** instead of storing plain-text credentials in _settings.xml_, which is much safer, though simply out of scope for this tutorial.

## Deploying to Docker Hub With Jib

Now, we can use _jib-maven-plugin_, or the [Gradle equivalent](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin), **to** **containerize our application with a simple command**:

```bash
mvn compile com.google.cloud.tools:jib-maven-plugin:2.5.0:build -Dimage=$IMAGE_PATH
```

where IMAGE\_PATH is the target path in the container registry.

For example, to upload the image _baeldungjib/spring-jib-app_ to _DockerHub_, we would do:

```bash
export IMAGE_PATH=registry.hub.docker.com/baeldungjib/spring-jib-app
```

And that’s it! This will build the docker image of our application and push it to the _DockerHub_.

[![ibDocker 1](https://www.baeldung.com/wp-content/uploads/2018/10/JibDocker-1-1024x640-1.jpg)](https://www.baeldung.com/wp-content/uploads/2018/10/JibDocker-1-1024x640-1.jpg)

**We can, of course,** **upload the image to [Google Container Registry](https://cloud.google.com/container-registry/) or [Amazon Elastic Container Registry](https://aws.amazon.com/ecr/) in a similar way**.

## Simplifying the Maven Command
Also, we can shorten our initial command by configuring the plugin in our _pom_ instead, like any other maven plugin.

```xml
<project>
    ...
    <build>
        <plugins>
            ...
            <plugin>
                <groupId>com.google.cloud.tools</groupId>
                <artifactId>jib-maven-plugin</artifactId>
                <version>2.5.0</version>
                <configuration>
                    <to>
                        <image>${image.path}</image>
                    </to>
                </configuration>
            </plugin>
            ...
        </plugins>
    </build>
    ...
</project>
```

With this change, we can simplify our maven command:

```bash
mvn compile jib:build
```

## Customizing Docker Aspects

By default, **Jib makes a number of reasonable guesses about what we want**, like the FROM and the ENTRYPOINT.

Let’s make a couple of changes to our application that are more specific to our needs.

First, Spring Boot exposes port 8080 by default.

But, let’s say, we want to make our application run on port 8082 and make it exposable through a container.

Of course, we’ll make the appropriate changes in Boot. And, after that, we can use Jib to make it exposable in the image:

```xml
<configuration>
    ...
    <container>
        <ports>
            <port>8082</port>
        </ports>
    </container>
</configuration>
```

Or, let’s say we need a different FROM. **By default, Jib uses the [distro-less java image](https://github.com/GoogleContainerTools/distroless/tree/master/java)**.

If we want to run our application on a different base image, like [alpine-java](https://hub.docker.com/r/anapsix/alpine-java/), we can configure it in a similar way:

```xml
<configuration>
    ...
    <from>
        <image>openjdk:alpine</image>
    </from>
    ...
</configuration>
```

We configure tags, volumes, and [several other Docker directives](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#extended-usage) in the same way.

## Customizing Java Aspects

And, by association, Jib supports numerous Java runtime configurations, too:

-   _jvmFlags_ is for indicating what startup flags to pass to the JVM.
-   _mainClass_ is for indicating the main class, which **Jib will attempt to infer automatically by default.**
-   _args_ is where we’d specify the program arguments passed to the _main_ method.

Of course, make sure to check out Jib’s documentation to see all the [configuration properties available](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin).


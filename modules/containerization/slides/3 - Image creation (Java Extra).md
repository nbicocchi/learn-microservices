# Image creation (Java)

## Multi-Layer JARS

```dockerfile
FROM eclipse-temurin:21-jre-ubi9-minimal
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
```

The issue with the file is that it isn’t very efficient if you frequently update your application. Docker images are built in layers, and in this case your application and all its dependencies are put into a single layer. Since you probably recompile your code more often than you upgrade the version of Spring Boot you use, it’s often better to separate things a bit more. If you put jar files in the layer before your application classes, Docker often only needs to change the very bottom layer and can pick others up from its cache.

By default, Spring defines four layers after we package the JAR. **We can inspect these layers using the _layertools_ mechanism** added by the Spring team:

```
$ java -Djarmode=layertools -jar target/spring-with-docker-end-0.1.0-SNAPSHOT.jar list
dependencies
spring-boot-loader
snapshot-dependencies
application
```

The command lists these four layers:

-   _dependencies:_ contains all the application dependencies
-   _spring-boot-loader:_ contains the Spring Boot loader classes
-   _snapshot-dependencies:_ contains snapshot dependencies that might change more often than normal dependencies
-   _application:_ contains our code

The [official documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/executable-jar.html#appendix.executable-jar) describes the internal structure of JAR files.

**We can use this same _layertools_ instrument to extract and make use of the layers when we build the Docker image.**

For instance, let's have a look at the Dockerfile included in this project:

```dockerfile
FROM eclipse-temurin:21-jre-ubi9-minimal AS builder
WORKDIR /extracted
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /application.jar
RUN java -Djarmode=layertools -jar /application.jar extract

FROM eclipse-temurin:21-jre-ubi9-minimal
WORKDIR /application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

We are using a feature in Docker called multi-stage builds. Doing this will keep the size of our build down because we can make builders copy its content to other stages and then discard them on the final stage. As you can see, we’re using _layertools_ to extract the layers. Then we run our actual build accessing the extracted content in the next stage. Finally, we are using the Spring _JarLauncher_ to execute the Spring Boot application.

```bash
$ mvn clean package
$ docker buildx build -f "Dockerfile.multi-layer" -t $(basename $(pwd)) .
```

**If we change something in our application, repackage it and rebuild the image we'll be able to see the build is using the cached layers except for the application layer**

## Thin JARs

![](images/jar-types.webp)

### JAR Types

- **Skinny** Contains ONLY the bits you literally type into your code editor, and NOTHING else. 
- **Thin** Contains all of the above PLUS the app’s direct dependencies of your app (db drivers, utility libraries, etc).
- **Hollow** The inverse of Thin. Contains only the bits needed to run your app but does NOT contain the app itself. Basically a pre-packaged “app server” to which you can later deploy your app, in the same style as traditional Java EE app servers.
- **Fat/Uber** – Contains the bit you literally write yourself PLUS the direct dependencies of your app PLUS the bits needed to run your app _on its own_.

### Why Bother?
The popularity of DevOps processes, Linux containers, and microservice architectures has made app footprint (the number of bytes that make up your app) important again. When you’re deploying to dev, test, and production environments multiple times a day (sometimes hundreds per hour or even [2 billion times a week](https://www.infoq.com/news/2014/06/everything-google-containers/)), minimizing the size of your app can have a huge impact on overall DevOps efficiency, and your operational sanity. You don’t have to minimize the lines of code in your app, but you should reduce the number of times your app and its app dependencies have to pass across a network, move onto or off of a disk, or be processed by a program. 

### How to Create a Thin JAR?
The [Spring Boot Thin Launcher](https://github.com/spring-projects-experimental/spring-boot-thin-launcher) is a small library that reads an artifact’s dependencies from a file bundled in the archive itself, downloads them from a Maven repository and finally launches the main class of the application.

So, when we build a project with the library, we get a JAR file with our code, a file enumerating its dependencies, and the main class from the library that performs the above tasks.

In a Maven project, we have to modify the declaration of the Boot plugin to include a dependency on the custom *thin* layout:

```
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <dependencies>
        <!-- The following enables the "thin jar" deployment option. -->
        <dependency>
            <groupId>org.springframework.boot.experimental</groupId>
            <artifactId>spring-boot-thin-layout</artifactId>
            <version>1.0.11.RELEASE</version>
        </dependency>
    </dependencies>
</plugin>
```

### Storing Dependencies
The point of thin jars is to avoid bundling the dependencies with the application. However, dependencies don’t magically disappear, they’re simply stored elsewhere.

In particular, the Thin Launcher uses the Maven infrastructure to resolve dependencies, so: 
* it checks the local Maven repository, which by default lies in ~/.m2/repository but can be moved elsewhere
* it downloads missing dependencies from Maven Central (or any other configured repository);
finally, it caches them in the local repository, so that it won’t have to download them again the next time we run the application.

Of course, the download phase is the slow and error-prone part of the process, because it requires access to Maven Central through the Internet, or access to a local proxy, and we all know how those things are generally unreliable.

Fortunately, there are various ways of deploying the dependencies together with the application, for example in a prepackaged container for cloud deployment.

#### Running the Application for Warm-up

The simplest way to cache the dependencies is to do a warm-up run of the application in the target environment. As we’ve seen earlier, this will cause the dependencies to be downloaded and cached in the local Maven repository. If we run more than one app, the repository will end up containing all the dependencies without duplicates.

Since running an application can have unwanted side effects, we can also perform a “dry run” that only resolves and downloads the dependencies without running any user code:

```
$ java -Dthin.dryrun=true -jar my-app-1.0.jar
```

#### Packaging the Dependencies During the Build

Another option is to collect the dependencies during the build, without bundling them in the JAR. Then, we can copy them to the target environment as part of the deployment procedure.

This is generally simpler because it’s not necessary to run the application in the target environment. However, if we’re deploying multiple applications, we’ll have to merge their dependencies, either manually or with a script.

We can point an application using the Thin Launcher to any such directory (including a local Maven repository) at runtime with the thin.root property:

```
$ java -jar my-app-1.0.jar --thin.root=my-app/deps
```

We can also safely merge multiple such directories by copying them one over another, thus obtaining a Maven repository with all the necessary dependencies.


#### Packaging the Dependencies With Maven

To have Maven package the dependencies for us, we use the resolve goal of the spring-boot-thin-maven-plugin. We can invoke it manually or automatically in our pom.xml:

```
<plugin>
    <groupId>org.springframework.boot.experimental</groupId>
    <artifactId>spring-boot-thin-maven-plugin</artifactId>
    <version>${thin.version}</version>
    <executions>
        <execution>
        <!-- Download the dependencies at build time -->
        <id>resolve</id>
        <goals>
            <goal>resolve</goal>
        </goals>
        <inherited>false</inherited>
        </execution>
    </executions>
</plugin>
```

After building the project, we’ll find a directory target/thin/root/ with the structure that we’ve discussed in the previous section.

## Resources

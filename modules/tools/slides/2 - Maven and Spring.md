# Maven

## Project Directory Structure

Since our project is Maven-based, **it follows Maven’s standard directory layout**. 

### _src/main/java_:

This directory contains the Java source code for the application we’re building; all our packages and classes go into this directory.

### _src/main/resources_:

This directory contains all the non-Java artifacts that are used by our application; we can put our configuration and property files in this directory.

### _src/test/java_ and _src/test/resources_:

These directories contain our test source code and resources, similarly to the _src/main/\*_ directories.

## Project configuration (pom.xml)

First, we've defined the basic identifying information about our project: the _groupId_, _artifactId_, _version_ and then _packaging_:

```
<groupId>com.nbicocchi</groupId>
<artifactId>product-service-no-db</artifactId>
<version>0.0.1-SNAPSHOT</version>
<description>Demo product for Spring Boot</description>
```

Next, we have a core part of the pom here, namely the _parent_:

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.4</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```

We are using the Boot parent, which defines dependencies, plugins, properties that our project will inherit. This greatly simplifies the configuration of our project, as no longer have to define all these explicitly.

Below, we have a handful of properties which override what’s defined in the parent:

```
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>21</java.version>
</properties>
```

Now we’re reached the _dependencies_ section. This section of the `pom.xml` configures the necessary libraries for developing a Spring Boot web application.

```
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
</dependencies>
```

* **Lombok**: Lombok simplifies the code by using annotations to auto-generate common Java methods, reducing redundancy and improving maintainability. 
* **Spring Boot Starter Web**: This dependency enables building web applications using Spring MVC. It includes essential libraries for creating REST APIs, handling HTTP requests, and rendering web content. It also comes with an embedded server (e.g., Tomcat) to run the application without external configuration. 
* **Spring Boot Starter Actuator**: Actuator adds production-ready features like health monitoring, metrics, and application diagnostics. It exposes various endpoints to track the status of the application (e.g., `/health` for health checks and `/metrics` for performance insights). 
* **Spring Boot Starter Test**: his dependency bundles essential libraries for testing Spring Boot applications, such as JUnit for writing unit tests, Mockito for mocking objects, and Spring's testing framework for integration tests. It simplifies the process of testing Spring components and ensures the application behaves as expected.

We can view all the transitive dependencies either by using the _Dependency Hierarchy_ tab (inside an IDE), or with the Maven command:

```
$ mvn dependency:tree

[INFO] com.baeldung:real-world-project:jar:0.0.1-SNAPSHOT
[INFO] +- org.springframework.boot:spring-boot-starter-web:jar:3.2.1:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:3.2.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot:jar:3.2.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-autoconfigure:jar:3.2.1:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-starter-logging:jar:3.2.1:compile
[INFO] |  |  |  +- ch.qos.logback:logback-classic:jar:1.4.14:compile
[INFO] |  |  |  |  \- ch.qos.logback:logback-core:jar:1.4.14:compile
[INFO] |  |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.21.1:compile
[INFO] |  |  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.21.1:compile
[INFO] |  |  |  \- org.slf4j:jul-to-slf4j:jar:2.0.9:compile
[INFO] |  |  +- jakarta.annotation:jakarta.annotation-api:jar:2.1.1:compile
[INFO] |  |  \- org.yaml:snakeyaml:jar:2.2:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-json:jar:3.2.1:compile
[INFO] |  |  +- com.fasterxml.jackson.core:jackson-databind:jar:2.15.3:compile
[INFO] |  |  |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.15.3:compile
[INFO] |  |  |  \- com.fasterxml.jackson.core:jackson-core:jar:2.15.3:compile
[INFO] |  |  +- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:jar:2.15.3:compile
[INFO] |  |  +- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:jar:2.15.3:compile
[INFO] |  |  \- com.fasterxml.jackson.module:jackson-module-parameter-names:jar:2.15.3:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-tomcat:jar:3.2.1:compile
[INFO] |  |  +- org.apache.tomcat.embed:tomcat-embed-core:jar:10.1.17:compile
[INFO] |  |  +- org.apache.tomcat.embed:tomcat-embed-el:jar:10.1.17:compile
[INFO] |  |  \- org.apache.tomcat.embed:tomcat-embed-websocket:jar:10.1.17:compile
[INFO] |  +- org.springframework:spring-web:jar:6.1.2:compile
[INFO] |  |  +- org.springframework:spring-beans:jar:6.1.2:compile
[INFO] |  |  \- io.micrometer:micrometer-observation:jar:1.12.1:compile
[INFO] |  |     \- io.micrometer:micrometer-commons:jar:1.12.1:compile
[INFO] |  \- org.springframework:spring-webmvc:jar:6.1.2:compile
[INFO] |     +- org.springframework:spring-aop:jar:6.1.2:compile
[INFO] |     +- org.springframework:spring-context:jar:6.1.2:compile
[INFO] |     \- org.springframework:spring-expression:jar:6.1.2:compile
[INFO] \- org.springframework.boot:spring-boot-starter-test:jar:3.2.1:test
[INFO]    +- org.springframework.boot:spring-boot-test:jar:3.2.1:test
[INFO]    +- org.springframework.boot:spring-boot-test-autoconfigure:jar:3.2.1:test
[INFO]    +- com.jayway.jsonpath:json-path:jar:2.8.0:test
[INFO]    |  \- org.slf4j:slf4j-api:jar:2.0.9:compile
[INFO]    +- jakarta.xml.bind:jakarta.xml.bind-api:jar:4.0.1:test
[INFO]    |  \- jakarta.activation:jakarta.activation-api:jar:2.1.2:test
[INFO]    +- net.minidev:json-smart:jar:2.5.0:test
[INFO]    |  \- net.minidev:accessors-smart:jar:2.5.0:test
[INFO]    |     \- org.ow2.asm:asm:jar:9.3:test
[INFO]    +- org.assertj:assertj-core:jar:3.24.2:test
[INFO]    |  \- net.bytebuddy:byte-buddy:jar:1.14.10:test
[INFO]    +- org.awaitility:awaitility:jar:4.2.0:test
[INFO]    +- org.hamcrest:hamcrest:jar:2.2:test
[INFO]    +- org.junit.jupiter:junit-jupiter:jar:5.10.1:test
[INFO]    |  +- org.junit.jupiter:junit-jupiter-api:jar:5.10.1:test
[INFO]    |  |  +- org.opentest4j:opentest4j:jar:1.3.0:test
[INFO]    |  |  +- org.junit.platform:junit-platform-commons:jar:1.10.1:test
[INFO]    |  |  \- org.apiguardian:apiguardian-api:jar:1.1.2:test
[INFO]    |  +- org.junit.jupiter:junit-jupiter-params:jar:5.10.1:test
[INFO]    |  \- org.junit.jupiter:junit-jupiter-engine:jar:5.10.1:test
[INFO]    |     \- org.junit.platform:junit-platform-engine:jar:1.10.1:test
[INFO]    +- org.mockito:mockito-core:jar:5.7.0:test
[INFO]    |  +- net.bytebuddy:byte-buddy-agent:jar:1.14.10:test
[INFO]    |  \- org.objenesis:objenesis:jar:3.3:test
[INFO]    +- org.mockito:mockito-junit-jupiter:jar:5.7.0:test
[INFO]    +- org.skyscreamer:jsonassert:jar:1.5.1:test
[INFO]    |  \- com.vaadin.external.google:android-json:jar:0.0.20131108.vaadin1:test
[INFO]    +- org.springframework:spring-core:jar:6.1.2:compile
[INFO]    |  \- org.springframework:spring-jcl:jar:6.1.2:compile
[INFO]    +- org.springframework:spring-test:jar:6.1.2:test
[INFO]    \- org.xmlunit:xmlunit-core:jar:2.9.1:test
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  18.961 s
[INFO] Finished at: 2024-01-01T22:22:15+01:00
[INFO] ------------------------------------------------------------------------
```

Next, we have the _build_ section.

```
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>com.google.cloud.tools</groupId>
            <artifactId>jib-maven-plugin</artifactId>
            <version>3.4.3</version>
            <configuration>
                <from>
                    <image>eclipse-temurin:21-jre-ubi9-minimal@sha256:9a524a6aed54d2c6445d98e00003b403231154a29253a6ebb7586475f3bb28e9</image>
                </from>
                <to>
                    <image>product-service-no-db</image>
                </to>
                <container>
                    <mainClass>com.nbicocchi.product.App</mainClass>
                </container>
            </configuration>
        </plugin>
    </plugins>
</build>
```

* **Spring Boot Maven Plugin**: This plugin is used to package Spring Boot applications as executable JARs or WARs. It provides goals like `spring-boot:run` for running the application directly from the command line and `spring-boot:repackage` for creating an executable JAR with embedded dependencies.
* **Jib Maven Plugin**: Jib is a plugin for building Docker and OCI containers without requiring Docker to be installed. It is used to containerize the application directly from the Maven build process, skipping the need for a Dockerfile.

Here’s a comprehensive overview of Maven lifecycles, phases, goals, and plugins, along with practical examples and guidance on recognizing whether you're invoking a phase or a goal.

## Lifecycles

Maven has three built-in lifecycles that govern the build process:

1. **Default Lifecycle**: Manages the project build, testing, and packaging.
2. **Clean Lifecycle**: Handles the cleaning of the project.
3. **Site Lifecycle**: Generates project documentation and reports.

### 1. Default Lifecycle

The default lifecycle is the main one used for building projects. It consists of several phases that are executed in order.

Key Phases in the Default Lifecycle:

- **validate**: Validates the project is correct and all necessary information is available.
- **compile**: Compiles the source code.
- **test**: Runs unit tests.
- **package**: Packages the compiled code into a distributable format (like JAR or WAR).
- **verify**: Runs checks to verify the package is valid and meets quality criteria.
- **install**: Installs the package into the local repository.
- **deploy**: Copies the final package to a remote repository for sharing with other developers.

Practical Example: To execute the default lifecycle up to the `package` phase, you can run:

```bash
mvn package
```

This command will execute the following phases in order:
- **validate**
- **compile**
- **test**
- **package** (creates the JAR or WAR file)

### 2. Clean Lifecycle

The clean lifecycle is focused on cleaning up the project by removing files created by previous builds.

Key Phases in the Clean Lifecycle:
- **pre-clean**: Executes processes needed prior to the cleaning.
- **clean**: Removes the `target` directory.
- **post-clean**: Executes processes needed after cleaning.

### 3. Site Lifecycle

The site lifecycle is used for generating project documentation and reports.

Key Phases in the Site Lifecycle:

- **pre-site**: Executes processes needed before generating the site.
- **site**: Generates the project's site documentation.
- **post-site**: Executes processes needed after generating the site.
- **site-deploy**: Deploys the generated site to a web server.

## Goals and plugins

A **goal** is a specific task that Maven performs, typically defined by a plugin. Goals can be executed independently or as part of a phase.

A **plugin** is a set of goals that provide specific functionalities to the Maven build process. Some commonly used plugins include:

1. **maven-compiler-plugin**: Used for compiling Java code.
    - Command:
      ```bash
      mvn compiler:compile
      ```
    - Explanation: Directly invokes the `compile` goal from the compiler plugin.

2. **maven-surefire-plugin**: Used for running unit tests.
    - Command:
      ```bash
      mvn surefire:test
      ```
    - Explanation: Runs the tests defined in the project using the Surefire plugin.

3. **maven-jar-plugin**: Used for creating JAR files.
    - Command:
      ```bash
      mvn jar:jar
      ```
    - Explanation: Invokes the `jar` goal from the JAR plugin.

### Recognizing Phase vs. Goal

To determine whether you are invoking a phase or a goal, consider the following:

1. **Check the Command Structure**:
    - **Phase**: If the command is in the format `mvn <phase-name>`, you are invoking a phase.
        - Example: `mvn install`
    - **Goal**: If the command is in the format `mvn <plugin>:<goal>`, you are invoking a specific goal.
        - Example: `mvn clean:clean` invokes the `clean` goal from the clean plugin.

2. **Help Command**:
    - **List Phases**: You can list the default lifecycle phases with:
      ```bash
      mvn help:effective-pom
      ```
    - **List Goals for a Plugin**: You can list the goals associated with a specific plugin with:
      ```bash
      mvn help:describe -Dplugin=org.apache.maven.plugins:maven-compiler-plugin
      ```

3. **Execution Behavior**:
    - Invoking a phase (e.g., `mvn package`) triggers all preceding phases in the lifecycle.
    - Invoking a goal (e.g., `mvn clean`) executes only that specific goal without triggering other phases.

## Running the project

### Running with the Spring Boot Maven plugin

**The Spring Boot Maven plugin provides various convenient features to build and run our application.** We have to explicitly include it in the _build > plugins_ section of our project’s _pom.xml_ file:

```
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

With this in place, we can now run the application by executing the _run_ goal of the plugin. This will build and then run the application in place:

```
$ mvn spring-boot:run
```

**It’s worth mentioning that running the application using the plugin isn’t recommended in production.** First of all, it would need the codebase to be present in the server. Furthermore, the process wouldn’t be optimized, as it has to pull the dependencies, build the app, and then run the application in place each time it’s executed.

Usually, in production environments, it’s suitable to run a pre-packaged artifact such as a JAR file or a [container](https://www.docker.com/resources/what-container/).

### Running as a Jar

**A regular Spring Boot jar can’t be executed out of the box, since it doesn’t include the “provided” dependencies (that should be supplied by the container)** that are required to run the application.

**The Spring Boot Maven Plugin comes into play again with a _repackage_ goal. This feature packages the jar into an executable fat jar containing the application class files and all the necessary dependencies to run the project as a self-contained app.** Since we’re inheriting the plugin from the _spring-boot-starter-parent_ pom, and including the plugin in our _build_ configuration, the _repackage_ goal execution is preconfigured and will be triggered as part of the regular build process:

```
$ mvn clean package
```

We can see in the logs that the _repackage_ goal is, in fact, executed:

```
[INFO] --- spring-boot-maven-plugin:repackage (repackage) @ deploying-boot-2-application-other-options ---
[INFO] Replacing main artifact with repackaged archive
```

**Having a packaged executable jar allows us to simply run the application using the _java_ command:**

```
$ java -jar target/product-service-no-db-0.0.1-SNAPSHOT.jar
```

As you might imagine, here is where we would provide runtime configurations using command line arguments, if necessary:

```
$ java -jar -Dserver.port=8082 target/product-service-no-db-0.0.1-SNAPSHOT.jar
```

or environment variables:

```
$ SERVER_PORT=8182 \
java -jar target/product-service-no-db-0.0.1-SNAPSHOT.jar
```

or both:

```
$ SERVER_PORT=8082 java -jar -Dserver.port=8083 \
target/product-service-no-db-0.0.1-SNAPSHOT.jar

2024-10-06T15:29:49.860+02:00  INFO 63986 --- [           main] com.nbicocchi.product.App                : No active profile set, falling back to 1 default profile: "default"
2024-10-06T15:29:50.944+02:00  INFO 63986 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8083 (http)
2024-10-06T15:29:50.957+02:00  INFO 63986 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2024-10-06T15:29:50.957+02:00  INFO 63986 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.30]
2024-10-06T15:29:50.987+02:00  INFO 63986 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext


...
```

Spring Boot uses a very particular order that is designed to allow sensible overriding of values. Later property sources can override the values defined in earlier ones as described [here](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config).

This is a comparatively better approach for running the app in a production environment, as we’ve decoupled the build and execution processes; the server only needs to have the required Java version installed to run the app.

## Resources
- [Maven Getting Started Guide](https://maven.apache.org/guides/getting-started/)
- [Building Java Projects with Maven](https://spring.io/guides/gs/maven/)
- [Spring Boot Maven Installation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#getting-started.installing.java.maven)
- [Maven Transitive Dependencies](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Transitive_Dependencies)
- [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)

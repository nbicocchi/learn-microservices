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

## Parent

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.3</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```

## Identifying information

```
<groupId>com.nbicocchi</groupId>
<artifactId>product-service-no-db</artifactId>
<version>0.0.1-SNAPSHOT</version>
<description>Demo product for Spring Boot</description>
```

## Properties

```
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>21</java.version>
</properties>
```

## Dependencies

```xml
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
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  18.961 s
[INFO] Finished at: 2024-01-01T22:22:15+01:00
[INFO] ------------------------------------------------------------------------
```

## Build

```xml
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
                    <image>eclipse-temurin:21-jdk-alpine@sha256:c63d8669d87e16bcee66c0379d1deedf844152da449ad48f2c8bd73a3705d36b</image>
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

Practical Example: To execute the default lifecycle up to the `package` phase, you can run (the name of the **lifecycle** is always omitted):

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

## Running the project

### Running with the Spring Boot Maven plugin

**The Spring Boot Maven plugin provides various convenient features to build and run our application.** We have to explicitly include it in the _build > plugins_ section of our project’s _pom.xml_ file:

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

With this in place, we can now run the application by executing the _run_ goal of the plugin. This will build and then run the application in place:

```bash
mvn spring-boot:run
```

**It’s worth mentioning that running the application using the plugin isn’t recommended in production.** 
* **Source code has to be present on the server**. 
* **Cold start would be slow**: it has to pull the dependencies, build the app, and then run the application (high Mean Time To Start).

In production environments, it is more suitable to run a pre-packaged artifact such as a container.

### Running as a Jar

**A regular jar can’t be executed out of the box, since it doesn’t include all the needed dependencies** that are required to run the application.

The Spring Boot Maven Plugin packages an executable **fat jar** containing the application class files and all the necessary dependencies to run the project as a self-contained app. Since we’re including the plugin in our _build_ configuration, the _package_ phase execution is preconfigured to build a **fat jar**.

```bash
mvn clean package -Dmaven.skip.test=true
```

```bash
java -jar target/product-service-h2-0.0.1-SNAPSHOT.jar
```

## Resources
- [Maven Getting Started Guide](https://maven.apache.org/guides/getting-started/)
- [Building Java Projects with Maven](https://spring.io/guides/gs/maven/)
- [Spring Boot Maven Installation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#getting-started.installing.java.maven)
- [Maven Transitive Dependencies](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Transitive_Dependencies)
- [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)

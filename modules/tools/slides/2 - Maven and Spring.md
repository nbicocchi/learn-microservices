# Maven and Spring

In this lesson, we'll have a look at how our Spring Boot project is set up with Maven. Note that this is not an intro to Maven itself. If you’re new to Maven, have a look at the Resources section for some helpful guides.

The relevant module for this lesson is: [real-world-project](../code/learn-spring-m1/real-world-project-end).

## Maven pom.xml

Let's start by opening up the pom of our project here and see exactly how everything is set up.

First, we've defined the basic identifying information about our project: the _groupId_, _artifactId_, _version_ and then _packaging_:

```
<groupId>com.baeldung</groupId>
<artifactId>real-world-project</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>real-world-project</name>
<description>Demo project for Spring Boot</description>
```

These have little impact on how we’ll build the project but they will determine the final output of that build, which is a _jar_ file.

Next, we have a core part of the pom here, namely the _parent_:

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.1</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```

Of course, **we’re using the Boot parent here**, which defines dependencies, plugins, properties that our project will inherit.

This greatly simplifies the configuration of our project, as no longer have to define all these explicitly.

Now we’re reached the _dependencies_ section, which is where things get interesting. As we can see, **we actually have a single dependency at this stage**:

```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

However, as we've seen, this is a fully running, functional project.

This is because **this one dependency is actually pulling in quite a large number of other artifacts using the transitive dependencies Maven functionality**.

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

By going through the IDE or console output, we see that among various artifacts, Spring Boot pulls the following ones:

```
ch.qos.logback:logback-classic:jar:1.2.3:compile
javax.annotation:javax.annotation-api:jar:1.3.2:compile
com.fasterxml.jackson.core:jackson-databind:jar:2.9.9.3:compile
org.hibernate.validator:hibernate-validator:jar:6.0.17.Final:compile
```

These are dependencies that allows us to use logging, annotation, serialization and database-related functionality that we'll usually need in any reasonable application. If we were using pure Spring, we would have to add those artifacts manually to our _pom.xml_.

Next, in the pom file, we have the _plugins_ section.

The only plugin we need to define explicitly at this stage is the Spring Boot Maven Plugin:

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

Finally, we have a handful of properties which override what’s defined in the parent:

```
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>21</java.version>
</properties>
```

And this completes our Maven pom configuration. As we can see, this is quite simple, but there’s still quite a lot here. Now let's run a simple build. We can do this via the IDE, or by using the command:

```
$ mvn clean
$ mvn package
```

You can run the result of the build with:

```
$ java -jar target/real-world-project-0.0.1-SNAPSHOT.jar
```

## Key Concepts

The Maven build follows a specific lifecycle to deploy and distribute the target project.

### Lifecycles
There are three built-in lifecycles:

* **clean**: to clean the project and remove all files generated by the previous build
* **default**: the main lifecycle, as it’s responsible for project deployment
* **site**: to create the project’s site documentation

A Maven **phase represents a stage in a Maven lifecycle. Each phase is responsible for a specific task.** 

#### _clean_ lifecycle
By using the _clean_ phase (mvn clean), you ensure a clean and consistent build environment. It removes previously generated artifacts, reducing the risk of conflicts or outdated files.

* _pre-clean_
* _clean_
* _post-clean_

#### _default_ lifecycle
The Build phase (mvn compile, mvn test, mvn package, etc.) automates the compilation, testing, and packaging processes. It ensures consistent and reliable builds across different test environments. Maven’s dependency management handles the resolution and integration of external libraries, simplifying the development process. Build reports and test results provide valuable insights into the project’s health and code quality.

* _validate:_ check if all information necessary for the build is available
* _compile:_ compile the source code
* _test:_ run unit tests
* _package:_ package compiled source code into the distributable format (jar, war, …)
* _verify:_ process and deploy the package if needed to run integration tests
* _install:_ install the package to a local repository
* _deploy:_ copy the package to the remote repository

#### _site_ lifecycle
The _site_ phase (mvn site) generates comprehensive project documentation, reports, and metrics. It facilitates better understanding and collaboration among team members.

* _pre-site_
* _site_
* _post-site_
* _site-deploy_


For the full list of each lifecycle’s phases, check out the [Maven Reference](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Lifecycle_Reference).

**Phases are executed in a specific order. This means that if we run a specific phase it won’t only execute the specified phase, but all the preceding phases as well.**

### Goals
However, even though a build phase is responsible for a specific step in the build lifecycle, the manner in which it carries out those responsibilities may vary. And this is done by declaring the plugin goals bound to those build phases.

A plugin goal represents a specific task (finer than a build phase) which contributes to the building and managing of a project. It may be bound to zero or more build phases. A goal not bound to any build phase could be executed outside of the build lifecycle by direct invocation.

When we run a phase, all goals bound to this phase are executed in order. For example, to list all goals bound to the _compile_ phase, we can run:

```
$ mvn help:describe -Dcmd=compile

* validate: Not defined
* initialize: Not defined
* generate-sources: Not defined
* process-sources: Not defined
* generate-resources: Not defined
* process-resources: org.apache.maven.plugins:maven-resources-plugin:3.3.1:resources
* compile: org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile
* process-classes: Not defined
* generate-test-sources: Not defined
* process-test-sources: Not defined
* generate-test-resources: Not defined
* process-test-resources: org.apache.maven.plugins:maven-resources-plugin:3.3.1:testResources
* test-compile: org.apache.maven.plugins:maven-compiler-plugin:3.11.0:testCompile
* process-test-classes: Not defined
* test: org.apache.maven.plugins:maven-surefire-plugin:3.2.2:test
* prepare-package: Not defined
* package: org.apache.maven.plugins:maven-jar-plugin:3.3.0:jar
* pre-integration-test: Not defined
* integration-test: Not defined
* post-integration-test: Not defined
* verify: Not defined
* install: org.apache.maven.plugins:maven-install-plugin:3.1.1:install
* deploy: org.apache.maven.plugins:maven-deploy-plugin:3.1.1:deploy
```

## Project Directory Structure

Let’s focus now on the directory structure of our project.

Since our project is Maven-based, **it follows Maven’s standard directory layout**. You can find more information about this in the Resources section.

In our project we’re adopting only the following directories:

#### **_src/main/java_**:

This directory contains the Java source code for the application we’re building; all our packages and classes go into this directory.

#### **_src/main/resources_**:

This directory contains all the non-Java artifacts that are used by our application; we can put our configuration and property files in this directory.

As an example, we have added a _sample-file.txt_ file in this directory, and you can see it's included in the classpath of the application after we build the project.

#### **_src/test/java_** and **_src/test/resources_**:

These directories contain our test source code and resources, similarly to the _src/main/\*_ directories.

As an example, we have written a simple unit test that will be executed when we build the application using, for example, the _mvn clean install_ command.

Note we also added a JUnit dependency to our _pom.xml_ file to support testing features:

```
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter-engine</artifactId>
  <scope>test</scope>
</dependency>
```

## Resources
- [Maven Getting Started Guide](https://maven.apache.org/guides/getting-started/)
- [Building Java Projects with Maven](https://spring.io/guides/gs/maven/)
- [Spring Boot Maven Installation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#getting-started.installing.java.maven)
- [Maven Transitive Dependencies](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Transitive_Dependencies)
- [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)

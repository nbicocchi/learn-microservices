# Maven and Spring

In this lesson, we'll have a look at how our Spring Boot project is set up with Maven.

Note that this is not an intro to Maven itself. If you’re new to Maven, have a look at the Resources section for some helpful guides.

The relevant module for this lesson is: [m1-real-world-project](https://github.com/eugenp/learn-spring/tree/module1/m1-real-world-project).

## Maven pom.xml

Let's start by opening up the pom of our project here and see exactly how everything is set up.

First, we've defined the basic identifying information about our project: the _groupId_, _artifactId_, _version_ and then _packaging_:

```
<groupId>com.baeldung</groupId>
<artifactId>real-world-project-lesson</artifactId>
<version>0.1.0-SNAPSHOT</version>
<packaging>jar</packaging>
```

These have little impact on how we’ll build the project but they will determine the final output of that build, which is a _jar_ file.

Next, we have a core part of the pom here, namely the _parent_:

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.3.RELEASE</version>
    <relativePath /> <!-- lookup parent from repository -->
</parent>
```

Of course, **we’re using the Boot parent here**, which defines dependencies, plugins, properties that our project will inherit.

This greatly simplifies the configuration of our project, as no longer have to define all these explicitly.

Then, we can skip the meta info, license and author, as that’s self-explanatory.

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

We can view all the transitive dependencies either in an IDE like Eclipse, by using the _Dependency Hierarchy_ tab, or with the Maven command:

_mvn dependency:tree_

By going through the IDE or console output, we can see how many other dependencies our project is pulling in, such as: Spring itself, Tomcat, Jackson, logging, etc.

Next, in the pom file, we have the _plugins_ section.

The only plugin we need to define explicitly at this stage is the Boot plugin:

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
    <java.version>1.8</java.version>
</properties>
```

And this completes our Maven pom configuration.

As we can see, this is quite simple, but there’s still quite a lot here.

Now let's run a simple build. We can do this via the IDE, or by using the command:

_mvn clean install_

Now, to wrap up, let’s have a look at the result of our build, which is the _real-world-project-lesson-0.1.0-SNAPSHOT.jar_ file.

## Boot Highlight

Let's briefly highlight the Spring Boot specific aspects used in this lesson.

As we've mentioned, when we include the _spring-boot-starter-parent_ artifact, Maven automatically pulls a lot of other artifacts that might be useful for us.

We can explore these either in the Dependency Hierarchy tab or by means of already mentioned _mvn_ _dependency:tree_ command. We see that among various artifacts, Spring Boot pulls the following ones:

```
ch.qos.logback:logback-classic:jar:1.2.3:compile
javax.annotation:javax.annotation-api:jar:1.3.2:compile
com.fasterxml.jackson.core:jackson-databind:jar:2.9.9.3:compile
org.hibernate.validator:hibernate-validator:jar:6.0.17.Final:compile
```

These are dependencies that allows us to use logging, annotation, serialization and database-related functionality that we'll usually need in any reasonable application. If we were using pure Spring, we would have to add those artifacts manually to our _pom.xml_.

## Project Directory Structure (extra)

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

## Upgrade Notes

Starting from version 3, Spring Boot requires Java 17 as a minimum version. We can set the Java version in the _pom.xml_ file as follows:

```
<properties>
   ...
   <java.version>17</java.version>
</properties>
```

## Resources
- [Maven Getting Started Guide](https://maven.apache.org/guides/getting-started/)
- [Building Java Projects with Maven](https://spring.io/guides/gs/maven/)
- [Spring Boot Maven Installation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#getting-started.installing.java.maven)
- [Maven Transitive Dependencies](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Transitive_Dependencies)
- [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)

# Deploying the Boot Application - Other Options

In this Learn Spring lesson, we’ll explore some alternative options to deploy our Spring Boot app.

We previously learned how to achieve this using the IDE. However, running the app through the IDE is only suitable for local dev environments. In this lesson, we’ll focus on building and running our app from the command line, including some approaches that can be extended to deploy our app in production or test environments.

The relevant module for this lesson is: [deploying-boot-application-other-options](https://github.com/nbicocchi/spring-boot-course/tree/module3/deploying-boot-application-other-options)

## The Spring Boot Maven Plugin

**The Spring Boot Maven plugin provides various convenient features to build and run our application.** We’ll discuss the basic semantics of this plugin, and then we’ll explore how we can use it to run our application.

The plugin’s base setup is defined by the Spring Boot parent pom, but in order to trigger its functionality in the maven build process, we still have to explicitly include it in the _build > plugins_ section of our project’s _pom.xml_ file:

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

Of course, we could override the default configuration here if necessary, but in this case, the base setup will work just fine.

With this in place, we can now run the application by executing the _run_ goal of the plugin. This will build and then run the application in place:

```
mvn spring-boot:run
```

The plugin offers a number of optional parameters that allow us to specify the runtime configuration from the command line. This includes, among others, the possibility of specifying application and JVM arguments, and enabling profiles:

```
mvn spring-boot:run \
    -Dspring-boot.run.arguments="property1, '--my.application.property=value'" \
    -Dspring-boot.run.profiles="dev" \
    -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" 
```

Please check out the link to the _run_ goal documentation in the Resources section to explore other parameters that the command supports.

**It’s worth mentioning that running the application using the plugin isn’t recommended in production.** First of all, it would need the codebase to be present in the server. Furthermore, the process wouldn’t be optimized, as it has to pull the dependencies, build the app, and then run the application in place each time it’s executed.

Usually, in production environments, it’s suitable to run a pre-packaged artifact.

## Running the Application as a Jar

Another option is to execute our application as a jar using the _java_ command.

**A regular Spring Boot jar can’t be executed out of the box, since it doesn’t include the “provided” dependencies (that should be supplied by the container)** that are required to run the application.

**The Spring Boot Maven Plugin comes into play again with a _repackage_ goal. This feature packages the jar into an executable fat jar containing the application class files and all the necessary dependencies to run the project as a self-contained app.**

Since we’re inheriting the plugin from the _spring-boot-starter-parent_ pom, and including the plugin in our _build_ configuration, the _repackage_ goal execution is preconfigured and will be triggered as part of the regular build process:

```
mvn package
```

We can see in the logs that the _repackage_ goal is, in fact, executed:

```
[INFO] --- spring-boot-maven-plugin:repackage (repackage) @ deploying-boot-2-application-other-options ---
[INFO] Replacing main artifact with repackaged archive
```

**Having a packaged executable jar allows us to simply run the application using the _java_ command:**

```
java -jar target/deploying-boot-2-application-other-options-0.1.0-SNAPSHOT.jar
```

As you might imagine, here is where we would provide runtime configurations using command line arguments, if necessary:

```
java -jar -Dspring.profiles.active=dev \
    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
    target/deploying-boot-2-application-other-options-0.1.0-SNAPSHOT.jar \ 
    property1 --my.application.property=value
```

This is a comparatively better approach for running the app in a production environment, as we’ve decoupled the build and execution processes; the server only needs to have the required Java version installed to run the app.

## Deploy the Application as a WAR

At some point, we might come across the need to deploy a Spring Boot application to an existing external servlet container running independently, especially if we work with legacy projects.

**To this end, Spring Boot and the Maven/Gradle plugins also support packaging the application as a traditional Web Application Resource (WAR) file.**

This can be done by making some simple configuration changes to our project. We’ve included a useful link in the Resources section that explains in detail what it takes to accomplish this.

## Other Approaches

There are several other approaches that we can use to deploy our Boot application. Although it’s case dependent, we’ll briefly discuss some of the widely adopted approaches to deploy Spring Boot applications to production or cloud environments.

First of all, it’s worth mentioning that, like the Maven plugin we used in this lesson, Spring Boot also provides a similar solution for Gradle.

**Another advanced approach to consider is deploying our application as a Docker container.** The maven plugin also provides a _build-image_ goal that creates an image out of the jar or war, but we’ll explore this approach in detail in a more advanced dedicated lesson.

**We might also want to deploy our application to the cloud using a Platform-as-a-Service (PaaS) provider.** Since the executable jar is self-contained, and has everything that’s needed to run the application, it becomes easier to deploy the application to the cloud. Of course, each provider still has its own peculiarities, thus we included a link in the Resources section with further instructions for the main platforms.

## Resources
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)
- [spring-boot:run](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#goals-run)
- [Spring Boot War Deploy](https://www.baeldung.com/spring-boot-war-tomcat-deploy)
- [Spring Boot Cloud Deployment](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud)

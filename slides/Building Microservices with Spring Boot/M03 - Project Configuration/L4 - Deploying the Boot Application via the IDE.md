# Deploying the Boot Application via the IDE

In this lesson, we'll have a look into **deploying our Spring Boot application using an IDE**.

Of course, this is useful primarily during development and not really for production use.

We also have other options such as launching the app from the command-line.

The relevant module you need to import when you're working with this lesson is: [m3-deploying-a-spring-boot-project](https://github.com/eugenp/learn-spring/tree/module3/m3-deploying-a-spring-boot-project)

This lesson only needs a single reference codebase, so there is no end version of the project.

## Using Eclipse

**We’ll see how we can launch a Spring Boot application using the most popular IDEs: Eclipse and IntelliJ**.

Most other IDEs follow a similar approach.

Let’s start with Eclipse, and more specifically, our standard STS distro that we’ve been using since the start. This includes several features that help in the development process.

First, we need to import the application into STS.

Then, **we can run our application without having to do any extra configuration**.

In the Project Explorer, we:

1.  right-click on our application
2.  ‘Run As’
3.  ‘Spring Boot App’

And we’re done. The application is now deployed and running, and we can see the log output in the IDE console.

Also, notice that this will create a run configuration in the IDE. Now that it exists, you can simply use it again to deploy.

Note this is a web application. And although we haven’t yet started to talk about the web support, we can still try it out by browsing to [http://_localhost:8080_](http://localhost:8080/). The error message indicates that the server is running.

**We can also deploy the application** **in** [**debug mode**](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftasks-debug-launch.htm) as easily as before.

## Using IntelliJ

Similar to Eclipse, we first need to import our Maven project.

Note that we need to pick the specific folder, not the parent, and check the option to _Import Maven projects automatically._

Now running the application with IntelliJ is just as easy. We can:

1.  right-click on the main Spring Boot App class
2.  ‘Run _LsApp.main()_’

This will start the application that we can access in the same way.

## Using the Command Line

As we said, launching the application from the IDE is not suitable for production use. We can use the command line for this purpose. Luckily for us, this is a simple procedure as well.

Since we're using the Maven Plugin in our application, we can use the following _mvn_ command:

```
mvn spring-boot:run
```

In the Resources section you'll find a link with more information about these and other options to launch a Spring Boot application.

## Self-Contained Spring Boot Apps (extra)

Traditional Java web apps run on external Servlet containers; the apps are packaged as a WAR artifact and deployed inside a Servlet container like Tomcat, which runs independently.

However, Spring Boot apps are self-contained, which means that the Servlet container is embedded within the deployment artifact. This makes it fairly simple to deploy and run them. By default, Spring Boot uses Tomcat as the embedded container, but it also supports Jetty and Undertow.

Naturally, we can still configure a Spring Boot app to run on an external server following the traditional approach.

## Spring Boot App Packaging (extra)

By default, Spring Boot apps are packaged as a JAR file rather than as a traditional WAR.

**This JAR is also referred to as a ‘fat JAR’ since it’s packaged in a way that contains all the project class files, resources, and dependencies as well as the embedded Servlet container.**

This makes the artifact completely self-contained and can be run on any machine with just a simple java command:

```
java -jar <artifact-name>
```

## Resources
- [Spring Tools](https://spring.io/tools)
- [Eclipse IDE](https://www.eclipse.org/downloads/packages/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [More Options to Run a Boot Application](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html)

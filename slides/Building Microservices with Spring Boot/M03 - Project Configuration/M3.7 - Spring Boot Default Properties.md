# Spring Boot Default Properties

In this lesson, we’ll see how Spring Boot defines the configuration properties that allow us to customize the behavior it provides out of the box according to our needs.

The relevant module you need to import when you're starting with this lesson is: [default-properties-start](https://github.com/eugenp/learn-spring/tree/module3/default-properties-start)

If you want to have a look at the fully implemented lesson, as a reference, feel free to import: [default-properties-end](https://github.com/eugenp/learn-spring/tree/module3/default-properties-end)

## Spring Boot and Application Properties

At this point, we know Spring Boot helps an application get up and running really quickly by providing a default configuration. We’ve also learned that the framework supports externalizing the configuration of our application using properties.

Well, in order to be flexible and able to back off and allow for the definition of custom behavior, **Spring Boot itself bases most of its autoconfiguration aspects on application properties that we can define or override ourselves.**

In this lesson, we’ll explore some of these customizations using the _src/main/resources/application.properties_ file that Boot examines by default.

## Default Boot Properties: Port Number

With this file being empty for the moment, let’s launch our application and check the console output. Among various messages, we can easily find the following one:

_\[main\] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat initialized with port(s):_ **_8080_** _(http)_

Spring Boot is the one managing the embedded servlet, and therefore, the one configuring the exposed port, using 8080 even though we haven’t specified this in our application. **This demonstrates that, out of the box, Spring Boot provides a reasonable set of default configurations.** One of them is the Tomcat server port number, and it establishes a property on top of that configuration to allow us to modify the behavior without having to meddle with the application codebase. Let’s change this value to be 8081.

To this end, we should specify the new value of the server port number in the _application.properties_ file. Now we come across a problem here. How do we know the exact name that Boot expects the property to have? Well, as you would expect at this point, **Boot offers clear documentation on this, exhaustively listing all the properties it supports to customize its default behavior.**

We’ve included a link in the Resources section of this lesson to explore these Common Application Properties in detail.

We can see the properties are conveniently presented into categories. In this case, we can navigate to the Server Properties, where we’ll find the _server.port_ property introduced:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/O80jtPFDQHCiupqdnvNE)

As we can appreciate, **the documentation shows not only the properties we can use, but also a description of its intention and the value represented by the default behavior.**

So let’s set it in our _application.properties_ file:

```
server.port=8081
```

If we restart the application, we’ll be able to see that it now binds the service to the new port number:

_\[main\] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat initialized with port(s):_ **_8081_** _(http)_

## Default Boot Properties: Banner

Now let’s see another example of a default behavior we can customize with properties, Boot’s start-up banner.

You’ve probably noticed in the console the ASCII art banner shown when the application starts, similar to the following one:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (<version>)
```

Even this behavior can be customized with a simple property. Let’s completely disable it in our application:

```
spring.main.banner-mode=off
```

Now if we restart the application again, we’ll observe that no banner appears in the console this time.

## Boot Default Properties’ Metadata

It’s worth mentioning that **Boot also makes the metadata of the properties it defines available.**

Simply put, this information can be used by the IDE to pull off a bunch of useful features, which include:

-   Autocompletion for property names, suggesting the ones that contain the text we’ve typed in:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/nVALKrEQQ0u9RtKMcrRg)

-   Relevant information, like the property description and its default value:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/SONwMEIFTGuOASDY2Krx)

-   And even autocompletion for its supported values:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/RluX01tRS9GrEaDqz9MU)

Of course, **it’s up to the IDE or plugin to provide support for these features.** For example, Spring Tool Suite (STS) provides such support.

## Common Application Properties

Even if we count on this helpful tool, we might have difficulties finding the right entry among the large list of supported properties.

Of course, it’s hardly possible to remember all of them either, so we encourage you to become familiar with the official Common Application Properties document and keep it handy, as you’ll most likely use it on several occasions. Remember, you can find the corresponding link in the Resources section below.

## Resources
- [Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
- [Disable Spring Boot Banner at Startup](https://www.baeldung.com/spring-boot-disable-banner)
- [A Guide to Spring in Eclipse STS](https://www.baeldung.com/eclipse-sts-spring)

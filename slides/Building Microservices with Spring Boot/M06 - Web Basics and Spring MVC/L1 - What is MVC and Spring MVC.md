# What is MVC and Spring MVC

In this lesson, we'll start looking into the web aspects of Spring.

The relevant module for this lesson is: [what-is-spring-mvc-lesson-end](https://github.com/nbicocchi/spring-boot-course/tree/module6/what-is-spring-mvc-lesson-end)

## Spring MVC

Up until this point, we developed our application, but we never actually consumed it. Naturally, we want to interact with an application.

So that’s what we’re going to be exploring in this module: opening up our simple application over HTTP so that we can then interact with it. This is where Spring MVC comes in.

**Broadly speaking, Spring MVC is the overall Spring support for the web.**

## Adding Web Support

Let’s have a look at this web support in Maven.

**This is the _spring-boot-starter-web_ dependency** that we had since the very beginning.

Without Boot, we would have to add the web dependencies that the starter brings in explicitly.

We'd also have to create a simple, separate config class _WebConfig_ that uses the _@EnableWebMvc_ annotation:

```
@EnableWebMvc 
public class WebConfig { }
```

Like every part of Spring, this is enough for us to get up and running with a fully functional web application.

But, again, as with any other part of Spring, we can go a lot further with the configuration, as we'll see in future lessons.

## Our First Controller

With our application configured for the web, let’s create our very first controller.

We’ll create a simple package: _com.baeldung.ls.web.controller_ and add a _ProjectController:_

```
@RestController 
public class ProjectController { 
    // ... 
}
```

**We’ve also annotated this with _@RestController_**, our very first Spring MVC annotation.

Note that we could also use the raw _@Controller_ annotation, but _@RestController_ will be simpler in our case.

Let's also add a simple method here returning a _Project_:

```
public Project findOne() {
    return new Project("testName", LocalDate.now());
}
```

But, in order for this to be an actual [endpoint](https://smartbear.com/learn/performance-monitoring/api-endpoints/) over HTTP, **we’ll annotate this method with _@GetMapping_ to map it to the GET** [HTTP verb](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods). We'll set the _path_ to _/1_ for now, because we want to make this simple to call from the client.

The next step is optional, but I typically like to **set up a base path for the entire controller**. So we’re going to be using another core Spring MVC annotation: _@RequestMapping._

Our full controller class is now:

```
@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    @GetMapping(path = "/1")
    public Project findOne() {
        return new Project("testName", LocalDate.now());
    }
}
```

Notice how simple things are at this stage, and yet, we’re looking at a fully functioning web layer.

We can start up the application and hit it from the browser: [http://localhost:8080/projects/1](http://localhost:8080/projects/1).

## Boot Highlight

In the previous lesson on "Maven and Spring" of Module 1 we've seen that once we include the _spring-boot-starter-web_ dependency, Spring Boot automatically imports other necessary web dependencies.

Besides adding these, in a pure Spring web application, we would need to enable Spring MVC support manually by adding the following bean:

```
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "com.baeldung.web.controller" })
public class WebConfig {...}
```

You can find further useful details on this topic in the Resources section.

## Resources
- [Spring MVC Tutorial](https://www.baeldung.com/spring-mvc-tutorial)
- [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)

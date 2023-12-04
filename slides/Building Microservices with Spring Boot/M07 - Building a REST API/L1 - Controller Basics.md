# Controller Basics

In this module, we’re going to take a closer look at some of the core Spring MVC annotations that are useful not only when building a REST API, but also when building a more traditional MVC application.

The relevant module you need to import when you're starting with this lesson is: [m8-controller-basics-start](https://github.com/eugenp/learn-spring/tree/module8/m8-controller-basics-start)

If you want have a look at the fully implemented lesson, as a reference, feel free to import: [m8-controller-basics-end](https://github.com/eugenp/learn-spring/tree/module8/m8-controller-basics-end)

## Overview

Let’s start with the primary annotations that actually define our controllers:

-   _@Controller_
-   _@RestController_

We’ve already seen these core annotations. For example, when we were implementing our first, simple controller, we’ve used _@RestController._ Also, when we were implementing our MVC application, we’ve used _@Controller_.

Let's discuss the exact differences between them.

**@Controller doesn’t make any assumption about the style of application we’re building.** It doesn’t matter if we’re building a REST API or a traditional, MVC-style of application.

**_@RestController, o_n the other hand, becomes handy when we’re building a REST API.** In this case, typically, we want to [marshall](https://en.wikipedia.org/wiki/Marshalling_(computer_science)) our responses, resources directly to the HTTP response body. So, we’ll need to use the _@ResponseBody_ annotation to do that.

The point is that putting that on each and every method gets repetitive. So, **the _@RestController_ annotation simply bundles _@ResponseBody_** so that we don’t have to manually add it each time.

Note that both _@Controller_ and _@RestController_ are stereotype annotations because they are a specialization of Spring’s _@Component_ stereotype annotation. These give more meaning to a controller class to clearly indicate whether it's an MVC or REST-style controller.

Another important aspect of handling the requests is the _@RequestMapping_ annotation. It helps us map our method with an HTTP verb, a request path and a few other details about the request.

**Spring MVC introduced a few simple shorthand annotations that we can use instead of the more open, low-level _@RequestMapping_**_:_

-   _@GetMapping_
-   _@PostMapping_
-   _@PutMapping_
-   _@DeleteMapping_

## Controllers

Our goal here is to create a simple CRUD REST API for our domain.

Let’s open _ProjectController_ that we’ve already worked on earlier in the course:

```
@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

}
```

We’re already using _@RestController_ and _@RequestMapping_ and we already have two endpoints declared here: one for the getting a project by its id and the other for creating a new project.

## _@RequestMapping_ at the Controller Level

Using an annotation and really understanding it - are two very different things. Let’s have a closer look at _@RequestMapping_.

**It is defined not on an individual method, but at the base controller level.**

Generally speaking, the mappings logically belong on controller's methods. Why do we have it on the controller?

**That’s because we can actually combine multiple annotations at multiple levels.** This fact allows _Spring_ to merge them behind the scenes to form our final mapping, at the method level.

That’s hugely powerful. Simply put, when we define this at the controller level, it will apply to all methods defined in the controller as a baseline of common configuration which we can then refine at the method level.

## Example Methods: Get By Id

We can see how the above-mentioned idea of the mapping refinement works in the get by id method. **T****he base annotation sets the URL to _/projects_ and this method-level annotation sets it to _/{id}_._Spring_ will combine them into _/projects/{id}_.**

Also, notice this is using the _@GetMapping_ annotation which is basically a more refined version of _@RequestMapping,_ pre-selecting the GET HTTP verb for the mapping:

```
@GetMapping(value = "/{id}")
public ProjectDto findOne(@PathVariable Long id) {
    Project entity = projectService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return convertToDto(entity);
}
```

So, for example, we could write it like this:

```
@RequestMapping(method = RequestMethod.GET, value = "/{id}")
```

To be clear, the base annotation sets the URL to _/projects_ and this method-level annotation sets it to _/{id}_. Spring will combine them into _/projects/{id}_.

**This, by the way, happens with any other attributes, not just the path.**

## Narrowing The Mapping

We can actually go beyond this by specifying the path and the HTTP verb in order to further narrow the mapping. Using other aspects of the request, we could, for example make sure this mapping only applies if a specific HTTP header is present on the request.

For example, let’s say we want this particular method to serve only [JSON](https://www.json.org/) and not [XML](https://en.wikipedia.org/wiki/XML)_._ We can easily do that:

```
headers = "accept=application/json"
```

We could also do the same thing using the more specific _produces_ attribute:

```
produces = "application/json"
```

Or, we could narrow the request based on a parameter:

```
params = "paramKey=paramValue"
```


## Resources
- [Quick Guide to Spring Controllers](https://www.baeldung.com/spring-controllers)
- [The Spring @Controller and @RestController Annotations](https://www.baeldung.com/spring-controller-vs-restcontroller)


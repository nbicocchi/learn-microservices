# Controllers and URLs - The @PathVariable Annotation

In this module, we’ll continue with our analysis of the Spring MVC Controller features. More precisely, we'll focus on the _@PathVariable_ annotation.

The relevant module for this lesson is: [controller-urls-pathvariable-end](https://github.com/nbicocchi/spring-boot-course/tree/module7/controller-urls-pathvariable-end)

## The _@PathVariable_ Annotation

**The** **_@PathVariable_** **annotation is generally used to extract different parts out of the request URL** which we can use to map URI template variables.

Let’s start by having a look at the _findOne_ method in our _ProjectController:_

```
@GetMapping(value = "/{id}")
public ProjectDto findOne(@PathVariable Long id) {
    Project entity = projectService.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return convertToDto(entity);
}
```

Notice we’re already using this _@PathVariable_ annotation to map the _id_ from the URL. This has already been defined in the URI pattern of the _@GetMapping_ annotation.

We could've also explicitly specified the name of the path parameter we wanted to bind our variable to, by using the main annotation argument:

```
@PathVariable("id") Long id
```

**As long as both the path parameter and the variable names match, we don’t need to do declare it explicitly.**

Of course, we can map any number of variables or even use regular expressions to map the corresponding values from the URL path.

## _@PathVariable_ with Regular Expressions

Let's see a quick example:

```
@GetMapping(value = "/{category}-{subcategoryId:\\d\\d}/{id}")
public ProjectDto findOne(@PathVariable Long id,
  @PathVariable String category,
  @PathVariable Integer subcategoryId) {
    // ...
}
```

Here we’re mapping a _category_ string and a two-digit _subcategoryId_ field separated with a hyphen ("_\-_") character.

If we set up a breakpoint in this endpoint and send a request using the browser:

_http://localhost:8080/projects/categoryA-12/1_

We'll see all three variables are initialized with the corresponding values.

Finally, if some of the path variables are not required we can specify that with the _required_ annotation argument:

JAVA

`@PathVariable(required = false) Long id`

And instead of throwing an exception when the path variable is missing, the service will set a _null_ value to the variable.

## Resources
- [Spring Web Annotations](https://www.baeldung.com/spring-mvc-annotations)
- [Spring: URI Patterns](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-requestmapping-uri-templates)

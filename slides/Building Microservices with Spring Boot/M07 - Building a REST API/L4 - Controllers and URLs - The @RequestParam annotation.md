# Controllers and URLs - The @RequestParam annotation

In this lesson, we’ll analyze one more Spring MVC Controller annotation that comes into play when building a REST API, the _@RequestParam_ annotation.

The relevant module for this lesson is: [controller-urls-requestparam-end](https://github.com/nbicocchi/spring-boot-course/tree/module8/controller-urls-requestparam-end)

## Uses of the _@__RequestParam_ Annotation

**The** **_@RequestParam_** **annotation is used to map different request variables from the URL, such as query params.**

One common case when we’re implementing the API is to have query parameters that affect how the request will be processed.

For instance, our business logic might have to define a strategy or filter its output based on the value of a parameter.

Let’s see a concrete example in our application. Right now, we have the 'find all' operation to retrieve all the Projects persisted in the database:

_http://localhost:8080/projects_

That’s OK, but not very granular. **Let’s make our controller not just retrieve all projects but to perform a search of particular projects that comply with certain constraints, in this case, based on the project’s _name_ field.**

For example:

_http://localhost:8080/projects?name=Project 2_

Should retrieve only one Project.

## Adapting Our Endpoint

Let’s add support for this feature in our API. **We'll start by renaming our method and adding an annotated parameter:**

```
public Collection<ProjectDto> findProjects(@RequestParam("name") String name) {
  // ...
}
```

Naturally, the next step is to **add support to this new feature in the repository and in the service layer**. We previously saw how to add this in Module 5. You can find the implementation for this in the source code, so we won't go through this again here.

Once we have that in place we can replace the _findAll_ method service we were using here with the new method:

```
Iterable<Project> allProjects = this.projectService.findByName(name);
```

If we now make the request in the browser, we'll see the service is retrieving just one entry as expected.

It's worth mentioning that this annotation can also be used to map form data and parts in multipart requests.

## The _required_ Attribute

**This annotation supports a _required_ attribute as well as a _defaultValue_ attribute.**

**The _required_ attribute is _true_ by default_._** This means that if we don’t provide a value for the request parameter the application will retrieve an error response, with a helpful message:

_Required String parameter 'name' is not present_

So, if we want to make this parameter optional we can either set a default value:

```
public Collection<ProjectDto> findProjects(@RequestParam(name = "name", required = false) String name) {
  // ...
}
```

Or wrap the value using Java 8’s _Optional_ class:

```
public Collection<ProjectDto> findProjects(@RequestParam(name = "name") Optional<String> name) {
  // ...
}
```

**Spring supports using the _Optional_ class in combination with annotations that support a _required_ attribute** -like this one, the _@PathVariable_ or the _@RequestHeader_ annotations, for example- and is equivalent to setting _required_ with a _false_ value.

Of course, we have to take into account that the parameter might have a null value and thus we have to handle that scenario as well.

## The _defaultValue_ Attribute

**Another possibility is to use an empty String as the default value which will also implicitly set the _required_ parameter as _false_ and will allow us to avoid null-checking the variable:**

```
public Collection<ProjectDto> findProjects(@RequestParam(name = "name", defaultValue = "") String name) {
  // ...
}
```

Of course, we could potentially provide an actual value here if our API specification dictates that.

## Spring MVC Handler Method Parameters (extra)

**Methods annotated with _@RequestMapping_ can have very flexible signatures and support several method argument types such as:**

-   _ServletRequest/ServletResponse_ - that represent the request and response objects
-   _HttpSession_ - enforces the presence of a session
-   _java.util.Map / org.springframework.ui.Model / org.springframework.ui.ModelMap_ - expand the model that is exposed to the web view

You can find the full list of supported parameters in the [official documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-arguments).

**Alongside these parameters, we can use Spring MVC annotations.**

Besides the most common method parameter annotations we saw in this lesson Spring MVC provides many more.

Let’s briefly look at what these are:

-   **_@CookieValue_** - Binds the value of an HTTP cookie to a method argument
-   **_@RequestHeader_** - Binds the value of an HTTP header to the argument
-   **_@RequestPart_** - Access a multipart after converting it with an _HttpMessageConverter_
-   **_@ModelAttribute_** - Accessing an existing attribute in the model with data binding and validation applied
-   **_@SessionAttribute_** - Access a pre-existing session attribute
-   **_@RequestAttribute_** - access a request attribute

## Resources
- [Spring Web Annotations](https://www.baeldung.com/spring-mvc-annotations)
- [Spring @RequestParam Annotation](https://www.baeldung.com/spring-request-param)

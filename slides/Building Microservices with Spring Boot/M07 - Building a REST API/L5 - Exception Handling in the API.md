# Exception Handling in the API

In this module, we’re going to see how exception handling is done in a REST API.

Specifically, we'll explore different mechanisms in Spring to handle exceptions gracefully and return a correct response back to the client.

The relevant module you need to import when you're starting with this lesson is: [m8-exception-handling-start](https://github.com/eugenp/learn-spring/tree/module8/m8-exception-handling-start)

If you want have a look at the fully implemented lesson, as a reference, feel free to import: [m8-exception-handling-end](https://github.com/eugenp/learn-spring/tree/module8/m8-exception-handling-end)

## Overview

We'll start with the mechanism we already used to tune the status code of the response in a previous lesson - the _ResponseStatusException._

Afterwards, we'll move on and implement a proper global exception handler with the help of:

-   the _@ControllerAdvice_ annotation
-   the versatile _@ExceptionHandler_ annotation for the individual exceptions we need to handle

Finally, we'll also make our global handler extend the _ResponseEntityExceptionHandler_ to get some predefined semantics for some common exceptions. This will make the implementation a lot simpler.

## Throwing a _ResponseStatusException_

Let’s open the _ProjectController_ class in which we have been working the last lessons, and focus on the _findOne_ method:

```
@GetMapping(value = "/{id}")
public ProjectDto findOne(@PathVariable Long id) {
    Project entity = projectService.findById(id)
      .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return convertToDto(entity);
}
```

Notice how we're throwing a _ResponseStatusException_ when the service isn't able to find the requested entity.

**The** **_ResponseStatusException_ provides a straightforward way to specify exactly what HTTP status code we'll send back to the client in a particular case.**

We can also decide the message we send out. So let's add a quick message here:

```
Project entity = projectService.findById(id)
  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
```

## Deploy and Test

If we launch the service and send a request using Postman to the _'Get by Id'_ endpoint with a non-existing _id_:

_localhost:8080/projects/4_

We receive a clear response; the status code is _404 Not Found_ and the body is a JSON with the right message:

```
{
    "timestamp": "2019-07-03T16:22:09.254+0000",
    "status": 404,
    "error": "Not Found",
    "message": "Project not found",
    "path": "/projects/4"
}
```

Of course, throwing exceptions manually on a case-by-case basis has its disadvantages.

For one thing, the framework already throws a number of well-established exceptions, and we don't necessarily want to wrap those manually. Instead, we want to define what the client should get when these are thrown.

Another disadvantage is simply that throwing these exceptions manually will make our exception handling logic scattered and hard to understand.

**It would be a lot better to handle exceptions globally in a single location.**

With this objective, we'll set up a global exception handler.

We'll start by creating a new class, the _GlobalExceptionHandler,_ which will contain the logic to handle the different exceptions.

## Upgrade Notes

Since Spring Boot 2.3, error messages are no longer included in the error response by default to avoid leaking potentially sensitive information.

So, to include the "_Project not found_" message in the service response, we need to add the following property to our _application.properties_ file:

```
server.error.include-message=always
```

## Handling Exceptions Globally

**To make the handlers of this class effective globally for all _Controllers,_ we'll annotate this class with the _@ControllerAdvice_ annotation:**

```
@ControllerAdvice
public class GlobalExceptionHandler {
  
}
```

**Now we can finally start to customize the behaviour of individual exceptions by using the _@ExceptionHandler_ annotation.**

The source or cause of the exception doesn't really matter; it can be our own exception coming out of the project's business logic, it can come out of the framework or from any other library on the stack.

Let's say we want to DELETE a non-existing entity. Typically, we'll obtain a 500 response caused by a DAO Exception, more precisely, an _EmptyResultDataAccessException._

Let's deploy the application and make the request using Postman:

_DELETE localhost:8080/projects/4_

We can confirm we get back a _500 Server Error,_ which is not ideal.

Let's have a look at the console to check which error is triggering this response: it's an _EmptyResultDataAccessException._ Let's handle this in our global exception handler.

We'll simply:

-   create a new method which will receive the exception as a parameter
-   annotate it with the _@ExceptionHandler_ annotation, and defining the exception class that will be handled as a parameter
-   define exactly what should happen in case this particular exception is triggered, in the method body:

```
@ExceptionHandler(EmptyResultDataAccessException.class)
public ResponseEntity<String> handleDataRetrievalException(EmptyResultDataAccessException ex) {
    return new ResponseEntity<String>("Exception retrieving data: " + ex.getMessage(), HttpStatus.NOT_FOUND);
}
```

We can redeploy the application and try again the endpoint.

We're now getting the back the 404 response and the exact message we configured.

In this case, for simplicity's sake we're retrieving a plain _String,_ losing therefore the nice JSON structure we had before; it's naturally better to return an actual object with the error information we want to expose to the client.

## Broader Mappings

In case handling individual exceptions like this seems tedious, we have an alternative.

**We can map multiple exceptions or family of exceptions (a parent exception) in the same way with this** **_@ExceptionHandler_ annotation.**

For example, here we could look at the parent of our exception, _DataRetrievalFailureException_, and handle that instead:

```
@ExceptionHandler(DataRetrievalFailureException.class)
public ResponseEntity<String> handleDataRetrievalException(DataRetrievalFailureException ex) {
    return new ResponseEntity<String>("Exception retrieving data: " + ex.getMessage(), HttpStatus.NOT_FOUND);
}
```

The output will be the same as before.

## Extending the _ResponseEntityExceptionHandler_

**Finally, Spring helps in the task of creating our own global exception handler by providing a base class to extend with some helpful defaults. That's the** **_ResponseEntityExceptionHandler_ class_._**

Let's extend that base class here:

```
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  
    // ...
  
}
```

This already maps a lot of internal, framework-specific exceptions.

It also offers a number of protected methods to allow us customize the default behavior.

## Upgrade Notes

Spring 6, which is utilized by Spring Boot 3, offers support for the "Problem Details for HTTP APIs" specification. This specification outlines the standardized requirements for including informative data in error response bodies. For example, the _'Get by Id_' response for a non-existing id would appear as follows:

```
{
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Project not found",
    "instance": "/projects/4"
}
```

To enable this functionality, we can make the _@ControllerAdvice_ exception handler class extend the _ResponseEntityExceptionHandler_, or add the following application property in a Boot project:

```
spring.mvc.problemdetails.enabled=true
```

Note: This will override Spring Boot’s Error Handling mechanism just for the Spring MVC exceptions. For other exceptions raised by the service, the application will still retrieve error responses using the format we’ve shown throughout the lesson.

In addition, beginning with Spring Boot 3.0.4, the _deleteById()_ operation's default implementation no longer throws the _EmptyResultDataAccessException_ exception when no entity is found for the specified ID.

In this lesson, we utilized this exception to activate the exception-handling mechanism. Hence, for educational purposes, we reintroduce this behavior by throwing the _EmptyResultDataAccessException_ explicitly in the _delete()_ method of the _ProjectServiceImpl_ class:

```
public void delete(Long id) {
    this.findById(id)
            .orElseThrow(() -> new EmptyResultDataAccessException(
              String.format("No class com.baeldung.ls.persistence.model.Project entity with id %s exists", id), 1)
            );
    projectRepository.deleteById(id);
}
```


## Resources
- [Error Handling for REST with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring)
- [Spring ResponseStatusException](https://www.baeldung.com/spring-response-status-exception)
- [Custom Error Message Handling for REST API](https://www.baeldung.com/global-error-handler-in-a-spring-rest-api)
- [Error Responses](https://docs.spring.io/spring-framework/docs/6.0.6/reference/html/web.html#mvc-ann-rest-exceptions-render)

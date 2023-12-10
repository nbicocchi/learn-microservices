# Expanding Our First Controller

In this lesson, we’re going to continue the work we started previously, by taking our naive controller implementation to the next level.

The relevant module for lesson is: [expanding-our-first-controller-end](https://github.com/nbicocchi/spring-boot-course/tree/module6/expanding-our-first-controller-end)

## The _findOne()_ Method

Now that our web support is starting to take shape, let’s do some simple [CRUD](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete) in our application.

Our current _findOne_ implementation in the _ProjectController_ simply returns some random data. However, **what it should be doing is using the service layer**.

So let’s inject the _ProjectService_ via the constructor:

```
@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    private IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }
    
    // ...
}
```

Now let’s use it in the _findOne_ method.

Notice that we need the _id_ of the Project, since that’s what we’re searching by. So let’s make sure that’s a parameter in the method here.

Next, we'll simply delegate to the service, extract the potential value and return:

```
@GetMapping(value = "/1")
public Project findOne(Long id) {
    return this.projectService.findById(id).get();
}
```

**The parameter also needs to be** **mapped to a** [**path component**](https://en.wikipedia.org/wiki/URL_Template)**, or path variable, by using the _@PathVariable_ annotation_._**

Finally, we'll remove the hardcoded "_1"_ in the URL and replace with the _id_ path variable. Now the _findOne_ method will be:

```
@GetMapping(value = "/{id}")
public Project findOne(@PathVariable Long id) {
    return this.projectService.findById(id).get();
}
```

Let’s now run the application.

We'll open up Postman and consume it from the client side by calling: [_http://localhost:8080/projects/159_](http://localhost:8080/projects/159).

## Error Handling Problems

There are a few things to address here. We’ll, of course, dedicate a full lesson to improving our exception/error handling, so we won’t fix these here. But let’s at least have a quick look at what the problems are.

One is that **we’re seeing the raw exception here** which is obviously not ideal.

The second issue is **the** [**status code**](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status). Logically, we’re expecting back a 404 (Not Found) since we’re trying to access a Resource that isn’t found. However, notice what we’re getting a 500 Server Error which is not correct.

Finally, the third point is that, instead of us, proactively checking the result, **we’re allowing the exception to be thrown by an internal piece of logic**, namely, the _Optional._ It’s usually better to take control over that.

We’ll deal with all of the issues in a separate, dedicated lesson.

## Write Operation

So far, we've seen a read operation. Let’s now do a write operation as well, as that’s going to involve other aspects of Spring MVC.

**We’re going to do a simple _save:_ basically, the creation of a new Project Resource.**

First off, let’s define the basic operation with no mappings:

```
public void create(Project newProject) {
    this.projectService.save(newProject);
}
```

Next, **we're going to map this to an HTTP POST using _@PostMapping_**_._ We'll also map the body of the HTTP request to the Project variable here, using _@RequestBody:_

```
@PostMapping
public void create(@RequestBody Project newProject) {
    this.projectService.save(newProject);
}
```

We can now switch back to the client and send our POST request with a Project Resource.

**Note**: In this lesson, **we've also updated the entities definition by changing the generation strategy for the ID**:

```
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    // ...
}
```

```
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    // ...
}
```

This is because in some lessons we're loading entities into the database automatically during the startup process. With Spring Boot, this is achieved by adding a _data.sql_ file in the resources of the project. In these cases, we have to change the Id-generation strategy for our entities to be compatible with the automatically created entities. You can find more information on this in the Resources section.

## Resources
- [Guide to Spring Controllers](https://www.baeldung.com/spring-controllers)
- [Spring Web Annotations](https://www.baeldung.com/spring-mvc-annotations)
- [Quick Guide on Loading Initial Data with Spring Boot](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql)
- [An Overview of Identifiers in Hibernate](https://www.baeldung.com/hibernate-identifiers)

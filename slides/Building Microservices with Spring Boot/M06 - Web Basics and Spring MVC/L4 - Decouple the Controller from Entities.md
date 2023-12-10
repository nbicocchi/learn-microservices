# Decouple the Controller from Entities

In this lesson, **we’ll focus on exposing Resources instead of Entities from our controllers back to the client**.

The relevant module for this lesson is: [decouple-controller-from-entities-end](https://github.com/nbicocchi/spring-boot-course/tree/module6/decouple-controller-from-entities-end)

## Reasons for Conversion

On a simple domain or for a simple [POC](https://en.wikipedia.org/wiki/Proof_of_concept), doing no conversion can be a good approach.

But for a real project, it can be very useful for several reasons:

-   entities are not resources; this means that not all entity data should be exposed to the client
-   exposing raw entities might lead to problems like security vulnerabilities due to exposing too much raw data
-   performance
-   maintainability of the system

Next, let’s get to the practical aspect of exactly how to do this.


## Converting to DTOs

**We’re going to expose the data from our controllers by converting our entities into** [**DTO**](https://martinfowler.com/eaaCatalog/dataTransferObject.html)**s or Resources.**

Let’s create a _ProjectDto_ class:

```
public class ProjectDto {

    private Long id;

    private String name;

    private LocalDate dateCreated;

    private Set<TaskDto> tasks;

  // constructor, getters and setters

}
```

Just like the entity, this needs setters, getters, _equals_, _hashCode_, _toString_ and a constructor that takes in all of these fields.

You’ll immediately notice that this is no different from our _Project_ entity and we’ll talk about that later.

For now, **let’s do the conversion between the _Entity_ and this new DTO**. We can handle this manually or using a library such as _ModelMapper_, _MapStruct_ etc.

Let’s add two methods for the conversions:

-   from _Entity_ to DTO
-   and back from the DTO to the _Entity_

```
protected ProjectDto convertToDto(Project entity) {
    ProjectDto dto = new ProjectDto(entity.getId(), entity.getName(), entity.getDateCreated());
    dto.setTasks(entity.getTasks().stream().map(t -> convertTaskToDto(t)).collect(Collectors.toSet()));

    return dto;
}

protected Project convertToEntity(ProjectDto dto) {
    Project project = new Project(dto.getName(), dto.getDateCreated());
    if (!StringUtils.isEmpty(dto.getId())) {
        project.setId(dto.getId());
    }
    return project;
}
```

**As you can see, the conversion is very simple but, what’s critical here is that there is a clear conversion point that we can control**.

So, when we chose to make the DTO different from the underlying entity this is where we can control exactly what gets exposed in the DTO and what doesn’t.

**Now we can change our controller to use _ProjectDto_.**

Our _GET_ method will now have a _ProjectDto_ return type and use the converter method:

```
@GetMapping(value = "/{id}")
public ProjectDto findOne(@PathVariable Long id) {
    Project entity = projectService.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return convertToDto(entity);
}
```

The method _create_ will accept a _ProjectDto_ parameter and convert it to an entity:

```
@PostMapping
public void create(@RequestBody ProjectDto newProject) {
    Project entity = convertToEntity(newProject);
    this.projectService.save(entity);
}
```

Let’s run the application and check our REST endpoints again. We'll call the [http://localhost:8080/projects](http://localhost:8080/projects) POST to create a project, then access [http://localhost:8080/projects/1](http://localhost:8080/projects/1).

Note the GET endpoint returns the _id_, _name_, _dateCreated_ fields.

If we remove the _dateCreated_ field from the Dto, then this will no longer be displayed in the response.

**Similarly, we can now create a DTO for the _Task_ entity:**

```
public class TaskDto {
  
    private Long id;
  
    private String name;
  
    private String description;
  
    private LocalDate dateCreated;
  
    private LocalDate dueDate;
  
    private TaskStatus status;
    
    // constructor, getters, setters

}
```

In _ProjectController_, we'll add the conversion methods from _TaskDto_ to _Task_ entity and back:

```
protected TaskDto convertTaskToDto(Task entity) {
    TaskDto dto = new TaskDto(
      entity.getId(),
      entity.getName(),
      entity.getDescription(),
      entity.getDateCreated(),
      entity.getDueDate(),
      entity.getStatus());
    return dto;
}

protected Task convertTaskToEntity(TaskDto dto) {
    Task task = new Task(
      dto.getName(),
      dto.getDescription(),
      dto.getDateCreated(),
      dto.getDueDate(),
      dto.getStatus());
    if (!StringUtils.isEmpty(dto.getId())) {
        task.setId(dto.getId());
    }
    return task;
}
```

## Upgrade Notes

Java 16 formally released the “Record classes” feature (previously introduced in Java 14 as a Preview Feature).

Simply put, Records are a special kind of class that acts as a carrier for immutable data and allows to reduce significantly the associated boilerplate.

We only need to specify the fields and the appropriate accessors, constructors, equals, hashCode, and toString methods are created automatically.

Even though it is obviously not mandatory **we decided to implement our REST DTO classes as Records** in the upgraded version of the codebase to showcase a suitable and common case in which Records can be handy.

Let’s see a quick example of how our TaskDto is now defined:

```
public record TaskDto(
    Long id,
    String name,
    String description,
    LocalDate dateCreated,
    LocalDate dueDate,
    TaskStatus status) {
}
```

It’s worth mentioning that we won't be following the same approach for projects that use the traditional Model-View-Controller framework in the course since in these cases the web framework needs to make use of DTOs setters and Records are defined as immutable data classes.

## Resources
- [Entity To DTO Conversion for a Spring REST API](https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application)
- [Java 16: Record Classes](https://docs.oracle.com/en/java/javase/16/language/records.html)

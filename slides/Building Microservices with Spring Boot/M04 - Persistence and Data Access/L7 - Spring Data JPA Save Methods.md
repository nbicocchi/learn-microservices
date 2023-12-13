# Spring Data JPA Save Methods

In this lesson, we're going to explore different operations for persisting new entities and updating existing ones.

The relevant module you need to import when you're starting with this lesson is: [save-methods-end](https://github.com/nbicocchi/spring-boot-course/tree/module5/save-methods-end)

## Persisting New Entities

Let's see how we can persist a new entity. First, we'll create a new _Project_ entity. Naturally, this object will initially have a null _id_ value, but let’s log that just to be clear:

```
Project newProject = new Project("NEW1", "new project", "new project description");
LOG.info("Project id before persisting:\n{}", newProject.getId());
```

Now let’s run the application to check the log message:

```
Project id before persisting:
null
```

To persist new entities in the database, the _CrudRepository_ implements a simple and intuitive _save_ method. As a result, a new entry will be inserted into the database.

Furthermore, and very interestingly, Spring JPA will automatically assign the generated id to the existing _Project_ object:

```
projectRepository.save(newProject);
LOG.info("Project id after persisting:\n{}", newProject.getId());
```

Let's re-run the app to check the output:

```
Project id after persisting:
4
```

Of course, we can explore the exact state of the database right before and after the operation of persisting the _newProject_ object. For example, after the entity is persisted, we'll see the following state of our database:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/yvrvZrFmQeWcsMjtpJcm)

## Updating Entities

Now let's see how **we can update an entity that has previously been persisted in our database**. For example, let's say that we need to update the name and tasks of the _Project_ instance that we created before:

```
newProject.setName("updated name");
Set<Task> newProjectTasks = Set.of(new Task("task name", "task description", 
  LocalDate.of(2025, 1, 1), newProject));
newProject.setTasks(newProjectTasks);
```

In order to persist these modifications, all we have to do is call the same _Project Repository's_ _save_ method again:

```
newProject = projectRepository.save(newProject);
```

Even though we're using the same method for updating the existing entity, this time **the framework will detect that the entity already exists in the database, and will update the existing record instead of creating a new one**.

Spring Data JPA offers different strategies to figure out if an entity is new. **The default strategy consists of checking whether the identifier attribute is _null_.** If it's _null_, then the entity will be treated as a new one.

In this case, as we saw in the previous section, the _id_ property isn’t null at this point, thus the entity gets updated.

Also, note that **we re-assigned the output of the _save()_ method to the _newProject_ variable** again. This may seem redundant, but it’s important to do it so that the child _Task_ entities get populated with the generated _id_ from the database as well. If we skip this step, then our _Task_ Java objects will still have null _id_ properties.

In fact, the CrudRepository suggests using the return value, warning that it can potentially be a totally different instance than the entity passed as the parameter:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/0Edmvgi9ThmKNfp6d6sl)

To inspect the _newProjects_' tasks, let’s print them as well:

```
LOG.info("Child Task after updating:\n{}", newProject.getTasks());
```

Now let’s launch the application, and check the logs:

```
Child Task after updating:
[Task [id=5, name=task name, description=task description, ...
```
After executing the update operation, we can check the state of the database to make sure that it looks like this:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/3oPelVQxSpGa0e0cVUC1)

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/iAdqtLrToGbmrhWLG8Or)

## Persisting Several Entities

We often have to persist or update multiple entities at the same time. For this purpose, **Spring Data Repository offers the convenient _saveAll()_ method.** This method follows the same behavior as the previously discussed _save()_ method; it persists new entities and updates existing ones.

For example, let’s save three projects; two of them are existing ones, and the third is a new one.

The first existing project to be updated is _newProject,_ and we'll update its name again:

```
newProject.setName("updated again");
```

The second existing project to be updated is one that’s stored in the database with the _id_ attribute equal to 1. Let's update this project's tasks:

```
Project p1 = projectRepository.findById(1L).get();
Set<Task> differentTasks = Set.of(
    new Task("different task", "different description", LocalDate.of(2025, 1, 1), p1)
);
p1.setTasks(differentTasks);
```

Finally, let's create a new project:

```
Project newProject2 = new Project("NEW2", "another project", "another project description");
```

Now we put these three entities in a list, and persist them all at once using the _saveAll()_ method:

```
Iterable<Project> severalProjects = Arrays.asList(newProject, p1, newProject2);
severalProjects = projectRepository.saveAll(severalProjects);
```

When we run the application, we can see that the database has the following state:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/ujiJnudzQXdE0vXA32g8)

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/IckNOS91QNKa13xPyLJG)

Note that when we updated the tasks of the _P1_ project, not only was the new task persisted, but the previously associated tasks were also removed from the database. This is because our _Project_ entity has the _tasks_ attribute decorated with the _@OneToMany_ annotation in such a way that all operations are cascaded, and the orphan _Task_ entities are removed:

```
@OneToMany(
    mappedBy = "project",
    orphanRemoval = true,
    fetch = FetchType.EAGER,
    cascade = CascadeType.ALL)
private Set<Task> tasks;
```
## Transactionality Preview

It’s worth noting that **in Spring Data JPA, all operations that are offered out-of-the-box are handled as transactions**. This means that if one of the save operations fails, then nothing is committed in the database.

Let’s illustrate this with an example using the _saveAll()_ method, in which we'll attempt to persist two entities.

Remember that the database schema has a unique constraint on the _code_ attribute of the _Project_ table. We'll make use of this constraint in order to generate a failure when persisting entities. One of the entities that we're going to persist undergoes an acceptable modification, while the other is a new one that violates the uniqueness of the _code_ attribute:

```
newProject.setName("updated once more");
Project newProject3 = new Project("NEW2", "duplicate code!", "project with constraint violation");
severalProjects = Arrays.asList(newProject, newProject3);

try {
    projectRepository.saveAll(severalProjects);
} catch (Exception ex) {
    LOG.info("error saving/updating multiple entities");
}
```

We wrapped the _saveAll()_ operation in a try-catch block in order to avoid the failure of the whole service initialization process. When we run the application, we can see an error gets logged right before our log message:

```
Unique index or primary key violation:
  "PUBLIC.UK_EH3NUSUTT0QY84A4YR9PFXKYG_INDEX_1 ON PUBLIC.PROJECT(CODE) VALUES 5";
  SQL statement:
insert into project (id, code, description, name) values (null, ?, ?, ?) [23505-200]
Error saving/updating multiple entities
```

A look at the database will confirm nothing has changed:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/l0SgitIQSlllinM4FVVu)

## Resources
- [Spring Data – CrudRepository save() Method](https://www.baeldung.com/spring-data-crud-repository-save)
- [Performance Difference Between save() and saveAll() in Spring Data](https://www.baeldung.com/spring-data-save-saveall)

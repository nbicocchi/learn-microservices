# Spring Data JPA Delete Methods

In this lesson from Learn Spring Data, we’ll explore how we can remove entries from the database using Spring Data JPA.

The relevant module for this lesson is: [delete-methods-end](https://github.com/nbicocchi/spring-boot-course/tree/module5/delete-methods-end)

## Delete Entities

To get started, let’s open our main class, _DeleteMethodsApp,_ and launch our application to **check the initial state of the database.**

We’ll check the entities we have in our database by using the H2-console [http://localhost:8080/h2-console](http://localhost:8080/h2-console):

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/yP0Y1bAuQdOuR1eQ74jg)

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/AxPxu1cPSEG2FG7wZu4w)

Now let’s explore the actual methods that the framework provides to remove entities from the database.

The methods are really intuitive, and **the signatures are rather similar to the ones we’ve seen for other operations.**

**We can use an entity reference, and call the _delete_ method of the repository:**

```
public class DeleteMethodsApp implements ApplicationRunner {
    // ...

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Project p1 = projectRepository.findById(1L)
            .get();
        projectRepository.delete(p1);
    }
}
```

Let's restart the app, and check the entities again from the H2-console from the browser:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/ZLF5jucgRbOgypYKiO7w)

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/b0YIYDwNQCzJB6PtAdQB)

As we can see, **when we delete a Project, its associated Task entities are also removed** due to how we defined the relationship.

**We can also delete an entity by providing an id:**

```
public class DeleteMethodsApp implements ApplicationRunner {
    // ...
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // ...

        projectRepository.deleteById(2L);
    }
}
```

Now let's restart the app, and check the entities from the H2-console:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/0txx3whbRt7WCuL4mTPg)

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/jFvMGLsgThOepRaW8bdM)

We can also **delete several entities at once:**

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    Iterable<Project> projectsToDelete = 
        projectRepository.findAllById(List.of(3L, 5L));
    projectRepository.deleteAll(projectsToDelete);
}
```

Let's restart and check again:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/4PGuMASiQ9KycnkLvZfG)

## Delete Entities Selectively

We often need to delete entities selectively, meaning we need to **delete several database entries at once, but only those that match a particular condition.**

Spring Data JPA allows us to do this filtering similar to how we do it for the read queries.

In fact, the only thing that’s different compared to other derived query methods, like the _findByNameContaining_ we have here, is **the subject introducing keyword, _delete_**:

```
public interface ProjectRepository extends CrudRepository<Project, Long> {
    // ...
    
    Iterable<Project> deleteByNameContaining(String name);
}
```

We can specify the method output to retrieve a _Long_ value, which represents the number of deleted entities, for example, or we can simply use _void_:

```
Long deleteByNameContaining(String name);

void deleteByNameContaining(String name);
```

It’s also worth mentioning that the _remove_ keyword is supported as well:

```
void removeByNameContaining(String name);
```

Before we try out these methods, there’s one more thing we need to do. Simply put, **all JPA persistence and deletion operations have to be executed within a transaction to work.**

All the Repository methods provided out-of-the-box are handled as transactions, but **for our custom methods we have to explicitly decorate our functionality with the proper _@Transactional_ annotation**:

```
public interface ProjectRepository extends CrudRepository<Project, Long> {
    // ...
    
    @Transactional
    Long deleteByNameContaining(String name);

    @Transactional
    void removeByNameContaining(String name);
}
```

Now we’re ready to use our functions.

First, in the _DeleteMethodsApp,_ let’s remove a non-existing entity and check the logs:

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    Long deleteCount = projectRepository.deleteByNameContaining("Project 2");
    LOG.info("Number of removed projects:\n{}", deleteCount);
}
```

Then we’ll execute the application, and have a look at the logs again:

```
Number of removed projects: 
0
```

Naturally, the delete count is zero.

Now let’s call the other delete method, this time matching the remaining project:

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    projectRepository.removeByNameContaining("project");
}
```

Let's restart the app, and switch to the H2-console.

With this, the last existing Project and its associated Task entities are removed from the database:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/Q6yY9ryBSAazeraUuqop)

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/C0azo8oCRT63DP1y4yTg)

We now have an understanding of the different Spring Data JPA capabilities we can use to delete entities.

## Resources
- [Spring Data JPA – Derived Delete Methods](https://www.baeldung.com/spring-data-jpa-deleteby)
- [Spring Data JPA Reference: Derived Delete Queries](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.modifying-queries.derived-delete)
- [Spring Data JPA: Supported query method subject keywords](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject)
- [Spring Data JPA Delete and Relationships](https://www.baeldung.com/spring-data-jpa-delete)

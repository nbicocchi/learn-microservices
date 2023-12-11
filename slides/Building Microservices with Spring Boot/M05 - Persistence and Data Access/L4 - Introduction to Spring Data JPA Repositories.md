# Introduction to Spring Data JPA Repositories

Welcome to a new lesson from the Learn Spring Data course. In this lesson, we’ll learn about Spring Data JPA Repositories.

This is a core topic in Spring Data and the Spring Data JPA framework, since they represent the main mechanism for interacting with the database.

The relevant module for this lesson is: [introduction-to-spring-data-jpa-repositories-end](https://github.com/nbicocchi/spring-boot-course/tree/module5/introduction-to-spring-data-jpa-repositories-end)

## Handling Data With Spring Data JPA

Spring Data Repositories allow us to **carry out standard database operations without having to write a single line of code**.

The framework provides interfaces that supply methods for querying our database. Some examples of these interfaces are:

* _[CrudRepository](https://docs.spring.io/spring-data/data-commons/docs/2.7.9/api/org/springframework/data/repository/CrudRepository.html)_ provides CRUD functions
* _[PagingAndSortingRepository](https://docs.spring.io/spring-data/data-commons/docs/2.7.9/api/org/springframework/data/repository/PagingAndSortingRepository.html)_ provides methods to do pagination and sorting of records
* _[JpaRepository](https://docs.spring.io/spring-data/jpa/docs/2.7.9/api/org/springframework/data/jpa/repository/JpaRepository.html)_ provides JPA-related methods such as flushing the persistence context and deleting records in a batch

In this lesson, we'll take a look at the _CrudRepository_ interface. This interface provides the base querying functionality, as the other repositories extend from it.

You can have a look at the Resources section for more information on the other available repository implementations.

Now we’ll jump right into the code.

## The _@EnableJpaRepositories_ Annotation

First, we'll open the _AppConfig.java_ class.

To enable scanning for Spring Data JPA repositories, our project needs to include the _@EnableJpaRepositories_ annotation, like the one we have here. Of course, this annotation can be incorporated by the Spring Boot autoconfiguration as well.

This annotation takes a lot of parameters that affect how the repositories will be scanned and configured, like the _basePackages_ we’re using here:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/zPZCZbbUTeuzRnAu5YXi)

## Creating Spring Data Repositories

As we can see, we’re specifying that our repositories are located in the _com.baeldung.lsd.persistence.repository_ package, so let’s create the package and define a new repository for our Project entity.

To create a JPA repository, we have to **create an interface extending one of the Spring Data Repository interfaces**, which in this case is the _CrudRepository_:

```
package com.baeldung.lsd.persistence.repository;

public interface ProjectRepository extends CrudRepository {
}
```

If we open the _CrudRepository_ implementation, we can see that at the core of these repositories is the _Repository<T, ID>_ interface:

```
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    // ...
}
```

If we go further and open this _org.springframework.data.repository.Repository_ definition, we’ll see that it’s just a marker interface, as it doesn’t define any actual method:

```
public interface Repository<T, ID> {
}
```

The **Repository interface takes two type parameters (_<T, ID>_)**, which are also taken when we define a _CrudRepository_ interface:

-   _T represents the entity class_
-   _ID represents the type of the entity Id field_

Before diving into the methods defined by this interface, let’s define these types in our custom repository.

Our managed entity type will be, of course, _Project,_ and if we analyze the model, we’ll see the @Id field is of type _Long._ So we'll have:

```
public interface ProjectRepository extends CrudRepository<Project, Long> {
}
```

Let’s have another quick look under the hood of the _CrudRepository_ interface. As we can guess, the purpose of this interface is to provide basic CRUD functionality for the specified entity class.

There are some methods defined here, such as save, _findById, existsById, count, deleteById,_ and so on.

**The beauty of this is that we don’t need to implement these methods; Spring will create a suitable proxy instance for the repository interface we define.**

Let’s test this out.

We’ll open the _IntroToJpaRepositoriesApp_ class to use our repository. **We can simply _@Autowire_ our created interface** into any component.

To find all the projects, we can call the _findAll()_ method. Then we’ll simply log the results:

```
public class IntroToJpaRepositoriesApp implements ApplicationRunner {

    @Autowired
    private ProjectRepository projectRepository;
   
    // ...

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Iterable<Project> allProjects = projectRepository.findAll();
        LOG.info("All Projects:\n{}", allProjects);
    }
}
```

Now when we run the application, we can see that the projects in the database are logged:

```
All Projects: 
[Project [id=1, code=P1, name=Project 1, description=Description of Project 1], Project [id=2, ...
```

## Repositories for Task and Worker Entities

Let’s create repositories for the _Task and_ _Worker_ classes too:

```
public interface TaskRepository extends CrudRepository<Task, Long> { }
```

```
public interface WorkerRepository extends CrudRepository<Worker, Long> { }
```

Now we can autowire the repositories in the _IntroToJpaRepositoriesApp_ class to test them out.

We’ve seen the _findAll_ method; now we can invoke _findById_ from our _taskRepository_ to retrieve the Task with ID 1. Note this method returns an _Optional_:

```
public class IntroToJpaRepositoriesApp implements ApplicationRunner {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private TaskRepository taskRepository;
  
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
      
        // ...

        Optional<Task> project1 = taskRepository.findById(1L);
        LOG.info("Task by id 1:\n{}", project1);
    }
}
```

Now let's re-run the application, and we’ll see in the logs that the Task is found and logged:

```
Task by id 1:
Optional[Task [id=1, name=Task 1, description=Task 1 Description, ...
```

Finally, let's query the number of Workers in our database. We can do this using the count method of the _workerRepository_ bean:

```
long noOfWorkers = workerRepository.count();
LOG.info("Number of workers:\n{}", noOfWorkers);
```

Here’s the corresponding output logged after launching the app once again:

```
Number of workers:
1
```

## Resources
- [Spring Data Repositories on Baeldung](https://www.baeldung.com/spring-data-repositories)
- [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

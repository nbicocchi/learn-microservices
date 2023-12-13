# Pagination and Sorting Support

In this lesson, we’ll look at how Spring Data JPA supports pagination and sorting.

The relevant module for this lesson is: [pagination-and-sorting-support-end](https://github.com/nbicocchi/spring-boot-course/tree/module5/pagination-and-sorting-support-end)

## Introduction

In previous lessons, we learned how to query data from repositories, but it’s not always practical to return large amounts of data in one go.

Imagine a scenario where we have to scroll away aimlessly on a screen just to get to a piece of information on a list. Now let’s also imagine that the information on the list isn’t in any particular order either.

This would provide a really poor experience, and as software engineers, this is something we always try to avoid when designing software.

**Whenever we present large amounts of data to users, it’s often good practice to divide the content into smaller chunks.** This is what we call pagination. In addition, sorting allows us to order data using relevant criteria.

Presenting smaller chunks of data to users can save both time and network bandwidth where it’s limited.

## Out-of-the-Box Support For Pagination and Sorting

Spring Data provides a repository out of the box, **_PagingAndSortingRepository,_ with 2 overloaded _findAll()_ methods that take in additional parameters, which allow pagination and sorting** for all entities.

Let’s have a look at this interface:

```
public interface PagingAndSortingRepository<T, ID> extends Repository<T, ID> {

  Iterable<T> findAll(Sort sort);

  Page<T> findAll(Pageable pageable);
}
```

The first one takes in a _Sort_ object:

```
Iterable<T> findAll(Sort sort);
```

This returns an _Iterable_ list of entities sorted by the options we pass in, while the other method takes in a _Pageable_ object:

```
Page<T> findAll(Pageable pageable);
```

It returns a page of entities corresponding to the requirements specified in the _Pageable_ parameter that we pass in.

It’s important to note that **we don’t necessarily need to extend this interface to get paging and sorting support**. It provides us two convenient methods to sort and paginate all entities, but **we can also add _Sort_ and _Pageable_ parameters in any Spring Data repository method.**

## Paginating All Entities

Now that we’ve examined the two default methods, let’s see how we can use them.

Let's open the _TaskRepository_ interface, and additionally extend the _PagingAndSortingRepository_ interface:

```
public interface TaskRepository extends CrudRepository<Task, Long>, PagingAndSortingRepository<Task, Long> {
}
```

This allows us to utilize the two methods we previously discussed that come out of the box.

Now let’s see how we can call these methods from our main class. We'll open _PaginationAndSortingSupportApp_ and inject _TaskRepository_:

```
public class PaginationAndSortingSupportApp implements ApplicationRunner {

    @Autowired
    private TaskRepository taskRepository;

    // ...
}
```

Before we proceed, let’s have a look at the initial data we load on application startup.

Let's open _data.sql_ and have a look at the Task table insert statements:

```
INSERT INTO Task(id, uuid, name, due_date, description, project_id, status) 
    VALUES (1, uuid(), 'Task 1', '2025-01-12', 'Task 1 Description', 1, 0);
INSERT INTO Task(id, uuid, name, due_date, description, project_id, status) 
    VALUES (2, uuid(), 'Task 2', '2025-02-10', 'Task 2 Description', 1, 0);
INSERT INTO Task(id, uuid, name, due_date, description, project_id, status) 
    VALUES (3, uuid(), 'Task 3', '2025-03-16', 'Task 3 Description', 1, 0);
INSERT INTO Task(id, uuid, name, due_date, description, project_id, status, assignee_id) 
    VALUES (4, uuid(), 'Task 4', '2025-06-25', 'Task 4 Description', 2, 0, 1);

```

**Note that we’re inserting four tasks in the database.**

Now let’s see how we can retrieve two tasks at a time.

Let's open _PaginationAndSortingSupportApp,_ and navigate to the _run()_ method.

First, let’s indicate that we want the first page of tasks containing two tasks a page. To achieve this, **we’ll create a _Pageable_ object** based on these conditions:

```
@Override
public void run(ApplicationArguments args) throws Exception {
    Pageable tasksFirstPage = PageRequest.of(0, 2);
}
```

Notice our use of the _PageRequest_ class; this is the most common _Pageable_ implementation.

Now, **if we look at the parameters we’re passing into the _PageRequest.of_ method, the first parameter is a zero-based page index; here, 0 indicates that we want the first page, and the second parameter indicates the page size, which in our case is 2.**

Let’s call the _findAll()_ method with this _Pageable_ parameter, and log the results:

```
@Override
public void run(ApplicationArguments args) throws Exception {
    Pageable tasksFirstPage = PageRequest.of(0, 2);

    Page<Task> tasksPage1 = taskRepository.findAll(tasksFirstPage);
    
    LOG.info("Page 1 of All Tasks:");
    tasksPage1.forEach(task -> LOG.info(task.toString()));
}
```

Now let's run the app, and inspect the log output:

```
Page 1 of All Tasks:
Task [id=1, name=Task 1, description=Task 1 Description, dueDate=2025-01-12, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
Task [id=2, name=Task 2, description=Task 2 Description, dueDate=2025-02-10, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
```

As expected, the method returns the first two tasks.

Now let’s see the second page:

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    Pageable tasksSecondPage = PageRequest.of(1, 2);
}
```

Let’s look at the parameters we’re passing into the _PageRequest.of_ method. **Remember that the page indexes are zero-based, so here, 1 means the second page, and the next parameter indicates that we want two tasks a page.**

Now let’s see how we can use this:

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    Pageable tasksSecondPage = PageRequest.of(1, 2);

    Page<Task> tasksPage2 = taskRepository.findAll(tasksSecondPage);
    LOG.info("Page 2 of All Tasks:");
    tasksPage2.forEach(task -> LOG.info(task.toString()));
}
```

Let’s run the app again, and look at the console:

```
Page 2 of All Tasks:
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO, 
    project=Project [id=2, code=P2, name=Project 2, description=About Project 2],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
```
As expected, the logs indicate that the method returns the second page with two tasks.

## Sorting All Entities

Next, let’s have a look at how we can sort all tasks by name:

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    Sort sortByTaskNameDsc = Sort.by(Direction.DESC, "name");
}
```

Here **we created a _Sort_ object indicating that we want results to be in descending order, using the _Direction_ enum parameter.** We also need to specify which property we want in descending order; in our case, this is the _name_ property from the _Task_ entity class.

Now let’s call the _findAll()_ method using this _Sort_ parameter:

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    Sort sortByTaskNameDsc = Sort.by(Direction.DESC, "name");

    Iterable<Task> tasksSortedByNameDsc = taskRepository.findAll(sortByTaskNameDsc);
    LOG.info("All Tasks Sorted By Name in Descending Order:");
    tasksSortedByNameDsc.forEach(task -> LOG.info(task.toString()));
}
```

Note that we’re logging the results here too.

We’re all set to run this:

```
All Tasks Sorted By Name in Descending Order:
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO, 
    project=Project [id=2, code=P2, name=Project 2, description=About Project 2],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
Task [id=2, name=Task 2, description=Task 2 Description, dueDate=2025-02-10, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
Task [id=1, name=Task 1, description=Task 1 Description, dueDate=2025-01-12, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
```

We can see that the method returns all four tasks sorted by name as expected.

## Pagination and Sorting Together

There may be situations where we have to **both paginate and sort entities.** In fact, it’s a very common requirement.

Let’s see how we can return **the first two tasks sorted by name in descending order:**

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    Pageable tasksFirstPageSortedByNameDsc = PageRequest.of(0, 2, Sort.by("name")
        .descending());
}
```

Here we've created a _Pageable_ object **using an overloaded method of the _PageRequest_ class that takes in a _Sort_ object.**

Let’s see how we can pass this to the task repository's _findAll_ method:

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    Pageable tasksFirstPageSortedByNameDsc = PageRequest.of(0, 2, Sort.by("name")
        .descending());

    Page<Task> tasksPage1SortedByNameDsc = taskRepository.findAll(tasksFirstPageSortedByNameDsc);
    LOG.info("Page 1 of All Tasks Sorted by Name in Descending Order:");
    tasksPage1SortedByNameDsc.forEach(task -> LOG.info(task.toString()));
}
```

Now let’s see what we get when we run this app again:

```
Page 1 of All Tasks Sorted by Name in Descending Order:
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO, 
    project=Project [id=2, code=P2, name=Project 2, description=About Project 2],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
```

As expected, we can see the first two tasks ordered by task name.

In the lessons to follow, we’ll learn how to address more complex pagination and sorting requirements.

## Boot 2 Notes

In Boot 2 / Spring Data 2, _PagingAndSortingRepository_ extends _CrudRepository_ out of the box.

Because of this, our custom repo automatically inherits all the core CRUD methods without having to extend the _CrudRepository_ explicitly as well:

```
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
}
```

The point of this change In Spring Data 3 is to allow choosing which base implementation we want to use for our custom repositories (e.g. _CrudRepository, ListCrudRepository, JpaRepository_)

## Resources
- [Pagination and Sorting](https://www.baeldung.com/spring-data-jpa-pagination-sorting)
- [PagingAndSortingRepository](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html)
# 
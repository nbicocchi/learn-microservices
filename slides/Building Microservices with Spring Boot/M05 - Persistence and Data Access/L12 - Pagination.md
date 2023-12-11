# Pagination

In this lesson, we'll explore different ways that we can paginate query results with Spring Data JPA. We'll demonstrate this for derived methods, as well as custom methods. Finally, we'll discuss what factors we should consider when returning results from paginated methods.

The relevant module for this lesson is: [pagination-end](https://github.com/nbicocchi/spring-boot-course/tree/module5/pagination-end)

## Introduction

Spring Data JPA allows us to implement pagination using various techniques.

In this lesson, **we'll also inspect the SQL queries** generated using these techniques. So before we start, let’s enable our application to log these queries by setting the corresponding parameter in our _application.properties_ file:

```
spring.jpa.show-sql=true
```

Now let’s take a look at how we can manage the results of paginated methods.

## The _Page_ Interface

In previous lessons, we learned how to use the _PagingAndSortingRepository._ Let’s revisit one of the default methods it provides and its return type:

```
Page<T> findAll(Pageable pageable);
```

**_Page_ is the most common representation of paginated entities. It represents a chunk of data or a sublist of a list of data.**

The _Page_ interface defines the contract that its implementations should follow:

```
public interface Page<T> extends Slice<T> {
    // ...
    int getTotalPages();
    long getTotalElements();
    // .

}
```

As we can see, the _Page_ interface extends the _Slice_ interface, which we’ll analyze shortly.

Additionally, it contains the total number of elements in the list and the total number of pages. However, this information comes at a cost, as an extra _count_ query is generated to retrieve the total number of elements.

### Paginating With Derived Methods Using _Page_

Now let's demonstrate how we can use the _Page_ interface with derived methods and focus on the extra information it provides. Remember that derived methods use keywords in the method signature to indicate which query Spring should generate.

Let's add **a method to find tasks by the status that also has a _Pageable_ parameter** to support pagination:

```
Page<Task> findByStatus(TaskStatus status, Pageable pageable);
```

In the main class, we're going to make use of this method by creating a criterion that allows us to retrieve only two entities, and then passing it to the _findByStatus()_ method:

```
Pageable twoTasksPagination = PageRequest.of(0, 2);
Page<Task> twoTasksAPage = taskRepository.findByStatus(TaskStatus.TO_DO, twoTasksPagination);

LOG.info("All Tasks by status Paginated:\n {}", twoTasksAPage.getContent());
LOG.info("Total No Of Tasks:\n {}", twoTasksAPage.getTotalElements());
LOG.info("Total Pages of Tasks \n {}", twoTasksAPage.getTotalPages());
```

We added additional log information in order to explicitly see the total number of elements and pages. As a reminder, our database gets initialized upon application startup, and contains four _Task_ entities.

Let's start the application, and have a look at the output:

```
All Tasks by status Paginated:
Task [id=1, name=Task 1, description=Task 1 Description, dueDate=2025-01-12, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1], assignee=null]
Task [id=2, name=Task 2, description=Task 2 Description, dueDate=2025-02-10, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1], assignee=null]

Total No Of Tasks: 4
Total Pages of Tasks: 2
```

As expected, the query has returned the first two tasks. It has also returned additional information about the total number of tasks and the total number of pages.

Now let's look at the generated queries:

```
select count(t1_0.id) from task t1_0 where t1_0.status=?
```

We can see that in addition to the standard SELECT queries, Spring Data generates a _count_ query so that the _Page_ objects have the total number of elements.

## The _Slice_ Interface

Apart from using the _Page_ interface, Spring JPA provides us with the ability to return a _Slice_ from any method that we use to paginate entities.

Similar to _Page_, **a slice represents a chunk of data, or more formally, a sublist of a list of data**. Let’s have a look at the _Slice_ interface:

```
public interface Slice<T> extends Streamable<T> {

    boolean hasNext();
    boolean hasPrevious();
    default Pageable getPageable() ..
    Pageable nextPageable();
    Pageable previousPageable();
    // ..
}
```

This interface defines a few important methods:

-   _hasNext()_ - it indicates whether there is another slice after the current one
-   _hasPrevious()_ - it indicates whether there is a slice before the current one
-   _getPageable()_ - it returns a _Pageable_ object that defines the current entities in the slice
-   _nextPageable()_ and _previousPageable()_ - they return _Pageable_ objects defining the next and previous entities respectively

We can use the _Pageable_ objects returned by the above methods to retrieve pages containing respective entities.

### Paginating With Derived Methods Using _Slice_

Now let's see how we can use the _Slice_ interface with a custom query method.

To this end, let's create a method in the _TaskRepository_ that filters tasks by name and also has a _Pageable_ parameter to support pagination:

```
Slice<Task> findByNameLike(String name, Pageable pageable);
```

As before, let’s call it from our main class by adding the following code to the _run()_ method:

```
Pageable twoTasksPagination = PageRequest.of(0, 2);
Slice<Task> twoTasksASlice = taskRepository.findByNameLike("Task%", twoTasksPagination);

LOG.info("All Tasks Sliced :\n {}", twoTasksASlice.getContent());
LOG.info("Are there Slices Prior:\n {}", twoTasksASlice.hasPrevious());
LOG.info("Are there Slices After:\n {}", twoTasksASlice.hasNext());
```

We reused the same pagination criteria we created earlier, and passed it to the method we just defined.

We also added a few more logs that make use of the other _Slice_ methods. The aim is to check whether the current slice has a previous one or next one.

When we run the application, we'll see the following output:

```
All Tasks Sliced :
Task [id=1, name=Task 1, description=Task 1 Description, dueDate=2025-01-12, status=TO_DO, 
  project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1], assignee=null]
Task [id=2, name=Task 2, description=Task 2 Description, dueDate=2025-02-10, status=TO_DO, 
  project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1], assignee=null]

Are there Slices Prior: false
Are there Slices After: true
```

In this output, we see two tasks. Since we're retrieving the first page of tasks, there can be no slices prior, as clearly demonstrated in the output message. Then the log message indicates that there are slices that follow the current slice, which is consistent with our initial dataset.

**Spring JPA uses the combination of the _offset_ and _fetch first ? rows only_ keywords to retrieve the paginated tasks.** To check this in the console output, we can find the query generated by Spring JPA (remember that we configured our application to show the SQL queries):

```
select t1_0.id,t1_0.assignee_id,t1_0.description,t1_0.due_date,
t1_0.name,t1_0.project_id,t1_0.status,t1_0.uuid
from task t1_0 where t1_0.name like ? escape '\' offset ? rows fetch first ? rows only
```

## Paginating With Custom Queries

To demonstrate how to paginate entities with custom queries, **let’s recreate the derived method we looked at earlier (retrieving all tasks matching a name) using a custom query**.

To this end, in the _TaskRepository,_ we add the following code:

```
@Query("select t from Task t where t.name like ?1")
Page<Task> allTasksByName(String name, Pageable pageable);
```

Notice that the query contains no pagination criteria at all. **Since we're using JPQL, Spring Data can modify and add the relevant pagination clause to the actual SQL query generated based on the _Pageable_ parameter we’re passing in.**

Now let's call this method from our main class. This time, let’s request page two of Tasks, then pass the _Pageable_ object to our repository method:

```
Pageable pageTwo = PageRequest.of(1, 2);
Page<Task> allTasksByNamePageTwo = taskRepository.allTasksByName("Task%", pageTwo);
LOG.info("Page Two of All Tasks By Name:\n {}", allTasksByNamePageTwo.getContent());
```

All we do here is pass in the task name we want to filter by, along with our pagination information. When we run the application, we'll notice the following output in the console log:

```
Page Two of All Tasks By Name:
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO, 
  project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1], assignee=null]
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO, 
  project=Project [id=2, code=P2, name=Project 2, description=About Project 2], 
  assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
```

As expected, we see the last two tasks.

Now let's check how Spring Data has translated our _Pageable_ object into pagination clauses in the generated SQL:

```
select t1_0.id,t1_0.assignee_id,t1_0.description,t1_0.due_date,
t1_0.name,t1_0.project_id,t1_0.status,t1_0.uuid
from task t1_0 where t1_0.name like ? escape '' offset ? rows fetch first ? rows only
```

Note that **Spring Data has cleverly added the _offset_ and _fetch first ? rows only_ keywords** to the first query that hits the tasks table.

### Paginating With Native Queries

We can paginate entities with native queries as well, but Spring won’t add any pagination-related clauses because there’s no reliable way to manipulate native SQL queries.

Therefore, we have to explicitly pass in the SQL _offset_ and _fetch first ? rows only_ values as parameters in our query methods. We've explored native queries and parameters in other lessons, so we won’t be covering it again here.

## When To Use _Page_ And _Slice_

We've learned how to use _Slice_ and _Page,_ but let's consider what factors we have to keep in mind when using them.

We know now that using the **_Page_ interface generates an additional _count_ query** to find out the total number of elements with every call to a paginated method.

Although this is an extra call to the database, **it comes in handy when we design user interfaces, as we could indicate the total number of results in the very first query itself**. Subsequently, since we know the total number of pages, we can construct page numbers accordingly and allow a user to jump to a particular page.

**In contrast, we can use the _Slice_ interface when we don't need that extra information.** This way we can avoid any extra calls to the database, which might be costly depending on the database used. Since the slices have information about the existence of the next ones and previous ones, we could use this approach to deal with large datasets where we might not require information about the total number of results.

For example, we could use slices in order to represent news feeds or social media feeds where we don’t necessarily require a total item count or even a page count.

One caveat to remember here is that the default _findAll(..)_ method we inherit from the _PagingAndSortingRepository_ can return a _Slice_ instead of a _Page._ Returning a _Slice_ in this particular instance won’t avoid the extra query that a _Page_ would otherwise generate. It has nothing to do with Spring Data JPA, it’s simply a type conversion performed by Java. Essentially, Spring Data will return a _Page,_ and Java will just convert it to a _Slice_.

## Resources
- [Pagination and Sorting using Spring Data JPA](https://www.baeldung.com/spring-data-jpa-pagination-sorting)
- [Paging and Sorting](https://docs.spring.io/spring-data/rest/docs/current/reference/html/#paging-and-sorting)
- [Derived Query Methods in Spring Data JPA Repositories](https://www.baeldung.com/spring-data-derived-queries)
- [Query Creation From Method Names](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)

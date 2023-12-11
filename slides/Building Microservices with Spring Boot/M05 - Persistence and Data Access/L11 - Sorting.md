# Sorting

In this lesson, we’ll learn how we can sort queried entities with Spring Data JPA. The focus of this lesson is to introduce different mechanisms we can employ to sort entities in Spring Data JPA, and look at how we can address some complex sorting requirements. **We’ll learn how to sort entities with derived query methods, and with _Sort_ parameters.**

The relevant module for this lesson is: [sorting-end](https://github.com/nbicocchi/spring-boot-course/tree/module5/sorting-end)

## Sort Order

Before we talk about the different mechanisms, let’s have a look at sort orders.

Sorting can be done in ascending or descending order based on the data type.

A point worth noting here is that Spring Data doesn’t actually sort entities, it merely **accommodates the corresponding ORDER BY clauses in the generated SQL queries** after performing basic validations, such as if the property we’re sorting on belongs to the domain model.

**Naturally, it’s the database engine that’s actually performing the sorting of the data.**

We’ll see this as we proceed.

Another interesting point is that **if the property being sorted contains null values, these will be placed either first or last** in the result set, but this depends on the underlying database engine. Even though the framework supports indicating which behaviour we want to follow by passing in null handling hints, we won’t be focusing on this aspect in this lesson.

Let’s have a look at the repository we’ll be using for our examples:

```
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
}
```

As we can see, our _TaskRepository_ is extending the _PagingAndSortingRepository_ interface; however, **the functionality we’ll be showcasing in this lesson can be applied to any Spring Data Repository.**

## Sorting With Derived Query Methods

The first mechanism we can use to sort entities is with derived query methods, whereby we indicate what query Spring should generate by using keywords in our method name.

**The keyword used to trigger sorting is _OrderBy_, which is in the predicate section of the method.**

Let’s look at an example. Suppose we want to fetch Tasks and sort them by their due date, retrieving first the Tasks whose due dates are the furthest away.

So our sorting order, or direction, should be descending.

Let's define this method:

```
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {

    List<Task> findAllByOrderByDueDateDesc();
}
```

Note that we’re not passing any filter parameters, so all entities will be retrieved from the database.

Now let’s inspect the predicate of this derived query method by breaking it down.

**...OrderBy…**

This is the keyword we use to indicate that we want the data in some sort of order. This is followed by the name of an entity attribute to indicate which column we want the results to be sorted by.

**...DueDate…**

In this case, this column is the due date.

**...Desc()**

**If we don’t specify anything, our data will be sorted in ascending order by default, so we have to be explicit here and indicate that we want the entities to be sorted in descending order.**

Note that _Asc_ is the keyword we use to explicitly indicate that we want to sort in ascending order; if that had been the case here, we would have named our method _findAllByOrderByDueDateAsc_.

Now let’s call the query method from our main class _DataSortingApp_:

```
public class DataSortingApp implements ApplicationRunner {
    // ...

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<Task> tasks = taskRepository.findAllByOrderByDueDateDesc();

        LOG.info("All Tasks Ordered by Due Date Descending Order:");
        tasks.forEach(t -> LOG.info("{}", t));
    }
}
```

This is straightforward, and we’re also logging the output here for visual verification.

Now we’ll start our application, and check the log output:

```
All Tasks Ordered by Due Date Descending Order :
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO,
    project=Project [id=2, code=P2, name=Project 2, description=About Project 2],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=5, name=Copying Task, description=Copying Task 2 Description, dueDate=2025-03-17, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=Worker [id=2, email=smith@test.com, firstName=John, lastName=Smith]]
Task [id=6, name=Copying Task, description=Copying Task 2 Description, dueDate=2025-03-17, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
...
```

Just as we expected, **we can see the Tasks with the latest due dates first in the list.**

## Derived Query Methods With Multiple Sort Criteria

What if we want to sort Tasks by multiple properties?

Let’s replicate the method we just defined, and see how we can further sort Tasks by their assignee’s last name in ascending order, which means using a nested property:

```
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
    // ...
    
    List<Task> findAllByOrderByDueDateDescAssigneeLastNameAsc();
}
```

That made the method name rather long, but let’s focus on how we added the second sort criteria right after the first one.

**...AssigneeLastNameAsc(...)**

This will be associated with the assignee’s last name, which as we said is a nested sort property.

It’s worth mentioning that we have to explicitly include the sort direction before concatenating a new sorting criteria, otherwise the framework will interpret it as nested properties and raise an error on startup.

Now let’s head back over to our main class _DataSortingApp,_ and run this:

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    List<Task> tasksSortedByMultipleProps = taskRepository.findAllByOrderByDueDateDescAssigneeLastNameAsc();

    LOG.info("All tasks ordered by due date in descending order and assignee last name in Ascending order :");
    tasksSortedByMultipleProps.forEach(t -> LOG.info("{}", t));
}
```

All that remains now is running the app:

```
All tasks ordered by due date in descending order and assignee last name in Ascending order :
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO,
    project=Project [id=2, code=P2, name=Project 2, description=About Project 2],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=6, name=Copying Task, description=Copying Task 2 Description, dueDate=2025-03-17, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=5, name=Copying Task, description=Copying Task 2 Description, dueDate=2025-03-17, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=Worker [id=2, email=smith@test.com, firstName=John, lastName=Smith]]
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
...
```

Notice the difference between the previous output in the order of the Tasks with the same due date (with id 5 and 6).

## Sorting With _Sort_ Parameter

Now let’s focus on how we can use _Sort_ parameters to sort entities.

**With this approach, we can dynamically define the _Sort_ criteria. For instance, we can do this based on conditions we may receive from a higher abstraction layer.**

Let’s replicate the sorting condition we developed where we sort Tasks by the due date and then by the assignee’s last name; however, this time we’ll use the new approach on a custom method that retrieves Tasks whose name contains a substring:

```
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
    // ...

    List<Task> findByNameContaining(String taskName, Sort sort);
}
```

Note that we’re passing in a _taskName_ filter parameter here.

Let’s head over to the main class, and invoke this query:

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    Sort sortByDueDateAssigneLastName = Sort.by(Direction.DESC, "dueDate")
        .and(Sort.by(Direction.ASC, "assignee.lastName"));

    List<Task> tasksSortedByDueDateAssigneLastName = taskRepository.findByNameContaining("Task", sortByDueDateAssigneLastName);

    LOG.info("All tasks ordered by due date in descending order and assignee last name in Ascending order using Sort Parameter :");
    tasksSortedByDueDateAssigneLastName.forEach(t -> LOG.info("{}", t));
}
```

First, we create the _Sort_ instance with the corresponding criteria. **Notice how we use the method _and(..)_ to combine the two sorting conditions.** We can chain any number of additional _Sort_ criteria this way.

Also, notice the notation we’re using to access the nested property; we separate the _assignee_ attribute and its _lastName_ child attribute with a dot _("_._")_.

We can then use this _Sort_ instance in our method.

Let's re-run the application, and inspect the logs:

```
All tasks ordered by due date in descending order and assignee last name in Ascending order using Sort Parameter :
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO,
    project=Project [id=2, code=P2, name=Project 2, description=About Project 2],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=6, name=Copying Task, description=Copying Task 2 Description, dueDate=2025-03-17, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=5, name=Copying Task, description=Copying Task 2 Description, dueDate=2025-03-17, status=TO_DO, 
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=Worker [id=2, email=smith@test.com, firstName=John, lastName=Smith]]
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
...
```

The logs show that we have Tasks ordered by due date and assignee's last name, the same as we did earlier using a derived query method with multiple parameters.

It's worth mentioning at this point that mixing an _OrderBy_ derived query method clause and a Sort parameter in the same method won’t raise an exception, but it would be a futile exercise, as **the _OrderBy_ sorting criteria will simply take precedence over the Sort parameter.**

## How to Use Typed Sort

In earlier examples, we used strings to indicate the properties we’re sorting on, which is cumbersome and potentially error-prone.

Now let’s have a look at how we can avoid that **by using _TypedSort,_ which provides a way to specify type-safe sort criteria.**

Let’s take the same example where we sort Tasks by due dates and the assignee's last name using the _findByNameContaining_ method:

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    TypedSort<Task> taskTypedSort = Sort.sort(Task.class);

    Sort sortByDueDate = taskTypedSort.by(Task::getDueDate)
        .descending();

    Sort sortByAssigneeLastName = taskTypedSort.by((Function<Task, String>) task -> task.getAssignee()
        .getLastName())
        .ascending();

    Sort sortByAssigneeLastNameAndDueDate = sortByDueDate.and(sortByAssigneeLastName);

    List<Task> typedSortResult = taskRepository.findByNameContaining("Task", sortByAssigneeLastNameAndDueDate);

    LOG.info("All tasks ordered by due date in descending order and assignee last name in Ascending order using TypedSort :");
    typedSortResult.forEach(t -> LOG.info("{}", t));
}
```

Let's go step by step here.

First, we’re defining a _TypedSort_ instance that will be applicable to _Task_ entities.

Then we define our type-safe sorting criteria:

-   For our primary criteria, **we use a double colon operator** as an argument of the _by_ method of our _TypedSort_ instance, along with the sort direction.
-   Then we define our secondary sorting condition in a similar manner, but since we need **to reference a nested getter method,** we can’t simply use the double colon operator as we did earlier. Instead, **we define a Lambda expression** (note we cast this expression to avoid a compilation issue here).

Notice how we no longer use _String_ property names.

Next, we chain the two sorting criteria together by using the _and(..)_ method to form our combined sorting criteria. We pass this into our query just as we did before, and then we log the results.

Let’s run this and inspect the logs to ascertain the result:

```
All tasks ordered by due date in descending order and assignee last name in Ascending order using TypedSort :
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO,
    project=Project [id=2, code=P2, name=Project 2, description=About Project 2],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=6, name=Copying Task, description=Copying Task 2 Description, dueDate=2025-03-17, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
Task [id=5, name=Copying Task, description=Copying Task 2 Description, dueDate=2025-03-17, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=Worker [id=2, email=smith@test.com, firstName=John, lastName=Smith]]
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
...
```

Once again, we obtain the same result as we did with the previous query.

## How to Define Empty Sort Criteria

**There could be situations where we have to use a method that takes in a _Sort_ parameter, but we don't actually want or need to sort entities**, like if we’re relying on user input to define our Sort criteria dynamically and none is specified.

The framework doesn’t allow passing a _null_ Sort value, or empty strings for the _Sort_ parameter. Instead, it provides a way to **return a _Sort_ instance with no sorting criteria specified by invoking _Sort.unsorted()_.**

We can pass this to any of the repository methods we saw earlier:

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    Sort unsortedTasks = Sort.unsorted();

    List<Task> unsortedTaskList = taskRepository.findByNameContaining("Task", unsortedTasks);

    LOG.info("All Tasks unsorted :");
    unsortedTaskList.forEach(t -> LOG.info("{}", t));
}
```

Now let’s run this to validate that the Tasks are returned in the order they’re in the database table:

```
All Tasks unsorted :
Task [id=1, name=Task 1, description=Task 1 Description, dueDate=2025-01-12, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
Task [id=2, name=Task 2, description=Task 2 Description, dueDate=2025-02-10, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
Task [id=3, name=Task 3, description=Task 3 Description, dueDate=2025-03-16, status=TO_DO,
    project=Project [id=1, code=P1, name=Project 1, description=Description of Project 1],
    assignee=null]
Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO,
    project=Project [id=2, code=P2, name=Project 2, description=About Project 2],
    assignee=Worker [id=1, email=john@test.com, firstName=John, lastName=Doe]]
...
```

## Resources
- [Sorting Query Results with Spring Data](https://www.baeldung.com/spring-data-sorting)
- [Pagination and Sorting using Spring Data JPA](https://www.baeldung.com/spring-data-jpa-pagination-sorting)
- [Class Sort](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Sort.html)
- [Derived Query Methods in Spring Data JPA Repositories](https://www.baeldung.com/spring-data-derived-queries)
- [Query Creation From Method Names](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)
- [Typed Sort](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Sort.TypedSort.html)

# Deep Dive Into Derived Query Methods

Welcome to this lesson from Learn Spring Data, where we’ll deep dive into the derived query methods of Spring Data JPA.

The relevant module for this lesson is: [deep-dive-into-derived-query-methods-end](https://github.com/nbicocchi/spring-boot-course/tree/module5/deep-dive-into-derived-query-methods-end)

## Deep Dive Into Derived Query Methods

In previous lessons, we learned how derived query methods are structured, and how to write simple conditions in our queries.

Now let’s go further by exploring some standard keywords supported by Spring, and the querying capability of derived query methods through practical examples.

## Equality Conditions

Equality is one of the most commonly used conditions. **Writing an equality condition can be as simple as specifying the property name in the predicate.**

Let's open _ProjectRepository_ and add the method:

```
public interface ProjectRepository extends CrudRepository<Project, Long> {

    Iterable<Project> findByName(String name);
}
```

The predicate of the above method simply translates to a condition with an equality operator applied on the _name_ property of the _Project_.

Optionally, we can use the '_Is'_ or '_Equals'_ keywords for better readability, but the resulting query will remain the same:

```
Iterable<Project> findByNameIs(String name);

Iterable<Project> findByNameEquals(String name);
```

**We can also use the negation operators to express inequality:**

```
Iterable<Project> findByNameIsNot(String name);
```

## Similarity Conditions

When we don’t want to go for exact equality, we can use similarity operators.

For example, **we can find Projects whose names start with a value using the _StartingWith_ operator**:

```
public interface ProjectRepository extends CrudRepository<Project, Long> {
    // ...
    
    Iterable<Project> findByNameStartingWith(String name);
}
```

Let’s invoke this method from our main class to try it out:

```
public class DeepDiveDerivedQueryMethodsApp implements ApplicationRunner {

    // ...
    
    @Override
    public void run(ApplicationArguments args) throws Exception {

        Iterable<Project> projects = projectRepository.findByNameStartingWith("Project");
        LOG.info("Projects name starting with Project:");
        projects.forEach(project -> LOG.info("{}", project));
    }
}
```

Now let’s run the app. If we check the logs, we'll see that **internally, our predicate condition translates into a '_LIKE'_ operator.**

Note: we’ve included the _spring.jpa.properties.hibernate.format\_sql_ property to our application setup to improve the readability of the generated queries, since in this lesson we’re using more complex queries.

```
select project0_.id as id1_0_, //...
from project project0_
where project0_.name like ? escape ?
...
projects name starting with "Project"
Project [id=1, code=P1, name=Project 1,//..]
Project [id=2, code=P2, name=Project 2,//..] 
Project [id=3, code=P3, name=Project 3,//..]
```

As expected, the query returns all the Projects with names starting with the given text.

**It’s worth mentioning that Spring internally sanitizes the parameters that we pass to the query method.**

Let’s see this in action with an example.

As we probably know, the percent sign (_%_) is used as a wildcard in a SQL LIKE expression; we'll include this character in our method argument to see what happens:

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    Iterable<Project> percentSignProjects = projectRepository.findByNameStartingWith("%");
    LOG.info("projects name starting with \"%\"\n{}", percentSignProjects);
}
```

When we re-run the application, we see it logs an empty collection:

```
projects name starting with "%"
[]
```

**If the LIKE expression wasn’t sanitized, this would have retrieved all the Projects in our database.**

This is particularly relevant in cases where the argument value we use in the method comes from an unsecured source, like the user input, and therefore is prone to SQL injection attacks, like the one we tried above.

In these cases, it’s important to understand what Spring does behind the scenes to create a secure data access implementation. As we just saw, Spring does cover some security aspects, but we still have to be cautious on how we proceed. For instance, knowing that the resulting query includes a _LIKE_ operator, what would happen if we allowed an empty String value? Let's see:

```
@Override
public void run(ApplicationArguments args) throws Exception {
    // ...

    Iterable<Project> allProjects = projectRepository.findByNameStartingWith("");
    LOG.info("Projects name starting with \"\"");
    allProjects.forEach(project -> LOG.info("{}", project));
}
```

In the logs, we can verify this would finally retrieve all the persisted entities, as Spring does include a wildcard in the expression internally:

```
Projects name starting with ""
Project [id=1, code=P1, name=Project 1, description=Description of Project 1]
Project [id=2, code=P2, name=Project 2, description=About Project 2]
Project [id=3, code=P3, name=Project 3, description=About Project 3]
```

Now let's switch back to the _ProjectRepository_ class.

Similar to _StartingWith_, we have other similarity operators:

-   an _EndingWith_ operator
-   **the C_ontaining_ operator to check if the argument is simply a substring of the entity attribute**
-   **the _Like_ operator for more complex pattern matching, directly supplying the pattern as the argument**

```
Iterable<Project> findByNameEndingWith(String name);

Iterable<Project> findByNameContaining(String name);

Iterable<Project> findByNameLike(String likePattern);
```

Of course, Spring won’t sanitize the _likePattern_ argument in this case, since it’s expected to use wildcards in these expressions.

## Comparison Conditions

Now let’s check out some comparison operators.

We’ll write some queries in the _TaskRepository_.

We can apply comparison operations, like _LessThan_ or _GreaterThan,_ on the records.

These operators are typically used to filter based on numeric attributes or dates, but we can actually apply comparison operators on any property whose type implements the _Comparable_ interface.

For example, we can find all Tasks that have a due date before or after a given date:

```
public interface TaskRepository extends CrudRepository<Task, Long> {

    List<Task> findByDueDateGreaterThan(LocalDate dueDate);
    
    List<Task> findByDueDateLessThan(LocalDate dueDate);
    
    List<Task> findByDueDateLessThanEqual(LocalDate dueDate);

    List<Task> findByDueDateGreaterThanEqual(LocalDate dueDate);
}
```

Let’s open the _DeepDiveDerivedQueryMethodsApp_ class, and invoke these queries from our main method:

```
List<Task> tasksStrictlyDue = taskRepository.findByDueDateGreaterThan(LocalDate.of(2025, 2, 10));
LOG.info("Number of Tasks due strictly after: \"2025-02-10\"\n{}", tasksStrictlyDue.size());
```

We’re logging the number of Tasks that are due after a given date. If we run the app and check the logs, we'll see that **internally, the _GreaterThan_ condition translates to a _\>_ operator**, and that there are two Tasks due after the given date:

```
select task0_.id as id1_1_, //...
from task task0_
where task0_.due_date>?
...
Number of Tasks due strictly after: "2025-02-10"
2
```

Now let’s find the number of Tasks due on or after the given date.

For this, we invoke the _GreaterThanEquals_ operator:

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    List<Task> tasksDue = taskRepository.findByDueDateGreaterThanEqual(LocalDate.of(2025, 2, 10));
    LOG.info("Number of Tasks due after: \"2025-02-10\"\n{}", tasksDue.size());
}
```

Let’s run the app, and check the logs once more:

```
select task0_.id as id1_1_, //...
from task task0_ where 
task0_.due_date>=?
...
Number of Tasks due after: "2025-02-10"
3
```

**Internally, the _GreaterThanEqual_ condition translates to a _\>=_ operator,** and we see there are three Tasks that are due on or after the given date.

We can also use the _After_ and _Before_ operators, which improve readability when it comes to comparing dates:

```
List<Task> findByDueDateAfter(LocalDate dueDate);

List<Task> findByDueDateBefore(LocalDate duaDate);
```

## Logical Operators

**We can combine multiple conditions using the _And_ and _Or_ logical keywords.**

For example, we can find all Tasks which have passed their due dates, but are still in status _TO\_DO,_ using the following function:

```
public interface TaskRepository extends CrudRepository<Task, Long> {
    // ...

    List<Task> findByDueDateBeforeAndStatusEquals(LocalDate dueDate, TaskStatus status);
}
```

Let’s invoke this query from our main method:

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    List<Task> overdueTasks = taskRepository.findByDueDateBeforeAndStatusEquals(LocalDate.now(), TaskStatus.TO_DO);
    LOG.info("Overdue Tasks:\n{}", overdueTasks);
}
```

Now let's run the app, and check the logs.

**Internally, the framework generates a query in which the where clause has the two conditions concatenated by a logical _and_ operator.**

We can see in the result that there is one Task overdue and still in _TO\_DO_ status:

TEXT

`select task0_.id as id1_1_//… from task task0_ where task0_.due_date<? and task0_.status=? ... Overdue Tasks: [Task [id=5, name=Task 5, dueDate=2020-01-01, status=TO_DO, //..]`

## Querying On Nested Properties

**We can also query based on the nested properties of an entity.**

For example, we can find all the Tasks whose assignees’ first names are equal to the argument:

```
select task0_.id as id1_1_//…
from task task0_ 
where task0_.due_date<? and task0_.status=?
...
Overdue Tasks:
[Task [id=5, name=Task 5, dueDate=2020-01-01, status=TO_DO, //..]
```

**Note that here, _Assignee_ is a property of type _Worker_ in the _Task_ entity, and _FirstName_ is a property of the _Worker_ entity itself.**

**We can traverse to a nested property through its parent, so in the above query, _AssigneeFirstName_ is interpreted as _assignee.firstName._**

Let’s invoke our query from the main method:

```
public void run(ApplicationArguments args) throws Exception {
    // ...
    
    List<Task> tasksByAssignee = taskRepository.findByAssigneeFirstName("John");
    LOG.info("Tasks assigned to John\n{}", tasksByAssignee);
}
```

Now let's run the app, and check the logs.

**Internally, the framework translates our query method to a query containing a JOIN clause over _Task_ and _Worker_ table, and then the filtering is done based on the worker’s _first\_name_ attribute.**

We can see in the result there is one Task assigned to the given Worker:

```
select task0_.id as id1_1_, task0_.assignee_id as assignee6_1_, 
from task task0_ left outer join worker worker1_ 
on task0_.assignee_id=worker1_.id 
where worker1_.first_name=?
...
Tasks assigned to John
[Task [id=4, name=Task 4, description=Task 4 Description, dueDate=2025-06-25, status=TO_DO, //..
assignee=Worker [id=1, firstName=John, lastName=Doe] ]]
```

## Limiting Results

**We can limit the results by using the _First_ or _Top_ keywords.**

For instance, we can select only the first two Tasks in the corresponding table by using _First2_; **if we don’t specify a numeric value after the _First_ keyword, only one record is returned:**

```
public interface TaskRepository extends CrudRepository<Task, Long> {
    // ...
    
    List<Task> findFirst2By();
    
    Task findFirstBy();
}
```

**Another keyword used to limit the query results is _Distinct_, which naturally removes duplicate entries in the output.**

Let's add a new distinct query in _ProjectRepository_:

```
public interface ProjectRepository extends CrudRepository<Project, Long> {
    // ...

    Iterable<Project> findDistinctByTasksNameContaining(String taskName);
}
```

Project has a one-to-many relationship with Tasks. Because of this, **if we omit the _Distinct_ keyword, our query could potentially retrieve duplicated Project entries**, one for each Task matching the filter.

Now let’s try this out in our _DeepDiveDerivedQueryMethodsApp_ class:

```
public void run(ApplicationArguments args) throws Exception {
    // ...

    Iterable<Project> distinctProjects = projectRepository.findDistinctByTasksNameContaining("Task");
    distinctProjects.forEach((project) -> LOG.info("distinct projects with Task name containing \"Task\": {}", project));
}
```

## Closing Notes

In this lesson, we only discussed a few of the many query capabilities offered by Spring Data JPA. To see more examples, you can check out the links in the Resources section.

We learned how powerful derived query methods are. Naturally, these are best suited for simple queries; for more complex queries, we might have to resort to a different mechanism that we’ll discuss in future lessons.

## Resources
- [Spring Data derived queries](https://www.baeldung.com/spring-data-derived-queries)
- [Spring JPA Repositories query creation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)
- [Spring Data query methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods)
- [Spring Data Supported keywords](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords)

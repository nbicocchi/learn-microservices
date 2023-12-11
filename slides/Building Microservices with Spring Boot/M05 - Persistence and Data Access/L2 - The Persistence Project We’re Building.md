# The Persistence Project We’re Building

Welcome to a new lesson out of Learn Spring Data. In this lesson, we’ll set up our project and create the domain entities that we’ll use throughout our course.

The relevant module for this lesson is: [spring-data-jpa-setup-end](https://github.com/nbicocchi/spring-boot-course/tree/module5/spring-data-jpa-setup-end)

## Setting up the Project

Our main focus will be the persistence layer, and an important part of this is the domain.

So, for our project, we’ll use **a domain that models a task management application**. We’ll also build our project as a Spring Boot app, as this is a natural pairing with Spring Data and provides an easy way to run the project.

Let’s start by **creating a simple Spring Boot project** using the Spring Initializr: [](https://start.spring.io/)[https://start.spring.io/](https://start.spring.io/)

We’ll generate a Maven project that uses the Java language.

Let’s also set:

-   group to _com.baeldung_
-   artifact to _persistence-project_
-   package to _com.baeldung.lsd_

In the dependencies section, **search for "Spring Data JPA" and add the dependency** from the search results.

We’ll examine this dependency in more detail in a future lesson. For now, keep in mind this brings in the JPA specification dependency and Hibernate as the default implementation, which will allow us to define the entities in our domain.

We’ll also search for “Spring Web” and add this dependency from the search results. Although we won’t focus on web aspects, this dependency allows us to start the application and keep it running.

Finally, let’s download the generated project, unzip the archive, and import the project into our IDE.

The only class in the project at this point is the standard main class annotated with _@SpringBootApplication,_ which allows us to run the project.

## Defining the Domain

Now we’ll continue with **defining our entities**.

Let’s have a look at a short diagram that presents our entities to get an idea of our goal before we start the implementation:

![](images/diagram-project.png)

As we can see here, we’ll have a _Project_ entity that has a 1-to-many relationship with the _Task_ entity. In addition, we’ll define a Worker entity that has a 1-to-many relationship with the _Task_ entity.

Now let’s move on to the implementation. We’ll start by **adding a _Task_ class** in a new _com.baeldung.lsd.persistence.model_ package.

Following the JPA standard, **we’ll annotate this class with _@Entity_ and add an id** field marked with _@Id_:

```
@Entity
public class Task {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Task() {
    }

}
```

We indicated here that the _id_ values will be generated automatically by adding the _@GeneratedValue_ annotation, and we also added the default constructor that JPA requires.

Now we'll add a String _uuid_ field:

```
@Entity
public class Task {
    // ... 
   
    @Column(unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();
}
```

Let’s explain the purpose of this field.

When we work with ORM, it’s usually suggested that we implement the _equals()_ and _hashcode()_ methods using the business (or ‘natural’) key fields because these are the properties that would uniquely identify the instance in the real world. You can read more about this by having a look at the links in the Resources section.

The business key could be a single field, a combination of properties, or a unique client-generated id or code.

**If we analyze our Task entity, we’ll realize it doesn’t contain a clear business key within its fields. This is why, in our case, we’re generating a unique UUID value,** which is set automatically when the Task object is created.

Of course, the _id_ field is also an auto-generated value, but this isn’t managed by us; in fact, it isn’t generated until it’s persisted in the database. Before the persistence, the id value is _null,_ so if we used this, two newly created instances would be considered ‘equal’ even though they’re clearly not.

Next, let’s add a few fields to describe our entity. We’ll start with two **String fields for the _name_ and _description_**:

```
@Entity
public class Task {
    //…
    
    private String name;
    private String description;
}
```

Then we’ll **add the _dueDate_ field and an enumeration field to denote the status** of the task:

```
@Entity
public class Task {
    //…
    private LocalDate dueDate;
    private TaskStatus status;
}
```

Of course, we need to create the _TaskStatus_ enum:

```
public enum TaskStatus {
    private final String label;
    
   private TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
```

We added a simple _label_ field to the enum, as well as a constructor and getter.

Then we’ll add the actual enum values with their labels:

```
public enum TaskStatus {
    TO_DO("To Do"), 
    IN_PROGRESS("In Progress"), 
    ON_HOLD("On Hold"), 
    DONE("Done");
    // ...
}
```

Now the _Task_ class will compile correctly, and we can generate the getters and setters of the fields using the IDE.

Finally, we’ll add **the _equals()_ method,** which compares the _uuid_ values:

```
@Entity
public class Task {

    // ...
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }
}
```

Our _hashCode_ method will use the same field:

```
@Entity
public class Task {

    // ...

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
```

**Next, we’ll add the _Project_ entity** in a similar way:

```
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Project() {}
}
```

Then we’ll add a few descriptive fields:

```
@Entity
public class Project {

    @Column(unique = true, nullable = false, updatable = false)
    private String code;

    private String name;
    private String description;
    
    // ...
}
```

**The _code_ field will be the _Project’s_ business key,** so we marked it as unique, non-nullable, and non-updatable.

At this point, we can **add the relationship with the _Task_ entity** using the _@OneToMany_ annotation:

```
@Entity
public class Project {

    @OneToMany(mappedBy = "project", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();
    
    // ...
}
```

At the end, we can **generate the standard methods: constructors, getters/setters, _toString_**_._

We’ll also **add the _equals()_ and _hashCode()_ methods** based on the unique _code_ field:

```
@Entity
public class Project {

    // ...
    
    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Project other = (Project) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }
}
```

We can now **add the other end of the relationship between _Task_ and _Project_**:

```
@Entity
public class Task {
    // ... 

    @ManyToOne(optional = false)
    private Project project;
}
```

Now that we have the _Project_ field in the _Task_ class, **we can generate the getter/setter for this field and the constructor using fields**.

In the constructor, we have to be sure **to not include the _id_ and _uuid_ fields,** as these values are generated.

Once the generated constructor is added, we’ll also add a constructor that sets the default value of the status to _TO\_DO_:

```
@Entity
public class Task {
    // ...
    
    public Task(String name, String description, LocalDate dueDate, Project project) {
        this(name, description, dueDate, project, TaskStatus.TO_DO);
    }
}
```

Next, we’ll **add the _Worker_ entity,** which represents the assignee of a task:

```
@Entity
public class Worker {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;
    private String firstName;
    private String lastName;

    public Worker() {}
}
```

> We add the _id_ and a few additional fields for the _Worker,_ after which, we can generate the standard methods of a Java POJO.

Since we have all the entities defined, we can now **add the relationship between _Task_ and _Worker_** through an _assignee_ field:

```
@Entity
public class Task {
    
    @ManyToOne
    private Worker assignee;

    // ...
}
```

Note that this is a many-to-one relationship from the task to the Worker, as a worker can have multiple tasks assigned.

At a high level, the Project-Task relationship is bidirectional, while the Task-Worker(assignee) relationship is unidirectional.

This is only because we foresee that these will be the most common cases.

This concludes the domain of our application.

Note that the model here is a simplified scenario, and shouldn't be taken as a definite reference. In practice, your entities have to be modeled based on the domain and business rules.

## Database Setup

Let’s have a look at the database we’ll be using.

**We chose an H2 in-memory database, which makes it easier to start the project without any additional setup.**

We’ll add the required dependency to the _pom.xml_:

```
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

Since we’re using Spring Boot, adding this dependency will auto-configure the H2 database as the main datasource.

However, as our main focus is the persistence layer, we want to be able to access the database and see the modifications.

For this, the **H2 database provides a web console that we can enable by adding a property in the _application.properties_** file:

```
spring.h2.console.enabled=true
```

The console requires us to provide a URL, user, and password to connect to the database, so we’ll configure these using properties as well:

```
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE 
spring.datasource.username=sa 
spring.datasource.password=
```

Spring suggests disabling the database’s automatic shutdown whenever we configure and use an embedded database that relies on Spring Boot to control when to effectively close it.

With H2, this is achieved by adding the _DB\_CLOSE\_ON\_EXIT=FALSE_ option to the connection URL, as we've done above.

By default, Spring enables what’s called "Open Session In View." This enables web requests to keep Hibernate Sessions open during view rendering. While this may be useful in certain cases, it’s often considered an anti-pattern. Here we’ll simply disable it:

```
spring.jpa.open-in-view=false
```

More information about the Open Session In View pattern is available in the resources section.

Now we’ve defined our entities and the database we’ll be using.

## Generating the Database Schema

The next aspect to clarify is how the tables in the database will be created.

Remember that we’re using the JPA module of Spring Data, which is an ORM framework, meaning **the entities are mapped to database tables.**

So an easy way to create the tables is to have our application generate these based on our Java entities.

In a Spring Boot application that uses Spring Data JPA with the default Hibernate implementation and an embedded database, **the database schema will be generated automatically on startup and dropped on shutdown.**

**We can control this behaviour using the _spring.jpa.hibernate.ddl-auto_ property; this property has the value _create-drop_ by default.**

Now let’s start our application and access the H2 console at [](http://localhost:8080/h2-console)[http://localhost:8080/h2-console](http://localhost:8080/h2-console).

We’ll enter the _jdbc:h2:mem:testdb_ URL and the _sa_ username on the first screen, then click "Connect."

Once we connect, we’ll be able to see the generated schema on the left side of the screen:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/CZfFGzapRQafTlmcO1bF)

We can run a query on a table by clicking on the table name, which will auto-fill the SELECT query in the SQL Statement window, and then hitting "Run":

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/gTt3CtaQv2VlMjYcy9mw)

At this point, we can see our tables were generated correctly, but we have no data.

So let’s define some sample data that we can later use in our application.

We’ll make use of Spring Boot’s powerful capabilities once again by **adding a _data.sql_ file in the _src/main/resources_ folder that will be picked up automatically by Boot and executed on startup.**

This file will contain INSERT statements for each table:

```
INSERT INTO Project(id, code, name, description) VALUES (1, 'P1', 'Project 1', 'Description of Project 1');
INSERT INTO Project(id, code, name, description) VALUES (2, 'P2', 'Project 2', 'About Project 2');
INSERT INTO Project(id, code, name, description) VALUES (3, 'P3', 'Project 3', 'About Project 3');

INSERT INTO Worker(id, email, first_name, last_name) VALUES(1, 'john@test.com', 'John', 'Doe');

INSERT INTO Task(id, uuid, name, due_date, description, project_id, status) VALUES (1, uuid(), 'Task 1', '2025-01-12', 'Task 1 Description', 1, 0);
INSERT INTO Task(id, uuid, name, due_date, description, project_id, status) VALUES (2, uuid(), 'Task 2', '2025-02-10', 'Task 2 Description', 1, 0);
INSERT INTO Task(id, uuid, name, due_date, description, project_id, status) VALUES (3, uuid(), 'Task 3', '2025-03-16', 'Task 3 Description', 1, 0);
INSERT INTO Task(id, uuid, name, due_date, description, project_id, status, assignee_id) VALUES (4, uuid(), 'Task 4', '2025-06-25', 'Task 4 Description', 2, 0, 1);
```

This file creates a few records in each table.

Note that we’re still relying on the framework and Hibernate to generate the database schema out of the entity classes we defined. Thus, we have to make sure that our _data.sql_ script is executed after Hibernate is initialized, otherwise, it won’t be able to find the tables we’re using and will fail.

For this, let’s open up the _application.properties_ file once again, and add the following property:

```
spring.jpa.defer-datasource-initialization=true
```

Then we’ll restart our application and open the H2 console.

Now if we run the SELECT statement on the Project table, we’ll see the records we just inserted:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/PbeNAC4RP230GtcUWzhM)

Before we finish, let’s make one more change in the main class by **implementing the _ApplicationRunner_ interface and overriding the _run()_ method**:

```
@SpringBootApplication
public class PersistenceProjectApplication implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceProjectApplication.class);
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Starting Spring Boot application...");   
    }
    // ... 
}
```

Now **any code we add to this method will be executed on startup.**

In our example, we added a simple log statement to see this behaviour working.

We’ve now completed the basic setup that we’ll use in the next lessons.

## Resources
- [How to implement equals and hashCode using the JPA entity identifier (Primary Key)](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/)
- [Spring Boot With H2 Database](https://www.baeldung.com/spring-boot-h2-database)
- [Defining JPA Entities](https://www.baeldung.com/jpa-entities)
- [Quick Guide on Loading Initial Data with Spring Boot](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql)
- [A Guide to Spring’s Open Session In View](https://www.baeldung.com/spring-open-session-in-view)
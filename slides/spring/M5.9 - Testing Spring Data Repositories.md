# Testing Spring Data Repositories

In this lesson, we're going to learn how to test Spring Data repositories’ methods using a convenience annotation provided by Spring Boot.

The relevant module you need to import when you're starting with this lesson is: [testing-spring-data-repositories-start](https://github.com/Baeldung/learn-spring-data/tree/module2/testing-spring-data-repositories-start)

If you want to have a look at the fully implemented lesson, as a reference, feel free to import: [testing-spring-data-repositories-end](https://github.com/Baeldung/learn-spring-data/tree/module2/testing-spring-data-repositories-end)

## Unit Test or Integration Test

Let’s quickly analyze the kinds of tests we can perform in order to check the correct behavior of the repositories’ methods.

If we want to create Unit Tests, then we have to mock several JPA components that Spring loads. Naturally, this would affect the functionality of the repository. Therefore, Unit Tests don't make much sense in this case.

On the other hand, when we test Spring Data repositories, we usually want to verify that our queries retrieve the expected data from a database when running a SELECT operation. When executing UPDATE, DELETE, and INSERT operations, we want to verify that our queries get executed successfully, maintaining the consistent state of our database.

Consequently, **we need integration tests, as the validation exceeds the bare Java code we actually develop.**

## Auto-configured Data JPA Tests

**Spring Boot provides the _@DataJpaTest_ annotation** to test our repositories. By default, this annotation invokes several Spring Boot auto-configurations that are used in our application. In this way, it **closely replicates the real behavior of our application**. Of course, it also allows us to customize these configurations if necessary.

Some of the features that _@DataJpaTest_ provides for the tests include enabling the scanning of our _@Entity_ classes, and configuring the Spring Data JPA repositories. Additionally, it **enables the logging of the database queries** that are executed, so that we can clearly see what's going on in the tests.

Naturally, this annotation doesn't configure all the layers when setting up the application context. Instead, **it configures only those that are suitable for data access tests**.

The _data.sql_ initialization script is also executed for our tests, but we won't use these records in our test examples. Instead, we'll opt for a different approach, using one more feature provided by the annotation we're analyzing here. We'll explore this in detail in the next section.

With this annotation, we don’t need to declare transaction scopes in a test. **By default, all tests decorated with the _@DataJpaTest_ annotation become transactional**. Moreover, the transaction rolls back at the end of each test, so as not to affect the initial database state of other tests.

There are use cases, however, in which we might not want to follow this approach. We’ll leave aside the analysis of the possible pitfalls of this approach, but it’s worth knowing that we can disable the default transaction management by adding an extra annotation:

```
@Transactional(propagation = Propagation.NOT_SUPPORTED)
```

## Injecting a _TestEntityManager_

When working with Spring Data JPA, we don’t usually talk to the _EntityManager_ directly. Our repositories will use a configured _EntityManager_ to interact with the database.

However, **in order to populate the database with the test data or verify the test results, we can also inject a _TestEntityManager_** **bean in our tests**, which as you can guess, is configured when we add the _@DataJpaTest_ annotation.

Alternatively, we can use the repositories' methods for this purpose, but we recommend using a _TestEntityManager,_ as sometimes the repositories offer a limited set of operations. One of the possible scenarios might be a situation where we need to test a read-only repository, but we need to add records in the database in order to execute the tests successfully.

Moreover, we usually want to test each repository feature separately; therefore, using some of its functions as helper methods would affect the scope of the test. For instance, we may want to test a query method, but we would also be indirectly testing the persistence functionality. As a result, an error in this test could be misleading.

## Creating a Test Class

Now let's see the _@DataJpaTest_ annotation and _TestEntityManager_ class in action. We'll test our _ProjectRepository_ repository by creating a test class in the _com.baeldung.lsd.persistence.repository_ package:

```
@DataJpaTest
class ProjectRepositoryIntegrationTest {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    TestEntityManager entityManager;
    
    // ...
}
```

Note that we annotated the class, and injected the entity manager and repository we'll be testing.

## Testing the Repository Methods

A repository can offer various methods that allow us to perform different queries against our database. Let's demonstrate how we can test them.

### Testing the Insertion Operation

As we know, the repositories’ _save()_ method can handle two operations: database insert and update. First, let's see how to test the insertion:

```
@Test
void givenNewProject_whenSave_thenSuccess() {
    Project newProject = new Project("PTEST-1", "Test Project 1",  "Description for project PTEST-1");
    Project insertedProject = projectRepository.save(newProject);
    assertThat(entityManager.find(Project.class, insertedProject.getId()) ).isEqualTo(newProject);
}  
```

As we can see, we create a new transient _Project_ object, and then we call the repository's _save()_ method. After that, we can find the newly inserted object using the injected _TestEntityManager,_ and we expect it to be equal to the _newProject_ instance.

Let's run the test, and make sure that it passes. If we check the console log, among other messages we’ll see the following:

```
Hibernate: insert into project (id, code, description, name) values (null, ?, ?, ?)
// ...
o.s.t.c.transaction.TransactionContext : Rolled back transaction for test
```

We can see the SQL statements executed during the test, which can undoubtedly be helpful when we’re hunting query problems. In addition, we're informed that the test transaction will be rolled back, so that the inserted data won’t actually be saved in our database and affect other tests.

### Testing the Update Operation

Similarly, we can test the update functionality:

```
@Test
void givenProjectCreated_whenUpdate_thenSuccess() {
    Project newProject = new Project("PTEST-1", "Test Project 1",  "Description for project PTEST-1");
    entityManager.persist(newProject);
    String newName = "New Project 001";
    newProject.setName(newName);
    projectRepository.save(newProject);
    assertThat(entityManager.find(Project.class, newProject.getId()).getName()).isEqualTo(newName);
}
```

First, we prepare the _newProject_ instance and persist it, this time using the _entityManager_ bean. Then we change its name, and call the repository's _save()_ method. Finally, we verify that the name has been updated in the database.

### Testing the _findById_ Method

Now let’s test the _findById()_ method. First, we'll persist a new _Project_ object using the _TestEntityManager_ bean, like we did in the previous test. Then we’ll call the repository's _findById()_ method, and verify the returned value:

```
@Test
void givenProjectCreated_whenFindById_thenSuccess() {
    Project newProject = new Project("PTEST-1", "Test Project 1", "Description for project PTEST-1");
    entityManager.persist(newProject);
    Optional<Project> retrievedProject = projectRepository.findById(newProject.getId());
    assertThat(retrievedProject).contains(newProject);
}
```

### Testing Other _findBy..._ Queries

We can easily test any other read methods defined in our repository in the way described in the previous section. For example, in our repository, we defined the _findByNameContaining()_ method, which retrieves an _Iterable_ collection of entities, instead of an _Optional_. We can test it as follows:

```
@Test
void givenProjectCreated_whenFindByNameContaining_thenSuccess() {
    Project newProject1 = new Project("PTEST-1", "Test Project 1", "Description for project PTEST-1");
	  Project newProject2 = new Project("PTEST-2", "Test Project 2", "Description for project PTEST-2");
	  entityManager.persist(newProject1);
	  entityManager.persist(newProject2);
	  Iterable<Project> projects = projectRepository.findByNameContaining("Test");
	  assertThat(projects).contains(newProject1, newProject2);
}
```

### Testing the Delete Operation

Finally, let's test the _delete()_ method. Once again, we create a _Project_ entry in the database, and then we call our repository's _delete()_ method. After that, we verify if the data entry has been removed:

```
@Test
void givenProjectCreated_whenDelete_thenSuccess() {
    Project newProject = new Project("PTEST-1", "Test Project 1", "Description for project PTEST-1");
	  entityManager.persist(newProject);
	  projectRepository.delete(newProject);
	  assertThat(entityManager.find(Project.class, newProject.getId())).isNull();
}
```

You can check the codebase to see examples of tests for our other repositories.

## Resources
- [Testing in Spring Boot - Integration Testing With @DataJpaTest](https://www.baeldung.com/spring-boot-testing#integration-testing-with-datajpatest)
- [Spring Boot - Auto-configured Data JPA Tests](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing-spring-boot-applications-testing-autoconfigured-jpa-test)

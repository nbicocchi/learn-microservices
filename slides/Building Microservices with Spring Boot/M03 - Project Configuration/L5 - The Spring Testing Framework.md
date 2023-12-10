# The Spring Testing Framework

In this lesson, we'll have a look at basics of the Spring Testing framework.

The relevant module for this lesson is: [spring-testing-framework-lesson-end](https://github.com/nbicocchi/spring-boot-course/tree/module3/spring-testing-framework-lesson-end)

## Unit Testing and Integration Testing

We’ll need to start our discussion about the fantastic testing support in Spring with a quick context about what kind of testing we’re focusing on here. And, simply put, that’s [integration testing](https://martinfowler.com/bliki/IntegrationTest.html).

This is because our focus here is to explore and understand the Spring support for testing.

[Unit testing](https://en.wikipedia.org/wiki/Unit_testing) is a highly useful tool, both for pure testing but also as a design tool. Of course, the Spring framework is built with unit testing and [TDD](https://martinfowler.com/bliki/TestDrivenDevelopment.html) in mind.

But, **the actual Spring Testing Framework starts being useful as we go into integration testing**, so that will be our focus.

## Initial Setup

Let's add the Boot support and JUnit 5:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <scope>test</scope>
</dependency>
```

Let’s create the folder and package: _/src/test/java_ and _com.baeldung.ls.service_.

In the next part, we'll continue with creating a test class.

## Upgrade Notes

Since Boot 2.2, the Spring team has added support for JUnit 5 Jupiter into the _spring-boot-starter-test_ by default, and therefore there is no need to include the _junit-jupiter-engine_ dependency ourselves anymore.

Together with this, they also included a _junit-vintage-engine_ library, which supported running JUnit 4 tests using the JUnit 5 Jupiter platform. If we don't need to run JUnit 4 tests in our project, we can exclude the Vintage Engine transitive dependency:

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
  <exclusions>
      <exclusion>
          <groupId>org.junit.vintage</groupId>
          <artifactId>junit-vintage-engine</artifactId>
      </exclusion>
  </exclusions>
</dependency>
```

**Later on, on version 2.4, Spring finally removed the Vintage Engine library from the _spring-boot-starter-test_ dependencies. So, for newer versions, we don't have to explicitly exclude the library ourselves, but instead, we do have to include it if we want to support tests using JUnit 4 features.**

Now, let’s create a simple context test:

```
@SpringJUnitConfig
public class ContextIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void whenContextIsLoaded1_thenNoExceptions() {
        System.out.println();
    }

    @Test
    public void whenContextIsLoaded2_thenNoExceptions() {
        System.out.println();
    }
    
}
```

**We’re used the JUnit Jupiter support here via the high-level annotation _@SpringJUnitConfig,_ and wired in the _ApplicationContext_**_._

Finally, we created two simple tests, each containing a _sys out_ to be able to debug.

**Note that we're using the JUnit 5 version, so make sure to import the _@Test_ annotation from the _org.junit.jupiter.api_ package instead of _org.junit_.**

## Caching the Application Context

A core aspect of the test framework is managing the application context.

Bootstrapping a full context is quite an expensive operation. Because of this, we’re trying to avoid doing that in each and every test. **The framework** **helps** [**cache**](https://en.wikipedia.org/wiki/Cache_(computing)) **the** **context by default** and then provides higher-level control over that.

If we pay close attention to the application context instance here, we’ll notice that it’s the same instance between tests.

This is due to the caching of the context kicking in.

## An Integration Test

Let’s write one more test to verify some actual logic.

We'll add the _ProjectServiceIntegrationTest_ class which contains a test method that verifies the _save_ operation:

```
@SpringJUnitConfig
public class ProjectServiceIntegrationTest {

    @Autowired
    private IProjectService projectService;

    @Test
    public void whenSavingProject_thenOK() {
        Project savedProject = projectService.save(new Project("name", LocalDate.now()));
        assertThat(savedProject, is(notNullValue()));
    }

}
```

If we run this test now, we'll see an error. The issue here is that we’re trying to inject the project service but Spring doesn’t find that bean. And that's because we’re not actually scanning it.

So let's add a simple configuration class with an annotation that scans the entire _com.baeldung.ls_ package:

```
@ComponentScan("com.baeldung.ls")
public class TestConfig {

}
```

Now let's use the new config in the _SpringJUnitConfig_ annotation:

```
@SpringJUnitConfig(value = TestConfig.class)
public class ProjectServiceIntegrationTest {
    // ...
}
```

This is actually using the highly useful _@ContextConfiguration_ annotation which is how the info about scanning the package is passed to the Spring context.

Now our test will pass successfully.

## _@DirtiesContext_ (extra)

As we saw earlier, the Spring framework caches the context so that it's not reloaded for every test.

However, there might be some tests which may intend to modify the application context, leading to side effects on other tests.

In such cases we can **notify the framework to close and recreate the context for later tests**. This is done by using the _@DirtiesContext_ annotation at the test class or method level.

## _@ActiveProfiles_ (extra)

Spring provides a very convenient _@ActiveProfiles_ annotation, that we can apply at the class level to **specify the active profiles during the execution of the tests**:

```
@SpringJUnitConfig
@ActiveProfiles("dev")
public class ProjectServiceIntegrationTest {
    //...
}
```

## Writing a Unit Test (extra)

So far we've learnt how to write integration tests using Spring’s testing framework. The Spring framework initializes the application context while executing the tests which ensures that all the dependencies of the class under test are injected.

However, **if we want to write unit tests for a certain class instead, meaning a test in isolation, we need to mock its dependencies.**

We can do this by using a mocking framework which allows us to create mock objects dynamically and define their behaviour. There are many mocking frameworks available; for our example, we'll use Mockito.

**Mockito is a standalone framework outside of Spring, but it’s also pre-packaged within the _spring-boot-starter-test_ dependency, so we can use it directly in our project.**

Let's write a unit test for our _ProjectServiceImpl_ class using Mockito to mock its dependencies.

Let’s create our test class in the test folder:

```
public class ProjectServiceUnitTest { }
```

**Notice that we have not marked this test with the _@SpringJUnitConfig_ because we don't want the application context to be initialized.**

We will be managing the dependencies using Mockito instead.

The only dependency of _ProjectServiceImpl_ that we need to mock is the _projectRepository_.

**We can create a mock object for the _projectRepository_ bean by simply adding it as a field in our test class and marking it with Mockito’s _@Mock_ annotation:**

```
public class ProjectServiceUnitTest {

    @Mock
    IProjectRepository projectRepository;
}
```

Next, we need to create the object of the _ProjectServiceImpl_ class that we need to test.

Mockito provides a convenient _@InjectMocks_ annotation that creates the target object and injects the _@Mock_ marked dependencies into it.

Method _initMocks()_ annotated with _@BeforeEach,_ calls _MockitoAnnotations.initMocks(this)_ to initialize the mocks to run before each test:

```
public class ProjectServiceUnitTest {

    @Mock
    IProjectRepository projectRepository;

    @InjectMocks
    ProjectServiceImpl projectService;
    
    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
```

Alright, the setup is done, we can write our test now:

```
public class ProjectServiceUnitTest {
    //...
    @Test
    public void whenSavingProject_thenOK() {
        Project project = new Project("name", LocalDate.now());
        when(projectRepository.save(project)).thenReturn(project);

        Project savedProject = projectService.save(project);

        assertNotNull(savedProject);
    }
}
```

Notice the _when(...).thenReturn(...)_ pattern on the second line. Mockito provides a fluent API to define what to return when a certain method is called on the mocked object.

Here, **we are telling the framework to return the _project_ object when the _projectRepository.save(project)_ is invoked from the _ProjectServiceImpl_ class.** This is how we are isolating our test by not actually calling the _projectRepository_ but by mocking its behavior.

## _@MockBean_ (extra)

**We can also mock the beans in integration tests by supplying mock objects to the application context.**

**This can be done by using Spring’s _@MockBean_ annotation:**

```
@SpringJUnitConfig(value = TestConfig.class)
public class ProjectServiceIntegrationTest {
	
    @MockBean
    IProjectRepository projectRepository;

    // ...
}
```

Since this is an integration test, we’re using the _@SpringJUnitConfig_ annotation here, compared to the unit test example seen before.

Since we want to mock the _projectRepository_ bean in the application context, we have marked it with the _@MockBean_ annotation.

**On execution, Spring would replace the actual _projectRepository_ bean present in the context, with a mocked bean.**

This annotation is useful in integration tests where a particular bean, for example an external service, needs to be mocked.

## _Mockito_ With the Junit 5 Extension Model (extra)

We can simplify the mock initialization and injection process by making use of JUnit’s Extension Model and the corresponding _MockitoExtension_ class that _Mockito_ provides to this end:

```
@ExtendWith(MockitoExtension.class)
public class ProjectServiceAlternativeUnitTest {

    @Mock
    IProjectRepository projectRepository;

    @InjectMocks
    ProjectServiceImpl projectService;

    @Test
    public void whenSavingProject_thenOK() {
        Project project = new Project("name", LocalDate.now());
        when(projectRepository.save(project)).thenReturn(project);

        Project savedProject = projectService.save(project);

        assertNotNull(savedProject);
    }
}
```

## Upgrade Notes

In newer Mockito versions (i.e. versions used since Spring Boot 2.4 onwards), the _MockitoAnnotations#initMocks_ directive has been deprecated in favor of _openMocks_.

We can call the _openMocks_ method to initialize annotated fields just as we did with _initMocks_. However, this new method will retrieve an _AutoCloseable_ object which we can hold to release the resources afterwards:

```
public class ProjectServiceUnitTest {
    //...

    private AutoCloseable closeable;

    @BeforeEach
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }
    
    //...
}
```

We necessarily need to close the initialization when we use static mocks to avoid the misbehavior of the injected mocks.

## Resources
- [Integration Testing in Spring](https://www.baeldung.com/integration-testing-in-spring)
- [The SpringJUnitConfig and SpringJUnitWebConfig Annotations in Spring 5](https://www.baeldung.com/spring-5-junit-config)
- [Spring Reference - Testing](https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html)
- [Mockito.mock() vs @Mock vs @MockBean](https://www.baeldung.com/java-spring-mockito-mock-mockbean)
- [A Quick Guide to @DirtiesContext](https://www.baeldung.com/spring-dirtiescontext)

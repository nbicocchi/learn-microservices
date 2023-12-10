# Testing with Boot

In this lesson, we’re going to focus on what testing support Spring Boot has, on top of the testing support in Spring, which we already discussed earlier.

The relevant module for this lesson is: [testing-with-boot-end](https://github.com/nbicocchi/spring-boot-course/tree/module6/testing-with-boot-end)

## The Spring Boot Test Starter

**We can enable test support in Spring Boot by including the _spring-boot-starter-test_ dependency.**

This is a powerful dependency, and loads the core modules _spring-boot-test_ and _spring-boot-test-autoconfigure._

It also pulls in test modules and libraries such as _JUnit_, _AssertJ_, _Hamcrest_, _Mockito_ etc. to get us started quickly with writing tests.

We can open up our pom and see the main dependency we need: _spring-boot-starter-test,_ as we added this in our previous testing lesson. Also, we can check the dependencies it pulls in by opening the Eclipse dependency hierarchy.

## The _@SpringBootTest_ Annotation

This is the primary annotation we’ll use to create the Application context which we’ll need for our test.

This is, in some ways, similar to the _@SpringJUnitConfig_ annotation from Spring Core.

However, unlike _@SpringJUnitConfig_, where we have to define configuration classes to be used for the test, in _@SpringBootTest_ we don’t need to define any such configurations. **It automatically detects our primary configuration by searching through a class annotated with _@SpringBootApplication_ or _@SpringBootConfiguration._**

This annotation goes even a step further. With it, the test basically tries to recreate the Spring Boot features that come into play when bootstrapping an application, for example, loading external configurations like the _application.properties_ files.

Let’s create a simple integration test for our DAO, called _ProjectRepositoryIntegrationTest_ and let’s annotate it with _@SpringBootTest._

We'll also autowire our _projectRepository_ and add a simple test to assert save of a _Project_:

```
@SpringBootTest
public class ProjectRepositoryIntegrationTest {

    @Autowired
    private IProjectRepository projectRepository;

    @Test
    public void givenNewProject_thenSavedSuccess() {
        Project newProject = new Project("First Project", LocalDate.now());
        assertNotNull(projectRepository.save(newProject));
    }

}
```

If we run the test and check the logs, we'll see that the Application context is loaded and then our tests are executed.

**Note that we're using the JUnit 5 version, so make sure to import the _@Test_ annotation from the _org.junit.jupiter.api_ package instead of _org.junit_.**

Next we're going to explore how we can configure the behaviour of our tests with _@SpringBootTest_ using its _webEnvironment_ attribute.

## The _webEnvironment_ Attribute

A core aspect of our integration tests is how they handle the web environment.

They may be running with a full web environment, which is expensive, but if we need that, then it’s important we have it.

They may also be mocking that, since it’s a lot quicker and is enough in a lot of tests.

Finally, they may simply run without any web environment at all if the test isn’t really focused on that.

**We can control this run by changing the _webEnvironment_ attribute of the _@SpringBootTest_ annotation.**

The default value of this attribute is _MOCK_, which provides the default behaviour for our tests, i.e. loads a web Application context.

For example, in our persistence test here, we can actually set this to _NONE_ since we’re not really focused on the web layer here.

In order to understand the other possibilities, we’ll need a test focused on the web.

Let’s create a simple Controller test:

```
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProjectControllerIntegrationTest {
    // ..
}
```

This will create a real web environment with servers started and listening to a random port.

We can run the test and check in the logs that Tomcat started on a random port:

_Tomcat started on port(s): 61011 (http) with context path ''_

**We can also define a specific port to run on:**

```
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProjectControllerIntegrationTest {
    // ..
}
```

The servers will now listen to default port _8080_ or the one defined in _application.properties._

Let's run the test again and check the logs:

_Tomcat started on port(s): 8080 (http) with context path ''_

Finally, if the focus of the test has nothing to do with the web, we can simply disable the web layer entirely with the _NONE_ option:

```
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ProjectRepositoryIntegrationTest {
    // ...
}
```

Now, only the core application context is loaded without any web support.

## Testing the Web Layer with _@WebMvcTest_ Annotation

Under the hood, _@SpringBootTest_ does a lot of work for us: it loads the complete Application context and sets up the web environment for our test.

But, in some cases, for example when we're testing only web layer components such as a _Controller_, _Converter_, _Filter_ etc, we don't need a fully loaded web Application context.

Bootstrapping the full application context can be an expensive operation; not necessarily for us, since our application is still simple, but for a more complex application, it’s not uncommon for that process to take a full minute or more.

If we’re specifically focused on the web layer of the application, the full application context is simply not needed.

In these cases, instead of _@SpringBootTest,_ **we can use the _@WebMvcTest_ annotation that only loads the web layer for us**. It also auto-configures the _MockMvc_ testing framework for us.

Let’s write a test for our _ProjectController_ using _@WebMvcTest._ We'll autowire _MockMvc_ and add a simple test:

```
@WebMvcTest
public class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenProjectExists_thenGetSuccess() throws Exception {

        mockMvc.perform(get("/projects/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name").value("testName"));
    }
}
```

Finally, we can run the test and check that it's successful.

Now, keep in mind that the Web MVC testing support isn’t our core focus here, so we won’t discuss that in this lesson.

What we care about here is seeing how, with this new annotation only a subset of our application context gets bootstrapped.

So let’s actually have a breakpoint in our service and our controller and debug to see which one fires and which one doesn’t. If we debug the test, we'll see how the controller bean is instantiated, but the service bean is not.

## Boot Highlight

As we've seen, once the _spring-boot-starter-test_ dependency is included, Spring Boot configures the testing environment. When we annotate the test classes with the _@SpringBootTest_, it also configures the test context of the application.

In a non-Boot Spring application, we would have to configure the test context manually by adding the following annotations to the test classes:

```
@ContextConfiguration(classes = { ... })
@WebAppConfiguration
```

## Resources
- [Testing in Spring Boot](https://www.baeldung.com/spring-boot-testing)


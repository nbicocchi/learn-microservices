# Intro to the RestTemplate

In this module we’re going to see how we can consume our REST API using an HTTP client.

We’re going to be looking at a really common and powerful option to drive our API client: Spring's _RestTemplate_ class_._

Keep in mind that even though we're using Spring both to implement our API and now to consume it, it certainly doesn’t have to be the case. Since this is REST, the API side of things can be implemented with pretty much any language or technology.

The relevant module for this lesson is: [intro-to-the-rest-template-end](https://github.com/nbicocchi/spring-boot-course/tree/module8/intro-to-the-rest-template-end).

## Adding _RestTemplate_

At this point we have all the Spring web dependencies we need on our classpath. Therefore, _RestTemplate_ is already available and we can start using it right away to consume the REST API we've already created.

We’ll, of course, consume _Project_ resources. More specifically, we'll make use of the _findOne_ and the _create_ endpoints we defined previously:

```
@GetMapping(value = "/{id}")
public ProjectDto findOne(@PathVariable Long id) {
    Project entity = projectService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return convertToDto(entity);
}

@PostMapping
public void create(@RequestBody ProjectDto newProject) {
    Project entity = convertToEntity(newProject);
    entity.setDateCreated(LocalDate.now());
    this.projectService.save(entity);
}
```

We’re going to deploy our application and just treat it as a black-box, exposing a clearly defined API.

## Live Testing with _RestTemplate_

Let’s write a simple Spring Boot Test to hit our running API.

**We'll need a _RestTemplate_ instance_._** Usually, we’d define this as a bean and inject it, but here, to keep things simple, we'll just initialize it on its declaration.

We'll also define the URL of our running API:

```
@SpringBootTest
public class ProjectRestAPILiveTest {
    
    private static final String BASE_URL = "http://localhost:8080/projects/";
    private RestTemplate restTemplate = new RestTemplate();
    
    // tests...
    
}
```

**Note:** Since we're creating the _RestTemplate_ object here, we don't technically need the _@SpringBootTest_ annotation, though this is useful if we're injecting beans in the test.

In our first test here we’re going to send a GET request to _/projects/{id}_.

We're finally at the point where **we can use the** **_RestTemplate -_ and more precisely its** **_getForEntity_ API** - to call the endpoint.

This takes the URL as the first param and the response type class as the next one, and retrieves a _ResponseEntity_ object:

```
@Test
public void givenProjectExists_whenGet_thenSuccess() {
    ResponseEntity<ProjectDto> response = restTemplate.getForEntity(BASE_URL + "1", ProjectDto.class);

    // assertions ...
}
```

With the full response available let’s check its status code -making sure it’s a _200 OK_\- and that the actual resource we received isn't _null_:

JAVA

```
assertThat(response.getStatusCodeValue(), Matchers.equalTo(200));
assertNotNull(response.getBody());
```

Let’s run the test and see that everything’s working.

As you can see, this is a really clean and simple API, but quite powerful.

If we now debug and have a closer look at **the _ResponseEntity_ object we’re actually getting here we'll see our full resource**. We only asserted this isn’t null but, naturally, we could assert a lot more. We can also notice we’re getting some lower-level HTTP information about the response, in case we need that to make assertions.


## Consuming a POST API with _RestTemplate_

Next, let’s add a test for our _create_ endpoint, which is defined as _POST /projects._

**For any POST API we can use the _postForEntity_ method**, which takes three parameters:

1.  the URL
2.  the actual body we want to send
3.  the response type class we are expecting

For the actual test body, we'll create a new _ProjectDto._ Afterwards, we'll be able to make the call and retrieve the corresponding _ResponseEntity:_

```
@Test
public void givenNewProject_whenCreated_thenSuccess() {
    ProjectDto newProject = new ProjectDto(1L, "First Project", LocalDate.now());
    ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL, newProject, Void.class);
    
    // assertions...
}
```

Here the response type is _Void.class_ because our API does not return any response.

Finally, we'll add the assertion to verify if the response is successful:

```
assertTrue(response.getStatusCode() == HttpStatus.OK);
```

We can now run the test to see if everything is working as expected.

## Advantages of _RestTemplate_ (extra)

Let’s briefly look at some advantages of the _RestTemplate_.

**_RestTemplate_ offers a higher-level abstraction over the underlying HTTP client libraries.** It can be configured to use different HTTP clients such as _OkHttp_, _Unirest_, _Apache_ _HttpComponents_, etc.

**Under the hood it performs content type detection, _URI_ encoding and HTTP message conversion to and from objects using _HttpMessageConverters._**

However, it’s important to note that in future versions of Spring, _RestTemplate_ will be deprecated in favor of the new [_WebClient_](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html) which offers the same features and adds an efficient, non-blocking approach.

## Resources
- [The Guide to RestTemplate](https://www.baeldung.com/rest-template)
- [RestTemplate Post Request with JSON](https://www.baeldung.com/spring-resttemplate-post-json)

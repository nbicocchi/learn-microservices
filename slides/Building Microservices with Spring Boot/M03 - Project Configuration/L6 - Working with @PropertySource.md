# Working with @PropertySource

In this lesson, we'll focus on the _@PropertySource_ annotation.

The relevant module for this lesson is: [working-with-property-source-end](https://github.com/nbicocchi/spring-boot-course/tree/module3/working-with-property-source-end)

## Main and Test Property Sources

Simply put, **_PropertySource_ is an annotation used to configure additional sources of properties** - i.e. additional property files - for our Spring Environment.

Similarly, _TestPropertySource_ is an annotation that can be used to configure property file sources for our tests. A property defined using this testing-focused annotation will have a higher priority than the same property defined in main property sources.

## Usage

As we already know, in a Spring Boot application the default property file is _application.properties._

With the _@PropertySource_ annotation we can define additional properties files. This will help us structure our properties better, and can be used in any configuration class.

In order to see how it works, **we'll create a custom property file called _additional.properties_ in the resource folder** (_src/main/resources):_

```
additional.info=Additional Info
```

**And introduce this property source in our _AppConfig_ class by using the _@PropertySource_ annotation:**

```
@PropertySource("classpath:additional.properties")
@Configuration
public class AppConfig {
  
    // ...
    
}
```

With this configuration, we can inject the newly defined property in our beans using the _@Value_ annotation.

In this case, in order to demonstrate that the value has been injected, we'll log its value as soon as the application starts:

```
public class LsApp {

    private static final Logger LOG = LoggerFactory.getLogger(LsApp.class);

    @Value("${additional.info}")
    private String additional;

    // ...

    @PostConstruct
    public void postConstruct() {
        LOG.info("Additional Property {}", additional);
    }

}
```

Now, when we start the application, we'll see that the correct property value gets logged.

If we’re developing a Spring application without the Spring Boot support, then this annotation is needed in order to register all property files.

## Test-Specific Properties

Often, we need to **define a different set of properties for testing purposes**. This is where the _@TestPropertySource_ annotation comes into play.

Let's start by creating the test class, using the @_SpringJUnitConfig_ annotation to load the Spring context in our test cases and **the _@TestPropertySource_ with the location of our test property file**:

```
@SpringJUnitConfig(value = LsApp.class)
@TestPropertySource(locations = "classpath:test.properties")
public class TestPropertySourceTest {
  
    // ...
    
}
```

Note: the _application.properties_ files won't have any effect here because we're not invoking Spring Boot features in this test, we'll see that in a future lesson.

Now we'll create this properties file with a new property:

```
testproperty=Test Property Value
```

Now we can inject this property into a _String_ variable and assert that it's receiving the expected value:

```
@Value("${testproperty}")
private String testproperty;

@Test
public void whenTestPropertySource_thenValuesRetreived() {
    assertEquals("Test Property Value", testproperty);
}
```

## Precedence

An important thing to mention here is that, since we're using the _@SpringJUnitConfig_ annotation, we can access properties defined in main property sources.

But, **if a property is redefined in test resources, then this value will have higher priority than the value specified in the main source**.

For example, let's try using the property we defined earlier, and assert its value:

```
@Value("${additional.info}")
private String additional;

@Test
public void whenPropertyDefinedInMain_thenValuesRetrieved() {
    assertEquals("Additional Info", additional);
}
```

This test will pass successfully at this point. Anyway, if we redefine the property in the _test.properties_ file:

```
additional.info=Additional Info From Test
```

The test will fail, and we'll have to change the assertion to make it pass successfully:

```
assertEquals("Additional Info From Test", additional);
```

## _PropertySourcesPlaceholderConfigurer_ (extra)

Let's dig a bit deeper to understand how values of the properties defined in a bean are injected.

During the context initialization, the container invokes a _PropertySourcesPlaceholderConfigurer_ which is applied to the bean definition and replaces the placeholders like _${additional.info}_ with their actual values.

The _PropertySourcesPlaceholderConfigurer_ searches for these placeholder keys in all the property sources present in the _Environment_.

## Resources
- [Properties with Spring and Spring Boot](https://www.baeldung.com/properties-with-spring)
- [Annotation Type PropertySource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/PropertySource.html)
- [Externalized Configuration Precedence with Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)

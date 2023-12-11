# Spring Boot autoconfiguration

In this lesson, we'll understand the Spring Boot autoconfiguration mechanism.

The relevant module for this lesson is: [boot-auto-configuration-end](https://github.com/nbicocchi/spring-boot-course/tree/module4/boot-auto-configuration-end)

## Boot autoconfiguration

**Spring Boot simplifies development with a core, versatile mechanism called autoconfiguration.** This is based on a number of aspects, such as properties, beans and [classpath](https://en.wikipedia.org/wiki/Classpath_(Java)) dependencies.

What’s powerful about this mechanism is that it intelligently backs off when we add our own configuration.

Like many other things in Spring, the mechanism is driven by annotations.

**Boot relies on the_@Conditional_annotation and on a number of variations to drive autoconfiguration:**

-   _@ConditionalOnClass,_
-   _@ConditionalOnMissingClass,_
-   _@ConditionalOnBean,_
-   _@ConditionalOnMissingBean_

These annotations allow us to define configuration based on runtime conditions.

Let's have a look at an example of an autoconfiguration, the_JacksonAutoConfiguration_class:

```
@Configuration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonAutoConfiguration {
    
    // ...
}
```

Notice this is a standard_@Configuration_class with an extra annotation: _@ConditionalOnClass_ . As the name suggests, this will only be enabled if the_ObjectMapper_class is on the classpath.

So, simply put, if Jackson isn’t on the classpath of our project, this entire configuration class will do nothing.

The Jackson library is brought in by the standard Boot web starter in our application.

Let’s add a breakpoint in the _jacksonObjectMapper_ and debug the application. We'll see that this breakpoint fires, which means the autoconfiguration is loaded.

## Overriding the autoconfiguration

Notice that some of the beans, like the _ObjectMapper_ here, have one more annotation: _@ConditionalOnMissingBean_

This is a critical aspect of Boot autoconfiguration, the backing-off we talked about earlier.

Simply put, **if we add our own _ObjectMapper,_ then this bean definition will become disabled and our own bean will take priority**.

Let’s define our own _ObjectMapper_ bean and verify that, once we do, this default bean will no longer be created by Spring:

```
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
```

If we add a breakpoint in this method and debug the application, we can see our bean is now created and the previous breakpoint no longer fires.

Therefore, the Boot autoconfiguration has intelligently backed off.

## Logging autoconfiguration Information

We can also check the auto-config info that’s enabled at startup.

**Let’s configure the DEBUG log level for the_boot.autoconfigure_package:**

```
logging.level.org.springframework.boot.autoconfigure=DEBUG
```

The autoconfiguration logging report will contain information on:

- _Positive matches_ - autoconfigurations that are enabled as their condition was matched
- _Negative matches_ - autoconfiguration classes with conditions evaluated to false, which remain disabled
- _Exclusions_ - classes we exclude
- _Unconditional classes_ - configurations without conditions

If we search for the _jacksonObjectMapper_ bean from the _JacksonAutoConfiguration_ class in the report, we'll see this was not created, as the_ConditionalOnMissingBean_condition was false.

This report is very useful to understand the configuration enabled in a Spring Boot project and troubleshoot any configuration issues.

## Boot Highlight

In this lesson, we explained how Spring Boot configures the application context. If we're not using Boot, we'd have to perform those steps manually.

As mentioned, the whole "magic" of Spring Boot is based on the _@ConditionalOn_ annotations. It uses them in order to control whether various useful artifacts are present in the project (on the classpath) or not. If present, they get used, otherwise their alternatives are used.

## The _@EnableAutoConfiguration_ Annotation (extra)

As the name suggests,**the _@EnableAutoConfiguration_ annotation is used to enable autoconfiguration ina Spring Boot application** which automatically applies autoconfiguration beans if they are found on the classpath.

It also allows excluding certain autoconfiguration classes using the_exclude_property.

Previously we saw that_@SpringBootApplication_also does the same so what is the difference between these two annotations?

Basically, _@SpringBootApplication_ is a combination of the annotations _@EnableAutoConfiguration_ , _@ComponentScan_ and _@Configuration_ thus it simplifies development by reducing the number of annotations needed to bootstrap the context.

You should only choose and add one of these two annotations to your primary _@Configuration_ class.

## The _spring.factories_ File (extra)

When Spring Boot starts up it looks for a file named _spring.factories_ on the classpath. This file contains, among others, a list of autoconfiguration classes that Spring Boot will try to enable.

Let's look at a snippet of this file from the spring-boot-autoconfigure project:

```
# Auto Configure
...
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
```

Based on this, Spring Boot will try to enable the autoconfiguration classes for _Jackson_ , _DataSource_ and _JdbcTemplate_ if the conditions for each are met.

For example, if the classes for _Jackson_ are present on the classpath, then the _JacksonAutoConfiguration_ will run and all the Jackson related beans will be initialized.

**The _spring.factories_ file is useful if we want to define our own custom autoconfiguration.**

## Using _CommandLineRunner_ (extra)

**_CommandLineRunner_ is an interface which indicates that a bean should run when it's found in a Spring Application.**

It contains only one method _run()_ which is called when the application context has been loaded.

We can define multiple _CommandLineRunner_ beans in the same application context and order them with the_@Order_annotation.

Let’s see an example of implementing this interface in our _LsApp_ class and overriding the _run()_ method to save and log a project:

```
@SpringBootApplication
public class LsApp implements CommandLineRunner {

   private static final Logger LOG = LoggerFactory.getLogger(LsApp.class);

   @Autowired
   private IProjectService projectService;

   public static void main(final String... args) {
       LOG.info("STARTING THE APPLICATION");
       SpringApplication.run(LsApp.class, args);
       LOG.info("APPLICATION STARTUP FINISHED");
   }

   @Override
   public void run(String... args) {
       projectService.save(new Project(1L,"Project 1", LocalDate.now()));

       Optional<Project> project = projectService.findById(1L);

       LOG.info("Project {}", project.toString());
   }
}
```

## Resources
- [Spring Boot autoconfiguration](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-autoconfiguration.html)
- [Create a Custom autoconfiguration with Spring Boot](https://www.baeldung.com/spring-boot-custom-autoconfiguration)

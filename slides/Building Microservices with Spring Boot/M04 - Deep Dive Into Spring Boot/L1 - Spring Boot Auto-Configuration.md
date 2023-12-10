# Spring Boot autoconfiguration

In this lesson, we'll understand the Spring Boot autoconfiguration mechanism.

The relevant module for this lesson is:[boot-auto-configuration-end](https://github.com/nbicocchi/spring-boot-course/tree/module4/boot-auto-configuration-end)

## Boot autoconfiguration

**Spring Boot simplifies development with a core, versatile mechanism called autoconfiguration.**This is based on a number of aspects, such as properties, beans and[classpath](https://en.wikipedia.org/wiki/Classpath_(Java))dependencies.

What’s powerful about this mechanism is that it intelligently backs off when we add our own configuration.

Like many other things in Spring, the mechanism is driven by annotations.

**Boot relies on the_@Conditional_annotation and on a number of variations****to drive autoconfiguration:**

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

Notice this is a standard_@Configuration_class with an extra annotation:_@ConditionalOnClass._As the name suggests, this will only be enabled if the_ObjectMapper_class is on the classpath.

So, simply put, if Jackson isn’t on the classpath of our project, this entire configuration class will do nothing.

The Jackson library is brought in by the standard Boot web starter in our application.

Let’s add a breakpoint in the_jacksonObjectMapper_and debug the application. We'll see that this breakpoint fires, which means the autoconfiguration is loaded.

## Overriding the autoconfiguration

Notice that some of the beans, like the_ObjectMapper_here, have one more annotation:_@ConditionalOnMissingBean._

This is a critical aspect of Boot autoconfiguration - the backing-off we talked about earlier.

Simply put,**if we add our own_ObjectMapper,_then this bean definition will become disabled and our own bean will take priority**.

Let’s define our own_ObjectMapper_bean and verify that, once we do, this default bean will no longer be created by Spring:

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

-   _Positive matches_\- autoconfigurations that are enabled as their condition was matched
-   _Negative matches_- autoconfiguration classes with conditions evaluated to false, which remain disabled
-   _Exclusions_- classes we exclude
-   _Unconditional classes_- configurations without conditions

If we search for the_jacksonObjectMapper_bean from the_JacksonAutoConfiguration_class in the report, we'll see this was not created, as the_ConditionalOnMissingBean_condition was false.

This report is very useful to understand the configuration enabled in a Spring Boot project and troubleshoot any configuration issues.

## Boot Highlight

In this lesson, we explained how Spring Boot configures the application context. If we're not using Boot, we'd have to perform those steps manually.

As mentioned, the whole "magic" of Spring Boot is based on the_@ConditionalOn\*_annotations. It uses them in order to control whether various useful artifacts are present in the project (on the classpath) or not. If present, they get used, otherwise their alternatives are used.

## The_@EnableAutoConfiguration_Annotation (extra)

As the name suggests,**the_@EnableAutoConfiguration_annotation is used to enable autoconfiguration ina Spring Boot application**which automatically applies autoconfiguration beans if they are found on the classpath.

It also allows excluding certain autoconfiguration classes using the_exclude_property.

Previously we saw that_@SpringBootApplication_also does the same so what is the difference between these two annotations?

Basically,**_@SpringBootApplication_is a combination of the annotations_@EnableAutoConfiguration_,_@ComponentScan_and_@Configuration_**thus it simplifies development by reducing the number of annotations needed to bootstrap the context.

You should only choose and add one of these two annotations to your primary_@Configuration_class.

## The_spring.factories_File (extra)

When Spring Boot starts up it looks for a file named_spring.factories_on the classpath. This file contains, among others,a list of autoconfiguration classes that Spring Boot will try to enable.

Let's look at a snippet of this file from the_spring-boot-autoconfigure_project:

```
# Auto Configure
...
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
```

Based on this, Spring Boot will try to enable the autoconfiguration classes for_Jackson_,_DataSource_and_JdbcTemplate_if the conditions for each are met.

For example, if the classes for_Jackson_are present on the classpath, then the_JacksonAutoConfiguration_will run and all the Jackson related beans will be initialized.

**The_spring.factories_file is useful if we want to define our own custom autoconfiguration.**

## Using_CommandLineRunner_(extra)

**_CommandLineRunner_is an interface which indicates that a bean should run when it's found in a Spring Application.**

It contains only one method_run()_which is called when the application context has been loaded.

We can define multiple_CommandLineRunner_beans in the same application context and order them with the_@Order_annotation.

Let’s see an example of implementing this interface in our_LsApp_class and overriding the_run()_method to save and log a project:

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

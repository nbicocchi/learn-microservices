# The Spring Expression Language (SpEL)

In this lesson, we’re going we're going to focus on Spring Expression Language or _SpEL_.

The relevant module for this lesson is: [the-spring-expression-language-end](https://github.com/nbicocchi/spring-boot-course/tree/module8/the-spring-expression-language-end).

## Spring Expression Language

According to the official docs, **_SpEL_ is quite a powerful expression language which basically supports querying and manipulating the full** [**object graph**](https://en.wikipedia.org/wiki/Object_graph) **at runtime**.

Let’s have a look at the syntax of a Spring expression. An _SpEL_ expression begins with a hash/pound symbol _#_ followed by an expression enclosed in curly braces _{}_.

## Adding _SpEL_ Support

_SpEL_ support is provided by _spring-expression_ library which is already present in our starter. In fact, if we inspect the dependency tree of our project, we may see that _spring-expression_ is a part of _spring-boot-starter-web_. So, we have the nesessary dependency automatically.

## Basic _SpEL_ Expressions

In order to test out our expressions, let's create a new package _com.baeldung.ls.spel_ in which we add a simple bean _SpELBeanA_:

```
@Component
public class SpELBeanA {

}
```

**We’ll explore all of our expressions by making good use of the _@Value_ annotation.**

To start, let’s look at **a few expressions that do basic arithmetic**:

```
@Value("#{2+3}")
private Integer add;
```

The value of this expression should naturally, evaluate to 5. In order to check this, we’re going to create a _JUnit_ test.

First, in _src/test/java_ folder, we'll create a package _com.baeldung.ls.spel_ and then add a class _SpELTest:_

```
@SpringBootTest
public class SpELTest {
    @Autowired
    private SpELBeanA spELBeanA;

    @Test
    public void whenSpELBeanA_thenAllResolvedCorrectly() {
        assertNotNull(spELBeanA);
    }
}
```

The test checks that the bean that we injected using the _@Autowired_ annotation is created correctly.

Let's run the test and check that it passes. After that, let’s add a breakpoint on the line with the _assertNotNull_ statement and run the test in debug mode. When the execution stops at the breakpoint, we inspect the state of _spELBeanA_ and we can easily see that resolved value of property _add_ is 5.

Similarly, **we can also add two strings**:

```
@Value("#{'Learn ' + 'Spring'}")
private String addString;
```

If we run the test in debug mode again (with the breakpoint still present), then inspecting the state of the been we find that _addString_ property evaluates to _Learn Spring_.

**We can** **also do logical operations:**

```
@Value("#{2 == 2}")
private boolean equal;

@Value("#{3 > 2 ? 'a' : 'b'}")
private String ternary;
```

Naturally, when running the test in debug mode, we find that property _equal_ resolves to _true_, while _ternary_ resolves to _a_.

## Accessing Bean in _SpEL_ Expression

_SpEL_ allows us to perform more complicate operations as well. For example, **we can access properties from other beans**.

To illustrate the idea, let’s create another bean _SpELBeanB_:

```
@Component
public class SpELBeanB {

    private Integer prop1=10;

    public Integer getProp1() {
        return prop1;
    }
}
```

in which we declare and initialize a private property _prop1._ We've also added the standard getter for this property.

Now, let's come back to our first bean _SpELBeanA_ and check that _SpEL_ will allow us to use _SpELBeanB_'s property:

```
@Value("#{spELBeanB.prop1}") 
private String otherBeanProperty;
```

Again, when we run test in debug mode, we may check that this property evaluates to 10, exactly as the value of _prop1_ in _SpELBeanB_.

## Boot Highlight

Once the _spring-boot-starter-web_ dependency is included, it brings in the _spring-webmvc_ one which in its turn brings _spring-expression_ dependency into the project. If we build a web application using pure Spring, we'll get the _spring-expression_ dependency by including _spring-webmvc_.

## Difference Between ‘#’ and ‘$’ in _@Value_ Expressions (extra)

The _@Value_ annotation also accepts expressions of the of the form “${...}”:

```
@Value(“${additional.prop}”)
String property;
```

However, this is different from using a _‘#’._ **_“${...}”_  is just a property placeholder syntax, used to de-reference properties**, as we've seen in the “Working with Properties” lesson.

Whereas **_“#{...}”_ is a SpEL syntax, capable of evaluating various expressions** that we've seen throughout this lesson.

## Resources
- [Spring Expression Language Guide](https://www.baeldung.com/spring-expression-language)

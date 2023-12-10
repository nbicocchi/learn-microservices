# Intro to AOP in Spring

In this lesson, we’re going to focus on Aspect Oriented Programming, or AOP, in Spring.

The relevant module for this lesson is: [spring-aop-in-practice-end](https://github.com/nbicocchi/spring-boot-course/tree/module8/spring-aop-in-practice-end)

## Aspect-Oriented Programming (AOP)

AOP is, in a way, a different programming paradigm in Java.

Simply put, **it** **allows us to isolate common (also called "cross-cutting") concerns in a cleaner way**, to decrease tight-coupling and code duplication.

It does so by adding additional behavior to existing code, without directly modifying the code.

Instead, we declare the new behavior separately and we’re coming from the outside using Aspects and some infrastructure around these aspects.

## Why AOP?

Whenever you’re considering using AOP to implement some functionality, the open question is why use AOP and not just add the code manually?

While that approach may seem simpler at first, it actually has a number of downsides.

One - we’d have to do it manually on each and every method. This can be feasible when we only have a single service like in our example, but it’s a lot of work and a lot of code repetition when you have tens, or hundreds of service beans in your application.

So, **the more complex the application, the more pulling out the common implementation aspects into aspects makes more sense**.

The [open-closed principle](https://stackify.com/solid-design-open-closed-principle/) is a good one to revisit when thinking about this.

Another core aspect here is that **we want to keep our business logic clean** of things like transaction support, tracing and auditing, exception handling, security and so on.

All of this infrastructure logic is important and, also very important, even if you’re not implementing anything yourself with AOP, is that this logic is already implemented using AOP in Spring.

That’s actually one of the core tools that Spring uses to allow us to focus on our business logic and keep the infrastructure code separate from our logic.

And that’s a primary use of AOP: pulling each of these concerns into separate aspects to leave our business logic clean and separate from them.

Finally, it’s a matter of **complexity.** As we have more and more of these cross-cutting concerns and we’re adding them to the logic of our services, we’re adding in complexity.

So, understanding the service, testing it, making changes to it, becomes correspondingly more and more complex and expensive to do.

With AOP, these concerns don’t add any complexity to the service itself. All of the complexity is in this separate, AOP implementation off to the side.

So, this is what Aspect Oriented Programming solves. In very simple terms, we end up with cleaner and more maintainable code.

## AOP Components

Let's start by going through the main components in AOP:

-   the **_Aspect_** is, simply put, the actual cross-cutting logic that we want to add
-   the **Join Point** - the point during execution of a program where we can hook in our logic to run
-   the **Point Cut** \- a way to match one or more join points and, as a result, determine **where** the logic is going to actually run
-   the **Advice** - this is the infrastructure around a join-point that runs the Aspect at that Join-Point; in practical terms, this is an interceptor

**AOP support is already part of the basic web and data starters** that we’re already using.

We can still have a quick look at the dependency by opening the dependency hierarchy in the IDE and searching for 'aop'.

Next, we'll implement a simple cross-cutting concern to start understanding how AOP works in Spring.

**A common such cross-cutting concern and the one we’ll be using here is logging** the usage patterns of our codebase.

Let’s say we’re interested in logging and getting visibility into the usage of our service beans, or, even more granularly, the usage of a specific operation out of a specific service bean.

Let’s focus, for the sake of our example on the _ProjectServiceImpl_ and the _findById_ method.

We’re going to log the requests and the parameters via AOP.

## The Aspect

First, we'll introduce our _Aspect_ class. This is a regular Java class that will contain all our aspect related code:

```
@Aspect @Component public class ProjectServiceAspect { }
```

**To register the class as an aspect, we need to annotate this class with _@Aspect_** and, of course, make it a _@Component._

The join point, in our case, is the _findById_ method in our _ProjectServiceImpl_.

**There are various types of advice: _before, after_ and _around._** Logically, they represent where our logic runs in relation to the join point.

In concrete terms: should our logic run before, after or both before and after the _findById_ call.

For our simple use case, we’ll use _before._

So, in our aspect implementation, we can simply declare a basic method:

```
@Before public void before(JoinPoint joinPoint) { 
    // ... 
}
```

Here, **the parameter _joinPoint_ is optional, depending on if we need any information from the JoinPoint**. In our case, we do need to extract and log the params of the JoinPoint execution, therefore, we need it.

We’ve also annotated the method with _@Before_ annotation, as we discussed.

Now, we can write our custom logic:

```
@Aspect
@Component
public class ProjectServiceAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceAspect.class);

    @Before
    public void before(JoinPoint joinPoint) {
        LOG.info("Searching Project with Id {}", joinPoint.getArgs()[0]);
    }

}
```

In our example, we've defined the logger, then used this to log the call and parameters.

Notice how we’re obtaining the arguments out of the join point and logging them.

So now we have our JoinPoint half-defined and our advice ready.

However, **there’s still a crucial element left: how is this logic actually linked to our _findById_ method from our service?**

That’s exactly what’s left to do here: we still need to bridge our JoinPoint with our Advice.

For this, **we need to define the third component: the PointCut**.

## The PointCut Expression

**A pointcut is a _Predicate_ that is written as an expression, to match the JoinPoint with the advice.**

That’s why our _@Before_ annotation still shows up as an error: it’s missing the PointCut expression.

Any PointCut expression starts with **a PointCut designator that tells what to match**.

There are several pointcut designators such as the execution of a method, a type, method arguments, or annotations.

We’ll use the primary one which is _execution:_

```
@Before("execution(* com.baeldung.ls.service.impl.ProjectServiceImpl.findById(Long))")
public void before(JoinPoint joinPoint) {
    LOG.info("Searching Project with Id {}", joinPoint.getArgs()[0]);
}
```

**We used _execution_ to start our expression, then,** **immediately after the designator, we defined the method signature we need to match.**

We've now defined the complete signature of our JointPoint so that our PointCut matches the execution of our method _findById._

Now that we have all the components of our Aspect ready, we can run the application and see this in action.

If we access [http://localhost:8080/projects/1](http://localhost:8080/projects/1) we'll see the logs from our Aspect.

## Boot Highlight

Out-of-the-box, Spring Boot provides us with the dependencies that allow us to use AOP. In a pure Spring application, we have to include them manually:

```
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>...</version>
</dependency>
```

In fact, very often we don't have to include it explicitly because various Spring artifacts (such as _spring-context_, _spring-webmvc_) already contain it.

To enable AOP in a Spring app, **we have to add the _@EnableAspectJAutoProxy_ annotation** to any configuration bean. This annotation is defined in the _spring-context_, therefore, we don't have to include anything special to make use of it.

On the other hand, if we need to create our own aspects, we have to add _aspectjweaver_ into the project:

```
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>...</version>
</dependency>
```

For more details, in the Resource section, we may find useful the link to a tutorial on pointcut expressions in Spring.

## Advice Types (extra)

We've seen the _Before_ advice in the examples above which executes our logic before the method runs.

There are several other types as well; let’s take a look at some of them.

**_AfterReturning_**

The _AfterReturning_ advice **runs when the matched method returns normally**.

If we need to access the value returned by the method in the advice body, we can do this using the ‘_returning_’ property of _@AfterReturning_ annotation.

For example, we can log the project that was returned after successful execution of the _findById()_ method:

```
@AfterReturning(pointcut = "execution(*..Optional<*..Project>  *..service..findById(*))", returning = "project")
public void afterReturningProject(Optional<Project> project) {
    LOG.info("project found: {}", project.orElse(null));
}
```

Notice above that **the value of the returning property, ‘_project_’ is equal to the advice method’s argument name**.

Let’s also take a closer look at the pointcut expression here:

_"execution(\*..Optional<\*..Project> \*..service..findById(\*))"_

This expression would match with the _findById(\*)_ method in all the classes inside the _service_ package, and returns a value of type _Optional<Project>_.

**_After_**

The _After_ advice **runs when the matched method execution exits.** It's also referred to as _After(finally)_ advice as it will run on both normal and exception exit conditions.

In the example below, the advice will log the method name after its execution:

```
@After("within(com.baeldung.ls.service.impl.ProjectServiceImpl)")
public void afterAllMethodsOfProjectServiceImpl(JoinPoint joinPoint) {
    LOG.info("After Invoking the method: {} ", joinPoint.getSignature().getName());
}
```

Note that we've used the **_within_ pointcut designator**, which matches all the methods declared within a type. A type here is an expression matching either package names, class names and interfaces.

In the example above, our pointcut expression will match with all the methods within the _ProjectServiceImpl_ class.

**_Around_**

The _Around_ advice **runs ‘around’ the matched method execution.** This gives us the opportunity to execute our logic both before and after the matched method runs.

Let’s add an example of a new advice which runs around the _save()_ method:

```
@Around("execution(* com.baeldung.ls.service.impl.ProjectServiceImpl.save(*))")
public Object aroundSave(ProceedingJoinPoint joinPoint) {
    Object val = joinPoint.getArgs()[0];
    try {
        LOG.info("saving project : {}", val);
        val = joinPoint.proceed();
        LOG.info("project saved successfully !!");
    } catch (Throwable e) {
        LOG.error("error while saving project: ", e);
    }
    return val;
}
```

In this example, note the parameter of the advice method which is of type _ProceedingJoinPoint_. Within the body of the advice, **we can call the _proceed()_ method on _ProceedingJoinPoint_ so that the advised method is executed**.

We have also surrounded the _proceed()_ method invocation with a _try-catch_ block, as the method throws a _Throwable_ object. Hence we can also use the around advice to handle exceptions thrown from the underlying methods.

Besides using the around advice, **we can also use the** [**_AfterThrowing_**](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop-advice-after-throwing) **advice to handle exceptions**. The _AfterThrowing_ advice runs when the matched methods throws a particular type of exception.

## Spring AOP vs AspectJ (extra)

In all our examples above, **we have used the annotation style for defining our aspects**. These annotations are part of the [AspectJ](https://www.eclipse.org/aspectj/) project.

AspectJ is a dedicated and more sophisticated framework for aspect oriented programming.

**Spring interprets the annotations of AspectJ, but the runtime is still pure Spring AOP.**

The lower-level AOP API can also be used to define aspects programmatically, but this is less common. **Spring itself recommends to use the _@AspectJ_ annotation style for most cases.**

Besides the ease of use, another notable difference is that the Spring AOP implementation performs runtime weaving by using proxies, while AspectJ performs weaving at the compile time by using the AspectJ compiler.

Due to this proxy-based nature of Spring AOP, the advices apply only on _public_ methods. To intercept _private_ or _protected_ methods as well we can use AspectJ.

## Resources
- [Aspect-oriented programming](https://en.wikipedia.org/wiki/Aspect-oriented_programming)
- [Introduction to Spring AOP](https://www.baeldung.com/spring-aop)
- [Introduction to Pointcut Expressions in Spring](https://www.baeldung.com/spring-aop-pointcut-tutorial#enabling)
- [Introduction to AspectJ](https://www.baeldung.com/aspectj)


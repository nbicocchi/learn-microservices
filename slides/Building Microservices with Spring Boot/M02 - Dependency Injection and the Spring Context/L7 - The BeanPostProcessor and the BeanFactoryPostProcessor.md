# The BeanPostProcessor and the BeanFactoryPostProcessor

In this lesson, we'll learn about the **Spring container extension points**.

The relevant module you need to import when you're starting with this lesson is: [beanPostProcessor-and-beanFactoryPostProcessor-start](https://github.com/eugenp/learn-spring/tree/module2/beanPostProcessor-and-beanFactoryPostProcessor-start)

If you want have a look at the fully implemented lesson, as a reference, feel free to import: [beanPostProcessor-and-beanFactoryPostProcessor-end](https://github.com/eugenp/learn-spring/tree/module2/beanPostProcessor-and-beanFactoryPostProcessor-end)

## The Spring Container Extension Points

Spring provides certain extension points to the context initialization process.

**We can hook into this process by using the interfaces provided by the framework.**

In this lesson, we’ll look into the _BeanPostProcessor_ and _BeanFactoryPostProcessor_ interfaces.

## _BeanPostProcessor_

**_BeanPostProcessor_ allows us to modify a bean or execute custom code after the bean is instantiated by the container.**

**The container invokes the _BeanPostProcessor_ for each and every bean.**

Let’s see how we can create a custom _BeanPostProcessor._

First, our custom class has to implement the _BeanPostProcessor_ interface:

```
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

}
```

We've also registered it as a bean using the _@Component_ annotation.

This interface has 2 default callback methods that we can override:

-   the **_postProcessBeforeInitialization_ -** which as the name suggests **is invoked by the container before any bean initialization methods**
-   the **_postProcessAfterInitialization_ - which is invoked after the bean initialization methods**

Let’s add these to our custom class so we can override them:

```
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
```

**The container here gives us access to the bean object being initialized and the _beanName_ which allows us to potentially modify the bean or perform any logic upon the bean initialization.**

For our example, we’ll just log the bean name being initialized.

Let’s add the _Logger_ and add the log statements in these methods:

```
private static final Logger LOG = LoggerFactory.getLogger(MyBeanPostProcessor.class);

@Override
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    LOG.info("Before initialising the bean: {}", beanName);
    return bean;
}

@Override
public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    LOG.info("After initialising the bean: {}", beanName);
    return bean;
}
```

Now let’s run the app and look at the logs.

As we can see from the logs, the _MyBeanPostProcessor_ is being invoked multiple times for each bean that the container initializes.

Next, let's look into the _BeanFactoryPostProcessor._

## _BeanFactoryPostProcessor_

**_BeanFactoryPostProcessor_ allows us to read the configuration metadata of a bean and potentially modify it before the container has actually instantiated any of the beans.**

Let's create a custom _BeanFactoryPostProcessor_ and then use it to modify the property value of a bean.

First, let's define a simple class:

```
public class BeanA {

    private String foo;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }
}
```

We have also added a property _foo_ and generated the getters/setters for it.

Next, let's define a bean of this type in the _AppConfig_:

```
@Configuration
public class AppConfig {

    @Bean
    public BeanA beanA() {
        return new BeanA();
    }

}
```

Now, let’s create a custom _BeanFactoryPostProcessor._

**Our custom class has to implement the _BeanFactoryPostProcessor_ interface:**

```
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

}
```

We've also registered it as a bean by using the _@Component_ annotation.

This interface contains the _postProcessBeanFactory_ method that we need to implement.

Let's add an implementation wherein we'll get the bean definition of our bean and modify its metadata:

```
@Override
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    BeanDefinition bd = beanFactory.getBeanDefinition("beanA"); 
    bd.getPropertyValues().add("foo", "bar");
}
```

Here, **we've used the bean factory that the container gives us access to, to obtain the _BeanDefinition_ of our _BeanA_**_._ The _BeanDefinition_ object here offers a variety of methods to read and modify the metadata of the bean.

In our example, we've used this to assign a value _bar_ to the _foo_ property of _BeanA._

Let’s make sure that the value was assigned correctly.

In _BeanA_ let's add a _@PostConstruct_ method and log the value of the property:

```
public class BeanA {

    // ...
    private static final Logger LOG = LoggerFactory.getLogger(BeanA.class);

    @PostConstruct
    public void post() {
        LOG.info("value of the property foo is: {}", this.foo);
    }

    // ...
}
```

## _BeanFactoryPostProcessor_ in action

To see all of this in action, let's add a breakpoint in _postProcessBeanFactory_ of _MyBeanFactoryPostProcessor_ and _post_ method of _BeanA_.

Let's run the _LsApp_ in Debug mode.

As the Spring context loads, the debugger will stop at the _postProcessBeanFactory_ method breakpoint.

As we can see on the console, none of the beans have been instantiated yet. **This indicates that the _BeanFactoryPostProcessor_ is invoked even before the bean instantiation happens.**

Next, as we resume the execution, the debugger stops at the _post_ method in _BeanA._

We step over the log statement and we can notice on the console the value of _foo_ being logged correctly.

**We can also see the flow of the invocation: first the _BeanFactoryPostProcessor_ was invoked, followed by the _BeanPostProcessor_.**

Finally, after resuming the program we’ll see the logs from the _BeanPostProcessor_.

## Multiple _BeanPostProcessors_

We can also implement multiple _BeanPostProcessors_ and **control their execution order by implementing the _Ordered_ interface** in each _BeanPostProcessor._

First, let’s create another _BeanPostProcessor_ similar to the _MyBeanPostProcessor_:

```
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CustomBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        LOG.info("CustomBeanPostProcessor is invoked for before initializing bean: {}", beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        LOG.info("CustomBeanPostProcessor is invoked for after initializing bean: {}", beanName);
        return bean;
    }

}
```

Now we have two _BeanPostProcessors_. To specify their execution order, we need to implement the _Ordered_ interface in both of them.

Let’s implement the _Ordered_ interface in _MyBeanPostProcessor_ and implement the interface method _getOrder()_:

```
@Component
public class MyBeanPostProcessor implements BeanPostProcessor, Ordered {

    // ...

    @Override
    public int getOrder() {
        return 1;
    }
}
```

In our example, we want the _MyBeanPostProcessor_ to be executed before the _CustomBeanPostProcessor,_ hence, we've kept the Order as 1.

When the container finds multiple _BeanPostProcessor_ in the context, it uses this integer value to determine the execution order. **The lower order value gets executed first.**

Next, let’s implement the _Ordered_ interface in the _CustomBeanPostProcessor_ and return the value '2' from the _getOrder()_ method:

```
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor, Ordered {

    // ...

    @Override
    public int getOrder() {
        return 2;
    }
}
```

Let’s start the app again.

**We can notice on the console that _MyBeanPostProcessor_ gets executed before the _CustomBeanPostProcessor._**

Similar to the _BeanPostProcessor_, we can also control the execution order of multiple _BeanFactoryPostProcessors_ by implementing the _Ordered_ interface.

Spring provides some out of the box _BeanFactoryPostProcessor_ implementations; one of them is the _PropertySourcesPlaceholderConfigurer_ which you can explore further using the link in the Resources section.

## Defining Post Processors Using Static Bean Methods (extra)

Until now, we have registered our _BeanPostProcessor_ and _BeanFactoryPostProcessor_ as beans by using the _@Component_ annotation when defining each PostProcessor class.

However, we can also register these post processors by using the _@Bean_ annotation in the _AppConfig_ class:

```
@Configuration
public class AppConfig {
    @Bean
    public static MyBeanPostProcessor beanPostProcessor() {
        return new MyBeanPostProcessor();
    }
//...
}
```

Notice in the example here, **we defined the bean method as static.**

This is because we want such post processor beans to get initialized early in the container lifecycle.

Defining the bean method as static **will ensure that such beans are created even before the containing configuration class gets initialized**, hence avoiding triggering other parts of the configuration at that point.

## The _FactoryBean_ Interface (extra)

Spring provides another extension point to the container wherein we can plug in our own factories into the container.

**If a bean has complex initialization logic, we can write that logic inside a factory class and plug in our custom factory into the container. For this, our custom factory simply needs to implement the _FactoryBean_ interface.**

Let’s take a look at the _FactoryBean_ interface:

```
public interface FactoryBean<T> {
  
    T getObject() throws Exception;
	
    Class<?> getObjectType();
	
    default boolean isSingleton() {
        return true;
    }
}
```

The container invokes these methods while injecting the beans produced by the factory.

**The _getObject()_ is the most important method. It is supposed to return the actual bean object that our factory produces. The bean initialization logic should go inside this method.**

The _getObjectType()_ should return the Class of the bean object that our factory produces.

The _isSingleton()_ method returns a _boolean_ value indicating if the bean produced by our factory is a singleton or not. By default, the bean is considered a singleton however, factory implementations can modify this behaviour by overriding this method.

## Resources
- [Container Extension Points in the Spring Documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-factory-extension)
- [The _PropertySourcesPlaceholderConfigurer_ in the Spring JavaDoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/PropertySourcesPlaceholderConfigurer.html)
- [The _PropertySourcesPlaceholderConfigurer_ in the Spring Documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-factory-placeholderconfigurer)

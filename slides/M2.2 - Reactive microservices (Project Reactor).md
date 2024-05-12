# Reactive microservices 

## Getting started with Project Reactor
Project reactor, often simply referred to as **Reactor**, is based on the [Reactive Stream Specification](https://www.reactive-streams.org/) which is implemented by Project reactor and RxJava. It is used in popular tools:
-   In frameworks such as **Spring Boot** and **WebFlux**
-   Drivers and clients such as the [CloudFoundry Java Client](https://github.com/cloudfoundry/cf-java-client)
-   In contracts or protocols such as [RSocket](https://github.com/rsocket) and [R2DBC](https://r2dbc.io/)

Technically, the Reactor is a fourth-generation reactive library, based on the Reactive Streams specification, for building non-blocking applications on the JVM. The reactor also supports non-blocking inter-process communication with the reactor-netty project.

-   It is fully **non-blocking** and it directly interacts with Java’s Functional API, [CompletableFuture](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletableFuture.html), [Stream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html), and [Duration](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Duration.html).
-   It fits well in microservices architecture, Reactor offers **backpressure-ready network engines** for HTTP (including Websockets), TCP, and UDP.
-   It has three main components, Reactor Core, Reactor Test, and Reactor Netty. Reactor Netty is suited for microservices architecture.


### Overview of Reactive Streams Specification

There are 4 main components of Reactive stream implementations, **Publisher**, **Subscriber**, **Subscription**, and **Processor**.

#### Publisher

A Publisher is a provider of a potentially unbounded number of sequenced elements, publishing them according to the demand received from its Subscriber(s).

```
public interface Publisher<T> {
    public void subscribe(Subscriber<? super T> s);
}
```

#### Subscriber
The responsibility of the Subscriber to decide when and how many elements it is able and willing to receive. It is **recommended** that Subscribers request the upper limit of what they are able to process, as requesting only one element at a time results in an inherently inefficient “stop-and-wait” protocol.

```
public interface Subscriber<T> {
    public void onSubscribe(Subscription s);
    public void onNext(T t);
    public void onError(Throwable t);
    public void onComplete();
}
```

#### Subscription
A Subscription represents the unique relationship between a Subscriber and a Publisher. The Subscriber is in control over when elements are requested and when more elements are no longer needed. The Subscription must allow the Subscriber to call `Subscription.request` synchronously from within `onNext` or `onSubscribe`.

```
public interface Subscription {
    public void request(long n);
    public void cancel();
}
```

#### Processor

A Processor represents a processing stage, which is both a `Subscriber` and a `Publisher` and must obey the contracts of both.

```
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {

}
```

### Components of Reactor core

There are several artifacts in **reactor-core**, reactor-test, reactor-tools, and reactor-netty. We will discuss all of them a little later, but will just focus on reactor-core at this moment.

Project reactors introduces two important reactive types that implements **`Publisher`** are Flux and Mono.

-   **Mono** A Mono object represents a single value or empty value (`0 to 1`) item, e.g. `Mono<HttpResponse>`.
-   **Flux** A Flux object represents a sequence of `0 to N` items. For example, `Flux<Long>`.


## Mono in Project Reactor

`Mono` is an abstract class that implements the `Publisher` from reactive streams. It has several factory methods to return an instance of its subclass. There are several subclasses of `Mono` abstract class like `MonoJust`, `MonoNever`, `MonoOperator`, `MonoProcessor` and many more classes.

```
public abstract class Mono<T> implements CorePublisher<T> {
    public static <T> Mono<T> create(Consumer<MonoSink<T>> callback) {..}
    public static <T> Mono<T> empty() {..}
    public static <T> Mono<T> error(Throwable error) {..}
    public static <T> Mono<T> just(T data) {..}
    ...
}
```

### Marble diagram of Mono

A **Marble diagram** represents the state of a stream when one or more operators are applied to it. The following diagram shows how a Mono behaves when one or more operators applied to it.

![Reactor Mono](https://jstobigdata.com/wp-content/uploads/2020/05/mono.svg)


### Create a Mono with test code

You can create a simple `Mono` using the `Mono.just`. The below code creates a Mono which emits `Hello World`. The second part of the code verifies the behavior of the above created Mono using `reactor-test`. In Reactor, you can manipulate a stream content using an **Operator**.

The `.log()` prints traces of a reactor stream (Mono and Flux) and `.map()` is an operator which is used here to concat ` World`.

The `StepVerifier.create()` creates the verifier, `.expectNext(..)` verifies the emitted value and finally it is important to call the `.verifyComplete()` which makes sure the stream completes its execution successfully.


```java
@Test
public void simpleMono(){
    Mono<String> message = Mono.just("Hello")
        .map(msg -> msg.concat(" World"))
        .log();
    
    message.subscribe(System.out::println);
    
    StepVerifier.create(message)
        .expectNext("Hello World")
        .verifyComplete();
}
```

### Subscribe a Mono stream

Call the `Mono.subscribe(...)` to subscribe any stream. There are several factory methods, the most general is listed below.

```java
@Test
public void subscribeMono() {
  Mono<String> message = Mono.just("Welcome to Jstobigdata")
      .map(msg -> msg.concat(".com")).log();
  
  message.subscribe(new Subscriber<String>() {
    @Override
    public void onSubscribe(Subscription s) {
      s.request(Long.MAX_VALUE);
    }
    
    @Override
    public void onNext(String s) {
      System.out.println("onNext: " + s);
    }
    
    @Override
    public void onError(Throwable t) {
      t.printStackTrace();
    }
    
    @Override
    public void onComplete() {
      System.out.println("====== Execution Completed ======");
    }
  });
  
  StepVerifier.create(message)
      .expectNext("Welcome to Jstobigdata.com")
      .verifyComplete();
}
```

If the stream throws an Exception, `errorConsumer` will be invoked and the execution will be terminated. Similarly, `completeConsumer` will be invoked when the Mono finishes its execution successfully.

### Create an Empty Mono

`Mono.empty()` creates a Mono that completes without emitting any item.

```java
@Test
public void emptyMono() {
  Mono empty =  Mono.empty().log();
  
  empty.subscribe(val -> {
    System.out.println("==== Value ====" + val);
  }, err -> {
  }, () -> {
    System.out.println("====== On Complete Invoked ======");
  });
  
  StepVerifier.create(empty)
      .expectNextCount(0) //optional
      .verifyComplete();
}
```

### Create a never emitting Mono

A **No Signal** or a **Never** Mono will never signal any data, error, or completion signal, essentially running indefinitely. Ideally, you should set a timeout duration in your test case, else it will never complete its execution.

```java
@Test
public void noSignalMono() {
  Mono<String> noSignal = Mono.never();
  
  //Can not use - Mono.never().log()
  noSignal.subscribe(val -> {
    System.out.println("==== Value ====" + val);
  });
  
  StepVerifier.create(noSignal)
      .expectTimeout(Duration.ofSeconds(5))
      .verify();
}
```

### Throw Exception inside a Mono

You can also throw an Exception inside the Mono and the error subscription callback is invoked.

```java
@Test
public void subscribeMono3() {
    Mono<String> message = Mono.error(new RuntimeException("Check error mono"));
    
    message.subscribe(
        value -> {
          System.out.println(value);
        },
        err -> {
          err.printStackTrace();
        },
        () -> {
          System.out.println("===== Execution completed =====");
        });
        
    StepVerifier.create(message)
        .expectError(RuntimeException.class)
        .verify();
}
```

### Mono from a Supplier

You can also create a Mono from `java.util.function.Supplier` as shown below.

```java
@Test
public void fromSupplier() {
  Supplier<String> stringSupplier = () -> "Sample Message";
  Mono<String> sMono = Mono.fromSupplier(stringSupplier).log();
  
  sMono.subscribe(System.out::println);
  
  StepVerifier.create(sMono)
      .expectNext("Sample Message")
      .verifyComplete();
}
```

### Filter a Mono

It also allows you to Filter the value using the standard java stream filter api.

```java
@Test
public void filterMono(){
  Supplier<String> stringSupplier = () -> "Hello World";
  Mono<String> filteredMono = Mono.fromSupplier(stringSupplier)
      .filter(str -> str.length() > 5)
      .log();
      
  filteredMono.subscribe(System.out::println);
  
  StepVerifier.create(filteredMono)
      .expectComplete();
}
```

## Flux in Project Reactor

**Flux** is a Reactive Streams Publisher that emits 0 to N elements, and then completes (successfully or with an error). Just like Mono, Flux also is an abstract class and it implements the Publisher interface from [reactive stream specification](https://github.com/reactive-streams/reactive-streams-jvm). It has several factory methods to create its instance.

```
public abstract class Flux<T> implements CorePublisher<T> {
    public static <T> Flux<T> create(Consumer<? super FluxSink<T>> emitter, OverflowStrategy 
    backpressure) {..}
    public static <T> Flux<T> empty() {..}
    public static <T> Flux<T> error(Throwable error) {..}
    public static <T> Flux<T> from(Publisher<? extends T> source) {..}
    public static Flux<Long> interval(Duration period) {..}
    public static <T> Flux<T> just(T... data) {..}
    ..
}
```

### Marble diagram of Flux

As shown in the below Marble diagram, a Flux emits zero or n number of items. When the Flux completes its emission successfully, the subscriber receives the completed signal which is shown as the vertical line. You can also use several reactive **operators** to transform or manipulate the values emitted. If the Flux terminates abruptly by throwing an Exception, the subscriber receives an Error signal and this is represented as the cross (**X**).

![Flux in Project Reactor](https://jstobigdata.com/wp-content/uploads/2020/05/flux.svg)


### Create a simple Flux
We will use the `Flux.just()` factory method to create a Flux from the given set of values. The StepVerfiier from the `reactor-test` is used to test our examples.

```java
@Test
public void justFlux() {
  // Flux
  Flux<String> simpleFlux = Flux.just("hello", "there").log();
  
  // Subscriber
  simpleFlux.subscribe(val -> System.out.println(val));
  
  //Test code
  StepVerifier.create(simpleFlux)
      .expectNext("hello")
      .expectNext("there")
      .verifyComplete();
}
```

### Subscribe a Flux stream

There’re several ways in which a Flux can be subscribed. Basically, you Need to invoke `Flux.subscribe()` and pass an instance of a `Subscriber` or lambdas.

```java
@Test
public void subscribeFlux() {
  Flux<String> messages = Flux.just("Welcome", "to", "this", "course").log();
  
  messages.subscribe(new Subscriber<String>() {
  
    @Override
    public void onSubscribe(Subscription s) {
      s.request(Long.MAX_VALUE);
    }
    
    @Override
    public void onNext(String s) {
      System.out.println("onNext: " + s);
    }
    
    @Override
    public void onError(Throwable t) {
      t.printStackTrace();
    }
    
    @Override
    public void onComplete() {
      System.out.println("====== Execution Completed ======");
    }
  });
  
  StepVerifier.create(messages)
      .expectNext("Welcome")
      .expectNext("to")
      .expectNext("this")
      .expectNext("course")
      .verifyComplete();
}
```

### Create an Empty Flux
Just like Mono, you can also create an empty Flux using `Flux.empty` method. As soon individual example, the subscribed method directly prints `completed` without emitting any value.

```java
@Test
public void emptyFlux() {
  Flux emptyFlux = Flux.empty().log();
  
  emptyFlux.subscribe(
      val -> {
        System.out.println("=== Never called ===");
      },
      error -> {
        //Error
      },
      () -> {
        System.out.println("==== Completed ===");
      });
      
  StepVerifier.create(emptyFlux)
      .expectNextCount(0)
      .verifyComplete();
}
```

### Never emitting Flux

For whatsoever reason if you need flux that never emits any value, you can make use of `Flux.never` to create one.

```java
@Test
public void neverFlux() {
  Flux neverFlux = Flux.never().log();
  
  StepVerifier.create(neverFlux)
      .expectTimeout(Duration.ofSeconds(2))
      .verify();
}
```

### Handling Error in Flux

The `error` callback has to handle the error thrown by a Flux. There are several ways it can throw an Exception as follows.

**Flux.error** is used to create a Flux that emits an Exception. In a practical scenario, a Flux can throw an Error, so the subscriber should have the error callback to handle these errors.

**Flux.concatWith** allows adding new values to an existing stream. In the below code, the reactive steam emits `"Tom"`, `"Jerry"` and then throws an Exception and terminates the stream. Therefore the subscriber never receives the value `"John"`.

```java
@Test
public void handleError() {
  Flux<String> names = Flux.just("Tom", "Jerry")
      .concatWith(Flux.error(new RuntimeException("Exception occurred..!")))
      .concatWith(Flux.just("John"));
  
  names.subscribe(
      name -> {
        System.out.println(name);
      }, err -> {
        err.printStackTrace();
      }
  );
  
  StepVerifier.create(names)
      .expectNext("Tom")
      .expectNext("Jerry")
      .expectError(RuntimeException.class)
      .verify();
}
```

### Filtering in Flux

The **Filter** is a special operator that allows you to evaluate each source value against a given **Predicate**. If the predicate test succeeds against the source value, it is emitted. If the predicate test fails, the value is ignored and a request of 1 is made to the upstream to test the next value.

![Flux Filter Marble diagram](https://jstobigdata.com/wp-content/uploads/2020/05/filterForFlux.svg)


Following is a simple example that demonstrates the working of Filter in emitting only the Odd numbers. The `Flux.range(10, 10)` emits ten values starting from 10, e.g. `10, 11, 12, 13 ....., 19`.

```java
@Test
public void rangeFlux() {
  Flux<Integer> range = Flux.range(10, 10)
      .filter(num -> Math.floorMod(num, 2) == 1)
      .log();
      
  range.subscribe(System.out::println);
  
  StepVerifier.create(range)
      //.expectNextCount(5)
      .expectNext(11, 13, 15, 17, 19)
      .verifyComplete();
}
```

## Transform and combine Reactive Stream

### Transform a Flux using `map`

We can use the `map` operator on a reactive stream to transform its values before emitting them to the subscribers. Transforming a value basically means performing manipulation on the individual values before they are finally emitted to the subscribers.

![Map applied on Flux](https://jstobigdata.com/wp-content/uploads/2020/05/mapForFlux.svg)

IThe filter operator first passes only the names which have more than 5 chars, then these values are further capitalised. 

```java
@Test
public void transformMap(){
    List<String> names = Arrays.asList("google", "abc", "fb", "stackoverflow");
    Flux<String> flux = Flux.fromIterable(names)
            .filter(name -> name.length() > 5)
            .map(name -> name.toUpperCase())
            .log();

    StepVerifier.create(flux)
            .expectNext("GOOGLE", "STACKOVERFLOW")
            .verifyComplete();
}
```

As you can see, only the strings with more than 5 characters are capitalised and emitted twice.

### Transform a Flux using `flatMap`

Just like `map`, you can also use the `flatMap` operator to transform a Reactive stream.

It transforms the elements emitted by this Flux asynchronously into Publishers, then flattens these inner publishers into a single Flux through merging, which allows them to interleave.

![](https://jstobigdata.com/wp-content/uploads/2020/05/flatMapForFlux.svg)

There are three dimensions to this operator that can be compared with flatMapSequential and concatMap:

* Generation of inners and subscription: this operator is eagerly subscribing to its inners.
* Ordering of the flattened values: this operator does not necessarily preserve original ordering, as inner elements are flattened as they arrive.
* Interleaving: This operator lets values from different inners interleave (similar to merging the inner sequences).

```java
@Test
public void transformUsingFlatMap(){
  List<String> names = Arrays.asList("google", "abc", "fb", "stackoverflow");
  Flux<String> flux = Flux.fromIterable(names)
      .filter(name -> name.length() > 5)
      .flatMap(name -> {
        return Mono.just(name.toUpperCase());
      })
      .repeat(1) //Just repeat once
      .log();
      
  StepVerifier.create(flux)
      .expectNext("GOOGLE", "STACKOVERFLOW", "GOOGLE", "STACKOVERFLOW")
      .verifyComplete();
}
```
The simple difference between `map` and `flatMap` is, flatMap needs to return a Mono.

### Combine multiple streams using `merge`
The `merge` Merge data from Publisher sequences contained in an array / vararg into an interleaved merged sequence. Unlike concat, sources are subscribed to eagerly.

![](https://jstobigdata.com/wp-content/uploads/2020/05/mergeFixedSources.svg)

```java
@Test
public void combineUsingMerge(){
  Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie");
  Flux<String> names2 = Flux.just("Pride", "Monk", "Walker");
  Flux<String> names = Flux.merge(names1, names2).log();
  
  StepVerifier.create(names)
      .expectSubscription()
      .expectNext("Blenders", "Old", "Johnnie", "Pride", "Monk", "Walker")
      .verifyComplete();
}
```

The **merge** operator emits the element as soon as one of its `sources` emits a value. It subscribes all the `sources` at the same time, therefore does not maintain sequence unlike the `concat` operator which is discussed later.

```java
@Test
public void mergeWithDelay(){
  Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie")
      .delayElements(Duration.ofSeconds(1));
  Flux<String> names2 = Flux.just("Pride", "Monk", "Walker")
      .delayElements(Duration.ofSeconds(1));
  Flux<String> names = Flux.merge(names1, names2).log();
  
  StepVerifier.create(names)
      .expectSubscription()
      .expectNextCount(6)
      .verifyComplete();
}
```

### Combine streams with `concat`

The `concat` operator concatenates the multiple sources and forwards the elements emitted by them to the downstream. Remember, `concat` emits the element sequentially by subscribing to the first `$source`, waiting for it to complete and then subscribes to the next one and so on.

![](https://jstobigdata.com/wp-content/uploads/2020/05/concatVarSources.svg)

```java
@Test
public  void concatWithDelay(){
  Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie")
      .delayElements(Duration.ofSeconds(1));
  Flux<String> names2 = Flux.just("Pride", "Monk", "Walker")
      .delayElements(Duration.ofSeconds(1));
  Flux<String> names = Flux.concat(names1, names2)
      .log();
 StepVerifier.create(names)
      .expectSubscription()
      .expectNext("Blenders", "Old", "Johnnie", "Pride", "Monk", "Walker")
      .verifyComplete();
}
```

As you can see, it waits for the source1 to complete and then subscribes to the second one. So the `concat` is useful when you want to subscribe to the sources sequentially.

### Combine streams with `zip`

The `zip` operator also used to combine elements from multiple `sources`. It waits for all the sources to emit **one element** and combine these elements once into an output value (constructed by the provided combinator). The operator will continue doing so until any of the sources completes. Errors will immediately be forwarded. This “Step-Merge” processing is especially useful in Scatter-Gather scenarios.

![](https://jstobigdata.com/wp-content/uploads/2020/05/zipIterableSourcesForFlux.svg)

```java
@Test
public void combineWithZip(){
  Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie")
      .delayElements(Duration.ofSeconds(1));
  Flux<String> names2 = Flux.just("Pride", "Monk", "Walker")
      .delayElements(Duration.ofSeconds(1));
  Flux<String> names = Flux.zip(names1, names2, (n1, n2) -> {
    return n1.concat(" ").concat(n2);
  }).log();
  
  StepVerifier.create(names)
      .expectNext("Blenders Pride", "Old Monk", "Johnnie Walker")
      .verifyComplete();
}
```

## Backpressure in Project reactor

Backpressure is the ability of a Consumer to signal the Producer that the rate of emission is higher than what it can handle. So using this mechanism, the Consumer gets control over the speed at which data is emitted. Using **Backpressure**: the Subscriber controls the data flow from the Publisher, making use of `request(n)` to request `n` number of elements at a time.

### Request only n elements
Let's assume there is a stream which emits `x` elements, but the subscriber only wants to receive `n` elements.

```java
@Test
public void subscriptionRequest() {
    Flux<Integer> range = Flux.range(1, 100);
    
    //gets only 10 elements
    range.subscribe(
      value -> System.out.println(value),
      err -> err.printStackTrace(),
      () -> System.out.println("==== Completed ===="),
      subscription -> subscription.request(10)
    );
}
```

### Cancel the subscription after n elements
A better approach is to have more control over the flow and cancel the subscription when needed. You can use the `BaseSubscriber::hookOnNext` to subscribe to a producer. Look at the example below, the producer emits the next value only when the subscriber sends `request(1)`. In reality, this producer can be a database system and the Subscriber could be a I/O device. So in order to match the speed of operation, I/O may request a batch of data, process them, and then request the next batch of data and so on.

```java
@Test
public void cancelCallback() {
  Flux<Integer> range = Flux.range(1, 100).log() ;
  range.doOnCancel(() -> {
    System.out.println("===== Cancel method invoked =======");
  }).doOnComplete(() -> {
    System.out.println("==== Completed ====");
  }).subscribe(new BaseSubscriber<Integer>() {
    @Override
    protected void hookOnNext(Integer value){
      try {
        Thread.sleep(500);
        request(1); // request next element
        System.out.println(value);
        if (value == 5) {
          cancel();
        }
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    }
  });
  
  StepVerifier.create(range)
    .expectNext(1, 2, 3, 4, 5)
    .thenCancel()
    .verify();
}
```
### Backpressure StepVerifier

The `StepVerifier` of `reactor-test` allows us to test a Producer properly w.r.t backpressure behavior. Only when the `thenRequest(n)` is triggered, the producer sends the next `n` number of elements.

```java
@Test
public void backpressureVerifier() {
  Flux data = Flux.just(101, 201, 301).log();
  StepVerifier.create(data)
    .expectSubscription()
    .thenRequest(1)
    .expectNext(101)
    .thenRequest(2)
    .expectNext(201, 301)
    .verifyComplete();
}
```

## Spring WebFlux

Spring WebFlux is a reactive web framework, newly added to Spring 5.x. It is fully non-blocking, supports Reactive Streams back pressure, and runs on such servers as Netty, Undertow, and Servlet 3.1+ containers. The Spring WebFlux uses Project reactor underneath for reactive programming.

An obvious question is, why to use Spring WebFlux when we already have **Spring Web-MVC**. There are several reasons as discussed below.

* The need of non-blocking web stack to handle concurrency with a small number of threads and scale with fewer hardware resources.
* Support for Functional Programming in web programming.

In this example, we’re going to see how to configure WebFlux and implement database operations using Reactive Programming through Spring Data Reactive Repositories with MongoDB.

**Step 1: Maven Setup**
```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-reactivestreams</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Step 2: Create the Controller and Service layers**

```
@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    private final IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(value = "/{id}")
    public Mono<Project> findOne(@PathVariable String id) {
        return projectService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Project> create(@RequestBody Project newProject) {
        return projectService.save(newProject);
    }

    @GetMapping
    public Flux<Project> findAll() {
        return projectService.findAll();
    }

    @PutMapping("/{id}")
    public Mono<Project> updateProject(@PathVariable("id") String id, @RequestBody Project updatedProject) {
        return projectService.save(updatedProject);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable("id") String id) {
        projectService.deleteById(id);
    }
}
```

```
@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository IProjectRepository;

    public ProjectServiceImpl(IProjectRepository IProjectRepository) {
        this.IProjectRepository = IProjectRepository;
    }

    @Override
    public Mono<Project> findById(String id) {
        return IProjectRepository.findById(id);
    }

    @Override
    public Flux<Project> findAll() {
        return IProjectRepository.findAll();
    }

    @Override
    public Mono<Project> save(Project project) {
        return IProjectRepository.save(project);
    }

    @Override
    public void deleteById(String id) {
        IProjectRepository.deleteById(id);
    }
}
```

We are already familiar with the repositories programming model, with the CRUD methods already defined plus support for some other common things as well.

Now with the Reactive model, we get the same set of methods and specifications, except that we’ll deal with the results and parameters in a reactive way.

We can use this repository the same way as the blocking CrudRepository:

```
public interface IProjectRepository extends ReactiveMongoRepository<Project, String> {
    Mono<Project> findByCode(String code);

}
```


**Step 3: Start the Spring-boot app**

Now that we have our sample controller in place, let us navigate inside the project from terminal and run the maven command to start the application.

```
mvn spring-boot:run
```

If everything is fine, you should be able to see the app started on `localhost:8080` port.

## References:
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 7)
- [Reactive-streams specifications](http://www.reactive-streams.org/)
- [Reactive streams specifications for JVM](https://github.com/reactive-streams/reactive-streams-jvm)
- [Testing Reactive Streams](https://www.baeldung.com/reactive-streams-step-verifier-test-publisher)
- [Spring Data Reactive Repositories with MongoDB](https://www.baeldung.com/spring-data-mongodb-reactive)
- [The Reactive Java era is over. Here is why](https://medium.com/alphadev-thoughts/the-reactive-java-era-is-over-here-is-why-5885caacdf43)

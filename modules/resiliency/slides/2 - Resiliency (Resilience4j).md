# Microservices resiliency

## Resilience4j
**Resilience4j is a lightweight fault tolerance library designed for functional programming. Resilience4j provides higher-order functions (decorators) to enhance any functional interface, lambda expression or method reference with a Circuit Breaker, Rate Limiter, Retry or Bulkhead.** 

You can stack more than one decorator on any functional interface, lambda expression or method reference. The advantage is that you have the choice to select the decorators you need and nothing else.

```java
Supplier<String> supplier = () -> service.sayHelloWorld(param1);

String result = Decorators.ofSupplier(supplier)
  .withBulkhead(Bulkhead.ofDefaults("name"))
  .withCircuitBreaker(CircuitBreaker.ofDefaults("name"))
  .withRetry(Retry.ofDefaults("name"))
  .withFallback(asList(CallNotPermittedException.class, BulkheadFullException.class),  
      throwable -> "Hello from fallback")
  .get();
```

To use the library as an integration in Spring Boot add the following Maven dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
      <groupId>io.github.resilience4j</groupId>
      <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
    </dependency>
</dependencies>
```

### Circuit Breaker

Resilience4j exposes information about circuit breakers at runtime in a number of ways:
* The current state of a circuit breaker can be monitored using the microservice’s actuator health endpoint, **/actuator/health**.
* The circuit breaker also publishes events on an actuator endpoint, for example, state transitions and **/actuator/circuitbreakerevents**.

To control the logic in a circuit breaker, Resilience4j can be configured using standard Spring Boot configuration files. We will use the following configuration parameters:

```
resilience4j.circuitbreaker:
  instances:
    time:
      allowHealthIndicatorToFail: false
      registerHealthIndicator: true
      slidingWindowType: COUNT_BASED
      slidingWindowSize: 5
      failureRateThreshold: 50
      waitDurationInOpenState: 10000
      permittedNumberOfCallsInHalfOpenState: 3
      automaticTransitionFromOpenToHalfOpenEnabled: true
      ignoreExceptions:
```

* **allowHealthIndicatorToFail**: tells Resilience4j not to affect the status of the health endpoint. This means that the health endpoint will still report "UP" even if one of the component’s circuit breakers is in an open or half-open state. It is very important that the health state of the component is not reported as "DOWN" just because one of its circuit breakers is not in a closed state.
* **registerHealthIndicator**: enables Resilience4j to fill in the health endpoint with information regarding the state of its circuit breakers.
* **slidingWindowType**: To determine if a circuit breaker needs to be opened, Resilience4j uses a sliding window, counting the most recent events to make the decision. The sliding windows can either be based on a fixed number of calls or a fixed elapsed time.
* **slidingWindowSize**: The number of calls in a closed state, which are used to determine whether the circuit should be opened. We will set this parameter to 5.
* **failureRateThreshold**: The threshold, in percent, for failed calls that will cause the circuit to be opened. We will set this parameter to 50%. This setting, together with slidingWindowSize set to 5, means that if three or more of the last five calls are faults, then the circuit will open.
* **waitDurationInOpenState**: Specifies how long the circuit stays in an open state, that is, before it transitions to the half-open state. We will set this parameter to 10000 ms. 
* **permittedNumberOfCallsInHalfOpenState**: The number of calls in the half-open state, which are used to determine whether the circuit will be opened again or go back to the normal, closed state. We will set this parameter to 3, meaning that the circuit breaker will decide whether the circuit will be opened or closed based on the first three calls after the circuit has transitioned to the half-open state. Since the failureRateThreshold parameters are set to 50%, the circuit will be open again if two or three calls fail. Otherwise, the circuit will be closed.
* **automaticTransitionFromOpenToHalfOpenEnabled**: Determines whether the circuit breaker will automatically transition to the half-open state once the waiting period is over. Otherwise, it will wait for the first call after the waiting period is over until it transitions to the half-open state.
* **ignoreExceptions**: This can be used to specify exceptions that should not be counted as faults. Expected business exceptions such as not found or invalid input are typical exceptions that the circuit breaker should ignore; users who search for non-existing data or enter invalid input should not cause the circuit to open.

### Time Limiter
To help a circuit breaker handle slow or unresponsive services, a timeout mechanism can be helpful. Resilience4j’s timeout mechanism, called a TimeLimiter, can be configured using standard Spring Boot configuration files. We will use the following configuration parameter:

```
resilience4j.timelimiter:
  instances:
    time:
      timeoutDuration: 3s
```

* **timeoutDuration**: Specifies how long a TimeLimiter instance waits for a call to complete before it throws a timeout exception. We will set it to 3 seconds.

### Retry

Resilience4j exposes retry information in the same way as it does for circuit breakers when it comes to events and metrics but does not provide any health information. 

Retry events are accessible on the actuator endpoint: **/actuator/retryevents**. To control the retry logic, Resilience4j can be configured using standard Spring Boot configuration files. We will use the following configuration parameters:

```yaml
resilience4j.retry:
  instances:
    time:
      maxAttempts: 3
      waitDuration: 500
      # enableExponentialBackoff: true
      # exponentialBackoffMultiplier: 2
      # enableRandomizedWait: true
      # randomizedWaitFactor: 0.5
      retryExceptions:
        - org.springframework.web.reactive.function.client.WebClientResponseException$InternalServerError
```

* **maxAttempts**: The number of attempts before giving up, including the first call. We will set this parameter to 3, allowing a maximum of two retry attempts after an initial failed call.
* **waitDuration**: The wait time before the next retry attempt. We will set this value to 500 ms, meaning that we will wait 1 second between retries.
* **retryExceptions**: A list of exceptions that will trigger a retry. We will only trigger retries on InternalServerError exceptions, that is, when HTTP requests respond with a 500 status code.
* **enableExponentialBackoff**: Exponential backoff is a common strategy for handling retries of failed network calls. In simple terms, the clients wait progressively longer intervals between consecutive retries:

```
wait_interval = base * exponentialBackoffMultiplier ^ n
```

![](images/microservices-resiliency-retry-1.webp)

* **enableRandomizedWait**:  Adding jitter provides a way to break the synchronization across the clients thereby avoiding collisions. In this approach, we add randomness to the wait intervals.

```
wait_interval = (base * exponentialBackoffMultiplier ^ n) +/- (random_interval)
```

* **randomizedWaitFactor**: Determines the range over which the random value will be spread with regard to the specified waitDuration. So for the value of 0.5 above, the wait times generated will be between 250ms (500 - 500 * 0.5) and 750ms (500 + 500 * 0.5).

![](images/microservices-resiliency-retry-2.webp)


Now, it is enough to annotate methods using external resources (e.g., other services, databases, etc.) 
with the corresponding annotation.

```java
    @Retry(name = "time")
    @TimeLimiter(name = "time")
    @CircuitBreaker(name = "time", fallbackMethod = "getTimeFallbackValue")
    public Mono<LocalTime> getTime(int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(TIME_SERVICE_URL + "?delay={delay}&faultPercent={faultPercent}").build(delay, faultPercent);

        LOG.info("Getting time on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(LocalTime.class)
                .doOnError(ex -> handleException(ex));
    }
```

### Bulkhead

The Bulkhead pattern isolates remote resources of an application into pools so that if one fails, 
the others can continue to function. 
Resilience4j’s Bulkhead can be configured using standard Spring Boot configuration files, 
and is implemented by limiting the number of concurrent calls to a single remote service. 
In other words, the thread pool of the application is statically partitioned into different pools, 
one for each remote service. 
We will use the following configuration parameters:

```yml
server:
  tomcat:
    threads:
      max: 4

resilience4j:

  bulkhead:
    instances:
      time:
        maxConcurrentCalls: 2
        maxWaitDuration: 10ms
      date:
        maxConcurrentCalls: 2
        maxWaitDuration: 10ms
```

- The max threads for the web server (tomcat) is 4. (This is just to simulate resource exhaustion).  
> As always, the configuration is local for a single instance, so we are able to configure different BulkHeads for different services.
> In this case, we have two services, "time" and "date", and we have configured a bulkhead for each of them, with these parameters:


* **maxConcurrentCalls**: max number of concurrent calls allowed downstream.
* **maxWaitDuration**: any additional requests will wait for the given duration. Otherwise, it will go with default/fallback method and/or generate a `BulkheadFullException`.

> Keep in mind that we cannot use Project Reactor (Mono) or any other asynchronous model, as the point is to demonstrate thread exhaustion problems and solution.

With the configuration in place, now, first of all, we need to handle the `BulkheadFullException`, 
by creating a dedicated class:  

```java
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(BulkheadFullException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void bulkheadException() {}

}
```

After that, we just need to annotate the method that we want to protect with the `@Bulkhead` annotation:

```java
    @Bulkhead(name = "time")
    public LocalTime getTimeWithBulkhead(int delay, int faultPercent) {
        return getTime(delay, faultPercent);
    }
```

That's it! Your endpoints are now protected with a Bulkhead pattern.

#### Testing the bulkhead

Run the spring-cloud-resiliency-end project. The following endpoints are available:
|||
|---|---|
|/datetime/timeRaw|Calls and returns time|
|/datetime/date|Calls and returns date|
|/datetime/timeBulkhead|Like timeRaw, but with Bulkhead|
|/datetime/dateBulkhead|Like date, but with Bulkhead|

The gateway microservice routes all the endpoints that start with `datetime` to the `composite` service.

All endpoints accept two parameters: `delay` and `faultPercent`. 
The `delay` parameter is used to simulate a slow response from the service 
and we will use it to simulate a slow service.

We can use cURL to test the endpoints:

Create a file named urls.txt with the following content (Change the base URL if necessary):

```
write-out = " %{time_total}s\n"
url = "http://127.0.0.1:8080/datetime/timeRaw?delay=10"
url = "http://127.0.0.1:8080/datetime/timeRaw?delay=10"
url = "http://127.0.0.1:8080/datetime/timeRaw?delay=10"
url = "http://127.0.0.1:8080/datetime/timeRaw?delay=10"
url = "http://127.0.0.1:8080/datetime/timeRaw?delay=10"
url = "http://127.0.0.1:8080/datetime/timeRaw?delay=10"
url = "http://127.0.0.1:8080/datetime/timeRaw?delay=10"
url = "http://127.0.0.1:8080/datetime/timeRaw?delay=10"
```


This tells cURL to write the total time the request took to complete and to make 8 requests to the timeRaw endpoint.

Let's make 4 parallel requests to the timeRaw endpoint:
```bash
curl --parallel --parallel-immediate --parallel-max 4 --config urls.txt
"15:39:43.449036489""15:39:43.449036513""15:39:43.449036537""15:39:43.449036526" 10.016571s
 10.016512s
 10.016491s
 10.016466s
"15:39:53.459668519""15:39:53.459668567""15:39:53.459668437""15:39:53.459668448" 10.017165s
 10.017235s
 10.017235s
 10.017445s
```
As we can see, cURL launches 4 parallel requests to the timeRaw endpoint.
We wrote 8 requests in the config file, so the requests are launched in two batches of 4.
That's why we see two groups of 4 requests.
The time in quotes is the response from the service, 
and the time at the end is the total time the requests took to complete.

Now, let's try again with unlimited parallel requests. Remember that the server has a maximum of 4 threads.

```bash
curl --parallel --parallel-immediate --config urls.txt
"15:44:45.486502948""15:44:45.48650294""15:44:45.486503019""15:44:45.486502909" 10.016264s
 10.016209s
 10.016131s
 10.016053s
"15:44:55.49194147""15:44:55.491940032""15:44:55.491941469""15:44:55.491940018" 20.021455s
 20.021440s
 20.021422s
 20.021381s
```

Now cURL has launched all 8 requests at once, but only 4 of them are processed at the same time. 
The other 4 requests are waiting for a thread to become available. 
The total time for the second batch of requests is around 20 seconds, which is twice the time of the first batch. 

Let's demonstrate that the resource exhaustion also affects the other non-protected endpoints, like the date endpoint. 
Let's test it normally first:

```bash
curl --write-out " %{time_total}s\n" http://127.0.0.1:8080/datetime/date
"2024-07-16" 0.009591s
```

As we can see, the date endpoint is working fine and returns immediately.

Now run both commands at the same time in two different terminals. 
The first command will exhaust the server's resources, and the second command will try to access the date endpoint. 
We can see that the date endpoint is severely slowed down 
(in a real world situation, this would result in timeouts, errors and cascading failures).

```bash
curl --write-out " %{time_total}s\n" http://127.0.0.1:8080/datetime/date
"2024-07-16" 14.121950s
```

Let's now do the same experiment with the bulkhead-protected endpoints. First, let's edit the URLs in the file:

```bash
sed -i 's/timeRaw/timeBulkhead/g' urls.txt
```

And let's repeat the experiment. The outputs of the commands are:
```bash
curl --parallel --parallel-immediate --config urls.txt
 0.098677s
 0.098203s
 0.101490s
 0.101103s
 0.107822s
 0.107345s
"18:06:46.466300883""18:06:46.466141895" 10.133357s
 10.133252s
```

```bash
curl --write-out " %{time_total}s\n" http://127.0.0.1:8080/datetime/dateBulkhead
"2024-07-17" 0.028515s
```

As we can see, the time endpoint has accepted only 2 requests and rejected the other 6. 
In this way, only 2 of the 4 threads have been used, thus there are other 2 threads available for the date endpoint, 
which has returned immediately.

### Rate Limiter
The date service is a simple service that returns the current date. In resilienc4j, the simple 
fixed window algorithm is implements, so we will only have a few configuration options:

```yaml
resilience4j:
  ratelimiter:
    instances:
      dateRateLimiter:
        limit-for-period: 20
        limit-refresh-period: 60s
        timeout-duration: 0s
```

* limit-for-period: Specifies the maximum number of requests allowed within a specific time period.
* limit-refresh-period: Defines the time after which the limit will be reset.
* timeout-duration: Determines how long a request can wait for the rate limiter to allow it to proceed. 
* If the rate limit is exceeded and we don’t receive permission within the specified timeout duration, 
* the request will be stopped and an error will be returned. 
* In this example we disabled this mechanism by setting it to 0s.

As an example, if we set timeout-duration to 10 seconds and the rate limit is exceeded, 
all further requests will wait for 10 seconds before they are stopped, but if during this time the rate limit resets, 
the requests will unlock and be served. This parameter can be used to implement a queue-like mechanism.

As always, we need to handle the generated exceptions by creating a dedicated class:

```java
@ControllerAdvice
public class DateExceptionHandler {
    @ExceptionHandler({ RequestNotPermitted.class })
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void handleRequestNotPermitted() {}
}
```

And annotate the methods we want to protect with the `@RateLimiter` annotation:
```java
    @GetMapping(value = "/date")
    @RateLimiter(name = "dateRateLimiter")
    public Mono<LocalDate> date(
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay
    ) {
        return Mono.just(LocalDateTime.now().toLocalDate())
                .delayElement(Duration.ofSeconds(delay + RND.nextInt(1)));
    }
```

To test the limiter, fire up many requests and see what happens. A convenient bash oneliner would be:
```bash
for i in {1..25}; do curl -i http://127.0.0.1:8080/date ; done
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 12

"2024-07-18"HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 12

"2024-07-18"HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 12

.....

"2024-07-18"HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 12

"2024-07-18"HTTP/1.1 429 Too Many Requests
content-length: 0

HTTP/1.1 429 Too Many Requests
content-length: 0

HTTP/1.1 429 Too Many Requests
content-length: 0

HTTP/1.1 429 Too Many Requests
content-length: 0

HTTP/1.1 429 Too Many Requests
content-length: 0

```

As we can see, the first 20 requests are served, and the next 5 are rejected with a 429 status code.

## Resources
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 13)
* https://www.baeldung.com/resilience4j
* https://www.baeldung.com/spring-boot-resilience4j
* https://www.baeldung.com/resilience4j-backoff-jitter
* https://blog.acolyer.org/2015/01/15/the-tail-at-scale/
* https://jmeter.apache.org/
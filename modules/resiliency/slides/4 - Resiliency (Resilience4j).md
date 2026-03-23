# Resiliency (Resilience4j)

## Resilience4j
**Resilience4j is a lightweight fault tolerance library designed for functional programming. It provides decorators to enhance any functional interface, with Circuit Breaker, Rate Limiter, Retry or Bulkhead patterns.** The advantage in using it is that you have the choice to select the decorators you need and nothing else.

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

To use the library integrated in Spring Boot (not in its native form) add the following Maven dependencies:

```xml
<dependencies>
    <dependency>
      <groupId>io.github.resilience4j</groupId>
      <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

### Retry

The current state of retries can be monitored at:
* [/actuator/retries](http://localhost:8080/actuator/retries)
* [/actuator/retryevents](http://localhost:8080/actuator/retryevents)

To control the retry logic, Resilience4j can be configured using standard Spring Boot configuration files. We will use the following configuration parameters:

```yaml
resilience4j.retry:
  configs:
    default:
      maxAttempts: 3
      waitDuration: 500
      enableExponentialBackoff: true
      exponentialBackoffMultiplier: 2
      enableRandomizedWait: true
      randomizedWaitFactor: 0.5
      retryExceptions:
        - org.springframework.web.client.HttpServerErrorException
  instances:
    time:
      base-config: default
```

* **maxAttempts**: The number of attempts before giving up, including the first call. We will set this parameter to 3, allowing a maximum of two retry attempts after an initial failed call.
* **waitDuration**: The wait time before the next retry attempt. We will set this value to 500 ms.
* **enableExponentialBackoff**: Enables exponential backoff.
* **exponentialBackoffMultiplier**: Determines the base of the exponential.
* **enableRandomizedWait**:  Enables jitter.
* **randomizedWaitFactor**: Determines the range over which the random value will be spread with regard to the specified waitDuration. So for the value of 0.5 above, the wait times generated will be between 250ms (500 - 500 * 0.5) and 750ms (500 + 500 * 0.5).

![](images/microservices-resiliency-retry-2.webp)

* **retryExceptions**: A list of exceptions that will trigger a retry. We will only trigger retries on InternalServerError exceptions, that is, when HTTP requests respond with a 500 status code.

To enable retries, annotate a method with the `@Retry` annotation:

```java
@Retry(name = "divisors")
public DivisorsWithLatency getDivisors(Long n, Long times, Long faults) {
    String url = UriComponentsBuilder.fromHttpUrl("http://MATH-SERVICE/divisors")
            .queryParam("n", n)
            .queryParam("times", times)
            .queryParam("faults", faults)
            .toUriString();
    return restClient.get()
            .uri(url)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
}
```

#### Testing

```bash
curl -X GET http://localhost:8080/divisors\?n=60\&times=1\&faults=100
```

```bash
curl -X GET 'http://127.0.0.1:8080/actuator/retryevents' | jq

{
  "retryEvents": [
    {
      "retryName": "time",
      "type": "RETRY",
      "creationTime": "2025-03-31T09:34:02.164010849Z[Etc/UTC]",
      "errorMessage": "org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 : [no body]",
      "numberOfAttempts": 1
    },
    {
      "retryName": "time",
      "type": "RETRY",
      "creationTime": "2025-03-31T09:34:02.449109899Z[Etc/UTC]",
      "errorMessage": "org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 : [no body]",
      "numberOfAttempts": 2
    },
    {
      "retryName": "time",
      "type": "ERROR",
      "creationTime": "2025-03-31T09:34:03.198614815Z[Etc/UTC]",
      "errorMessage": "org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 : [no body]",
      "numberOfAttempts": 3
    }
  ]
}

```

Looking carefully at the timestamps you can see both exponential backoff and randomness in action.

You can run the following command to observe how rare it is to encounter an error when the failure rate is 20% and retries are set to 3.

```bash
curl -X GET http://localhost:8080/divisors\?n=60\&times=2\&faults=20 
```

To see retry in Grafana Dashboard:

```bash
echo 'GET http://127.0.0.1:8080/mcd?a=18&b=12&times=10000&faults=30' | vegeta attack -duration=120s --rate=2 -timeout=180s | vegeta report
```

### Circuit Breaker

The current state of circuit breakers can be monitored at:
  * [/actuator/circuitbreakers](http://localhost:8080/actuator/circuitbreakers)
  * [/actuator/circuitbreakerevents](http://localhost:8080/actuator/circuitbreakerevents)

To control the logic in a circuit breaker, we use the following configuration:

```yaml
resilience4j.circuitbreaker:
  configs:
    default:
      allowHealthIndicatorToFail: false
      slidingWindowType: COUNT_BASED
      slidingWindowSize: 13
      failureRateThreshold: 50
      waitDurationInOpenState: 10000
      permittedNumberOfCallsInHalfOpenState: 3
      automaticTransitionFromOpenToHalfOpenEnabled: true
      record-exceptions:
        - org.springframework.web.client.HttpServerErrorException
  instances:
    time:
      base-config: default
```

* **allowHealthIndicatorToFail**: tells Resilience4j not to affect the status of the health endpoint. This means that the health endpoint will still report "UP" even if one of the component’s circuit breakers is in an open or half-open state. It is very important that the health state of the component is not reported as "DOWN" just because one of its circuit breakers is not in a closed state.
* **slidingWindowType**: To determine if a circuit breaker needs to be opened, Resilience4j uses a sliding window, counting the most recent events to make the decision. The sliding windows can either be based on a fixed number of calls or a fixed elapsed time.
* **slidingWindowSize**: The number of calls in a closed state, which are used to determine whether the circuit should be opened. We will set this parameter to 13.
* **failureRateThreshold**: The threshold, in percent, for failed calls that will cause the circuit to be opened. We will set this parameter to 50%. This setting, together with slidingWindowSize set to 13, means that if 7 of the last 13 calls are faults, then the circuit will open.
* **waitDurationInOpenState**: Specifies how long the circuit stays in an open state, that is, before it transitions to the half-open state. We will set this parameter to 10000 ms.
* **permittedNumberOfCallsInHalfOpenState**: The number of calls in the half-open state, which are used to determine whether the circuit will be opened again or go back to the normal, closed state. We will set this parameter to 3, meaning that the circuit breaker will decide whether the circuit will be opened or closed based on the first three calls after the circuit has transitioned to the half-open state. Since the failureRateThreshold parameters are set to 50%, the circuit will be open again if two or three calls fail. Otherwise, the circuit will be closed.
* **automaticTransitionFromOpenToHalfOpenEnabled**: Determines whether the circuit breaker will automatically transition to the half-open state once the waiting period is over. Otherwise, it will wait for the first call after the waiting period is over until it transitions to the half-open state.
* **record-exceptions**: A list of exceptions representing a fault that will trigger the circuit breaker.

Now, it is enough to annotate methods using external resources (e.g., other services, databases, etc.) with the corresponding annotation.

```java
@CircuitBreaker(name = "divisors")
public DivisorsWithLatency getDivisors(Long n, Long times, Long faults) {
    String url = UriComponentsBuilder.fromHttpUrl("http://MATH-SERVICE/divisors")
            .queryParam("n", n)
            .queryParam("times", times)
            .queryParam("faults", faults)
            .toUriString();
    return restClient.get()
            .uri(url)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
}
```

#### Testing

Give the following commands multiple times to see the circuit breaker change state:

```bash
# simulates a succeeded call
curl -X GET http://localhost:8080/divisors\?n=60\&times=1\&faults=0
```

```bash
# simulates a failed call
curl -X GET http://localhost:8080/divisors\?n=60\&times=1\&faults=100
```

```bash
# show the status of the circuit breaker
curl -X GET 'http://localhost:8080/actuator/circuitbreakers' | jq
```

```json
{
  "circuitBreakers": {
    "time": {
      "failureRate": "0.0%",
      "slowCallRate": "0.0%",
      "failureRateThreshold": "50.0%",
      "slowCallRateThreshold": "100.0%",
      "bufferedCalls": 13,
      "failedCalls": 0,
      "slowCalls": 0,
      "slowFailedCalls": 0,
      "notPermittedCalls": 0,
      "state": "CLOSED"
    }
  }
}
```



### Bulkhead

The current state of bulkheads can be monitored at:
* [/actuator/bulkheads](http://localhost:8080/actuator/bulkheads)
* [/actuator/bulkheadevents](http://localhost:8080/actuator/bulkheadevents)


Resilience4j's Bulkhead statically partitions the application into different thread pools, one for each remote service.
We will use the following configuration parameters:

```yml
server:
  tomcat:
    threads:
      max: 4

resilience4j.bulkhead:
  configs:
    default:
      maxConcurrentCalls: 2
  instances:
    divisors:
      base-config: default
    mcd:
      base-config: default
```

- The max threads for the web server (tomcat) is 4. (This is just to simulate resource exhaustion).
- We have two services, `time` and `date`, and we have configured a bulkhead for each of them (allowing to use at most 2 threads).

After that, we just need to annotate the method that we want to protect with the `@Bulkhead` annotation:

```java
@Bulkhead(name = "divisors")
public DivisorsWithLatency getDivisors(Long n, Long times, Long faults) {
    String url = UriComponentsBuilder.fromHttpUrl("http://MATH-SERVICE/divisors")
            .queryParam("n", n)
            .queryParam("times", times)
            .queryParam("faults", faults)
            .toUriString();
    return restClient.get()
            .uri(url)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
}
```

#### Testing

The datetime-composite-service exposes, among others, the following endpoints:

* /time -> Local call returns time
* /date -> Local call returns date
* /divisors -> Call a remote math service
* /mcd -> Call a remote math service

Inside the /testing directory you can find a file that can be used with curl.

**urls.txt**

```yaml
url = "http://localhost:8080/divisors\?n=99999\&times=200000000\&faults=0"
url = "http://localhost:8080/divisors\?n=99999\&times=200000000\&faults=0"
url = "http://localhost:8080/divisors\?n=99999\&times=200000000\&faults=0"
url = "http://localhost:8080/divisors\?n=99999\&times=200000000\&faults=0"

```

The following command send all the requests in parallel so that the thread pool of the service (size=4) gets saturated and the endpoints /time or /date becomes unavailable despite being unused.

```bash
curl --parallel --parallel-immediate --config urls-shared-pool.txt
```

```bash
curl -X GET 'http://127.0.0.1:8080/date' 
```

### Rate Limiter

The current state of bulkheads can be monitored at:
* [/actuator/ratelimiters](http://localhost:8080/actuator/ratelimiters)
* [/actuator/ratelimiterevents](http://localhost:8080/actuator/ratelimiterevents)

datetime-service is a simple service that returns the current time and date. In Resilienc4j, the simple fixed window algorithm is implemented, so we will only have a few configuration options:

```yaml
resilience4j.ratelimiter:
  configs:
    default:
      limit-for-period: 100
      limit-refresh-period: 60s
      timeout-duration: 0s
  instances:
    time:
      base-config: default
    date:
      base-config: default
```

* **limit-for-period**: Specifies the maximum number of requests allowed within a specific time period.
* **limit-refresh-period**: Defines the time after which the limit will be reset.
* **timeout-duration**: Determines how long a request can wait for the rate limiter to allow it to proceed. As an example, if we set timeout-duration to 10 seconds and the rate limit is exceeded, all further requests will wait for 10 seconds before they are stopped, but if during this time the rate limit resets, the requests will unlock and be served. This parameter can be used to implement a queue-like mechanism.

As always, annotate the methods we want to protect with the `@RateLimiter` annotation:

```java
@RateLimiter(name = "time")
@GetMapping("/time")
public LocalTime getLocalTime() {
    return LocalTime.now();
}

@RateLimiter(name = "date")
@GetMapping("/date")
public LocalDate getLocalDate() {
    return LocalDate.now();
}
```

#### Testing
To test the limiter, fire up many requests and see what happens. A convenient bash oneliner would be:

```bash
for i in {1..105}; do curl -i http://127.0.0.1:8080/date ; done
```

```text
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 15 Oct 2024 11:01:41 GMT

"2024-10-15"HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 15 Oct 2024 11:01:41 GMT

...

Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 15 Oct 2024 11:01:44 GMT
Connection: close

{"timestamp":"2024-10-15T11:01:44.994+00:00","status":500,"error":"Internal Server Error","path":"/date"}HTTP/1.1 500 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 15 Oct 2024 11:01:44 GMT
Connection: close

{"timestamp":"2024-10-15T11:01:45.002+00:00","status":500,"error":"Internal Server Error","path":"/date"}%   
```

The first 100 requests are served, but the last 5 are rejected.

## Resources
* https://www.baeldung.com/resilience4j
* https://www.baeldung.com/spring-boot-resilience4j
* https://www.baeldung.com/resilience4j-backoff-jitter
* https://blog.acolyer.org/2015/01/15/the-tail-at-scale/
* https://jmeter.apache.org/


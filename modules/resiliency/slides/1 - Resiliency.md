# Microservices resiliency

## What is Resilience?
**Microservices are distributed in nature**. When you work with distributed systems, always remember this number one rule **anything could happen**. We might be dealing with network issues, service unavailability, application slowness etc. (remember [Fallacies of Distributed Systems](https://www.youtube.com/watch?v=8fRzZtJ_SLk&list=PL1DZqeVwRLnD3EjyciYAO82dT9Owiq8I5)!) **An issue with one system might affect another system behavior/performance**. The capacity of a system to recover from such failures, remain functional, and avoid cascading failures to downstream services makes it **resilient**.

## Threads and thread-related risks

### One Thread Per Request Design Pattern

The "One Thread Per Request" design pattern is commonly used in traditional server-side applications. In this model, when a client sends a request to a server (e.g., via HTTP), the server creates or assigns a dedicated thread to handle that request. The thread is responsible for executing the necessary operations (e.g., database queries, computations) and preparing the response to send back to the client. These are often **I/O-bound operations** that can take some time to complete.

During these I/O operations, the thread is **blocked**, meaning it cannot proceed with the next steps until the operation is finished. While blocked, the thread remains in a **waiting state**, simply consuming system resources without doing any actual work. This is problematic for the following reasons:

1. **Memory Consumption**: Each thread consumes memory, primarily for its stack space. Even though the thread is waiting and not using CPU resources, it still occupies memory, which increases with the number of concurrent requests.

2. **Thread Pool Saturation**: If many threads are simultaneously waiting for I/O to complete, the thread pool can become exhausted or saturated. When no free threads are available, the system may queue incoming requests, leading to increased response times, or worse, it may reject new requests altogether.

3. **Resource Inefficiency**: Having a large number of threads that are mostly idle (waiting for I/O) leads to inefficient use of system resources. The system could be using the available CPU and memory more effectively to handle more requests or perform useful work, but instead, these resources are tied up by threads in a waiting state.

As such, if many requests arrive simultaneously or long-running requests consume threads for extended periods, the pool can become exhausted, meaning no threads are available to process new requests. This can lead to:
- **Increased Latency**: New requests may be queued or delayed until a thread becomes available.
- **Denial of Service (DoS)**: In extreme cases, the system may fail to handle incoming requests entirely, resulting in service outages.
- **High Resource Consumption**: Each thread consumes memory (for stack space) and CPU time. Running many threads concurrently increases memory usage and context-switching overhead, potentially degrading performance.

### Reactive Programming as a Solution

Reactive programming is an asynchronous, non-blocking paradigm that allows applications to handle a high volume of requests or events efficiently by using fewer threads. Instead of dedicating one thread per request, reactive applications handle requests via event-driven models. Operations that would typically block a thread (such as waiting for I/O) are handled asynchronously. When a response is ready (e.g., when data from a database becomes available), a callback mechanism resumes the computation.

**Key Benefits**:
- **Non-blocking I/O**: Instead of a thread waiting for I/O operations (e.g., database or network calls), the system registers a callback to handle the operation once it's done, freeing up the thread for other work.
- **Efficient Resource Usage**: By leveraging non-blocking I/O, a reactive system can handle many more concurrent requests with fewer threads.
- **Scalability**: Reactive systems scale better because they can handle higher loads without a significant increase in resource consumption.

**Java and Reactive Programming**:
In Java, frameworks like **Spring WebFlux** and libraries like **Project Reactor** and **RxJava** are designed to build reactive applications. These libraries provide abstractions like reactive streams (Publisher, Subscriber, etc.), which allow for asynchronous, non-blocking communication.

### Java Virtual Threads (Project Loom) as a Solution

Java Virtual Threads, introduced as part of **Project Loom**, offer an alternative solution to traditional "One Thread Per Request" models. Unlike platform (OS) threads, which are heavy-weight and tied to the underlying operating system, virtual threads are light-weight and managed by the JVM itself. Virtual threads allow Java applications to create a large number of concurrent threads without the usual overhead associated with traditional threads.

**Key Features**:
- **Lightweight**: Virtual threads are much cheaper to create and manage than platform threads, allowing thousands or even millions of threads to be created without significant resource strain.
- **Blocking and Non-blocking Together**: Virtual threads allow developers to write blocking code (e.g., blocking on I/O operations) without the performance penalties typically associated with blocking. This makes it easier to reason about thread management and write simpler, sequential code that is still scalable.
- **No Need for Callbacks**: Unlike reactive programming, where callbacks are often used, virtual threads allow traditional, synchronous-looking code to be written, avoiding the complexity and cognitive overhead of managing asynchronous callbacks.

**How Java Virtual Threads Solve Thread Exhaustion**:
- **Massive Concurrency**: With virtual threads, the JVM can manage a huge number of threads concurrently, reducing the risk of thread pool exhaustion. Each request can still have its own thread, but the threads are much more lightweight than traditional threads.
- **Lower Overhead**: Since virtual threads are not tied directly to OS threads, they consume fewer resources, enabling the server to handle a higher volume of concurrent requests without the typical memory and CPU overhead.

### Comparison Between Reactive Programming and Virtual Threads

| **Aspect**               | **Reactive Programming**                               | **Java Virtual Threads**                             |
|--------------------------|--------------------------------------------------------|-----------------------------------------------------|
| **Concurrency Model**     | Asynchronous, non-blocking                            | Synchronous, blocking (but with lightweight threads) |
| **Thread Management**     | Minimal thread use, relies on event loops              | Massive number of lightweight threads managed by JVM|
| **Programming Model**     | Uses reactive streams, callbacks, and event-driven code| Traditional, imperative programming                 |
| **Learning Curve**        | Higher, requires understanding of reactive streams     | Lower, since developers can write familiar blocking code |
| **Use Case**              | Best for event-driven systems with complex I/O         | Great for applications that require simplicity and high concurrency |
| **Performance**           | High throughput for I/O-bound workloads                | Good performance with easier coding patterns         |




## Understanding the Importance of Resiliency
When it comes to building resilient systems, most software engineers only take into account the complete failure of a piece of infrastructure or critical service. **They focus on building redundancy into each layer of their application** using techniques such as:
* clustering key servers
* load balancing between services
* segregating infrastructure into multiple locations

However, when a service is running slow, detecting that poor performance and routing around it is often difficult:
* Service degradation can start out as intermittent and then build momentum. Service degradation might also occur only in small bursts. **The first signs of failure might be a small group of users complaining about a problem until, suddenly, the application container exhausts its thread pool and collapses completely.**
* Calls to remote services are usually synchronous and imply a wait for the service to return. **The caller has no concept of a timeout to keep the service call from hanging.**
* **Applications are often designed to deal with complete failures of remote resources, not partial degradations**. Often, as long as the service has not entirely failed, an application will continue to call a poorly behaving service and won’t fail fast. In this case, the calling service is at risk of crashing because of resource exhaustion.

What’s insidious about problems caused by **poorly performing remote services is that they are not only difficult to detect but can trigger a cascading effect (callers might exhaust their thread pools!) that can ripple throughout an entire application ecosystem**. Without safeguards in place, a single, poorly performing service can quickly take down entire applications.

### A real-world story

![](images/why-resiliency-matters.webp)

In the scenario above, three applications are communicating in one form or another with three different services. Applications A and B communicate directly with the licensing service. The licensing service retrieves data from a database and calls the organization service to do some work for it.

The organization service retrieves data from a completely different database platform and calls out to another service, the inventory service, from a third-party cloud provider, whose service relies heavily on an internal Network Attached Storage (NAS) device to write data to a shared filesystem. Application C directly calls the inventory service.

Over the weekend, a network administrator made what they thought was a small tweak to the configuration on the NAS. This change appeared to work fine, but on Monday morning, reads to a particular disk subsystem began performing exceptionally slow.

The developers who wrote the organization service never anticipated slowdowns occurring with calls to the inventory service. They wrote their code so that the writes to their database and the reads from the service occur within the same transaction. When the inventory service starts running slowly, not only does the thread pool for requests to the inventory service start backing up, the number of database connections in the service container’s connection pools becomes exhausted. These connections were held open because the calls to the inventory service never completed.

Now the licensing service starts running out of resources because it’s calling the organization service, which is running slow because of the inventory service. Eventually, all three services stop responding because they run out of resources while waiting for the requests to complete.

### State of Resilience 2025 Survey
The [State of Resilience 2025 Survey](https://www.cockroachlabs.com/guides/the-state-of-resilience-2025/) conducted among 1,000 senior cloud architects, engineers, and technology executives across North America, EMEA, and APAC showed:

* **Widespread Operational Weaknesses**: 95% of executives are aware of existing operational weaknesses that leave their organizations vulnerable to financial and operational damage from unplanned outages. Nearly half (48%), however, admit their companies’ efforts are insufficient to address these issues.

* **High Cost of Service Disruption**: All surveyed organizations reported suffering outage-related revenue loss over the last twelve months, with 84% losing at least $10,000. One-third indicated that their per-outage revenue loss ranged from $100,000 to $1,000,000 or more, highlighting the severe economic consequences when resilience measures fall short.

* **Frequent Outages are the New Normal**: Organizations reported experiencing 86 outages annually on average, with 55% experiencing disruptions at least once a week. Notably, 70% of large enterprises said their outages typically take 60 minutes or more to resolve – and almost half experienced downtime for two hours or more.

* **Internal and External Consequences**: The impact of unplanned outages goes far beyond financial losses; they also erode the confidence of consumers and business partners, and damage internal trust in IT teams. Even worse, frequent outages accelerate employee burnout, as 39% of respondents reported increased workloads from missed deadlines and accumulated requests.

* **Spotty Preparation**: Just one in three executives claimed their organizations have an organized approach to responding to downtime, and fewer than one-third conduct any failover testing. This lack of preparation exposes organizations to further risks while reinforcing the need for enhanced resilience strategies.

* **Overdue Investments in Resilience**: Overwhelmingly, participants stated a need for investment in operational resilience, especially in automation and AI-driven solutions (49%) and cloud infrastructure services (49%). These investments reflect a forward-looking mindset as organizations recognize the growing importance of AI in disaster prevention and outage recovery capabilities.


## Client-side resiliency patterns
**Client-side resiliency software patterns focus on protecting a client of a remote resource (another microservice call or database lookup) from crashing when the remote resource fails because of errors or poor performance**. These patterns allow the client to fail fast and not consume valuable resources, such as database connections and thread pools. 

![](images/client-side-resiliency.webp)

### Client-side load balancing

Client-side load balancing involves having the client look up all of a service’s individual instances from a service discovery agent (like Netflix Eureka) and then caching the physical location of said service instances.

When a service consumer needs to call a service instance, the client-side load balancer returns a location from the pool of service locations it maintains. Because the client-side load balancer sits between the service client and the service consumer, the load balancer can detect if a service instance is throwing errors or behaving poorly. **If the client-side load balancer detects a problem, it can remove that service instance from the pool of available service locations and prevent any future calls from hitting that service instance.**

### Circuit breaker

**The Circuit Breaker is a crucial pattern in making synchronous communication in microservices more resilient. It acts as a safety mechanism that monitors the availability and responsiveness of dependent services.** The Circuit Breaker maintains a state based on the success or failure of previous requests. If the response indicates a failure, such as a timeout or an error, the Circuit Breaker opens the circuit, preventing further requests from being sent to the failing service. **This avoids overwhelming the failing service and reduces the risk of cascading failures throughout the system.**

The key features of a circuit breaker are as follows:

* If a circuit breaker detects too many faults, it will open its circuit not allowing new calls.
* When the circuit is open, a circuit breaker will perform **fail-fast logic**. This means that it does not wait for a new fault to happen but, instead, it redirects the calls to a **fallback method**.
* After a while, the circuit breaker will be half-open, allowing new calls to see whether the issue that caused the previous failures is still there. If new failures are detected by the circuit breaker, it will open the circuit again and go back to the fail-fast logic. Otherwise, it will close the circuit and go back to normal operation.

![](images/circuit-breaker-internals.webp)

To monitor the rate of failures and determine when to open or close, circuit breakers use time-based windowing mechanisms to track error counts. Three common methods are: **Fixed Window**, **Sliding Window**, and **Leaky Bucket**.

**Fixed Window**

The **fixed window** method divides time into regular, non-overlapping intervals (windows) of fixed duration (e.g., every 30 seconds or 1 minute). It tracks the number of successful and failed requests within each interval separately.

Example:
- Window size: 1 minute.
- Failure threshold: 50%.
- If 100 requests occur in a 1-minute window, and more than 50 requests fail, the circuit breaker opens. Once the 1-minute window ends, the counts reset.

**Sliding Window**

A **sliding window** is similar to a fixed window, but instead of using discrete, non-overlapping windows, the sliding window continuously updates over time, giving more real-time failure tracking. The window "slides" as new requests come in, maintaining a record of failures and successes over a defined duration.

Example:
- Window size: 1 minute.
- Failure threshold: 50%.
- The system tracks the last 1 minute of requests, recalculating the failure rate every second. If more than 50% of requests fail within any 1-minute period, the circuit breaker opens.

**Leaky Bucket**

The **leaky bucket** mechanism is inspired by a physical bucket with a hole at the bottom, where water (representing requests) leaks out at a constant rate. The bucket represents the capacity of the system to handle failures, and water drips in as requests fail. If the bucket fills up (reaches its limit), the circuit breaker opens.

The **leaky bucket** algorithm smooths out sudden bursts of errors, only triggering when failures consistently occur over time. It helps prevent spikes in traffic from overwhelming the system by gradually letting failures "leak" out.

Example:
- Bucket size: 100 failures.
- Leak rate: 1 failure every second.
- If failures occur faster than the leak rate (e.g., more than 1 failure per second), the bucket fills up. If it fills completely (100 failures), the circuit breaker opens.

| **Aspect**             | **Fixed Window**                              | **Sliding Window**                               | **Leaky Bucket**                                |
|------------------------|-----------------------------------------------|-------------------------------------------------|------------------------------------------------|
| **Error Detection**     | Tracks failures over discrete, fixed intervals| Continuously tracks over a moving time window   | Tracks failures as they fill a bucket, leaks over time |
| **Boundary Effects**    | Sharp reset at the end of each window         | Smooth, real-time tracking without resets       | Gradual leak to smooth out sudden bursts       |
| **Responsiveness**      | Slower; can miss errors at window boundaries  | Fast; continuously recalculates failure rates   | Slower; designed to smooth out failures over time |
| **Complexity**          | Simple to implement                           | More complex, requires tracking over sub-windows| Moderate; bucket-filling and leaking behavior |
| **Use Case**            | Steady traffic patterns                       | Systems with variable loads, real-time needs    | Systems prone to bursts of traffic/failures     |


### Fallback processing
**With the fallback pattern, when a remote service call fails, rather than generating an exception, the service consumer executes an alternative code path and tries to carry out the action through another means.**

For instance, let’s suppose you have an e-commerce site that monitors your user’s behavior and gives them recommendations for other items they might want to buy. If the preference service fails, your fallback might be to retrieve a more general list of preferences that are based on all user purchases from a different service and data source.

![](images/microservices-resiliency-fallback.webp)

### Retry
When a request fails, the Retry pattern initiates a retry mechanism, which can be configured with a certain number of retries and backoff strategies. The following circumstances have to be understood before applying this pattern:

* **Non-idempotent operations** can cause unintended side effects if retried multiple times. Examples include operations that modify data, perform financial transactions, or have irreversible consequences. Retrying such operations can lead to data inconsistency or duplicate actions.
* **Circuit breaker**: always consider implementing circuit breakers when enabling retry. When failures are rare, that's not a problem. Instead, retries that increase load can make matters significantly worse.
* **Exponential backoff/jitter**: Implementing exponential backoff can be an effective retry strategy. It involves increasing the delay between each retry attempt exponentially, reducing the load on the failing service and preventing overwhelming it with repeated requests. Here is a good article on how [AWS SDKs support exponential backoff and jitter](https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/) as a part of their retry behaviour.


| Exponential backoff                               | Exponential backoff + jitter                      |
|---------------------------------------------------|---------------------------------------------------|
| ![](images/microservices-resiliency-retry-1.webp) | ![](images/microservices-resiliency-retry-2.webp) |



* **Time-sensitive operations**: Retries may not be appropriate for time-critical operations. Retries might not work well where latency's 99th percentile is close to 50th percentile. Look at the graphs below. On the first one, timeouts occasionally happens, a good case for enabling retries. On the second graph, timeouts happen periodically, do not enable retries.


| Retry suitable                    | Retry not suitable                    |
|-----------------------------------|---------------------------------------|
| ![](images/retry-applicable.webp) | ![](images/retry-not-applicable.webp) |



### Timeout

It introduces a time limit for synchronous operations, ensuring that requests do not wait indefinitely for a response. When a service makes a request to a dependent service, a timeout value is set. **If a response is not received within the specified time, the operation is considered failed, and appropriate actions can be taken. By setting appropriate timeouts, services can avoid getting stuck in unresponsive states and prevent bottlenecks in the system.**

Typically, the most difficult problem is choosing a timeout value to set:
* **Setting a timeout which is too high** reduces its usefulness, because resources are still consumed while the client waits for the timeout
* **Setting a timeout which is too low** might increase traffic on the backend and latency because too many requests are retried. It might also lead to a complete outage, because all requests start being retried.

**A good practice for choosing a timeout is to start with the latency metrics of the downstream service**. When we make one service call another service, we choose an acceptable rate of false timeouts (such as 0.1%). Then, we set the timeout at the corresponding latency percentile (99.9th percentile in this example).

### Bulkhead

A ship is split into small multiple compartments using Bulkheads. Bulkheads are used to seal parts of the ship to prevent entire ship from sinking in case of flood. Similarly, failures should be expected when we design software. The application should be split into multiple components and resources should be isolated in such a way that failure of one component is not affecting the other.

**The thread pools act as the bulkheads for your service**. Each remote resource is segregated and assigned to a thread pool. If one service is responding slowly, the thread pool for that type of service call can become saturated and stop processing requests. **Assigning downstream services to different thread pools helps in confining resource exhaustion to specific thread pools instead of the whole service**.

![](images/microservices-resiliency-bulkhead-3.webp)

For example: Lets assume that there are 2 services A and B. Service A has very limited resources (say 5 threads). It can process only 5 concurrent requests with its available threads. Service A has 2 sets of APIs as shown below.

* /a/b – depends on Service B (which is slow sometimes)
* /a – depends on Service A
  
When there are multiple concurrent requests to Service A, say 10, 5 of them are for endpoint /a/b and 5 of them are for endpoint /a, there is a chance that Service A might use all its threads to work on the requests for /a/b and block all the 5 threads.

Even though the remaining requests are for /a which does not have any other service dependency, Service A does not have free threads to work on the requests (resource exhaustion)! Service B slowness indirectly affects Service A performance as well.

**Bulkhead Pattern helps us to allocate limit the resources which can be used for specific services so that resource exhaustion can be reduced.**

### Caching

![](images/cache-architecture.webp)

The Caching pattern is a valuable technique for improving the performance and scalability of microservices in synchronous communication. **It involves storing frequently accessed data or computation results in a cache, which is a high-speed storage system, to serve subsequent requests more quickly.**

**By caching data, microservices can reduce the need for repeated, expensive operations, such as retrieving data from a database or performing complex computations**. Instead, the cached results can be directly served, significantly improving response times and overall system performance.

**Caching improves scalability by offloading the workload from backend systems.** By serving cached data, microservices can handle more requests without overloading the underlying resources, ensuring that the system remains responsive even under high traffic conditions.

**Caching also improves resiliency because services can continue to serve requests even if their backend systems are temporarily unavailable**. This improves fault tolerance and ensures that the system can gracefully handle disruptions.

## Server-side resiliency patterns

### Rate Limiter

**The Rate Limiting pattern is a powerful technique for making synchronous communication in microservices more resilient by controlling the rate at which requests are made to a service**. It sets limits on the number of requests that can be processed within a specific time period, ensuring that a service is not overwhelmed by excessive traffic.

By implementing rate limiting, microservices can protect themselves from being overloaded, prevent resource exhaustion, and maintain optimal performance. It allows services to handle requests within their capacity and ensures fair distribution of resources among clients.

When implementing the Rate Limiting pattern, **it is crucial to consider factors such as the maximum allowed requests per unit of time and different rate-limiting strategies, such as fixed windows or sliding windows (see circuit breaker strategies)**. The careful configuration ensures that the rate limits are appropriate for the service's capabilities and the expected load.

Three types of attacks that can be prevented by rate limiting are:
* **DDoS attacks** - Distributed Denial of Service (DDoS) attacks involve sending a massive number of requests to a system or service to overwhelm it, causing it to become unresponsive or unavailable. 
* **Brute force attacks** - Brute force attacks involve repeatedly attempting to guess login credentials or other sensitive information by using automated tools to send a large number of requests. 
* **API abuse** - API abuse involves sending a large number of requests to an API with the intention of extracting large amounts of data or causing resource exhaustion.

## Resources
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 13)



# Reactive microservices

## What is Reactive Programming?
The cloud—public, private, or hybrid—has put Reactive in the spotlight. The cloud is a distributed system. When you run your application on the cloud, that application faces a high degree of uncertainty. The provisioning of your application can be slow, or fast, or even fail. Communication disruptions are common, because of network failures or partitions. You may hit quota restrictions, resource shortages, and hardware failures. Some services you are using can be unavailable at times or moved to other locations. 

While the cloud provides outstanding facilities for the infrastructure layer, it covers only half of the story. **The second half is that your application needs to be designed to be a part of a distributed system**. This emerging notion has been coined reactiveness and the set of properties that constitute reactive systems has been defined in the [reactive manifesto](https://www.reactivemanifesto.org/). 

The main characteristics are: **responsiveness, resiliency, elasticity, and being message-driven**. 
* **Responsive**: The system provides rapid and consistent response times, establishing reliable upper bounds and delivering a consistent quality of service.
* **Resilient**: The system stays responsive in the face of failure.
* **Elastic**: The system stays responsive under varying workload.
* **Message Driven**: The system rely on asynchronous message-passing to establish a boundary between components that ensures **both loose time and space coupling**.

Based on this general notion of reactive systems, reactive programming can be assumed to refer to any paradigm, pattern or technique that facilitates the realization of these properties in software.

### A practical example
Let's consider a system willing to perform the below task:

* Execute a DB query based on a set of input parameters
* Process the DB query result (say lowercase to uppercase)
* Write the result into a file.

The above 3 steps are synchronous by design. That is: each step can not be done until the step before is completed.

### Single-Thread Pattern

```java
public static void main(String[] args) {
    int port = 9999;
    
    // Create a server socket
    try (ServerSocket server = new ServerSocket(port)) {
        while (true) {
            // Wait for the next connection from a client
            Socket client = server.accept();
            PrintWriter response = newPrintWriter(client.getOutputStream(), true);
            BufferedReader request = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            
            String line = request.readLine();
            // query DB
            // process results
            // write to file
            client.close();
        }
    }
}
```

### Thread-per-request Pattern
With the thread per request programming model, we have a dispatcher component creating a new thread object for managing the steps of each request. 

```java
public static void main(String[] args) {
    int port = 9999;
    ExecutorService executors = Executors.newFixedThreadPool(10);
    // Create a server socket
    try (ServerSocket server = new ServerSocket(port)) {
        while (true) {
            // Wait for the next connection from a client
            Socket client = server.accept();
            executors.submit(() -> {
                PrintWriter response =
                        new PrintWriter(client.getOutputStream(), true);
                BufferedReader request = new BufferedReader(
                        newInputStreamReader(client.getInputStream()));
                
                String line = request.readLine();
                // query DB
                // process results
                // write to file
                client.close();
            });
        }
    }
}
```

Everything looks nice, but **there is a catch: context switching and memory consumption!**

If we receive 1000 requests per second, then we create 1000 threads per second. While each thread is performing the IO tasks, it has to wait for DB to execute the underlying query. If the DB query takes 3 seconds to be completed we will have, on average 3GB of RAM occupied by threads waiting for I/O. If anything happens, the node running the DB slows down, a spike in network latency etc., we could saturate the RAM of the node running the microservice with threads waiting for I/O.

### Reactor Pattern
The reactor pattern is based on [asynchronous procedure calls](https://en.wikipedia.org/wiki/Asynchronous_procedure_call). That is, **we split the above 1 big synchronous task into 3 synchronous simple tasks (i.e., each step becomes a task). All these tasks are queued. All these tasks are read from a queue one by one and executed using dedicated worker threads from a thread-pool**. When there are no tasks in queue, worker threads would simply wait for the tasks to arrive. Usually the number of worker threads matches the number of available cores. This way, you can have 10000 tasks in the queue and process them without creating 10000 threads.

![](../../../slides/images/threads-reactor-pattern.png)

This approach solves the problem of platform threads waiting for responses from other systems. The asynchronous APIs do not wait for the response, rather they work through the callbacks. Whenever a thread invokes an async API, the platform thread is returned to the pool until the response comes back from the remote system or database. Later, when the response arrives, the JVM will allocate another thread from the pool that will handle the response and so on. This way, **multiple threads are involved in handling a single async request**.

Issues:
* Latency is removed but the number of platform threads are still limited due to hardware limitations, so we have a limit on scalability.
* **Async programs are executed in different threads so it is very hard to debug or profile them**. 
* We have to adopt a new programming style away from [typical loops](https://howtodoinjava.com/java/flow-control/enhanced-for-each-loop-in-java/) and [conditional statements](https://howtodoinjava.com/java/flow-control/control-flow-statements/). The new [lambda-style syntax](https://howtodoinjava.com/java8/lambda-expressions/) makes it hard to understand the existing code and write programs because we must now break our program into multiple smaller units that can be run independently and asynchronously.


### Virtual Threads
Like a platform thread, a virtual thread is also an instance of *java.lang.Thread*. However, a virtual thread isn't tied to a specific OS thread. A virtual thread still runs code on an OS thread. However, when code running in a virtual thread calls a blocking I/O operation, the Java runtime suspends the virtual thread until it can be resumed. The OS thread associated with the suspended virtual thread is now free to perform operations for other virtual threads.

Use virtual threads in high-throughput concurrent applications, especially those that consist of a great number of concurrent tasks that spend much of their time waiting. Server applications are examples of high-throughput applications because they typically handle many client requests that perform blocking I/O operations such as fetching resources.

Virtual threads are not faster threads; they do not run code any faster than platform threads. They exist to provide scale (higher throughput), not speed (lower latency).

![](../../../slides/images/threads-virtual-threads-mapped-to-carrier-threads.png)
![](../../../slides/images/threads-multiple-virtual-threads-mapped-to-one-carrier-thread.png)

## Resources

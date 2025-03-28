# Labs

## Lab 1: Implementing Resiliency Patterns

1. Implement a **datetime-service** returning the current date and time (read from internal clock).
    * `GET /date` → returns the current date
    * `GET /time` → returns the current time
2. Implement a **datetime-composite-service** returning the current date and time (communicates with a pool of instances of **datetime-service** for getting the current date and time).
    * `GET /datetime` → returns the current date and time
3. Implement an architecture delivering the above service in which **datetime-composite-service** finds healthy instances of **datetime-service** using Eureka service discovery and client-side load balancing.
4. ...


# Questions
1. What is resiliency, and why is it essential in distributed systems?
2. Describe the *one thread per request* pattern and its key issues (i.e., thread pool/memory saturation).
3. Which are the most used alternatives to the *one thread per request* pattern? Highlight their key features and mutual differences.
4. Describe the difference between client-side and server-side resiliency patterns.
5. Describe the most used client-side resiliency patterns. 
6. Describe the most used server-side resiliency patterns.
7. Describe pros and cons of *fixed-window*, *sliding-window*, *leaky bucket* policies for circuit breaker implementations.
8. What is a fallback mechanism and how does it relate with the circuit breaker pattern?
9. What role do retries play in improving the resiliency of microservices? Describe best practices for setting retry configuration.
10. What role do timeouts play in improving the resiliency of microservices? Describe best practices for setting timeout values.

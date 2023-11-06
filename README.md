# Distributed Edge Programming (DIEF, UNIMORE)

## Software
* Frameworks: spring boot
* Orchestration: docker, kubernetes
* Service discovery: eureka, consul
* Service mesh: istio, linkerd, dapr, envoy
* Message queue: kafka, rabbitmq
* Caching: redis, memcached 
* Observability: prometheus/grafana, jaeger, zipkin, elasticsearch/kibana (ELK)
* Distributed database: cockroachdb, cassandra, debezium

## Books
* Spring Microservices in Action; Carnell, Sánchez
* Microservices Patterns; Richardson
* Microservices with Spring Boot 3 and Spring Cloud; Larsson
* Microservices AntiPatterns and Pitfalls; Richards
* Cloud Native DevOps with Kubernetes; Arundel, Domingus
* Modern DevOps Practices; Agarwal
* Practical MLOps: Operationalizing Machine Learning Models; Gift, Deza; O'Reilly

## Videos
* [What is 12-Factor App?](https://www.youtube.com/watch?v=1OhmRmMsGdQ)
* [Fallacies of Distributed Systems](https://www.youtube.com/watch?v=8fRzZtJ_SLk&list=PL1DZqeVwRLnD3EjyciYAO82dT9Owiq8I5)
* [Visualising software architecture with the C4 model](https://www.youtube.com/watch?v=x2-rSnhpw0g&t=11s)
* [The Many Meanings of Event-Driven Architecture](https://www.youtube.com/watch?v=STKCRSUsyP0)
* [Event-Driven Architectures Done Right, Apache Kafka](https://www.youtube.com/watch?v=A_mstzRGfIE)
* [The ULTIMATE Guide to Spring Boot](https://www.youtube.com/watch?v=Nv2DERaMx-4)
* [Spring Boot Microservice Project Full Course](https://www.youtube.com/watch?v=mPPhcU7oWDU)

## Exam
* Answer general questions about the key topics of the course (1 or 2) (50% weight)
* Discuss the structure and engineering choices of a home project (50% weight)

## Modules
[W1-1] Introduction to microservices
* What is software architecture and why does it matter 
* Overview of architectural styles (monolithic, layered-monolithic, clean, modular-monolithic, microservices)
* Benefits, drawbacks, and anti-patterns of the microservice architecture
  * The scale cube and microservices
  * The twelve factors (of cloud-native applications)
  * The eight fallacies of Distributed Computing
* Microservices decomposition
  * Defining services (business capability/sub-domain pattern)
  * Establish service granularity
  * Defining service interfaces (REST/GraphQL/gRPC APIs)

[W1-2] Introduction to Spring Boot and Spring Cloud

[W2-1] Service configuration
* On managing configuration (and complexity)
* Building our Spring Cloud Configuration Server
* Integrating Spring Cloud Config with a Spring Boot client
* Protecting sensitive configuration information

[W2-1] Service discovery
* Service discovery in the cloud
* Building our Spring Eureka service
* Registering services with Spring Eureka
* Using service discovery to look up a service

[W2-2] Resiliency patterns (Resilience4j)
* Client-side resiliency patterns? 
* Implementing a circuit breaker
* Fallback processing
* Implementing the bulkhead pattern
* Implementing the retry pattern
* Implementing the rate limiter pattern

[W2-2] Service routing
* Introducing Spring Cloud Gateway
* Configuring the Spring Cloud Gateway to communicate with Eureka
* Automated mapping of routes via service discovery 
* Manually mapping routes using service discovery
* Dynamically reloading route configuration
* Predicate and Filter Factories 

[W3-1] Persistence

[W3-2] Event-driven architectures
* The case for messaging, EDA, and microservices
* Introducing Spring Cloud Stream
* Configuring Apache Kafka and Redis in Docker 
* Spring Cloud Stream use case: Distributed caching 

[W4-1] Distributed tracing 
* Spring Cloud Sleuth and the correlation ID
* Log aggregation and Spring Cloud Sleuth
* Distributed tracing with Zipkin 

[W4-2] Deploying microservices
* The architecture of a build/deployment pipeline
* Setting up the core infrastructure in the cloud
* Deploying with ELK
* Your build/deployment pipeline in action

[W5-1] 
* Chapters 15-16 Larsson

[W5-2] 
* Chapters 17-18 Larsson

## FAQ
n/a

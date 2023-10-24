# Distributed Edge Programming (DIEF, UNIMORE)

## Software
* Frameworks: spring boot, quarkus, micronaut 
* Orchestration: docker, kubernetes
* Service discovery: eureka
* Service mesh: istio, linkerd, dapr, envoy
* Message queue: kafka, rabbitmq
* Caching: redis, memcached 
* Observability: prometheus/grafana, consul, jaeger, zipkin, elasticsearch/kibana (ELK)
* Distributed database: cockroachdb, cassandra, debezium

## Books
* Microservices Patterns; Richardson
* Microservices with Spring Boot 3 and Spring Cloud; Larsson
* Microservices AntiPatterns and Pitfalls; Richards
* Cloud Native DevOps with Kubernetes; Arundel, Domingus
* Designing Distributed Systems Patterns and Paradigms for Scalable, Reliable Services; Burns
* Practical MLOps: Operationalizing Machine Learning Models; Gift, Deza; O'Reilly

## Articles
* [microservices.io](https://microservices.io/index.html)
* [vinsguru](https://www.vinsguru.com/)
* [Azure Design Patterns](https://learn.microsoft.com/en-us/azure/architecture/patterns/)
* [Java Microservices Collection](https://dzone.com/articles/java-microservices-tutorials-and-articles)
* [The Complete Kubernetes Collection](https://dzone.com/articles/the-complete-kubernetes-collection-tutorials-and-tools)
* [Building Mancala Game in Microservices Using Spring Boot](https://dzone.com/articles/mancala-game-implementation-using-microservices-ap)
* [Dynamo: Amazon’s Highly Available Key-value Store](https://www.allthingsdistributed.com/2007/10/amazons_dynamo.html)
* [Bigtable: A Distributed Storage System for Structured Data](https://static.googleusercontent.com/media/research.google.com/en//archive/bigtable-osdi06.pdf)
* [Service Discovery in Microservices](https://www.baeldung.com/cs/service-discovery-microservices)
* [Distributed Tracing Infrastructure with Jaeger on Kubernetes](https://masroorhasan.medium.com/tracing-infrastructure-with-jaeger-on-kubernetes-6800132a677)

## Videos
* [The Many Meanings of Event-Driven Architecture](https://www.youtube.com/watch?v=STKCRSUsyP0)
* [Event-Driven Architectures Done Right, Apache Kafka](https://www.youtube.com/watch?v=A_mstzRGfIE)
* [Spring Boot Microservice Project Full Course](https://www.youtube.com/watch?v=mPPhcU7oWDU)
* [Visualising software architecture with the C4 model](https://www.youtube.com/watch?v=x2-rSnhpw0g&t=11s)

## Code Examples
* https://github.com/vinsguru/vinsguru-blog-code-samples/

## Exam
* Answer general questions about the key topics of the course (1 or 2) (50% weight)
* Discuss the structure and engineering choices of a home project (50% weight)

## Modules
[1] Transitioning from monolithic architectures to microservices

* What is software architecture and why does it matter 
* Overview of architectural styles (monolithic,layered-monolithic, clean, modular-monolithic, microservices)
* Microservices as a form of modularity (scale cube)
* Benefits, drawbacks, and anti-patterns of the microservice architecture
* The scale cube and microservices
* The twelve factors (of cloud-native applications)
* The eight fallacies of Distributed Computing

[2] Decomposition strategies 
* Defining services
    * Decompose by business capability pattern
    * Decompose by sub-domain pattern
* Decomposition guidelines
* Defining service APIs

[3] Synchronous remote procedure invocation
* REST/GraphQL/gRPC APIs
* Service discovery pattern
    * Client-side service discovery
    * Server-side service discovery
* Handling failures (resiliency patterns)
    * Circuit breaker, retry, fallback
    * Rate limiter, time limiter
    * Bulkhead
    * Cache

[4] Asynchronous messaging
* Benefits and challenges of asynchronous Communication
* Messaging-based service APIs
* Using a message broker
* Competing receivers and message ordering
* Transactional messaging
* Libraries and frameworks for messaging
* Kafka and RabbitMQ architectures

[5] Managing transactions and queries
* The need for distributed transactions in a microservice architecture
* Using the Saga pattern to maintain data consistency
    * Choreography-based sagas
    * Orchestration-based sagas
* Handling the lack of transactions isolation
* The API composition pattern 
* The CQRS pattern

[6] Business logic in a microservice architecture
* Business logic organization patterns
    * Transaction script pattern
    * Domain model pattern
    * Domain-driven aggregate pattern
* Designing business logic with aggregates
* Generating and publishing domain events
* Consuming domain events

[7] Business logic with event sourcing
* The trouble with traditional persistence
* Event sourcing and publishing events
* Using snapshots to improve performance
* Idempotent message processing
* Implementing an event store
* Using sagas and event sourcing together
    * Choreography-based sagas using event sourcing
    * Orchestration-based sagas using event sourcing

[8] API Gateways
* The API gateway pattern
* Backends for Frontends pattern
* Implementing an API gateway
    * Using an off-the-shelf API gateway product/service
    * Developing your own API gateway
    * Implementing an API gateway using GraphQL

[9] Testing microservices
* Testing strategies for microservice architectures
    * The challenge of testing microservices
    * The deployment pipeline
* Writing tests
    * Unit tests
    * Integration tests
    * Component tests 
    * End-to-end tests
* Introducing chaos-mesh

[10] Production-ready services
* Developing secure services
* Designing configurable services
* Designing observable services
    * Health check API pattern 
    * Log aggregation pattern (Elasticsearch/Logstash/Kibana)
    * Distributed tracing pattern (OpenTelemetry/Zipkin)
    * Application metrics pattern (Prometheus/Grafana)
* Developing services using the Microservice chassis pattern
* From microservice chassis to service mesh (Istio/Linkerd/Dapr/Envoy)

[11] Deploying microservices
* Deployment strategies
    * Language-specific packaging pattern
    * Virtual machine pattern
    * Service container pattern
    * Serverless pattern
* Deploying with Kubernetes
* Devops CI/CD IaC GitOps

[12] AI-enabled microservices
* Integrating ML models and reusing workflows
* Packaging services and ML models
* Monitoring ML models in production
* ML patterns (Resilient Serving, reproducibility)

## FAQ
n/a

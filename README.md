# Distributed Edge Programming (DIEF, UNIMORE)

## Software
* [spring boot](https://spring.io/projects/spring-boot)
* [docker](https://www.docker.com/), [kubernetes](https://kubernetes.io/), [istio](https://istio.io/)
* [kafka](https://kafka.apache.org/), [rabbitmq](https://www.rabbitmq.com/)
* [redis](https://redis.io/), [memcached](https://memcached.org/) 
* [prometheus](https://prometheus.io/), [grafana](https://grafana.com/)
* [cockroachdb](https://www.cockroachlabs.com/), [cassandra](https://cassandra.apache.org/), [debezium](https://debezium.io/)

## Books
* Microservices Patterns; Richardson
* Designing Distributed Systems Patterns and Paradigms for Scalable, Reliable Services; Burns
* Hands-On Microservices with Kubernetes; Sayfan
* Practical MLOps: Operationalizing Machine Learning Models; Gift, Deza; O'Reilly

## Articles
* [microservices.io](https://microservices.io/index.html)
* [vinsguru](https://www.vinsguru.com/)
* [Azure Design Patterns](https://learn.microsoft.com/en-us/azure/architecture/patterns/)

## Videos
* [The Many Meanings of Event-Driven Architecture](https://www.youtube.com/watch?v=STKCRSUsyP0)
* [Event-Driven Architectures Done Right, Apache Kafka](https://www.youtube.com/watch?v=A_mstzRGfIE)

## Code Examples
* https://github.com/vinsguru/vinsguru-blog-code-samples/

## Modules
[] Transitioning from monolithic architectures to microservices

* What is software architecture and why does it matter 
* Overview of architectural styles (monolithic,layered-monolithic, clean, modular-monolithic, microservices)
* Microservices as a form of modularity (scale cube)
* Benefits and drawbacks of the microservice architecture
* Beyond microservices: process and organization
* The Twelve Factors (of cloud-native applications)
* The Eight Fallacies of Distributed Computing

[] Decomposition strategies 
* Defining services
    * Decompose by business capability pattern
    * Decompose by sub-domain pattern
* Decomposition guidelines
* Defining service APIs

[] Synchronous remote procedure invocation
* Using REST/GraphQL/gRPC
* Handling partial failure using the Circuit breaker pattern
* Using service discovery
* API Gateways
    * Routing pattern
    * Aggregation Pattern
    * Offloading Pattern
    * Service Registry/Discovery Pattern

[] Asynchronous messaging
* Benefits and challenges of asynchronous Communication
* Messaging-based service APIs
* Using a message broker
* Competing receivers and message ordering
* Transactional messaging
* Libraries and frameworks for messaging
* Kafka and RabbitMQ architectures

[] Distributed data management
* The Database-per-Service Pattern (Polyglot Persistence)
* Database sharding
* Cross-service queries
    * Materialized View and CQRS patterns
    * Transactional boundaries
* Cross-service transactions
    * SAGA pattern (choreography and orchestration)
    * Transactional Outbox pattern
    * Change Data Capture (CDC) pattern
* Distributed caching

[] Resilience, Observability and monitoring
* Microservices Observability with Elastic Stack 
* Microservices Distributed Tracing with OpenTelemetry and Zipkin
* Microservices Health Monitoring with Kubernetes, Prometheus and Grafana

[] Orchestrators
* Kubernetes design-patterns
    * Single node patterns (Sidecar, Ambassador, Adapters)
    * Serving patterns (Replicated service, load-balanced service, gather/scatter)
    * Batch patterns (Queue Systems, Event-driven, coordinated batches)

[] Communications (Services Mesh)

[] Backing Services (K8s Databases, Caches, Message Brokers)

[] Scalability (HPA, KEDA)

[] Devops CI/CD IaC GitOps

[] Monitoring & Observability

[] AI-enabled microservices
* Integrating ML models and reusing workflows
* Packaging applications, services, and ML models
* Monitoring ML models with stream processing systems
* ML patterns (Resilient Serving, reproducibility)




## Exam

## FAQ

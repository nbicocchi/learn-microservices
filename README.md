# Learn Microservices

## Software
* Frameworks: spring boot
* Orchestration: docker, podman, kubernetes
* Service discovery: eureka, consul
* Service mesh: istio, linkerd, dapr, envoy
* Message queue: kafka, rabbitmq
* Caching: redis, memcached 
* Observability: prometheus/grafana, jaeger, zipkin, elasticsearch/kibana (ELK)
* Distributed database: cockroachdb, cassandra, debezium

## Books
* **Microservices Patterns; Richardson**
* **Spring Microservices in Action; Carnell, Sánchez**
* **Microservices with Spring Boot 3 and Spring Cloud; Larsson**
* Microservices AntiPatterns and Pitfalls; Richards
* Cloud Native DevOps with Kubernetes; Arundel, Domingus
* Modern DevOps Practices; Agarwal
* Practical MLOps: Operationalizing Machine Learning Models; Gift, Deza; O'Reilly

## Modules
These topics assume a decent understanding of Spring Boot 3. A dedicated course can be found at [https://github.com/nbicocchi/learn-spring-boot](https://github.com/nbicocchi/learn-spring-boot).

* Introduction to microservices
* Reactive microservices
* Event-driven async microservices
* Service discovery
* Service routing
* Centralized configuration
* Resiliency patterns (Resilience4j)
* Observability (Tracing, Metrics, Log aggregation)
* Deploying Microservices to Kubernetes
* Implementing Kubernetes Features to Simplify the Microservices Landscape
* Using a Service Mesh to Improve Observability and Management

## Project Ideas
Alternative microservices frameworks: 
* Quarkus
* Micronaut
* https://gokit.io/
* https://github.com/python-microservices/pyms

Design Patterns
* Patterns for container-based distributed systems https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/45406.pdf
* Patterns for data management (SAGAs, API composition, CQRS, ...) https://microservices.io/patterns/data/saga.html)
* Patterns for distributed caching (Redis/Memcached)

Benchmarking protocols/architectures
* https://httpd.apache.org/docs/2.4/programs/ab.html

Kubernetes
* Introduction to Kubernetes
* Service mesh: istio, linkerd, dapr, envoy
* https://chaos-mesh.org/

Communications
* https://graphql.org/
* https://protobuf.dev/
* https://avro.apache.org/

Ops
* MLOps: investigate how to develop/deploy/maintain ML models within a microservices ecosystem
* SECops: see [here](https://www.practical-devsecops.com/securing-microservices-architecture-with-devsecops-and-kubernetes/) for inspiration

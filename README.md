# Learn Microservices

## Prerequisites
The course requires a decent understanding of Java. A dedicated course can be found [here](https://github.com/nbicocchi/learn-java-core).

## Software
* [JDK Development Kit 21](https://www.oracle.com/it/java/technologies/downloads/)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) [Plugins: [Envfile](https://plugins.jetbrains.com/plugin/7861-envfile),[Mermaid](https://plugins.jetbrains.com/plugin/20146-mermaid)]
* [Apache Maven](https://maven.apache.org/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## Books
* Microservices Patterns; Richardson

## Videos
* [MIT 6.824 Distributed Systems](https://www.youtube.com/watch?v=cQP8WApzIQQ&list=PLrw6a1wE39_tb2fErI4-WkMbsvGQk9_UB)

## Modules
[M1 Introduction](modules/introduction)
* Software production metrics
* Monolithic and distributed architectures
* Automation in software production (DevOps, DevSecOps) 

[M2 Microservices chassis](modules/chassis)
* Java microservices (Maven, Spring Boot)
* Python microservices (FastAPI)

[M3 Containerization and orchestration](modules/containerization)
* Building *docker* images (single-layer, multi-layer)
* Container orchestration with *docker compose*
* Orchestration for the edge-cloud continuum (K8s, K3s, MicroK8s, KubeEdge)

[M4 Synchronous Communication](modules/communication-sync)
* Synchronous communications and their limitations
* GraphQL and Protobuf as a REST alternatives
* Latency-sensitive communication

[M5 Infrastructure](modules/infrastructure)
* Service discovery, routing, configuration
* Edge-cloud hybrid routing strategies
* Raft consensus algorithm

[M6 Observability](modules/observability)
* Instrumentation and maintenance costs
* Key observability stacks: *Prometheus*, *ELK*, *Jaeger/Zipkin*, *Grafana*
* *OpenTelemetry*

[M7 Resiliency](modules/resiliency)
* Client-side patterns
* Server-side patterns

[M8 ASynchronous Communication](modules/communication-sync)
* Asynchronous messaging systems (*RabbitMQ*)
* https://groups.csail.mit.edu/tds/papers/Lynch/jacm85.pdf
* Event-driven architectures
* Event-sourcing architectures

[M9 Distributed data management](modules/data-management)
* The SAGA pattern
* The CQRS pattern
* The *Conductor* orchestration framework

[M10 Security]()
* JWT...

[M11 MLOps](modules/mlops)
* The MLOps model applied to edge-cloud contexts
* Tracking ML experiments with MLFlow
* Observing ML models in production with EvidentlyAI
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

## Contributing
Your pull requests are very welcome! To contribute, please refer to [this](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request) guide. For a more general introduction to GitHub, refer to [this](https://github.com/skills/) page. By contributing to this repository, you agree that your contributions will be licensed under the LICENSE file in the root directory of this source tree.

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
* Lightweight orchestration for the edge (K3s, MicroK8s, KubeEdge)

[M4 Synchronous Communication](modules/communication-sync)
* Synchronous communications and their limitations
* GraphQL and Protobuf as a REST alternatives
* Latency-sensitive communication

[M5 Infrastructure](modules/infrastructure)
* Service discovery, routing, configuration
* Edge-cloud hybrid routing strategies
* The RAFT distributed algorithm

[M6 Observability](modules/observability)
* Instrumentation and maintenance costs
* Key observability stacks: *Prometheus*, *ELK*, *Jaeger/Zipkin*, *Grafana*
* *OpenTelemetry*

[M7 Resiliency](modules/resiliency)
* Client-side patterns
* Server-side patterns

[M8 ASynchronous Communication](modules/communication-sync)
* Asynchronous messaging systems (*RabbitMQ*)
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
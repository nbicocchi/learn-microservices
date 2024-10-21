# Learn Microservices

## Prerequisites
The course requires a decent understanding of Java. A dedicated course can be found [here](https://github.com/nbicocchi/learn-java-core).

## Software
* [JDK Development Kit 21](https://www.oracle.com/it/java/technologies/downloads/)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) [Plugins: [Envfile](https://plugins.jetbrains.com/plugin/7861-envfile)]
* [Apache Maven](https://maven.apache.org/)
* [Apache JMeter](https://jmeter.apache.org/)
* [EvidentlyAI](https://github.com/evidentlyai/evidently)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## Books
* Microservices Patterns; Richardson
* Spring Microservices in Action; Carnell, Sánchez
* Microservices with Spring Boot 3 and Spring Cloud; Larsson

## Videos
* [Mark Richards' YouTube Channel](https://www.youtube.com/@markrichards5014/videos)
* [MIT 6.824 Distributed Systems](https://www.youtube.com/watch?v=cQP8WApzIQQ&list=PLrw6a1wE39_tb2fErI4-WkMbsvGQk9_UB)

## Contributing
Your pull requests are very welcome! To contribute, please refer to [this](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request) guide. For a more general introduction to GitHub, refer to [this](https://github.com/skills/) page. By contributing to this repository, you agree that your contributions will be licensed under the LICENSE file in the root directory of this source tree.

## Modules
[M1 Software architectures](modules/software-architectures)
* Monolithic and distributed architectural styles
* The transition towards microservices architectures

[M2 Tools: Java, Maven, Spring Boot, JMeter](modules/tools)
* Java recap: generics, functional programming
* Building a basic layered microservice with Spring Boot
* Project packaging with Maven (one or multiple services per project)
* Testing microservices with JMeter

[M3 Containerization and orchestration](modules/containerization)
* Containerization vs virtualization
* Building *docker* images (single-layer, multi-layer, Buildpacks, Jib)
* Container orchestration with *docker compose*

[M4 Communication](modules/communication)
* Synchronous communications and their limitations (*Rest/GraphQL/Protobuf*)
* Asynchronous messaging systems (*RabbitMQ*)
* *Spring Cloud Stream* and its abstraction over messaging systems

[M5 Infrastructure](modules/infrastructure)
* Service discovery
* Service routing 
* Centralized configuration

[M6 Resiliency](modules/resiliency)
* What is resiliency and why it is relevant
* Client-side resiliency patterns
* Server-side resiliency patterns
* *Resilience4j* and its integration with Spring Boot

[M7 Observability](modules/observability)
* What is observability and why it is relevant
* Instrumentation and maintenance costs
* Key aggregation platforms: ELK stack, Grafana stack, Jaeger, Zipkin
* Zero-code instrumentation with OpenTelemetry

[M8 Distributed transaction management](modules/data-management)
* Issues of database transactions in distributed contexts
* The SAGA pattern
* The *Conductor* orchestration framework

[M9 DevSecOps](modules/devsecops)
* DevOps and their benefits in distributed contexts
* Automating security tests before deployment

[M10 MLOps](modules/devsecops)
* MLOps and their benefits in distributed contexts
* Automating model observability with EvidentlyAI
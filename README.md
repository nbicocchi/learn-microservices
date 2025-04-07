# Learn Microservices

## Prerequisites
The course requires a decent understanding of Java. A dedicated course can be found [here](https://github.com/nbicocchi/learn-java-core).

## Software
* [JDK Development Kit 21](https://www.oracle.com/it/java/technologies/downloads/)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) [Plugins: [Envfile](https://plugins.jetbrains.com/plugin/7861-envfile)]
* [Apache Maven](https://maven.apache.org/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* [mlflow](https://mlflow.org/)
* [EvidentlyAI](https://github.com/evidentlyai/evidently)

## Books
* Microservices Patterns; Richardson
* Spring Microservices in Action; Carnell, Sánchez
* Microservices with Spring Boot 3 and Spring Cloud; Larsson

## Videos
* [MIT 6.824 Distributed Systems](https://www.youtube.com/watch?v=cQP8WApzIQQ&list=PLrw6a1wE39_tb2fErI4-WkMbsvGQk9_UB)

## Contributing
Your pull requests are very welcome! To contribute, please refer to [this](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request) guide. For a more general introduction to GitHub, refer to [this](https://github.com/skills/) page. By contributing to this repository, you agree that your contributions will be licensed under the LICENSE file in the root directory of this source tree.

## Modules
[M1 Software architectures](modules/software-architectures)

* Monolithic and distributed architectural styles
* The transition towards microservices architectures

[M2 Tools: Maven, Spring Boot](modules/tools)
* Introduction to Spring Boot as microservice chassis
* Building a basic layered microservice with Spring Boot
* Packaging a project with Maven

[M3 Containerization and orchestration](modules/containerization)
* Containerization vs virtualization
* Building *docker* images (single-layer, multi-layer, Buildpacks, Jib)
* Container orchestration with *docker compose*

[M4 Communication](modules/communication)
* Synchronous communications and their limitations (*Rest/GraphQL/Protobuf*)
* Asynchronous messaging systmettere qualcosa di caching ems (*RabbitMQ*)
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
* Key observability stacks: *Prometheus*, *ELK*, *Jaeger/Zipkin*, *Grafana*
* *OpenTelemetry*

[M8 Distributed transaction management](modules/data-management)
* Issues of database transactions in distributed contexts
* The SAGA pattern
* The CQRS pattern
* The *Conductor* orchestration framework

[M9 DevSecOps](modules/devsecops)
* The DevSecOps model and its benefits in distributed contexts
* Automating code linting, security testing, and deployment with GitHub Actions

[M10 MLOps](modules/mlops)
* The MLOps model and its benefits in distributed contexts
* Tracking ML experiments with MLFlow
* Observing ML models in production with EvidentlyAI

## Project Ideas
- Studiare e documentare il problema della modellazione un sistema distribuito (definire microservizi, API, bounded contexts).
- Confrontare REST, GraphQL e Protobuf implementando un servizio con tutte e tre le tecnologie e analizzandone le differenze.
- Continuare a sviluppare progetto già presente di un social network.
- Studiare e documentare l’architettura distribuita di piattaforme come Facebook, Uber o Netflix.
- Creare un laboratorio interattivo per testare la resilienza di microservizi utilizzando strumenti di osservabilità (testare anche signoz e openobserve).
- Eseguire test di performance su un sistema distribuito con strumenti come Istio, Locust, k6 o Gatling.
- Utilizzare **[Microsim](https://github.com/yurishkuro/microsim)** per simulare il comportamento di un’architettura a microservizi in scenari di carico e guasto.
- Esplorare progetti open-source tra quelli elencati in **[Awesome Distributed System Projects](https://github.com/roma-glushko/awesome-distributed-system-projects?tab=readme-ov-file)**.
- Porting current examples in Python
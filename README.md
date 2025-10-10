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
[M1 Software architectures](modules/software-architectures)
* Monolithic and distributed architectural styles
* The transition towards microservices architectures

[M2 Tools: Maven, Spring Boot](modules/tools)
* Introduction to Spring Boot as microservice chassis
* Building a basic layered microservice with Spring Boot
* Packaging a project with Maven

[M3 Containerization and orchestration](modules/containerization)
* Introduction to containerization
* Building *docker* images (single-layer, multi-layer)
* Container orchestration with *docker compose*

[M4 Communication](modules/communication-sync)
* Synchronous communications and their limitations (*Rest/GraphQL/Protobuf*)
* Asynchronous messaging systems (*RabbitMQ*)
* *Spring Cloud Stream* and its abstraction over messaging systems

[M5 Infrastructure](modules/infrastructure)
* Service discovery
* Service routing 
* Centralized configuration

[M6 Resiliency](modules/resiliency)
* Client-side patterns
* Server-side patterns
* *Resilience4j* and its integration with Spring Boot

[M7 Observability](modules/observability)
* Instrumentation and maintenance costs
* Key observability stacks: *Prometheus*, *ELK*, *Jaeger/Zipkin*, *Grafana*
* *OpenTelemetry*

[M8 Distributed transaction management](modules/data-management)
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

* Study and document the problem of modeling a distributed system (define microservices, APIs, bounded contexts).
* Compare REST, GraphQL, and Protobuf by implementing a service using all three technologies and analyzing their differences.
* Continue developing an existing social network project.
* Study and document the distributed architecture of platforms like Facebook, Uber, or Netflix.
* Create an interactive lab to test the resilience of microservices using observability tools (also test Signoz and OpenObserve).
* Perform performance testing on a distributed system using tools like Istio, Locust, k6, or Gatling.
* Use **[Microsim](https://github.com/yurishkuro/microsim)** to simulate the behavior of a microservices architecture under load and failure scenarios.
* Explore open-source projects listed in **[Awesome Distributed System Projects](https://github.com/roma-glushko/awesome-distributed-system-projects?tab=readme-ov-file)**.
* Deepen knowledge of automatic deployment tools (e.g., Chef, Puppet, Ansible).
* Port current examples to Python.

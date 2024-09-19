# Learn Microservices

## Prerequisites
These topics assume a decent understanding of both Java and Spring Boot 3. Dedicated courses can be found:
* [https://github.com/nbicocchi/learn-java-core](https://github.com/nbicocchi/learn-java-core)
* [https://github.com/nbicocchi/learn-spring-boot](https://github.com/nbicocchi/learn-spring-boot)

## Software
* [JDK Development Kit 21](https://www.oracle.com/it/java/technologies/downloads/)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) + [Envfile Plugin](https://plugins.jetbrains.com/plugin/7861-envfile)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* [Apache JMeter](https://jmeter.apache.org/)

## Books
* Microservices Patterns; Richardson
* Spring Microservices in Action; Carnell, Sánchez
* Microservices AntiPatterns and Pitfalls; Richards
* Modern DevOps Practices; Agarwal

## Videos
* [Mark Richards' YouTube Channel](https://www.youtube.com/@markrichards5014/videos)
* [MIT 6.824 Distributed Systems](https://www.youtube.com/watch?v=cQP8WApzIQQ&list=PLrw6a1wE39_tb2fErI4-WkMbsvGQk9_UB)

## Contributing
Your pull requests are very welcome! To contribute, please refer to [this](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request) guide. For a more general introduction to GitHub, refer to [this](https://github.com/skills/) page. By contributing to this repository, you agree that your contributions will be licensed under the LICENSE file in the root directory of this source tree.

## Modules
[M1 Software architectures](modules/software-architectures)
* Monolithic and distributed architectural styles
* The transition towards microservices architectures
* #8 Fallacies of distributed computing

[M2 Tools: Java, Maven, Spring Boot](modules/spring-boot)

[M3 Containerization and orchestration](modules/containerization)

[M4 Communications](modules/communication)
* Synchronous communications (REST/GraphQL/Protobuf) and their limitations
* Asynchronous messaging systems (*Kafka* and *RabbitMQ*), their advantages and limitations
* *Spring Cloud Stream* and its abstraction over messaging systems

[M4 Infrastructure](modules/infrastructure) 
* Service discovery
* Service routing 
* Centralized configuration

[M5 Resiliency](modules/resiliency)
* What is resiliency and why it is relevant
* Client-side resiliency patterns
* Server-side resiliency patterns
* *Resilience4j* and its integration with Spring Boot

[M6 Observability](modules/resiliency)
* What is observability and why it is relevant for microservices
* Metrics aggregation with *Micrometer*, *Prometheus* and *Grafana*
* Distributed tracing with *Micrometer* and *Zipkin*
* Log aggregation with the *ELK* Stack

[M8 Data management](modules/data-management)

[M9 Distributed caching](modules/caching)

[M10 DevSecOps](modules/devsecops)

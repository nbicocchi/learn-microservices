# Learn Microservices

## Prerequisites
These topics assume a decent understanding of both Java and Spring Boot 3. Dedicated courses can be found:
* [https://github.com/nbicocchi/learn-java-core](https://github.com/nbicocchi/learn-java-core)
* [https://github.com/nbicocchi/learn-spring-boot](https://github.com/nbicocchi/learn-spring-boot)

## Software
* [JDK Development Kit 21](https://www.oracle.com/it/java/technologies/downloads/)
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) + [Envfile Plugin](https://plugins.jetbrains.com/plugin/7861-envfile)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* [minikube](https://minikube.sigs.k8s.io/)
* [Apache JMeter](https://jmeter.apache.org/)

## Books
* **Microservices Patterns; Richardson**
* **Spring Microservices in Action; Carnell, Sánchez**
* **Microservices with Spring Boot 3 and Spring Cloud; Larsson**
* Microservices AntiPatterns and Pitfalls; Richards
* Cloud Native DevOps with Kubernetes; Arundel, Domingus
* Modern DevOps Practices; Agarwal
* Practical MLOps: Operationalizing Machine Learning Models; Gift, Deza; O'Reilly

## Videos
* [Mark Richards' YouTube Channel](https://www.youtube.com/@markrichards5014/videos)

## Contributing
Your pull requests are very welcome! To contribute, please refer to [this](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request) guide. For a more general introduction to GitHub, refer to [this](https://github.com/skills/) page. By contributing to this repository, you agree that your contributions will be licensed under the LICENSE file in the root directory of this source tree.

## Modules
[M1] Introduction to microservices
* Monolithic and distributed architectural styles
* The transition towards microservices architectures
* The *Twelve Factor App*
* *#8 Fallacies of distributed computing*

[M2] Reactive microservices
* The dangers of *one-thread-per-request* design pattern
* What is a reactive microservice ?
* Java virtual threads and the *Reactor* pattern
* Implementing reactive microservice with *Project Reactor*'s *Mono* and *Flux*

[M3] Event-driven async architectures
* Issues of synchronous communications
* Asynchronous messaging systems, their advantages and limitations
* Introduction to *Kafka* and *RabbitMQ* messaging systems
* *Spring Cloud Stream* and its abstraction over messaging systems

[M4] Service discovery
* DNS-based vs cloud-native service discovery
* Server-side vs client-side load-balancing
* Implementing service discovery with *Netflix Eureka*
* Implementing client-side load-balancing with *Spring WebClient*

[M5] Service routing
* Implementing cross-cutting concerns with a library or gateway service
* *Spring Cloud Gateway* and its architecture
* *Spring Cloud Gateway* routing rules

[M6] Centralized configuration
* Configuration management architecture
* Key principles behind configuration management
* Centralize microservices configuration with *Spring Cloud Config*

[M7] Microservices resiliency
* What is resiliency and why it is relevant for microservices
* Client-side resiliency patterns
* Server-side resiliency patterns
* *Resilience4j* and its integration with Spring Boot

[M8] Microservices observability
* What is observability and why it is relevant for microservices
* Metrics aggregation with *Micrometer*, *Prometheus* and *Grafana*
* Distributed tracing with *Micrometer* and *Zipkin*
* Log aggregation with the *ELK* Stack

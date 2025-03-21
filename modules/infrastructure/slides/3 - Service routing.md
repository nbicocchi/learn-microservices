# Service Routing

## Implementing Cross-cutting concerns
In microservice architectures, usually comes a point where we’ll need to ensure that critical *cross-cutting concerns* such as security, logging, and tracking users across multiple service calls occur. **We want these functionalities to be consistently enforced across all services** without the need for each team to build their own solution. 

### Implementing cross-cutting concerns with a shared library
While it’s possible to use a common library for embedding these capabilities into service, doing so has several (negative) implications:
* **Consistency:** It’s challenging to implement these capabilities in each service consistently. 
* **Bug Proness:** Pushing the responsibilities to implement cross-cutting concerns like security and logging down to the individual teams greatly increases the odds that someone will not implement them properly or will forget to do them. 
* **Flexibility:** It’s possible to create a hard dependency across all our services. The more capabilities we build into a common framework shared across all our services, the more difficult it is to change or add behavior in our common code without having to recompile and redeploy all our services. Suddenly an upgrade of core capabilities built into a shared library becomes a long migration process.

### Implementing cross-cutting concerns with a gateway service
To solve these issues, we need to abstract these cross-cutting concerns into a service that can sit independently and act as a filter and router for all the microservice calls in our architecture. We call this service a *gateway*. Clients no longer directly call a microservice. Instead, all calls are routed through the service gateway, which acts as a single *Policy Enforcement Point (PEP)*, and are then routed to a final destination.

The use of a centralized *PEP* means that cross-cutting service concerns can be carried out in a single place without the individual development teams having to implement those concerns. Examples of cross-cutting concerns that can be implemented in a service gateway:
* **Static routing** A service gateway places all service calls behind a single URL and API route. This simplifies development as we only have to know about one service endpoint for all of our services.
* **Dynamic routing** A service gateway can inspect incoming service requests and, based on the data from the incoming request, perform intelligent routing for the service caller. For instance, customers participating in a beta program might have all calls to a service routed to a specific cluster of services that are running a different version of code.
* **Authentication and authorization** Because all service calls route through a service gateway, the service gateway is a natural place to check whether the callers of a service have authenticated themselves.
* **Metric collection and logging** A service gateway can be used to collect metrics and log information as a service call passes through it. You can also use the service gateway to confirm that critical pieces of information are in place for user requests, thereby ensuring that logging is uniform. 

## API Gateway Pattern

The **API Gateway** is a design pattern commonly used in microservices architectures to handle incoming requests from clients and route them to the appropriate backend microservices. It serves as a reverse proxy that consolidates requests, performing functions such as authentication, routing, load balancing, and request transformation. The API Gateway acts as a single entry point into the system, abstracting the complexity of individual microservices from the client.

**Advantages:**
- **Centralized Control:** The API Gateway consolidates all external traffic, simplifying the management of cross-cutting concerns like security and monitoring.
- **Client Simplicity:** Clients interact with a single endpoint (the API Gateway), abstracting away the complexities of calling multiple microservices.
- **Performance Optimizations:** The gateway can cache responses, compress payloads, or aggregate data from multiple services into a single response.

**Challenges:**
- **Single Point of Failure:** The API Gateway can become a bottleneck or a single point of failure if not properly managed and scaled.
- **Increased Complexity:** The API Gateway itself becomes a critical component that needs to be maintained, monitored, and optimized.

**Correlated with Server-Side Load Balancing:**
API Gateways often implement **server-side load balancing** by distributing requests among multiple instances of the same service. This ensures even traffic distribution without burdening the client with choosing a specific service instance, providing more control over service performance.

## Backend for Frontends (BFF) Pattern

The **Backends for Frontends (BFF)** pattern is a variation of the API Gateway pattern that provides specialized backends for different types of clients (e.g., web, mobile, IoT devices). Instead of having a single, monolithic API Gateway, the BFF pattern creates a separate backend tailored to the specific needs of each frontend application. This allows the backend to serve optimized responses that fit the specific requirements of different client types.

**Advantages:**
- **Customization for Each Client:** Each frontend receives data in a format that is most useful to it, leading to better performance and a more efficient user experience.
- **Reduced Latency for Frontends:** Because the BFF is tailored to specific clients, it can pre-aggregate data from various microservices, reducing the number of round trips between the client and backend.
- **Decoupled Frontend Development:** Teams can develop and evolve frontend applications independently, without worrying about impacting other clients.

**Challenges:**
- **Increased Maintenance:** Managing multiple backends for different clients can introduce more complexity and increase the overhead of maintaining different APIs.
- **Redundancy of Logic:** Certain business logic might need to be replicated across different BFFs, leading to code duplication.

**Differences from API Gateway:**
- **Granularity:** The API Gateway is a single entry point for all clients, while BFFs provide multiple, client-specific entry points.
- **Client Optimization:** BFFs are focused on optimizing for the needs of specific clients (e.g., mobile vs. desktop), whereas the API Gateway is a more general-purpose solution.
- **Complexity:** The API Gateway provides a unified control point, while BFFs offer more flexibility at the cost of increased backend complexity.

The **BFF pattern** is particularly useful in scenarios where different types of clients require different types of data or functionality, allowing for more efficient and customized interactions.

## Resources
- Spring Microservices in Action (Chapter 8)
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 10)
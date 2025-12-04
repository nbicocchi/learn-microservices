# Software Architectures

## What is Software Architecture?

Architecture defines **components, connectors, and constraints** that shape how systems are organized.
Different styles change **local complexity** (inside modules) and **global complexity** (system-wide).

![](images/software-architectures.webp)

---

## Monolithic Architectures

A **single, unified application** where all parts run in one process.

![](images/ftgo-monolitic-architecture.webp)

**Pros**

* Simple development tools
* Straightforward testing
* Easy deployment
* Fast in-process communication

**Cons**

* Tends toward a *big ball of mud*
* Slow builds and startup
* Hard to refactor
* Tech lock-in
* Can't scale or deploy individual pieces

**When Monoliths Grow**

* Slow development loops
* Heavy end-to-end testing
* Long, risky deployments
* *Big Ball of Mud*

![](images/swarch-big-ball-of-mud.webp)

![](images/ftgo-monolitic-hell.webp)


---

## Multi-Layered (N-tier) Architecture

Application split into technical layers (UI → business → data).

![](images/swarch-layered.webp)
![](images/swarch-layered-open.webp)

**Pros**

* Clear separation of concerns
* Easy for teams to work on different layers
* Familiar and well-supported

**Cons**

* Vertical features require changes across layers
* Inefficient message flow through layers
* Harder to refactor around domain boundaries

**Use**

* Simple applications
* Technically structured teams

---

## Clean Architecture

Domain and use-cases at the center; frameworks and infrastructure at the edges.

![](images/clean-architecture.webp)

**Pros**

* Strong decoupling
* Easy to test business logic
* Swap DB/UI/frameworks without touching core logic

**Cons**

* More abstraction and indirection
* Requires architectural discipline

**Use**

* Systems expected to evolve for years
* Applications with complex business rules

---

## Modular Monolithic Architecture

Organize code by **business modules**, not layers.

![](images/swarch-modular-monolith.webp)
![](images/swarch-modular-monolith-2.webp)

**Pros**

* Strong domain encapsulation
* Good consistency (single database)
* Natural path toward microservices

**Cons**

* No independent deployment
* No technology polyglot
* Scaling still applies to entire app

**Use**

* Domain-oriented teams
* Large monoliths undergoing modernization
* Workloads requiring strong consistency

---

## Scaling Monoliths — The Scale Cube

![](images/scale-cube.webp)

### X-axis Scaling

Clone the entire application behind a load balancer.
![](images/scale-cube-x.webp)

### Z-axis Scaling

Shard by data (e.g., userId).
![](images/scale-cube-z.webp)

### Y-axis Scaling

Split the application by function.
![](images/scale-cube-y.webp)

---

# Distributed Architectures

Applications composed of multiple components running on different machines, communicating over RPC.

**Pros**

* Independent deployment
* Independent scaling
* Smaller units → lower local complexity

**Cons**

* High global complexity (APIs, retries, consistency)
* Requires service discovery, tracing, logging
* Risk of a *distributed monolith*

![](images/swarch-big-ball-of-distributed-mud.webp)

---

## Service-Oriented Architecture (SOA)

*Dumb services, smart pipes.*
Often built around shared enterprise services and protocols like SOAP.

![](images/swarch-service-oriented-architecture.webp)

**Pros**

* Good for integrating heterogeneous systems
* Reusable, discoverable services

**Cons**

* Heavy protocols
* Shared services become bottlenecks
* Slow adaptation to change

**Use**

* Large enterprises with legacy systems
* Cross-system workflows

---

## Microservices Architecture

*Smart services, dumb pipes.*
Each service owns its data and is deployed independently.

![](images/swarch-microservices.webp)

**Pros**

* High evolvability
* Independent deploy and scale
* Technology freedom
* Team autonomy

**Cons**

* Operational complexity
* Harder debugging and monitoring
* Many small services/databases to manage

**Use**

* Complex systems with fast evolution
* Teams experienced in distributed systems

---

## Resources

* *Fundamentals of Software Architecture* — Richards & Ford
* Mark Richards – YouTube channel

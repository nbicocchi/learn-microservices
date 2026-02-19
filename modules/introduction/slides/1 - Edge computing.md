# Edge Computing


## What is Edge Computing?

Modern software systems increasingly operate in environments where **latency, bandwidth, privacy, and reliability** are as important as raw computational power. **Edge computing** is a distributed computing in which instead of relying solely on centralized cloud servers, part of the application logic runs on:

* industrial controllers
* micro data centers
* embedded devices
* on-premise servers
* regional/5G edge nodes


Edge computing does **not replace** cloud computing; it complements it.
* Computation and data storage are moved **closer to where data is produced**. This shift allows systems to react faster, reduce network usage, and operate even during connectivity disruptions.
* The cloud still handles global coordination, long-term storage, machine learning training, and orchestration, while the edge handles **time-critical**, **local**, **bandwidth-sensitive**, or **privacy-sensitive** tasks.

![](images/edge-computing.avif)

---

## Edge Computing is Inherently Distributed

Edge computing is a form of **distributed system**: instead of one centralized system, computation is spread across many geographically dispersed nodes.
This naturally introduces classic distributed systems challenges:

**Multiple Nodes Working Together**

* remote communication
* state synchronization
* coordination mechanisms

**Heterogeneity**

* small embedded devices
* powerful on-prem servers
* cloud services

**Intermittent Connectivity**

* go offline
* operate in degraded network conditions
* sync periodically


**Low Latency Requirements**

* detecting anomalies on a production line
* braking in autonomous vehicles
* local video analytics

---

## Distributed Programming Techniques

Programming for the edge means programming **distributed systems at scale**, where the cloud, edge, and devices form a single continuum. This introduces challenges such as:

* microservices architecture
* container and workload orchestration (K3s, MicroK8s, Nomad, containerd)
* distributed communication/coordination
* synchronous messaging (REST/GraphQL/Protobuf)
* asynchronous messaging (MQTT, AMQP, Kafka)
* resiliency
* eventual consistency
* distributed observability

* DevOps/MLOps
* device shadowing / digital twins
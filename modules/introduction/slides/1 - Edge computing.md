# Edge Computing

Modern software systems increasingly operate in environments where **latency, bandwidth, privacy, and reliability** are as important as raw computational power. This is especially true in domains such as industrial automation, autonomous vehicles, smart cities, robotics, and IoT. In all these scenarios, sending every piece of data to a central cloud for processing is no longer viable.

## What is Edge Computing?

**Edge computing** is a distributed computing paradigm in which computation and data storage are moved **closer to where data is produced** — sensors, devices, machines, vehicles, cameras, and local gateways.
Instead of relying solely on centralized cloud servers, part of the application logic runs on:

* industrial controllers
* micro data centers
* embedded devices
* on-premise servers
* regional/5G edge nodes

This shift allows systems to react faster, reduce network usage, and operate even during connectivity disruptions.

Edge computing does **not replace** cloud computing; it complements it. The cloud still handles global coordination, long-term storage, machine learning training, and orchestration, while the edge handles **time-critical**, **local**, **bandwidth-sensitive**, or **privacy-sensitive** tasks.

![](images/edge-computing.avif)

---

## Why Edge Computing is Inherently Distributed

Edge computing is a form of **distributed system**: instead of one centralized system, computation is spread across many geographically dispersed nodes.
This naturally introduces classic distributed systems challenges:

### 1. **Multiple Nodes Working Together**

Each edge node runs part of the application, processes local data, and collaborates with other nodes and the cloud. This requires:

* remote communication
* state synchronization
* coordination mechanisms

### 2. **Heterogeneity**

Edge environments mix:

* small embedded devices
* powerful on-prem servers
* cloud services
* mobile or intermittently connected nodes

Distributed programming patterns (e.g., event-driven communication, pub/sub, request/reply, distributed caches, sharding, consensus) are needed to make these heterogeneous systems behave coherently.

### 3. **Intermittent Connectivity**

Unlike cloud-to-cloud communication, edge nodes may:

* go offline
* operate in degraded network conditions
* sync periodically

This requires designing resilient distributed protocols, offline-first behavior, and asynchronous messaging patterns.

### 4. **Low Latency Requirements**

Many edge workloads need sub-millisecond reaction times:

* detecting anomalies on a production line
* braking in autonomous vehicles
* local video analytics

This is only possible with a **distributed architecture**, where computation is strategically placed close to the events.

---

## Why Distributed Programming Techniques?

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
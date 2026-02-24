# Edge Computing


## Limitations of Cloud-Only Architectures

* **High latency:** Centralized data centers introduce delays unsuitable for time-critical applications
* **Network dependency:** Full connectivity required; outages can halt operations
* **Bandwidth constraints:** Transmitting large volumes of data to the cloud is costly and inefficient
* **Privacy concerns:** Sensitive data may traverse multiple jurisdictions
* **Scalability limitations:** Centralized cloud may struggle with real-time, high-frequency workloads

---

## Definition of Edge Computing

**Edge computing** is a distributed computing paradigm in which computational tasks and data storage are performed closer to the sources of data, rather than solely in centralized cloud servers.

**Deployment locations include:**

* Industrial controllers and IoT devices
* Embedded systems and on-premise servers
* Micro data centers and regional/5G edge nodes

---

## Edge-Cloud Complementarity

* **Edge:** Executes **time-critical**, **local**, **bandwidth-sensitive**, and **privacy-sensitive** operations
* **Cloud:** Performs **global coordination**, **long-term storage**, **machine learning training**, and **orchestration**

**Key Benefit:** This hybrid model reduces latency, conserves network bandwidth, enhances reliability, and preserves data privacy, while retaining the computational power and orchestration capabilities of the cloud.

---

![](images/edge-computing.avif)

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


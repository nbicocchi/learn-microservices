# Distributed Systems Algorithms

A distributed system consists of independent computers that appear to the user as a **single, coherent system**. To achieve this, it must solve 6 fundamental challenges:

1. **Consensus:** Reaching agreement on a single value despite node failures or malicious actors.
2. **Replication & Consistency:** Duplicating data for reliability while managing how updates spread.
3. **Leader Election:** Appointing a coordinator to manage shared resources and tasks.
4. **Fault Detection:** Monitoring node health to trigger automatic self-healing.
5. **Mutual Exclusion:** Controlling access to shared resources to prevent data corruption.
6. **Clock & Ordering:** Synchronizing time or event sequences without a central clock.

---

## 1. Consensus and Agreement

Ensures all nodes agree on a decision (e.g. the state of a variable), even with network delays or crashes. This is essential for maintaining a consistent global state.

**Example: Distributed Bank Account**

* Suppose three servers maintain the balance of the same bank account: **$100**.
* Two clients simultaneously try to **withdraw $70**.
* Without consensus:

    * Server A approves the first transaction.
    * Server B also approves the second transaction because it hasn’t seen the update from A yet.
* Result: **balance becomes $-40**, violating correctness.

Using a **consensus algorithm** (like Raft or Paxos), the nodes **agree on the order of transactions**, ensuring the final balance is correct: only **one \$70 withdrawal** succeeds first, the second sees $30 and can be rejected or queued.

Types of consensus algorithms:
* **Crash Fault Tolerant (CFT):** Handles nodes going offline (e.g., Paxos, Raft).
* **Byzantine Fault Tolerant (BFT):** Handles nodes that lie or act maliciously (e.g., PBFT).

### **The Byzantine Context**

* **Origin (1982):** Coined by **Leslie Lamport** in *"The Byzantine Generals Problem"*.
* **The Allegory:** Generals must decide to **Attack** or **Retreat**. Traitors may send conflicting messages to different generals to prevent a unanimous decision.
* **The Rule:** To tolerate $f$ traitors, a system requires at least $3f + 1$ total nodes.
* **First Practical Implementation:** **PBFT (Practical Byzantine Fault Tolerance, 1999)** by **Miguel Castro and Barbara Liskov** demonstrated a working solution for distributed systems.
* **Modern Impact:** With **Bitcoin (2008)**, BFT became the cornerstone of blockchain technology.

![](images/bft-algorithm.png)


### Real software
* **etcd**. Uses Raft to maintain a consistent distributed key-value store. It is widely used in container orchestration systems.
* **Apache Kafka**. Uses a Raft-based protocol (KRaft) for metadata consensus in modern versions.
* **Apache ZooKeeper**. Uses the ZAB (ZooKeeper Atomic Broadcast) protocol, a Paxos-like consensus algorithm.
* **Consul** – HashiCorp’s service discovery and configuration system.
* **RethinkDB** – used Raft internally for consensus across nodes.
* **CockroachDB** – a distributed SQL database using Raft for replication and consistency.
* **TiDB** – distributed NewSQL database in the cloud-native ecosystem.

---

## 2. Replication and Consistency

Manages data duplication to ensure **Fault Tolerance** and **High Availability**.

### Replication

Distribute data across multiple nodes while enabling **scalability** and **fault tolerance**.
* **Consistent Hashing:** Maps keys to a **ring of nodes**, so each node is responsible for a portion of the key space.
* **Replication Factor:** Each key is stored on multiple nodes (e.g., 3 nodes) to ensure **redundancy**.
* **Node Addition/Removal:** Only a fraction of keys need to be moved, minimizing disruption compared to naive hashing.

### Real Software
* **Cassandra:** Uses consistent hashing + replication factor to store data across cluster nodes.
* **Amazon DynamoDB:** Partition keys distributed with consistent hashing; supports multiple replicas.
* **Riak:** Distributed key-value store relying on consistent hashing and vector clocks for conflict resolution.

### Consistency
* **Strong Consistency:** All replicas update immediately; users always see the latest data (High accuracy, low availability).
* **Eventual Consistency:** Replicas converge over time; the system is temporarily out of sync but eventually uniform (Low accuracy, high availability).

### **Key Protocols**
* **Two-Phase Commit (2PC):** A strong consistency "stop-the-world" approach where all nodes must vote "Yes" to commit.
* **Sagas:** Manages eventual consistency in microservices via a sequence of local transactions and **compensating transactions** (undo) if a step fails.
* **CRDTs:** Mathematical data structures that allow concurrent updates to be merged without conflicts (e.g., Google Docs).

### Real Software
* **Amazon DynamoDB**. Primarily implements **eventual consistency**, allowing replicas to converge over time, but also offers optional **strongly consistent reads**.
* **PostgreSQL** and **MySQL**. Use **Two-Phase Commit (2PC)** to coordinate distributed transactions across multiple nodes or databases.
* **Redis** (with modules like Redis CRDT). Use **CRDT-based data structures** that allow concurrent updates to be merged automatically without coordination.

---

## 3. Leader Election & Coordination

Leader election ensures that a distributed system **operates as a unified whole** by appointing a single **coordinator (leader)**.

The leader is responsible for **serializing operations**, **managing shared resources**, and **orchestrating tasks across worker nodes**.
* **Safety:** Ensures that **at most one leader exists at any time**, preventing split-brain scenarios.
* **Liveness:** Guarantees that if the current leader fails, the system **eventually elects a new leader**, maintaining continuous operation.

### **Bully Algorithm**

Based on the concept of **"brute force"** tied to a unique Node ID. The node with the highest ID always wins.

* **Mechanism:** When a node detects the leader is offline, it sends an "Election" message to all nodes with a **higher ID** than its own.
* If it receives a response from a higher-ID node, it stands down.
* If no response is received, it proclaims itself the leader and sends a "Victory" message to all other nodes.

* **Strengths:** Very fast if high-ID nodes are stable and reliable.
* **Weaknesses:** Generates high network traffic ($O(n^2)$ messages) and suffers if the highest-ID node "flaps" (constantly crashes and restarts).

### **Ring Algorithm**

Based on a **logical circle structure**. Each node is aware only of its immediate successor.

* **Mechanism:** When a node detects a leader failure, it creates an "Election" message containing its own ID and sends it to its neighbor.
* Each node receiving the message adds its ID to the list and passes it forward.
* Once the message returns to the initiator (completing the full circle), the node identifies the highest ID in the list as the winner.
* A second round of messages is sent to inform the entire ring of the new leader.

* **Strengths:** More organized and predictable; prevents network "flooding."
* **Weaknesses:** Slower performance (requires a full cycle) and vulnerable if another node in the ring fails during the election process.

### Real Software

* **etcd** – Uses the **Raft consensus algorithm** to elect a leader that coordinates updates to the cluster state. Widely used by **Kubernetes** for cluster coordination and leader election among control-plane components.
* **Apache Kafka** – Uses **leader election for partition leaders**, so one broker handles reads and writes for each partition while followers replicate the data.
* **Eureka** – In a **cluster of Eureka servers**, one instance is designated as the **“replication leader”** to manage **replicating registry information** across all nodes. Other servers synchronize with this leader to ensure consistent service registration and availability.

---

## 4. Fault Detection & Recovery

Continuously monitors the system to detect failures and initiate "self-healing."

* **Heartbeat Protocols:** Periodic "I am alive" signals.
* **Adaptive Detectors:** Adjust timeouts dynamically based on network latency.
* **Failover:** Shifting workloads to a healthy standby node.
* **Checkpointing:** Rolling back to the last "safe" snapshot of the global state.


### Real Software
* **etcd**. Implements **heartbeat messages** between cluster nodes to detect failures quickly and uses **Raft leader election** to recover from leader crashes.
* **Apache Kafka**. Brokers and controllers monitor each other using **heartbeat protocols**. In case of failure, Kafka automatically **fails over partition leadership** to another broker to maintain availability.
* **Redis Sentinel**. Monitors master and replica nodes, detects failures using **heartbeat messages**, and performs **automatic failover** to promote a replica to master.

---

## 5. Mutual Exclusion & Resource Allocation

Prevents **Race Conditions** by ensuring coordinated access to shared resources.

**Requirements:**
* **Safety:** Only one node accesses the **Critical Section** at a time.
* **Liveness:** Prevents **Deadlocks** (everyone waiting forever) and **Starvation** (one node waiting forever).

**Algorithms:**
* **Lamport’s Bakery:** Uses "ticket numbers" to serve nodes in order.
* **Ricart–Agrawala:** A message-based approach requiring "Grants" from all nodes.
* **Token Ring:** A unique digital token circulates; only the holder can access the resource.

### Real Software

* **Apache ZooKeeper** – Distributed locks and barriers ensure only one process accesses a resource at a time.
* **etcd** – Lock primitives using Raft coordinate access across the cluster.
* **Redis** – **Redlock** algorithm provides token-based distributed locks.

---

## 6. Clock Synchronization & Ordering

In distributed systems, **no single universal clock** exists. Without proper ordering:

* **Logs** become inconsistent → hard to debug.
* **Transactions** may conflict → data inconsistency.
* **Causal relationships** are lost → cannot tell which event triggered another.

### Physical Clocks

* Measure real-world time; use **NTP** to sync across nodes.
* Good for **logging** and **auditing**.
* Limitation: high-speed events may be **misordered** due to drift/network delay.

### Logical Clocks

* Track **event order** rather than real time.

**Lamport Timestamps**

* Each node keeps a counter; increments on events and messages.
* Provides **total ordering**: `e1 → e2 → e3`.
* Limitation: cannot detect **concurrent events**.

**Vector Clocks**

* Each node keeps an **array of counters** (one per node).
* Compare vectors to detect:

    * `A < B` → A happened before B
    * `B < A` → B happened before A
    * Mixed → **concurrent events / conflicts**
* Useful for **causal relationships** and **conflict resolution**.

### Real Software

* **Google Spanner** – Uses **TrueTime** (GPS + atomic clocks) for globally consistent timestamps across data centers.
* **Apache Cassandra** – Uses **lightweight transactions and Lamport-style logical clocks** to order updates.
* **Amazon DynamoDB** – Implements **Lamport/vector clocks** internally to maintain eventual consistency across replicas.
* **Google Docs** – Uses CRDTs and **vector clocks** to merge concurrent edits without losing causality.

---

## References

* [Raft Simulator](https://raft.github.io) | [Paxos Made Simple](https://lamport.azurewebsites.net/pubs/paxos-simple.pdf)
* [Practical Byzantine Fault Tolerance (PBFT)](http://pmg.csail.mit.edu/papers/osdi99.pdf)
* [Conflict-Free Replicated Data Types (CRDTs)](https://crdt.tech)
* [Leader Election in Distributed Systems](https://en.wikipedia.org/wiki/Leader_election)

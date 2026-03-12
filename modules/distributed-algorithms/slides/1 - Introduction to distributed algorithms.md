# Distributed Systems Algorithms

A distributed system consists of independent computers that appear to the user as a **single, coherent system**. To achieve this, it must solve 6 fundamental challenges:

1. **Consensus (CFT/BFT):** Reaching agreement on a single value despite node failures or malicious actors.
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
* **Quorum:** The minimum number of votes (usually a majority) required to reach a decision.

### **The Byzantine Context**

* **Origin (1982):** Coined by **Leslie Lamport** in *"The Byzantine Generals Problem"*.
* **The Allegory:** Generals must decide to **Attack** or **Retreat**. Traitors may send conflicting messages to different generals to prevent a unanimous decision.
* **The Rule:** To tolerate $f$ traitors, a system requires at least $3f + 1$ total nodes.
* **Modern Impact:** With **Bitcoin (2008)**, BFT became the cornerstone of blockchain technology.

![](images/bft-algorithm.png)


### Real software using consensus algorithms
* **etcd**. Uses Raft to maintain a consistent distributed key-value store. It is widely used in container orchestration systems.
* **Apache Kafka**. Uses a Raft-based protocol (KRaft) for metadata consensus in modern versions.
* **Apache ZooKeeper**. Uses the ZAB (ZooKeeper Atomic Broadcast) protocol, a Paxos-like consensus algorithm.


---

## 2. Replication and Consistency

Manages data duplication to ensure **Fault Tolerance** and **High Availability**.

### **Consistency Models**

* **Strong Consistency:** All replicas update immediately; users always see the latest data (High accuracy, lower availability).
* **Eventual Consistency:** Replicas converge over time; the system is temporarily out of sync but eventually uniform (High availability).

### **Key Protocols**

* **Two-Phase Commit (2PC):** A strong consistency "stop-the-world" approach where all nodes must vote "Yes" to commit.
* **Sagas:** Manages eventual consistency in microservices via a sequence of local transactions and **compensating transactions** (undo) if a step fails.
* **CRDTs:** Mathematical data structures that allow concurrent updates to be merged without conflicts (e.g., Google Docs).

### Real Software Using Consistency Protocols and Models

* **Amazon DynamoDB**. Primarily implements **eventual consistency**, allowing replicas to converge over time, but also offers optional **strongly consistent reads**.
* **PostgreSQL** and **MySQL**. Use **Two-Phase Commit (2PC)** to coordinate distributed transactions across multiple nodes or databases.
* **Redis** (with modules like Redis CRDT). Use **CRDT-based data structures** that allow concurrent updates to be merged automatically without coordination.

---

## 3. Leader Election & Coordination

Leader election ensures that a distributed system **operates as a unified whole** by appointing a single **coordinator (leader)**.

* The leader is responsible for **serializing operations**, **managing shared resources**, and **orchestrating tasks across worker nodes**.
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

### Real Software Using Leader Election and Coordination

* **etcd** – Uses the **Raft consensus algorithm** to elect a leader that coordinates updates to the cluster state. Widely used by **Kubernetes** for cluster coordination and leader election among control-plane components.
* **Apache Kafka** – Uses **leader election for partition leaders**, so one broker handles reads and writes for each partition while followers replicate the data.
* **Eureka** – In a **cluster of Eureka servers**, one instance is designated as the **“replication leader”** to manage **replicating registry information** across all nodes. Other servers synchronize with this leader to ensure consistent service registration and availability.

---

## 4. Fault Detection & Recovery

Continuously monitors the system to detect failures and initiate "self-healing."

* **Detection:**
* **Heartbeat Protocols:** Periodic "I am alive" signals.
* **Adaptive Detectors:** Adjust timeouts dynamically based on network latency.


* **Recovery:**
* **Failover:** Shifting workloads to a healthy standby node.
* **Checkpointing:** Rolling back to the last "safe" snapshot of the global state.


### Real Software Using Fault Detection & Recovery

* **etcd**. Implements **heartbeat messages** between cluster nodes to detect failures quickly and uses **Raft leader election** to recover from leader crashes.
* **Apache Kafka**. Brokers and controllers monitor each other using **heartbeat protocols**. In case of failure, Kafka automatically **fails over partition leadership** to another broker to maintain availability.
* **Redis Sentinel**. Monitors master and replica nodes, detects failures using **heartbeat messages**, and performs **automatic failover** to promote a replica to master.

---

## 5. Mutual Exclusion & Resource Allocation

Prevents **Race Conditions** by ensuring coordinated access to shared resources.

* **Requirements:**
* **Safety:** Only one node accesses the **Critical Section** at a time.
* **Liveness:** Prevents **Deadlocks** (everyone waiting) and **Starvation** (one node waiting forever).


* **Algorithms:**
* **Lamport’s Bakery:** Uses "ticket numbers" to serve nodes in order.
* **Ricart–Agrawala:** A message-based approach requiring "Grants" from all nodes.
* **Token Ring:** A unique digital token circulates; only the holder can access the resource.



**Lamport’s Bakery Algorithm – Overview**

1. **Take a Ticket:**

    * `ticket[i] = max(ticket[0..N-1]) + 1`
    * Marks intent to enter critical section.

2. **Wait Your Turn:**

    * Wait if any other process is **choosing**.
    * Wait if another process has a **smaller ticket**.
    * Tie-breaker: **lower process ID wins**.

3. **Enter Critical Section:**

    * Once no one has a lower ticket, proceed.

4. **Release:**

    * Set `ticket[i] = 0` after leaving, letting the next process proceed.

### Real Software Using Mutual Exclusion & Resource Allocation

* **Apache ZooKeeper** – Distributed locks and barriers ensure only one process accesses a resource at a time.
* **etcd** – Lock primitives using Raft coordinate access across the cluster.
* **Redis** – **Redlock** algorithm provides token-based distributed locks.

---

## 6. Clock Synchronization & Ordering

In a distributed system, time is a massive headache because there is no single "universal clock." If Server A says it received a request at 10:00:01 and Server B says 10:00:02, you can't be sure which happened first unless their clocks are perfectly in sync.

### Physical Clocks (Real-World Time)

These measure actual seconds, minutes, and hours. We use them for things humans care about.

* **Mechanism:** Every computer has a quartz crystal clock, but they "drift" (gain or lose seconds). To fix this, we use **NTP (Network Time Protocol)**, which periodically syncs the computer's clock with a highly accurate atomic clock via the internet.
* **Best For:**
* **Logs:** "At what time did the server crash?"
* **Auditing:** "When did the user make this bank transfer?"


* **The Problem:** In high-speed systems, NTP isn't precise enough. If two events happen within milliseconds, physical clocks might record them in the wrong order due to network lag during the sync.

---

### 2. Logical Clocks (Event-Based Time)

Logical clocks don't care about the time of day. They only care about **Causality**: "Did Event A happen before Event B?" They use counters that increment whenever an event occurs.

#### **A. Lamport Timestamps**

This is a simple integer (a counter) that every node maintains.

* **The Rule:** Whenever a node does something, it increments its counter. When it sends a message, it includes that counter. When a receiver gets a message, it updates its own counter to be higher than the one it just received.
* **Result:** It provides a **Total Ordering**. You can say "Event 1 happened before Event 2."
* **Limitation:** If you see two timestamps, you can't tell if they were truly related (causal) or just happened at the same time on different servers.

**Example: Ordering Events with Lamport Timestamps**

Consider two nodes **A** and **B**, each maintaining a **logical counter**.

| Step | Event                    | Timestamp                       |
| ---- | ------------------------ | ------------------------------- |
| 1    | Initial state            | A=0, B=0                        |
| 2    | A performs an event      | A=1                             |
| 3    | A sends a message to B   | Message carries timestamp **1** |
| 4    | B receives the message   | B = max(B,1) + 1 → **2**        |
| 5    | B performs another event | B=3                             |

Event timeline:

| Event | Node               | Timestamp |
| ----- | ------------------ | --------- |
| e1    | A local event      | 1         |
| e2    | B receives message | 2         |
| e3    | B local event      | 3         |

Because timestamps increase monotonically:

**e1 → e2 → e3**

The system can say **Event e1 happened before e2**, and **e2 before e3**.

💡 **Limitation Example**

Suppose two independent events occur:

| Node | Event  | Timestamp |
| ---- | ------ | --------- |
| A    | update | 4         |
| B    | update | 5         |

Lamport timestamps say:

```
4 < 5 → A happened before B
```

But in reality the events may have been **independent** (no message exchange).

➡ Lamport timestamps provide **total ordering**, but **cannot detect concurrency**.

This limitation is why distributed systems often use **vector clocks** when they need to detect **causal relationships and conflicts**.


#### **B. Vector Clocks**

This is a more advanced version where each node keeps an **array (vector)** of counters—one for every node in the system.

* **The Rule:** Node A knows its own counter AND its best guess of the counters for Node B and Node C. Every message carries this entire "vector" of information.
* **The Magic:** By comparing two vectors, the system can detect **Conflicts** or **Parallelism**.
* If Vector A is strictly "less than" Vector B, then A happened before B.
* If they are "mixed" (some numbers higher, some lower), the events happened **Concurrently** (at the same time).

**Example: Detecting Causality with Vector Clocks**

Consider a system with **three nodes: A, B, C**.
Each node keeps a vector `[A, B, C]` representing its knowledge of events.

| Event                    | Node                    | Vector Clock |
| ------------------------ | ----------------------- | ------------ |
| Initial state            | All nodes               | `[0,0,0]`    |
| A performs an event      | A                       | `[1,0,0]`    |
| A sends a message to B   | B merges and increments | `[1,1,0]`    |
| B performs another event | B                       | `[1,2,0]`    |

Now suppose **A and B update the same object independently**:

| Node     | Vector    |
| -------- | --------- |
| A update | `[2,1,0]` |
| B update | `[1,3,0]` |

Comparison:

* `[2,1,0]` vs `[1,3,0]`
* Some components are **larger in A**, others **larger in B**

➡ The events are **concurrent**.

**Conclusion:**
The system detects a **conflict**, meaning both updates happened independently and may require **merging or resolution**.

---

💡 **Key intuition**

Vector clocks allow distributed systems to distinguish:

| Case    | Meaning                          |
| ------- | -------------------------------- |
| `A < B` | Event A happened before B        |
| `B < A` | Event B happened before A        |
| Neither | Events happened **concurrently** |


### Real Software Using Clock Synchronization & Ordering

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

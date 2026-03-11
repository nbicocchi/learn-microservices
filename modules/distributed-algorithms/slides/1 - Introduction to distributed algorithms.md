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

Ensures all nodes agree on a decision, even with network delays or crashes. This is essential for maintaining a consistent global state.

* **Crash Fault Tolerant (CFT):** Handles nodes going offline (e.g., Paxos, Raft).
* **Byzantine Fault Tolerant (BFT):** Handles nodes that lie or act maliciously (e.g., PBFT).
* **Quorum:** The minimum number of votes (usually a majority) required to reach a decision.

### **The Byzantine Context**

* **Origin (1982):** Coined by **Leslie Lamport** in *"The Byzantine Generals Problem"*.
* **The Allegory:** Generals must decide to **Attack** or **Retreat**. Traitors may send conflicting messages to different generals to prevent a unanimous decision.
* **The Rule:** To tolerate $f$ traitors, a system requires at least $3f + 1$ total nodes.
* **Modern Impact:** With **Bitcoin (2008)**, BFT became the cornerstone of blockchain technology.

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

---

## 3. Leader Election & Coordination

Ensures the system acts as a single unit by appointing a **Coordinator** (Leader).

* **Core Functions:** Serializing operations, managing resources, and orchestrating worker tasks.
* **Guarantees:**
* **Safety:** Only one leader exists at a time (prevents **Split-Brain**).
* **Liveness:** A new leader is eventually elected if the current one crashes.

---

### **Bully Algorithm**

Based on the concept of **"brute force"** tied to a unique Node ID. The node with the highest ID always wins.

* **Mechanism:** When a node detects the leader is offline, it sends an "Election" message to all nodes with a **higher ID** than its own.
* If it receives a response from a higher-ID node, it stands down.
* If no response is received, it proclaims itself the leader and sends a "Victory" message to all other nodes.


* **Strengths:** Very fast if high-ID nodes are stable and reliable.
* **Weaknesses:** Generates high network traffic ($O(n^2)$ messages) and suffers if the highest-ID node "flaps" (constantly crashes and restarts).

---

### **Ring Algorithm**

Based on a **logical circle structure**. Each node is aware only of its immediate successor.

* **Mechanism:** When a node detects a leader failure, it creates an "Election" message containing its own ID and sends it to its neighbor.
* Each node receiving the message adds its ID to the list and passes it forward.
* Once the message returns to the initiator (completing the full circle), the node identifies the highest ID in the list as the winner.
* A second round of messages is sent to inform the entire ring of the new leader.


* **Strengths:** More organized and predictable; prevents network "flooding."
* **Weaknesses:** Slower performance (requires a full cycle) and vulnerable if another node in the ring fails during the election process.

---

## 4. Fault Detection & Recovery

Continuously monitors the system to detect failures and initiate "self-healing."

* **Detection:**
* **Heartbeat Protocols:** Periodic "I am alive" signals.
* **Adaptive Detectors:** Adjust timeouts dynamically based on network latency.


* **Recovery:**
* **Failover:** Shifting workloads to a healthy standby node.
* **Checkpointing:** Rolling back to the last "safe" snapshot of the global state.



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

#### **B. Vector Clocks**

This is a more advanced version where each node keeps an **array (vector)** of counters—one for every node in the system.

* **The Rule:** Node A knows its own counter AND its best guess of the counters for Node B and Node C. Every message carries this entire "vector" of information.
* **The Magic:** By comparing two vectors, the system can detect **Conflicts** or **Parallelism**.
* If Vector A is strictly "less than" Vector B, then A happened before B.
* If they are "mixed" (some numbers higher, some lower), the events happened **Concurrently** (at the same time).


* **Best For:** Conflict resolution in databases (like Amazon’s Dynamo or Riak).

---

## References

* [Raft Simulator](https://raft.github.io) | [Paxos Made Simple](https://lamport.azurewebsites.net/pubs/paxos-simple.pdf)
* [Practical Byzantine Fault Tolerance (PBFT)](http://pmg.csail.mit.edu/papers/osdi99.pdf)
* [Conflict-Free Replicated Data Types (CRDTs)](https://crdt.tech)
* [Leader Election in Distributed Systems](https://en.wikipedia.org/wiki/Leader_election)

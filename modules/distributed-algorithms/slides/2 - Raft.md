# Consensus Algorithms
## What Are Consensus Algorithms?

**Definition:**
Consensus algorithms are **protocols used in distributed systems** to ensure that multiple nodes agree on a single value or state, even in the presence of failures.

**Why we need them:**

* Distributed systems often replicate data or state across multiple nodes.
* Nodes may **fail, crash, or be slow**.
* Network delays or partitions can occur.
* Without consensus, nodes may **disagree**, causing inconsistency.

**Key Properties of Consensus Algorithms:**

1. **Agreement** — All non-faulty nodes decide on the same value.
2. **Validity** — The chosen value must be proposed by some node.
3. **Termination** — All non-faulty nodes eventually decide.

### Viewstamped Replication

* Introduced by **Oki & Liskov (1988)**.
* **Leader-based replication** protocol.
* Key ideas:

    * One **primary** (leader) coordinates requests.
    * **Backups** follow the primary’s sequence.
    * Requests are **committed in the same order** across replicas.
* **View changes:** If primary fails, a new primary is elected.
* Fault tolerance: can survive up to **f crashes with 2f+1 replicas**.

### Paxos

* Introduced by **Lamport (1990s)**.
* **General-purpose consensus protocol** for asynchronous networks.
* Key ideas:

    * **Proposers**, **Acceptors**, **Learners** roles.
    * Agreement is reached even with **message delays** and **failures**.
    * Focuses on **safety** (never choose conflicting values).
    * Liveness guaranteed only under **partial synchrony** assumptions.
* Foundation for:
    * Google Chubby, Spanner
    * Many modern distributed databases

### Raft

* Introduced by **Ongaro e Ousterhout (2008)**
* Raft builds on VR and Paxos ideas:

    * Leader-based replication (like VR)
    * Safety-first consensus (like Paxos)

* Raft achieves consensus through three core components:

- **Leader Election** – Selecting a single node as leader, responsible for log management and coordination.
- **Log Replication** – Ensuring the leader’s log entries are consistently replicated to followers.
- **Safety** – Guaranteeing that committed entries remain durable and consistent across all nodes.


## Definitions

### Deterministic Algorithms

**Definition:**

* An algorithm is **deterministic** if, given the same initial state and same inputs, it always:

    * Produces the **same output**
    * Follows the **same execution path**

**Key idea:**

* No randomness, no probabilistic choices
* Behavior is fully **predictable**

**Example:**

* A node always chooses the first value it receives in a queue — no random tie-breaking.


### Network Asynchrony

**Definition:**
A distributed system is **asynchronous** if:

* Messages can be **delayed arbitrarily long**
* Nodes can take **arbitrary time to process messages**
* No global clock or timing guarantees
* Messages may arrive out of order

**Key idea:**

* Nodes cannot reliably distinguish a **slow node** from a **crashed node**.

**Examples:**

* LAN with predictable latency → synchronous
* Internet with variable delays → asynchronous


## Two Generals Problem

**Scenario:**

* Two generals must attack a city **simultaneously** to succeed.
* They can communicate **only via messengers**, who may be **captured or lost**.
* Goal: agree on the **exact time of the attack**.

---

**Why it’s hard:**

* General A sends a message → General B confirms → A confirms the confirmation…
* Any message can be lost → there is **no way to guarantee that both generals know that the other knows**.
* In theory → **no deterministic protocol can solve this**.

---

**Key Lesson:**

* Abstract model for **consensus problems in unreliable networks**.
* Shows that in **asynchronous networks**, guaranteed coordination is impossible **without extra assumptions**.
* Foundation for understanding:

    * **FLP impossibility**
    * The need for **timeouts, quorums, or leader election** in real-world consensus algorithms


## Fischer–Lynch–Paterson (FLP)

> In a **purely asynchronous network**, with even **one possible crash**, **no deterministic consensus algorithm can guarantee termination**.

**Assumptions:**

* Asynchronous network (unbounded message delays)
* Algorithm is deterministic
* At least one node may crash


**Intuition:**

1. Node A and B must agree on a value.
2. Node C is slow (or crashed).
3. Waiting for C → may never terminate.
4. Ignoring C → may violate agreement.

**Result:**

* **No deterministic algorithm can guarantee both agreement and termination** in this setting.

**Takeaway:**

* Real-world systems **escape FLP** by introducing:

    * **Timeouts**
    * **Partial synchrony**
    * **Leader election**

### Timeouts

Timeouts introduce **time-based decisions**:

* “If I don’t hear from the leader in 150ms, I assume it failed.”
* “If a follower is silent for 500ms, I skip it.”

This breaks the FLP assumption of *pure asynchrony*, because now the system relies on **timing guarantees**.

In a truly asynchronous network, a message might be delayed arbitrarily long → so a timeout cannot be trusted.

But real networks **usually** behave well enough that timeouts work.

👉 Timeouts = “Let’s use time to guess failure.”

### Partial Synchrony

FLP assumes:

* **No upper bound** on message delays
* No guarantee that delays will ever become stable

Partial synchrony says:

* The system might behave badly now…
* But **eventually** message delays become bounded and stable

So after some unknown point:

* messages arrive on time
* nodes respond predictably
* the system behaves almost synchronous

This makes timeouts **meaningful** and lets algorithms like Raft/Paxos **eventually terminate**.

👉 Partial synchrony = “Eventually, the network behaves predictably.”


### Leader Election

Consensus is hard when:

* Every node proposes values
* Nodes cannot tell slow nodes from dead nodes

So real systems elect **one leader** to:

* serialize log entries
* send heartbeats
* coordinate replication

Leader election uses timeouts and is *not deterministic*, which again breaks FLP’s assumptions.

Once a leader stabilizes:

* only one node drives the protocol
* fewer conflicts
* progress becomes possible

👉 Leader election = “Pick one coordinator so the system can make progress.”





## Raft

### Leader Election

Leader election is the process by which Raft selects a single leader from the cluster. The leader handles client interactions, manages log replication, and maintains the system’s state. If the current leader fails or becomes unreachable, Raft initiates a new election.

**Process:**

1. **Election Timeout** – Each follower starts an election timer. If no heartbeat (AppendEntries RPC) is received, it becomes a candidate.
2. **Becoming a Candidate** – The candidate increments its term and requests votes from other nodes via RequestVote RPCs.
3. **Voting** – Nodes respond based on their current term and log state. A candidate needs majority votes to become the leader.
4. **Leader Declaration** – Once a candidate receives majority votes, it becomes the leader and sends heartbeats.

---

### Log Replication

Once a leader is elected, it manages the replicated log. Client requests are appended to the leader’s log and replicated to followers.

**Process:**

1. **Appending Entries** – Leader appends client commands to its log and sends AppendEntries RPCs to followers.
2. **Acknowledgments** – Followers append the entries and acknowledge receipt.
3. **Commitment** – After a majority acknowledgment, the leader commits the entry, applies it to its state machine, and instructs followers to commit.
4. **Consistency** – If a follower’s log is inconsistent, the leader overwrites it to maintain uniformity.

---

### Safety

Raft ensures committed entries remain consistent and cannot be lost.

**Safety Properties:**

- **Election Safety** – Only one leader per term, avoiding split-brain.
- **Log Matching** – Logs with the same index and term are identical up to that point.
- **Leader Append-Only** – Leaders never overwrite or delete entries, maintaining history integrity.

---

## Detailed Mechanisms

### Election Process

Leader election ensures only one authoritative leader exists.

**Steps:**

1. **Start as Follower** – Nodes wait for heartbeats.
2. **Timeout and Candidate State** – Follower times out and becomes a candidate.
3. **Request Votes** – Candidate increments term and requests votes.
4. **Voting Criteria**:
    - Candidate term ≥ voter term
    - Candidate log ≥ voter log
5. **Majority Vote** – Candidate becomes leader with majority votes.
6. **Split Votes** – Randomized timeouts reduce repeated split votes.

---

### Phases of Leader Election

1. **Initialization** – All nodes are followers, waiting for heartbeats.  
   ![Phase 1](images/phase1-initialization.png)  
   *Figure 2: Nodes waiting with varying timeouts*

2. **First Candidate Timeout** – First node times out, votes for itself, and sends candidacy.  
   ![Phase 2](images/phase2-first-candidate.png)  
   *Figure 3: Candidate sends requests and votes for itself*

3. **Subsequent Timeouts and Voting** – Other nodes timeout and vote for the first candidate.  
   ![Phase 3](images/phase3-subsequent-timeouts.png)  
   *Figure 4: Votes sent to the first candidate*

4. **Leader Declared** – Candidate becomes leader and sends heartbeats.  
   ![Phase 4](images/phase4-leader-declared.png)  
   *Figure 5: Leader starts heartbeats*

5. **Heartbeat Acknowledgments** – Followers acknowledge heartbeats.  
   ![Phase 5](images/phase5-heartbeat-acks.png)  
   *Figure 6: Followers confirm heartbeats*

6–7. **Continued Heartbeats** – Heartbeats and acknowledgments continue.  
![Phase 6](images/phase6-continued-heartbeats.png)  
![Phase 7](images/phase7-continued-heartbeats.png)

8. **Leader Failure** – Leader stops; followers wait.  
   ![Phase 8](images/phase8-leader-failure.png)

9. **New Election Initiated** – Followers timeout and start new elections.  
   ![Phase 9](images/phase9-new-election.png)

10. **New Leader Elected** – Majority votes elect a new leader.  
    ![Phase 10](images/phase10-new-leader.png)

---

### Handling Failures

- **Leader Failures** – Followers detect missing heartbeats and elect a replacement.
- **Network Partitions** – Only a majority leader can operate; split-brain is prevented.
- **Old Leader Rejoining** – Rejoining leader steps down if a current leader with higher term exists.

---

### Log Consistency

- **Log Matching** – Same index and term → logs identical up to that entry.
- **Conflict Resolution** – Leader overwrites inconsistent follower logs.
- **Commit Index** – Leader tracks highest committed entry for durability and uniform application.

---



## References

- https://thesecretlivesofdata.com/raft/
- https://raft.github.io/

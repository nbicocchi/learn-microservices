# Distributed Systems Algorithms

Algorithms in distributed systems form the **foundation** for seamless operation. They define how data is stored, replicated, and synchronized; how consensus is achieved; how failures are detected; and how coordination is maintained. Without robust algorithms, distributed systems risk inconsistencies, inefficiencies, and vulnerabilities.

## Overview

**Consensus and Agreement**

* Ensures all nodes in a distributed system agree on a single value or decision, even in the presence of failures, network delays, or node crashes. This is essential to maintain a consistent system state and avoid conflicting actions among nodes.
* Includes both **Crash Fault Tolerant (CFT)** algorithms, which handle only crash failures (e.g., Paxos, Raft), and **Byzantine Fault Tolerant (BFT)** algorithms, which also handle malicious or arbitrary failures (e.g., PBFT).
* Examples: Paxos, Raft, PBFT

**Replication and Consistency**

* Manages the duplication of data across multiple nodes to ensure fault tolerance and high availability.
* Maintains **consistency models** ranging from strong consistency (all replicas agree immediately) to eventual consistency (replicas converge over time), depending on system requirements.
* Examples: Two-Phase Commit (2PC), Three-Phase Commit (3PC), Conflict-Free Replicated Data Types (CRDTs)

**Leader Election and Coordination**

* Elects a leader or coordinator among nodes to manage shared resources, coordinate tasks, or serialize operations, ensuring orderly execution in the system.
* Helps prevent conflicts and ensures smooth operation even when nodes fail or leave the system.
* Examples: Bully Algorithm, Ring Algorithm

**Fault Detection and Recovery**

* Continuously monitors the health of nodes and communication links to detect failures quickly.
* Initiates recovery procedures such as failover, checkpoint recovery, or restarting failed components to restore the system to a correct operational state.
* Examples: Heartbeat protocols, Checkpointing, Failure detectors

**Mutual Exclusion and Resource Allocation**

* Ensures safe, coordinated access to shared resources in a distributed system to prevent conflicts, inconsistencies, or deadlocks.
* Critical in scenarios where multiple nodes or processes must not access a resource simultaneously.
* Examples: Lamport Bakery Algorithm, Ricart–Agrawala Algorithm

**Clock Synchronization and Ordering**

* Maintains consistent time across nodes and ensures a correct ordering of events, which is essential for causal relationships, logging, and debugging.
* Can involve **logical clocks** (tracking event order) or **physical clocks** (synchronizing actual time across machines).
* Examples: Lamport Timestamps, Vector Clocks, Network Time Protocol (NTP)


## References

- [Raft Simulator](https://raft.github.io)
- [Paxos Made Simple](https://lamport.azurewebsites.net/pubs/paxos-simple.pdf)
- [In Search of an Understandable Consensus Algorithm (Raft)](https://raft.github.io/raft.pdf)
- [Practical Byzantine Fault Tolerance (PBFT)](http://pmg.csail.mit.edu/papers/osdi99.pdf)
- [Conflict-Free Replicated Data Types (CRDTs)](https://crdt.tech)
- [Leader Deputies Algorithm for Leader Election in Distributed Systems](https://www.researchgate.net/publication/346932388_Leader_Deputies_Algorithm_for_Leader_Election_in_Distributed_Systems)
- [Leader Election](https://en.wikipedia.org/wiki/Leader_election)
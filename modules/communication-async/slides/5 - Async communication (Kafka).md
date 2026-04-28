# **Asynchronous Communications (Kafka)**

[Apache Kafka](https://kafka.apache.org/) is a **distributed streaming platform** designed for **high-throughput, fault-tolerant, and scalable** data pipelines.
Kafka enables applications to:
* **publish and subscribe to streams of records**
* **store records durably**
* **process streams in real-time**

---

## Key Components

* **Broker**: Kafka server that stores records and serves clients.
* **Producer**: Application that publishes messages to Kafka topics.
* **Consumer**: Application that subscribes to topics and processes messages.

---

## Kafka Topics

```mermaid
flowchart LR
    Producer1[Producer]
    Producer2[Producer]
    Topic[Topic: orders]
    InventoryConsumer[Consumer: Inventory]
    AnalyticsConsumer[Consumer: Analytics]

    Producer1 --> Topic
    Producer2 --> Topic
    Topic --> InventoryConsumer
    Topic --> AnalyticsConsumer
```

A **topic** is like a **channel** or **folder** where messages (called *records*) are stored.
Producers write to a topic, consumers read from it. Topics enable **logical separation** of data streams.

* A topic is **append-only** (Kafka never modifies existing events).
* Consumers can read the same topic independently without interfering with each other.
* **Offset**: Each message in a partition has a unique sequential ID called an **offset**.
* **Retention**: Kafka keeps messages for a configurable duration or size, independent of consumption.
* **Replay**: Consumers can re-read messages by resetting their offsets.


Example topics:

* `orders`
* `payments`
* `temperature-readings`

## Kafka Partitions

```mermaid
flowchart LR
    Producer1[Producer]
    Producer2[Producer]
    Topic[Topic: orders]
    Partition1[Partition 0]
    Partition2[Partition 1]
    InventoryConsumer[Consumer Group: Inventory]
    AnalyticsConsumer[Consumer Group: Analytics]

    Producer1 --> Topic
    Producer2 --> Topic
    Topic --> Partition1
    Topic --> Partition2
    Partition1 --> InventoryConsumer
    Partition2 --> InventoryConsumer
    Partition1 --> AnalyticsConsumer
    Partition2 --> AnalyticsConsumer
```

A **partition** is the *unit of parallelism and scaling* inside a topic.


A topic is split into N partitions:

```
Topic: orders
    ├── Partition 0
    ├── Partition 1
    └── Partition 2
```

```
Partition 0: [event1][event2][event3]...
Partition 1: [event4][event5][event6]...
Partition 2: [event7][event8]...
```

A producer decides **which partition** to send a message to:

* By key hashing (preferred)
* Round-robin (if no key)
* Custom partitioner

### Scalability

Partitions allow Kafka to scale horizontally across many brokers.

More partitions → More consumers can process the topic **in parallel** → higher throughput.


### Replication

Partitions can be **replicated** across brokers for durability and fault tolerance.

Example:

```
Partition 0:
  Leader: Broker 1
  Followers: Broker 2, Broker 3
```

If the leader fails, a follower becomes the new leader (**RAFT**).





## Kafka – Consumer Groups

* **Consumer Group** = a set of consumers that **work together to read a topic**
* **Each partition** is read by **only one consumer per group** → preserves **message order**
* **Multiple consumers in the same group** → Kafka **balances partitions** among them
* **Multiple consumer groups** → each group receives **all messages independently**

```mermaid
flowchart LR
subgraph Topic Orders 3 Partitions
P0[Partition 0]
P1[Partition 1]
P2[Partition 2]
end

subgraph CG1 Consumer Group 1
C1[Consumer 1]
C2[Consumer 2]
end

subgraph CG2 Consumer Group 2
C3[Consumer 3]
end

%% Assign partitions to CG1
P0 --> C1
P1 --> C2
P2 --> C2

%% Assign partitions to CG2 (all partitions go to C3)
P0 --> C3
P1 --> C3
P2 --> C3
```


### Ordering

**Ordering is guaranteed only within one partition**.
Across partitions, there is *no ordering guarantee*.

If you need ordering per key (e.g., same `orderId`), you send messages using a **key**, and Kafka ensures:
**same key → same partition → same order**.




## **Resources**

* [Kafka Official Documentation](https://kafka.apache.org/documentation/)
* [Kafka Tutorials](https://kafka.apache.org/quickstart)
* [Kafka in Action](https://www.manning.com/books/kafka-in-action)
* https://www.redpanda.com/


# **Asynchronous Communications (Kafka)**

[Apache Kafka](https://kafka.apache.org/) is a **distributed streaming platform** designed for **high-throughput, fault-tolerant, and scalable** data pipelines.
Kafka enables applications to **publish and subscribe to streams of records**, **store records durably**, and **process streams in real-time**.

---

## **Kafka vs RabbitMQ**

| Feature                | Kafka                                                            | RabbitMQ                                                                |
| ---------------------- | ---------------------------------------------------------------- | ----------------------------------------------------------------------- |
| **Messaging Model**    | Topic-based log; persistent; consumers track offsets             | Queue-based; messages removed after consumption; broker pushes messages |
| **Delivery Semantics** | At-most-once, At-least-once, Exactly-once                        | At-most-once, At-least-once (limited exactly-once)                      |
| **Persistence**        | Always persisted; configurable retention                         | Optional; durable vs transient queues                                   |
| **Ordering**           | Within partition                                                 | Within queue                                                            |
| **Throughput**         | Very high (millions msgs/sec)                                    | Moderate (tens of thousands msgs/sec per node)                          |
| **Scalability**        | Partitioned topics; automatic rebalancing                        | Queues can be sharded; horizontal scaling less automatic                |
| **Consumer Model**     | Pull-based; consumers fetch at own pace                          | Push-based; broker pushes messages                                      |
| **Replay**             | Full replay possible                                             | Limited; once consumed, messages gone unless requeued                   |
| **Use Cases**          | Event streaming, analytics, log aggregation, CDC, event sourcing | Work queues, notifications, RPC, pub/sub, small-scale messaging         |


## **Key Components**

* **Broker**: Kafka server that stores records and serves clients.
* **Producer**: Application that publishes messages to Kafka topics.
* **Consumer**: Application that subscribes to topics and processes messages.
* **Topic**: Logical channel where messages are published.
* **Partition**: Each topic is divided into partitions for parallelism and scalability.
* **Consumer Group**: A group of consumers sharing the work of reading a topic’s partitions.

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

---

## **Topics and Partitions**

* **Topic**: Stream of messages identified by a name.
* **Partition**: A topic is divided into partitions for **parallelism and scalability**.
* **Offset**: Each message in a partition has a unique sequential ID called an **offset**.
* **Retention**: Kafka keeps messages for a configurable duration or size, independent of consumption.
* **Replay**: Consumers can re-read messages by resetting their offsets.

```mermaid
flowchart LR
    Topic[Topic: orders]
    Partition0[Partition 0]
    Partition1[Partition 1]
    Partition2[Partition 2]
    ConsumerGroup1[Consumer A]
    ConsumerGroup2[Consumer B]

    Topic --> Partition0
    Topic --> Partition1
    Topic --> Partition2
    Partition0 --> ConsumerGroup1
    Partition1 --> ConsumerGroup1
    Partition1 --> ConsumerGroup2
    Partition2 --> ConsumerGroup2
```

**Explanation:**

* Partitions are **shards of a topic** → events are distributed across partitions.
* Ordering is **guaranteed within each partition**, not across partitions.
* Consumer groups can process the same topic independently.

---

## **Top Kafka Use Cases**

---

### **1. Log Aggregation**

Kafka centralizes logs from many services or servers. Producers send log messages to Kafka topics, and consumers like Elasticsearch or monitoring dashboards read them.

```mermaid
flowchart LR
    Service1[Service 1]
    Service2[Service 2]
    KafkaTopic[Kafka Topic: logs]
    Elasticsearch[Elasticsearch]
    Kibana[Kibana Dashboard]

    Service1 --> KafkaTopic
    Service2 --> KafkaTopic
    KafkaTopic --> Elasticsearch
    Elasticsearch --> Kibana
```

**Key Points:**

* Decouples log producers and consumers.
* Handles high throughput.
* Messages can be replayed if needed.

---

### **2. Change Data Capture (CDC)**

Kafka can capture database changes (insert/update/delete) and stream them to other systems in real-time.

```mermaid
flowchart LR
    Database[(Database)]
    Debezium[Debezium CDC Connector]
    KafkaTopic[Kafka Topic: db-changes]
    DataWarehouse[(Data Warehouse)]
    SearchEngine[(Elasticsearch)]

    Database --> Debezium
    Debezium --> KafkaTopic
    KafkaTopic --> DataWarehouse
    KafkaTopic --> SearchEngine
```

**Key Points:**

* Keeps multiple systems in sync.
* Supports replay by consumers.
* Maintains ordering per key (e.g., primary key).

---

### **3. Real-Time Analytics**

Kafka streams data to analytics systems for immediate insights.

```mermaid
flowchart LR
    App1[App / IoT Device]
    App2[App / IoT Device]
    KafkaTopic[Kafka Topic: sensor-data]
    StreamProcessor[Kafka Streams / Flink]
    Dashboard[Kibana / Grafana]

    App1 --> KafkaTopic
    App2 --> KafkaTopic
    KafkaTopic --> StreamProcessor
    StreamProcessor --> Dashboard
```

**Key Points:**

* Real-time processing and transformations.
* Supports aggregation, windowing, anomaly detection.
* Provides instant dashboards and alerts.

---

### **4. Event Sourcing**

Kafka acts as the event store: all state changes are persisted as events and can be replayed to rebuild system state.

```mermaid
flowchart LR
    UserService[User Service]
    KafkaTopic[Kafka Topic: user-events]
    MaterializedView[Materialized View / DB]
    Replay[Replay Service]

    UserService --> KafkaTopic
    KafkaTopic --> MaterializedView
    KafkaTopic --> Replay
```

**Key Points:**

* Immutable event log enables state reconstruction.
* Supports auditing and debugging.
* Can build derived views via stream processing.


## **Resources**

* [Kafka Official Documentation](https://kafka.apache.org/documentation/)
* [Kafka Tutorials](https://kafka.apache.org/quickstart)
* [Kafka in Action](https://www.manning.com/books/kafka-in-action)


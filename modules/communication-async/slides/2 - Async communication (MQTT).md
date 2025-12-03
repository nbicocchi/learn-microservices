# Asynchronous communications (MQTT)

The **Message Queuing Telemetry Transport (MQTT)** is a lightweight, publish-subscribe messaging protocol designed for constrained devices, low-bandwidth networks, and IoT applications. It is widely used in microservices when efficiency and low latency are important.

| Broker                          | MQTT Version      | Notes                                                                                |
| ------------------------------- | ----------------- | ------------------------------------------------------------------------------------ |
| **Mosquitto**                   | 3.1 / 3.1.1 / 5.0 | Open source, lightweight broker, widely used in IoT and microservices.               |
| **HiveMQ**                      | 3.1 / 3.1.1 / 5.0 | Commercial broker with enterprise support and high scalability.                      |
| **EMQX**                        | 3.1 / 3.1.1 / 5.0 | High-performance, open-source broker with clustering and cloud-native support.       |
| **RabbitMQ (with MQTT plugin)** | 3.1 / 3.1.1 / 5.0 | Supports MQTT alongside AMQP/STOMP. Useful for heterogeneous messaging environments. |
| **AWS IoT Core**                | 3.1.1 / 5.0       | Cloud-managed MQTT broker with secure device connectivity.                           |
| **Microsoft Azure IoT Hub**     | 3.1 / 3.1.1       | Cloud MQTT broker supporting device-to-cloud and cloud-to-device messaging.          |
| **Google Cloud IoT Core**       | 3.1 / 3.1.1       | Managed MQTT broker for secure IoT device communication.                             |
| **LavinMQ**                     | 3.1.1 / 5.0       | High-performance broker, compatible with standard MQTT clients.                      |

## Message Structure

### Message Overview

An **MQTT message** consists of a **topic**, **payload**, and optional **QoS / properties**. MQTT messages are typically very lightweight.

**Components:**

* **Topic**: hierarchical string used to route messages
* **Payload**: the data being sent (binary, JSON, text, etc.)
* **QoS (Quality of Service)**: delivery guarantee level
* **Retain flag**: indicates if broker should store the last message for new subscribers
* **Message ID / Properties**: optional metadata

```
## Example MQTT PUBLISH

Topic: bank/account/BA-1001-2025/deposit
Payload: { "amount": 100.0 }
QoS: 1
Retain: false
Message ID: 42
```

### Topics

* Topics are **hierarchical strings**: `level1/level2/level3`
* Examples:

    * `bank/account/BA-1001-2025/deposit`
    * `sensors/temperature/lab1`
* Supports **wildcards** for subscriptions:

    * `+` → single-level wildcard
    * `#` → multi-level wildcard
* Enables **selective message delivery** to subscribers.

### Quality of Service (QoS)

* MQTT defines **three QoS levels** for message delivery:

    * `0` → At most once (fire-and-forget)
    * `1` → At least once (message may be delivered multiple times)
    * `2` → Exactly once (ensures no duplicates)

Absolutely! Let’s dive deeper into **Retained Messages** and **Last Will and Testament (LWT)** in MQTT. These are key concepts that make MQTT more than just a simple pub/sub protocol.


### Retained Messages

A **retained message** is a special kind of MQTT message that the broker **remembers** even after it has been delivered to all current subscribers.

How it works:

1. When a publisher sends a message with the **retain flag set to `true`**, the broker **stores the last retained message per topic**.
2. Any **new subscriber** who subscribes to that topic **immediately receives the retained message**, even if the message was published before they subscribed.
3. Only **one retained message per topic** exists at a time. When a new retained message is sent on the same topic, it **replaces the previous one**.

```
Topic: sensors/temperature/lab1
Payload: 22.5
Retain: true
```

> Think of retained messages like a “sticky note” attached to a topic — the broker always keeps the **latest sticky note** for new subscribers.


### Last Will and Testament (LWT)

The **Last Will and Testament (LWT)** is a mechanism for **notifying other clients if a client disconnects unexpectedly**.

How it works:

1. When a client connects to the broker, it can specify an **LWT message** with:

    * **Topic** where it should be published
    * **Payload** (the message content)
    * **QoS** and **retain flag** for the message
2. If the client **disconnects gracefully**, nothing happens.
3. If the client **disconnects ungracefully** (e.g., network failure, crash), the broker **automatically publishes the LWT message** to the specified topic.
4. Subscribers of that topic can then take **corrective action**.

```
Topic: clients/BA-1001-2025/status
Payload: "offline"
QoS: 1
Retain: true
```

### Subscriptions

* Subscribers register interest in **topics** or **topic patterns**.
* Broker delivers messages according to **topic hierarchy** and QoS settings.
* Supports **multiple subscribers per topic**.

## Why MQTT is used

* Extremely lightweight → suitable for **IoT and constrained devices**
* Low bandwidth and low latency
* Reliable delivery with configurable QoS
* Easy integration with **microservices**, edge devices, and cloud services
* Ideal for **telemetry, monitoring, notifications, and event-driven architectures**

## Resources

* [MQTT Official Specification](https://mqtt.org/)
* [Eclipse Mosquitto](https://mosquitto.org/)
* [HiveMQ Documentation](https://www.hivemq.com/docs/)
* [MQTT Essentials Series](https://www.hivemq.com/mqtt-essentials/)

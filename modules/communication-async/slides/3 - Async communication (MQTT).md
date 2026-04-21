# Asynchronous communications (MQTT)

The **Message Queuing Telemetry Transport (MQTT)** is a lightweight, publish-subscribe messaging protocol designed for constrained devices, low-bandwidth networks, and IoT applications. 

* Extremely lightweight → suitable for **IoT and constrained devices**
* Low bandwidth and low latency
* Reliable delivery with configurable QoS
* Easy integration with **microservices**, edge devices, and cloud services
* Ideal for **telemetry, monitoring, notifications, and event-driven architectures**

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

An **MQTT message** consists of:
* **Topic**: hierarchical string used to route messages
* **Payload**: the data being sent (binary, JSON, text, etc.)
* **QoS (Quality of Service)**: delivery guarantee level
* **Retain flag**: indicates if broker should store the last message for new subscribers
* **Message ID / Properties**: optional metadata

```
## Example MQTT PUBLISH

Topic: bank/account/BA-1001-2025
Payload: { "amount": 100.0 }
QoS: 1
Retain: false
```

### Topics

* Topics are **hierarchical strings**: 
  * `level1/level2/level3`
  * `bank/account/BA-1001-2025`
  * `sensors/temperature/lab1`

### Quality of Service (QoS)

* MQTT defines **three QoS levels** for message delivery:

    * `0` → At most once (fire-and-forget)
    * `1` → At least once (message may be delivered multiple times)
    * `2` → Exactly once (ensures no duplicates)

### Retained Messages

**Retained message (MQTT)**: a message the broker **remembers** for a topic.

* Publisher sets **retain = true** → broker stores it.
* New subscribers **immediately receive** the last retained message.
* Only **one retained message per topic** exists; new retained messages **replace** the old one.

```
Topic: sensors/temperature/lab1
Payload: 22.5
Retain: true
```

> Think of retained messages like a “sticky note” attached to a topic — the broker always keeps the **latest sticky note** for new subscribers.


### Last Will and Testament (LWT)

**Last Will and Testament (LWT) – MQTT**: a mechanism to **notify others if a client disconnects unexpectedly**.

* Client sets **LWT** on connect: topic, payload, QoS, retain.
* **Graceful disconnect** → LWT is ignored.
* **Ungraceful disconnect** → broker **automatically publishes** the LWT message.
* Subscribers can take **corrective action**.

```
LWT topic: clients/BA-1001-2025/status
LWT payload: "offline"
LWT retain: true
```

### Subscriptions

* Subscribers register interest in **topics** or **topic patterns**.
* Broker delivers messages according to **topic hierarchy** and QoS settings.
* Supports **multiple subscribers per topic**.
* Supports **wildcards** for subscriptions:

* `+` → Matches **exactly one level** of the topic hierarchy
* `#` → Matches **any number of levels**, including zero


```text
Topic subscription: home/+/temperature
Matches:
home/livingroom/temperature
home/kitchen/temperature
Does NOT match:
home/livingroom/inside/temperature
```

```text
Topic subscription: home/#
Matches:
home/livingroom/temperature
home/kitchen/humidity
home/livingroom/inside/light
```

## Resources

* [MQTT Official Specification](https://mqtt.org/)
* [Eclipse Mosquitto](https://mosquitto.org/)
* [HiveMQ Documentation](https://www.hivemq.com/docs/)
* [MQTT Essentials Series](https://www.hivemq.com/mqtt-essentials/)

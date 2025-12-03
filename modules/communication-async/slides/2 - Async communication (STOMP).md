# Asynchronous communications (STOMP)

The **Simple (or Streaming) Text Oriented Messaging Protocol (STOMP)** is a lightweight, text-based protocol designed for messaging between clients and brokers. It is often used in web applications and microservices for simplicity and interoperability.

| Broker                           | STOMP Version | Notes                                                                                            |
| -------------------------------- | ------------- | ------------------------------------------------------------------------------------------------ |
| **RabbitMQ**                     | 1.2           | Supports STOMP alongside AMQP. Great for microservices, especially when using WebSocket clients. |
| **ActiveMQ (Classic / Artemis)** | 1.2 / 1.1     | Open source, supports STOMP, MQTT, AMQP. Popular in enterprise systems.                          |
| **LavinMQ**                      | 1.2           | High-performance, compatible with standard STOMP clients.                                        |
| **Apollo (ActiveMQ Apollo)**     | 1.2           | Lightweight broker focused on STOMP and MQTT.                                                    |
| **HornetQ**                      | 1.0           | Java-based, legacy broker with STOMP support.                                                    |
| **Solace PubSub+**               | 1.2           | Commercial broker; supports STOMP, MQTT, AMQP, and REST messaging.                               |

## Message Structure

### Message Overview

A **STOMP message** consists of a frame, which contains **headers** and a **body** (payload). Unlike AMQP, STOMP is **text-based** and simple.

**Components:**

* **Command**: indicates the action (`SEND`, `SUBSCRIBE`, `ACK`, `CONNECT`, etc.)
* **Headers**: metadata such as `destination`, `content-type`, `receipt`, etc.
* **Body**: the actual data payload (text, JSON, XML, etc.)

```
## Example STOMP SEND frame

SEND
destination:/queue/account-updates
content-type:application/json
priority:5
correlation-id:123e4567-e89b-12d3-a456-426614174000

{
    "accountId": "BA-1001-2025",
    "amount": 100.0
}
^@
```

> Note: `^@` is the STOMP frame terminator (null character).

### Headers

* Key-value pairs attached to a STOMP frame.
* Typical use cases:

    * Routing (`destination`)
    * Priorities
    * Traceability (`correlation-id`)
    * Subscription management (`ack`, `id`)
* Example:

```text
headers = {
  "destination": "/queue/money.deposit",
  "receipt": "msg-12345",
  "priority": "5",
  "correlation-id": "123e4567-e89b-12d3-a456-426614174000"
}
```

### Destination

* STOMP uses **destinations** instead of exchanges.
* A **destination** is like a topic or queue in AMQP.
* `/queue/name` → point-to-point queue
* `/topic/name` → publish-subscribe topic
* Supports wildcards with `/topic/*` for multiple subscriptions.

### Acknowledgements

* Consumers can acknowledge messages to signal successful processing.
* Modes:

    * `auto` → automatic acknowledgment
    * `client` → client must explicitly send `ACK`
    * `client-individual` → acknowledge individual messages

## Subscriptions

* Consumers **subscribe** to destinations.
* Example:

```
SUBSCRIBE
id:sub-001
destination:/queue/money.deposit
ack:client
^@
```

* Multiple consumers can subscribe to the same destination.
* Messages are distributed according to **queue semantics** (point-to-point) or **topic semantics** (publish-subscribe).

## Why STOMP is used

* Very simple and lightweight, easy to implement in clients.
* Works well with **WebSockets** for browser-based applications.
* No need for heavy brokers or advanced routing logic if simple pub/sub or queue semantics suffice.
* Useful for **real-time notifications** and **microservices with light messaging requirements**.

## Resources

* [STOMP Protocol Specification](https://stomp.github.io/)
* [RabbitMQ STOMP Plugin](https://www.rabbitmq.com/stomp.html)
* [ActiveMQ STOMP Documentation](https://activemq.apache.org/stomp)



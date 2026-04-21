# Asynchronous Communication (STOMP)

## What is STOMP?

The **Simple Text Oriented Messaging Protocol (STOMP)** is a **lightweight, text-based messaging protocol** used to communicate between **clients and message brokers**.

👉 Key idea:
STOMP prioritizes **simplicity and interoperability** over advanced features.

**Typical use cases:**

* Web applications (via WebSocket)
* Lightweight microservices communication
* Real-time notifications

---

## Supported Brokers

| Broker                       | STOMP Version | Notes                                                       |
| ---------------------------- | ------------- | ----------------------------------------------------------- |
| RabbitMQ                     | 1.2           | Supports STOMP alongside AMQP; widely used in microservices |
| ActiveMQ (Classic / Artemis) | 1.2 / 1.1     | Enterprise-ready; supports STOMP, MQTT, AMQP                |
| LavinMQ                      | 1.2           | High-performance, STOMP-compatible                          |
| ActiveMQ Apollo              | 1.2           | Lightweight; STOMP + MQTT focused                           |
| HornetQ                      | 1.0           | Legacy Java broker                                          |
| Solace PubSub+               | 1.2           | Commercial; multi-protocol (STOMP, MQTT, AMQP, REST)        |

---

## STOMP Message Structure

A STOMP message is called a **frame** and has three parts:

### 1. Command

👉 **Command**: specifica l’operazione del frame

* `CONNECT` → opens a connection with the broker
* `SEND` → sends a message to a destination
* `SUBSCRIBE` → registers to receive messages
* `ACK` → confirms successful processing of a message

### 2. Headers

Headers are simple **key-value pairs**:

```text
destination: /queue/money.deposit
receipt: msg-12345
priority: 5
correlation-id: 123e4567...
```

👉 They are used for:

* Routing
* Message tracking
* Delivery control

### 3. Body

The payload (e.g., JSON, XML, text)

---

### Example: SEND Frame

```
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

👉 `^@` = frame terminator (null character)

---

## Destinations

STOMP uses **destinations** instead of exchanges.

Think of them as:

* `/queue/...` → **Point-to-point (load balanced)**
* `/topic/...` → **Publish/subscribe (broadcast)**

Examples:

* `/queue/orders`
* `/topic/notifications`

👉 Wildcards supported:

* `/topic/*`

---

## Subscriptions

Consumers subscribe to a destination:

```
SUBSCRIBE
id:sub-001
destination:/queue/money.deposit
ack:client
^@
```
--- 

## Acknowledgements

Consumers can confirm message processing.

Modes:

* `auto` → the broker **automatically considers the message delivered** as soon as it’s sent; the client **does nothing**.
* `client` → the client must **manually send an ACK**; all messages received up to the ACK are confirmed together.
* `client-individual` → the client sends an **ACK for each individual message**, confirming each message separately.

👉 Important for:

* Reliability
* Backpressure control


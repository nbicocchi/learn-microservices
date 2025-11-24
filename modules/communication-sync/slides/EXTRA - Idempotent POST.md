# Implementing Idempotent POST Requests


* Idempotence = operation that can be repeated with the same effect
* GET/PUT/DELETE → idempotent
* POST → *not* idempotent
* Duplicate POSTs → duplicate orders / events

![](https://miro.medium.com/v2/resize:fit:1400/format:webp/1*2eZKbFnA-OFHifRCwz4Jkg.png)

---

## The Problem

* Retries happen because of:

    * client timeouts
    * network failures
    * server crashes
* POST /orders sent twice may create two orders
* DB constraints don’t help when the same order is valid twice
* Downstream (Kafka, billing, logistics) → becomes a mess

![](https://miro.medium.com/v2/resize:fit:2000/format:webp/1*7UYZf4-Vyf5FKGCIwyUIMw.png)

---

## Solution Overview

* Client sends a **unique idempotency key**

    * Header: `X-Idempotency-Key: <uuid>`
* Server stores the key
* If key already present → immediately return **409 Conflict**
* Guarantee: each *intent* is processed once

![](https://miro.medium.com/v2/resize:fit:4800/format:webp/1*cKMgClCxAXOBaifX_RpJPQ.png)

---

## Architecture

* Client → sends key + request body
* Server:

    1. try to create key record (transaction)
    2. if locked → 409
    3. else create order + publish event
    4. return 201

![](https://miro.medium.com/v2/resize:fit:2000/format:webp/1*PZMRJDL1X1rkJ4Yy8Q0-rQ.png)
---

## Controller Example

```java
@PostMapping
public ResponseEntity<?> createOrder(
    @RequestHeader("X-Idempotency-Key") String key,
    @RequestBody CreateCustomerOrderDto order) {

    IdempotencyKey entity;
    try {
        entity = idempotencyKeyService
            .getOrCreateIdempotencyKeyByKeyAndUserId(key, order.getUserId());
    } catch (IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("status","duplicate"));
    }

    Long orderId = ordersService.createOrder(order);
    kafkaProducerService.sendMessage("orders-topic", orderId);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of("orderId", orderId));
}
```

---

## Idempotency Service

```java
@Transactional
public IdempotencyKey getOrCreateIdempotencyKeyByKeyAndUserId(
       String key, String userId) {

    Optional<IdempotencyKey> existing =
        repo.findByIdempotencyKeyAndUserId(key, userId);

    IdempotencyKey idKey;

    if (existing.isPresent()) {
        idKey = existing.get();
        if (checkIfIsLocked(idKey)) {
            throw new IllegalStateException("Key is locked");
        }
    } else {
        idKey = new IdempotencyKey(key, userId);
    }

    idKey.setLockedAt(LocalDateTime.now());
    return repo.save(idKey);
}
```

## Resources
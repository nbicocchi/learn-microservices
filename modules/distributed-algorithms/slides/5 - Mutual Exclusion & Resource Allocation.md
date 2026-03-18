# Distributed Mutual Exclusion Algorithms

## **Lamport’s Bakery Algorithm (Shared-Memory Style)**

**Idea:** Logical “take-a-number” system.

**Steps:**

1. **Take a Ticket**

    * `ticket[i] = max(ticket[]) + 1`

2. **Wait Your Turn**

    * Wait if another process:

        * is **choosing**, OR
        * has **smaller ticket**
    * Tie-break: lower **process ID**

3. **Enter CS**

4. **Release**

    * `ticket[i] = 0`

**Properties:**

* ✔ Fair (FIFO ordering)
* ✔ No starvation
* ❌ Not message-based (assumes shared memory)
* ❌ Not scalable in distributed systems

---

## **Lamport’s Distributed Mutex (Timestamp-Based)**

**Idea:** Total ordering using logical clocks.

**Steps:**

1. **Request**

    * Send `REQUEST(ts, i)` to all
    * Add to local **priority queue**

2. **Receive**

    * Reply with `REPLY`
    * Insert request into queue

3. **Enter CS**

    * Your request is:

        * at **top of queue**, AND
        * you received **all replies**

4. **Release**

    * Send `RELEASE` to all

**Properties:**

* ✔ Strong ordering (Lamport clocks)
* ✔ Fully distributed
* ❌ High message cost: **3(N−1)**
* ❌ Sensitive to node failures

---

## **Ricart–Agrawala Algorithm**

**Idea:** Optimization of Lamport (no RELEASE message).

**Steps:**

1. **Request**

    * Send `REQUEST(ts, i)` to all

2. **On Receive**

    * Send `REPLY` if:

        * not interested, OR
        * your request has **lower priority**
    * Otherwise → **defer**

3. **Enter CS**

    * After receiving **all replies**

4. **Release**

    * Send all **deferred replies**

**Properties:**

* ✔ Fewer messages: **2(N−1)**
* ✔ Fully distributed
* ❌ Blocking if a node crashes
* ❌ Still requires all-to-all communication

---

## **Token Ring Algorithm**

**Idea:** Permission is embodied in a circulating token.

**Steps:**

1. **Logical Ring**

    * Nodes arranged in a ring

2. **Token Circulation**

    * Single token moves continuously

3. **Request**

    * Wait for token

4. **Enter CS**

    * Hold token → enter CS

5. **Release**

    * Pass token to next node

**Properties:**

* ✔ Very low message overhead (1 token)
* ✔ No contention messages
* ✔ Simple
* ❌ Token loss = system halt
* ❌ Latency depends on ring size

---

# When to Use What

* **Teaching / theory** → Bakery (intuitive fairness)
* **Strict ordering needed** → Lamport / Ricart–Agrawala
* **Lower message overhead** → Ricart–Agrawala
* **Simple & efficient systems** → Token Ring


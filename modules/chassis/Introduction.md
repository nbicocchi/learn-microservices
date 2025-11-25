## Synchronous Microservices
**Definition:** Each request is handled in a **dedicated thread** and blocks until the operation completes.  

**Technical Details / Limitations:**
- **Blocking I/O:** CPU waits while DB or HTTP call executes  
- **Thread-per-request model:** Memory usage grows linearly with concurrent requests  
- **Limited scalability:** Only as many simultaneous requests as available threads  
- **Poor support for long-lived connections:** Each WebSocket/SSE consumes a thread  
- **High latency under load:** Slow downstream calls block processing  

**Spring Boot Example:**
```java
@GetMapping("/items")
public List<String> getItems() {
    try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    return List.of("Item1","Item2","Item3");
}
```

**Python / FastAPI Example (blocking):**
```python
from fastapi import FastAPI
import time

app = FastAPI()

@app.get("/items-sync")
def get_items_sync():
    time.sleep(1)  ## blocks thread
    return ["Item1","Item2","Item3"]
```

---

## Asynchronous Microservices
**Definition:** Tasks **yield control while awaiting I/O**; the **event loop** schedules pending operations.  

**Benefits:**
- Single thread handles thousands of connections efficiently  
- Better CPU utilization  
- Supports long-lived connections (WebSockets, streaming)  
- Reduces latency spikes under load  

**Spring Boot WebFlux Example:**
```java
@GetMapping("/items")
public Mono<List<String>> getItems() {
    return Mono.delay(Duration.ofSeconds(1))
               .map(ignore -> List.of("Item1","Item2","Item3"));
}
```

**Python / FastAPI Example (async):**
```python
from fastapi import FastAPI
import asyncio

app = FastAPI()

@app.get("/items-async")
async def get_items_async():
    await asyncio.sleep(1)  ## non-blocking
    return ["Item1","Item2","Item3"]
```

---

## Event Loop
**Definition:** Core engine of async programming that **schedules tasks without blocking threads**.  

**Key Points:**
- Operates in a **single thread** per loop  
- Tasks **yield control** during I/O operations (`await`)  
- Can schedule thousands of **concurrent tasks**  
- Present in Python (`asyncio`), Node.js, Spring WebFlux/Netty, Quarkus/Vert.x  

**Python Example:**
```python
async def fetch_data():
    await asyncio.sleep(1)
    return "data"

async def main():
    task1 = asyncio.create_task(fetch_data())
    task2 = asyncio.create_task(fetch_data())
    result = await task1
    print(result)

asyncio.run(main())
```

---

## Slide 5: Async vs Reactive Programming
| Aspect               | Async Programming                   | Reactive Programming                     |
|---------------------|------------------------------------|----------------------------------------|
| Focus               | Non-blocking I/O                    | Streams of events/data with flow control |
| Control             | Await tasks via event loop          | Consumer-driven: backpressure & flow management |
| Composition         | Sequential `await` or callbacks    | Chain operators (`map`, `flatMap`, `filter`) |
| Error Handling      | Try/except or callbacks             | Operators like `onErrorResume` handle errors in streams |
| Backpressure support | Limited / manual                   | Built-in, prevents overload in pipelines |
| Example             | FastAPI `async def`                 | Spring WebFlux `Mono` / `Flux`         |

---

## Backpressure

**Definition:** Prevents fast producers from overwhelming slow consumers.

**Strategies:**

* Blocking / pause producer
* Buffering / temporary queue
* Dropping items
* Latest-only (keep most recent)
* Error signaling

**Examples:**

* **Java / Reactor (producer-consumer with backpressure)**

```text
Flux.range(1, 1000)
    .onBackpressureBuffer(100) // buffer up to 100 items
    .limitRate(50)             // request 50 items at a time
    .subscribe(
        item -> process(item),
        err -> System.err.println("Error: " + err),
        () -> System.out.println("Processing complete")
    );
```

* **Python / asyncio (producer-consumer with async queue)**

```python
import asyncio

async def producer(queue):
    for i in range(1000):
        await queue.put(i)  ## waits if queue is full
        print(f"Produced {i}")

async def consumer(queue):
    while True:
        item = await queue.get()
        print(f"Consumed {item}")
        await asyncio.sleep(0.1)  ## simulate slow consumer
        queue.task_done()

async def main():
    queue = asyncio.Queue(maxsize=50)  ## backpressure: limits queue size
    prod_task = asyncio.create_task(producer(queue))
    cons_task = asyncio.create_task(consumer(queue))
    await prod_task
    await queue.join()  ## wait until all items are processed
    cons_task.cancel()

asyncio.run(main())
```

**Notes:**

* Java `onBackpressureBuffer` buffers items safely and `limitRate` controls flow
* Python `asyncio.Queue(maxsize)` ensures the producer waits if the consumer is slow

This shows **realistic backpressure handling** in both sync and async systems.


---

## Modern Framework Examples
**Spring Boot:**
- Sync: Servlet/Tomcat → blocking threads  
- Async/Reactive: WebFlux + Netty → Mono/Flux, event-driven, backpressure  

**FastAPI / Starlette:**
- Async endpoints: `async def`, awaitable I/O  
- Event loop (`asyncio`) schedules tasks efficiently  
- Backpressure via async queues  

**Quarkus / Vert.x:**
- Event-driven with lightweight event loops  
- Event bus enables async messaging  
- Reactive messaging support (Kafka, AMQP)  

---

## References
- FastAPI docs: https://fastapi.tiangolo.com  
- Python asyncio: https://docs.python.org/3/library/asyncio.html  
- Spring WebFlux: https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html  
- Quarkus reactive guide: https://quarkus.io/guides/reactive  
- Reactive Streams spec: https://www.reactive-streams.org/

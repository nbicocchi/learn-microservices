# Concurrency vs Parallelism

In computer science, we often need to manage multiple tasks to improve efficiency. This is where the concepts of concurrency and parallelism come into play. While often used interchangeably, they are fundamentally different.

## Concurrency

**Concurrency** is about dealing with many things at once. It's a way of structuring a program so that it can handle multiple tasks seemingly at the same time, even if the computer's processor can only execute one instruction at a time.

A good analogy is a single *juggler*. The juggler is only catching and throwing one ball at a time, but by quickly switching between them, they keep all the balls in the air. This is a context-switching process. In a single-core CPU, concurrent tasks take turns using the processor.

## Parallelism

**Parallelism** is about doing *many things at the same time*. This requires multiple processors or processor cores.

Think of a team of chefs in a kitchen. Each chef is working on a different dish simultaneously. This is true simultaneous execution. Parallelism is essential for CPU-bound tasks, which are operations that are limited by the speed of the CPU, such as heavy computations or complex calculations.

## Is concurrency better than parallelism?

Concurrency is different than parallelism. And it is better on specific scenarios that involve a lot of waiting. Because of that, it generally is a lot better than parallelism for web application development. But not for everything.

# Python async/await

## Synchronous vs Asynchronous

A **synchronous** program executes tasks *one after the other*, in a sequential manner. The program must wait for one task to complete before it can start the next one. 

Think of a queue at a coffee shop: you must wait for the person in front of you to be served before you can place your order. This approach is simple to understand but can be inefficient, especially for I/O-bound tasks like fetching data from a network or a database, which involve waiting.

An **asynchronous** program allows tasks to be executed *independently*, without waiting for the previous one to finish.

Going back to the coffee shop analogy, an asynchronous model is like a waiter taking multiple orders from different tables before the first meal is ready. The waiter isn't blocked by the kitchen's preparation time. Instead, they can continue to take more orders.

In Python, this is particularly useful for tasks that involve a lot of waiting (I/O-bound operations), as it prevents the program from being idle.


## Asyncio library

Python's asyncio library, introduced in Python 3.4, provides a framework for writing concurrent code using the asynchronous paradigm (in particular using an event loop). The `async` and `await` keywords are the core components of this framework.

`async` is used to define a function as a **coroutine**. A coroutine is a special type of generator that can be *paused and resumed*. When you call an async function, it does **not execute immediately**, but it **returns a coroutine object**.

```py
async def fetch_data():
    # This is a coroutine
    ...
```

`await` is used *within* an async function to pause its execution and "await" the result of another coroutine. When an await is encountered, the control is *yielded back* to the **event loop** (the asyncio scheduler), allowing other tasks to run. Once the awaited task is complete, the event loop resumes the coroutine from where it left off.


```py
async def main():
    data = await fetch_data() # Execution pauses here
    ...
```

Together, `async` and `await` enable a style of programming called cooperative multitasking. The coroutines voluntarily yield control to each other, allowing the program to perform multiple operations concurrently on a single thread without blocking. This is an excellent solution for building high-performance, scalable applications that deal with many I/O-bound operations, such as web servers and network clients.


### Event loop

The **event loop** is a fundamental component of asynchronous programming in Python. It's essentially a scheduler or an orchestrator for your asynchronous tasks. The event loop continuously monitors coroutines for completion and dispatches new tasks as soon as the CPU becomes available.

Here's how it works:

1. A task starts, and if it encounters an I/O-bound operation (e.g., waiting for a network request to complete), it awaits the result.
2. When a task awaits, it temporarily yields control back to the event loop.
3. The event loop then looks for other tasks that are ready to run and dispatches one of them.
4. Once the original I/O operation is complete, the event loop is notified. It then puts the original task back on the schedule to continue its execution from where it left off.

This continuous cycle allows the program to remain responsive and perform multiple operations concurrently on a single thread. The event loop is a core mechanism that prevents the program from being idle while waiting for slow operations to finish.


### Why asyncio?

The **Global Interpreter Lock** (GIL) is a mutex (mutual exclusion lock) that protects access to Python objects, preventing multiple native threads from executing Python bytecode at the same time. This means that even on a multi-core processor, only one thread can execute Python code at any given moment.

The GIL was implemented to simplify memory management and prevent race conditions when multiple threads try to access the same memory objects. While it's a simplification, the main takeaway is that the GIL is a bottleneck for CPU-bound tasks in multi-threaded applications. If your program is crunching numbers, adding more threads won't make it faster because they will all be fighting for the GIL.


> [!NOTE]
> GIL will be removed in version 3.14!

Since the GIL is a limitation for multi-threading, asyncio is considered a good way to work around it. Given that asyncio operates on a single thread, it completely sidesteps the GIL's limitations. When an async function awaits an I/O operation, it releases the event loop, allowing another coroutine to run. Since no Python bytecode is being executed during the wait, the GIL is not a concern. The program is not blocked; it's just switching between tasks that are all waiting for external resources.



# Introduction to Pydantic



https://docs.pydantic.dev/latest/

## What is Pydantic?

Pydantic is a Python library that helps you define **data models** with automatic validation and parsing.  
It uses **Python type hints** to check that the data you receive matches the expected format, and it can convert types when possible.

In short:

1. You define a class with attributes and their types.
2. Pydantic validates and parses incoming data.
3. You get clean, safe, and predictable Python objects.

> [!TIP]
> If you are a Java developer, Pydantic is similar to *Lombok* library.

## Why use Pydantic?

In real-world applications, especially when dealing with **APIs**, **databases**, or **user input**, the data you receive may not always be:

- Complete
- Correctly typed
- Safe to use

Pydantic helps you:

1. **Validate** input data (e.g., an email must be valid).
2. **Parse** strings into proper types (e.g., `"2025-08-15"` into a `datetime` object).
3. **Avoid boilerplate code** for checking and cleaning data manually.
4. **Integrate easily** with frameworks like **FastAPI** and **Django**.

## Pydantic vs Dataclasses

Python already has a similar concept, i.e. `dataclasses`, which is the standard way (no dependencies) to create classes with built-in capabilities such as auto-generation of constructors. Dataclasses based their type hints on `typing` library.

Pydantic is *more powerful* given that it **validates** fields (raising an error if wrong data type is fed), instead dataclasses only provides auto-generated code.

```python
from dataclasses import dataclass

@dataclass
class User:
    email: str
    password: str

User(42, 3.14)      # no errors are raised
```

> [!TIP]
> Dataclasses is a very huge arguments, to know more see https://docs.python.org/3/library/dataclasses.html

## BaseModel

`BaseModel` is the main super-class provided by Pydantic to turn into managed classes your regular classes.

```python
from pydantic import BaseModel, EmailStr, ValidationError

class User(BaseModel):
    email: EmailStr
    password: str
    

User("mail@mail.com", "secret") # OK
User(42, 3.14)  # raise ValidationError

# Python async/await

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


# Python async/await

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

# Environment Variables

In modern software development, it's crucial to separate configuration from code.
**Environment variables** are key to achieving this. They're dynamic, named values that are external to your application's source code, allowing you to change the behavior of your application without modifying the code itself.

Why are they so important?

- **Security**: They let you manage sensitive information like API keys, database credentials, and secret keys securely. Instead of hard-coding them directly into your application, you store them as environment variables. This prevents them from being accidentally committed to version control systems like Git, where they could be exposed.
- **Flexibility**: Environment variables make your application portable across different environments. You can use one codebase for development, testing, and production, and simply change the environment variables to point to the correct database, API endpoint, or other service for each environment. For example, your DATABASE_URL can point to a local database for development and to a production database when deployed.
- **Decoupling**: They promote a clean separation between your application's logic and its configuration. This makes your code cleaner, more maintainable, and easier to test.


There are two main ways to manage environment variables:

- Manually with `os`
- Using Pydantic


## Os library

`os` is the Python standard library which allows you to interact with system.

An environment variable can be read thanks to `os.getenv`:

```py
import os

db_name = os.getenv("DB_NAME", "my_db")     # "my_db" is the default value
db_password = os.getenv("DB_PASSWORD")      # None as default value
```

> [!TIP]
> Try it on Linux using for example `export DB_PASSWORD=secret`


## Pydantic

First of all, you must install `pydantic-settings` library.

```
pip install pydantic-settings
```

Suppose a `.env` file such as:

```
DATABASE_URL=postgresql://user:password@localhost:5432/mydb
DEBUG=True
API_KEY=secret
```

You can easily access those values using this code:

```py
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    database_url: str
    debug: bool = False # default value
    api_key: str

    # optional, you can specify env file and its encoding
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

settings = Settings()

print(settings.database_url)
print(settings.debug)
print(settings.api_key)
```

Attribute names must be expressed in *lowercase* with respect to its actual environment variable (e.g. `api_key` for `API_KEY`).

> [!TIP]
> `model_config` could be omitted, especially if you will not use `.env` file.

In particular, followed order for environment variables is:

1. Environment variable (i.e., `export ...`)
2. `.env` (if provided)
3. Default value (if provided)

If an attribute doesn't have any default value and no environment variable (or `.env` is provided), then an error will be raised!

In fact, unlike Pydantic BaseModel, default values of BaseSettings **fields are validated by default**. You can disable this behaviour by setting `validate_default=False` either in model_config or on field level by `Field(validate_default=False)`.

```py
class Settings(BaseSettings):
    model_config = SettingsConfigDict(validate_default=False)

    # default won't be validated
    foo: int = 'test'
```

```py
class Settings1(BaseSettings):
    # default won't be validated
    foo: int = Field('test', validate_default=False)
```


# Logging

**Logging** is an essential practice in software development and system administration. It allows developers to record events, errors, and informational messages about the execution of a program. Instead of relying on `print()` statements, Python provides a powerful and flexible logging module that should be used in production code.


Why Logging?

- **Debugging**: Helps track down the root cause of errors.
- **Monitoring**: Provides insights into how a program behaves in real time.
- **Persistence**: Logs can be stored in files, databases, or monitoring systems for later analysis.
- **Control**: Logging allows filtering and categorizing messages by importance.


```py
import logging

logging.debug("This is a debug message.")
logging.info("This is an informational message.")
logging.warning("This is a warning.")
logging.error("This is an error.")
```

In order to configure logging, you must use `logging.basicConfig`

The `basicConfig` function can be use to set the minimum severity level of messages to display. In the following example, all messages with severity INFO and above will appear:

```py
import logging

logging.basicConfig(level=logging.INFO)

logging.debug("This is a debug message.")    # not visible!
logging.info("This is an informational message.")
logging.warning("This is a warning.")
logging.error("This is an error.")
```

Using `basicConfig` we can also:

- Use a custom prefix, e.g. the datetime

```py
logging.basicConfig(
    level=logging.INFO,
    stream=sys.stdout,  # by default stderr is used
    format="[%(asctime)s][%(levelname)s]: %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S"
)
```

- Store logs in a file

```py
logging.basicConfig(
    filename="app.log",
    filemode="a",  # append mode
    format="%(asctime)s - %(levelname)s - %(message)s",
    level=logging.DEBUG
)
```




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


## Python typing

Starting from version 3.6, Python provides **typing** to overcome weakly typed Python code, which can be dangerous in big projects.

"Unfortunately", Python doesn't have a compiler, therefore no compile-time errors can be raised (first main difference with Java). In addition, typing doesn't enforce you to provide right data type at runtime, i.e. no errors are raised if you provide for example a `str` instead of an `int`.

In other words, regular typing is only a fancy features for IDEs.

```python
def hello(name: str) -> str:
    return f"Hello {name}!"
```

You can access a lot of data types thanks to `typing` library.

```python
from typing import List, Dict, Tuple
```

> [!TIP]
> Actually, there are some projects which provides a compiler for Python. See the *Mojo project*.

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

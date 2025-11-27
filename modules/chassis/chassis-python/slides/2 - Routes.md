# Routes

In FastAPI, **routes** are the core mechanism that connects HTTP requests to the code that should handle them. Each route corresponds to a specific path (URL) and an HTTP method (GET, POST, etc.).

A **route** is simply a function *decorated* with an HTTP method on your `app` object. For example:

```py
from fastapi import FastAPI

app = FastAPI()

@app.get("/")   # <-- GET route 
def read_root():
    return {"message": "Welcome to FastAPI"}
```

`@app.get("/")` means:

- Listen for an HTTP **GET** request
- At the path `/` (the root URL)
- Execute the function `read_root()`

The return value (if provided) is automatically converted to JSON.


## Supported HTTP Methods

FastAPI supports all the most common HTTP methods. You can declare them using decorators on the `app` object:

- `@app.get(path)`: Retrieve data (read operations)
- `@app.post(path)`: Create new data
- `@app.put(path)`: Replace existing data
- `@app.patch(path)`: Update part of existing data
- `@app.delete(path)`: Remove data
- `@app.options(path)`: Discover supported operations
- `@app.head(path)`: Retrieve metadata (like headers) without a body

## Request/Response

A **response** body is the data your API sends to the client.

You can declare the **type** used for the response by annotating the path operation function **return type**.

You can use *type annotations* the same way you would for input data in function parameters, you can use Pydantic models, lists, dictionaries, scalar values like integers, booleans, etc.

FastAPI will use this return type to **validate the returned data**. If the data is invalid (e.g. you are missing a field), it means that your app code is *broken*, not returning what it should, and it will return a **server error** instead of returning incorrect data. This way you and your clients can be certain that they will receive the data and the data shape expected.

```py
# main.py

from fastapi import FastAPI
from pydantic import BaseModel
from typing import Optional, List

class Product(BaseModel):
    """
    Model which represent a product
    """

    name: str
    price: float
    tags: List[str]
    description: Optional[str] = None


app = FastAPI()     # FastAPI instance


@app.post("/products/")
async def create_product(product: Product) -> Product:
    """
    Should create a product based on input, returns created product
    """

    return product


@app.get("/products/")
async def read_products() -> List[Product]:
    """
    Return list of available products
    """

    return [
        Product(
            name="Keyboard",
            price=42.0,
            tags=["PC", "Hardware"]
        ),
        Product(
            name="Mouse",
            price=32.0,
            tags=["PC", "Hardware"],
            description="It is not the animal!"
        ),
    ]
```

### Less pedantic response

There are some cases where you need or want to return some data that is not exactly what the type declares.

In those cases, you can use the path operation decorator parameter `response_model` instead of the return type.

```py
@app.get("/products/", response_model=List[Product])    # <-- provide return type here
async def read_products() -> Any:
    return [
        {"name": "Keyboard", "price": 42.0, "tags": ["PC", "Hardware"]},
        {"name": "Mouse", "price": 32.0, "tags": ["PC", "Hardware"], "description": "It is not the animal!"},
    ]
```

This is very useful when we use DTOs. In fact, we can "change" DTO from input to output automatically. For example, suppose to have input and output of users in which we want to remove (obviously) password:

```py
from typing import Any
from fastapi import FastAPI
from pydantic import BaseModel, EmailStr

app = FastAPI()


class UserIn(BaseModel):
    username: str
    password: str
    email: EmailStr
    full_name: str | None = None


class UserOut(BaseModel):
    username: str
    email: EmailStr
    full_name: str | None = None


@app.post("/user/", response_model=UserOut)
async def create_user(user: UserIn) -> Any:
    return user
```

### Status code

#### Default status code

The same way you can specify a response model, you can also declare the ***default* HTTP status code** used for the response with the parameter `status_code` in any of the path operations.

```py
from fastapi import FastAPI

app = FastAPI()


@app.post("/items/", status_code=201)
async def create_item(name: str):
    return {"name": name}
```

`201` is the status code for "Created", but you can use the convenience variables from `fastapi.status`.

```py
from fastapi import FastAPI, status

app = FastAPI()


@app.post("/items/", status_code=status.HTTP_201_CREATED)
async def create_item(name: str):
    return {"name": name}
```

#### Custom status code

In order to provide a ***custom* HTTP status code**, based on route logic, we must use `Response` parameter, which will be automatically injected if present in method signature.

```py
from fastapi import FastAPI, Response, status

app = FastAPI()

tasks = {"foo": "Listen to the Bar Fighters"}


@app.put("/get-or-create-task/{task_id}", status_code=200)  # <-- default status code
def get_or_create_task(task_id: str, response: Response):
    if task_id not in tasks:
        tasks[task_id] = "This didn't exist before"
        response.status_code = status.HTTP_201_CREATED  # <-- custom status code
        
    return tasks[task_id]
```

### Path parameters

We can declare **path parameters** with the same syntax used by Python format strings in path and we can access them declaring a variable with the same name in the method signature:

```py
from fastapi import FastAPI

app = FastAPI()


@app.get("/products/{product_id}", status_code=200)
async def read_product(product_id: int) -> Product:
    
    products = [
        Product(
            name="Keyboard",
            price=42.0,
            tags=["PC", "Hardware"]
        ),
        Product(
            name="Mouse",
            price=32.0,
            tags=["PC", "Hardware"],
            description="It is not the animal!"
        ),
    ]

    return products[product_id]
```


### Query parameters

When you declare other function parameters that are not part of the path parameters, they are automatically interpreted as **query parameters**.

```py
from fastapi import FastAPI

app = FastAPI()

fake_items_db = [{"item_name": "Foo"}, {"item_name": "Bar"}, {"item_name": "Baz"}]


@app.get("/items/")
async def read_item(skip: int = 0, limit: int = 10):
    return fake_items_db[skip : skip + limit]
```

The query is the set of key-value pairs that go after the ? in a URL, separated by & characters.

For example, you must use as URL:

```
http://127.0.0.1:8000/items/?skip=0&limit=10
```

If default value is not provided, that query parameter is **mandatory**. 


## Resources

https://fastapi.tiangolo.com/tutorial/response-model/

https://fastapi.tiangolo.com/tutorial/response-status-code/

https://fastapi.tiangolo.com/tutorial/body/


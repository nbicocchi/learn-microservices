# Project Structure

If you are building an application or a web API, it's rarely the case that you can put everything in a single file.

FastAPI provides a convenience tool to structure your application while keeping all the flexibility.

One of the most used project structure is the following:

```
.
├── app                  # "app" is a Python package
│   ├── __init__.py      # this file makes "app" a "Python package"
│   ├── main.py          # "main" module, e.g. import app.main
│   ├── dependencies.py  # "dependencies" module, e.g. import app.dependencies
│   └── routers          # "routers" is a "Python subpackage"
│   │   ├── __init__.py  # makes "routers" a "Python subpackage"
│   │   ├── products.py  # "products" submodule, e.g. import app.routers.products
│   │   └── users.py     # "users" submodule, e.g. import app.routers.users
│   └── internal         # "internal" is a "Python subpackage"
│       ├── __init__.py  # makes "internal" a "Python subpackage"
│       └── admin.py     # "admin" submodule, e.g. import app.internal.admin
```

`main.py` contains configuration and initialization of FastAPI application.

`dependencies.py` (or if needed a submodule `dependencies`) contains all common and shared dependencies, for example the database session manager or session token manager.
Things in this file should be used using `fastapi.Depends`

```py
# dependencies.py example

from typing import Annotated
from fastapi import Header, HTTPException


async def get_token_header(x_token: Annotated[str, Header()]):
    if x_token != "fake-super-secret-token":
        raise HTTPException(status_code=400, detail="X-Token header invalid")
```

`routers` module contains different files, each of them represents a **controller** (set of routes) for an entity. `routers` contains **public API**.

`internal` module contains internal organization use only code, or **private API**, e.g. admin dashboard API.


## APIRouter (Controller)

`APIRouter` is object which allows us to manage routes (exactly as `FastAPI` instance) in files which are not `main.py`

In fact, we can have **only one** `FastAPI` instance, which is one run in `main.py` using `fastapi dev main.py`

In other locations, such as files in `routers` submodule, we will use a `APIRouter` instance and then we will plug the router in the main `FastAPI` instance.

For example, let's consider products controller:

```py
# routers/products.py

from fastapi import APIRouter, Depends
from typing import List, Optional
from pydantic import BaseModel


router = APIRouter(
    prefix="/products", # route prefix
    tags=["products"], # documentation tag
    responses={404: {"description": "Not found"}}, # message associate to 404 responses
)

class Product(BaseModel):
    """
    Model which represent a product
    """

    name: str
    price: float
    description: Optional[str] = None


DATABASE: List[Product] = [] # Mock database


@router.get("/")
async def read_all() -> List[Product]:
    return DATABASE


@router.post("/", status_code=201)
async def create(product: Product) -> Product:
    DATABASE.append(product)

    return product
```

```py
# main.py

from fastapi import FastAPI
from .routers import products, users


app = FastAPI()

app.include_router(products.router)
```


## Very big project

```
app/
├── main.py                # entrypoint FastAPI
├── core/                  # main configurations
│   ├── config.py          # settings (es. DB, env, ecc.)
│   ├── database.py        # DB connection
│   └── security.py        # auth, JWT, password hashing
│
├── common/                # shared components
│   ├── exceptions.py      # e.g., NotFound
│   ├── schemas.py         # DTO / base Pydantic
│   ├── repository.py      # BaseRepository (if needed)
│   └── utils.py           # helper
│
├── user/                  # feature "User"
│   ├── dto/...            # DTOs
│   ├── user_model.py      # ORM model
│   ├── user_repository.py # Data access
│   ├── user_service.py    # business logic
│   └── user_controller.py # router FastAPI
│
└── product/               # feature "Product"
    ├── dto/...            # DTOs
    ├── product_model.py      # ORM model
    ├── product_repository.py # Data access
    ├── product_service.py    # Business logic
    └── product_controller.py # router FastAPI
```

```
app/
├── main.py
│
├── api/                   # Presentation Layer (controllers / routers)
│   ├── v1/
│   │   ├── user_controller.py
│   │   ├── product_controller.py
│   │   └── ...
│   └── dependencies.py
│
├── services/              # Business Logic Layer
│   ├── user_service.py
│   ├── product_service.py
│   └── ...
│
├── repositories/          # Persistence Layer
│   ├── user_repository.py
│   ├── product_repository.py
│   └── ...
│
├── models/                # Domain Layer (entities / SQLAlchemy models)
│   ├── user_model.py
│   ├── product_model.py
│   └── ...
│
├── schemas/               # DTOs (Pydantic models)
│   ├── user_schemas.py
│   ├── product_schemas.py
│   └── ...
│
├── core/                  # Infrastructure
│   ├── config.py
│   ├── database.py
│   ├── security.py
│   └── logging.py
│
├── common/                # Shared utils / exceptions
│   ├── exceptions.py
│   ├── utils.py
│   └── constants.py
│
└── tests/
    ├── unit/
    ├── integration/
    └── e2e/

```

## References

https://fastapi.tiangolo.com/tutorial/bigger-applications/#an-example-file-structure






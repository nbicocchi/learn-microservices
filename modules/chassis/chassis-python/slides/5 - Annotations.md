# Annotations

In modern FastAPI applications, type **annotations** are not only useful for type checking but also for declaring request parameters, **validation rules**, and **metadata**.

Starting from FastAPI v0.95+, the recommended way to declare such parameters is by using `Annotated`, introduced in Python 3.9 (via `typing.Annotated`).

Annotated allows us to combine a **type hint** (e.g., `int`, `str`, `dict`) with **additional metadata** that frameworks like FastAPI can interpret.

In other words, it tells FastAPI what type the variable is, and how it should be treated.


## Validation

We can use `Annotated` to specify if a method parameter is a query (using `Query`) or path (using `Path`) parameter and its **constraints**.

For example, if you want to validate a query parameter `q`:

```py
from typing import Annotated

from fastapi import FastAPI, Query

app = FastAPI()


@app.get("/items/")
async def read_items(q: Annotated[str | None, Query(max_length=50)] = None):
    results = {"items": [{"item_id": "Foo"}, {"item_id": "Bar"}]}
    if q:
        results.update({"q": q})
    return results
```

Some of the most commonly used parameters include:

- `default`: Defines the default value if the client does not provide the parameter. Use None for optional parameters.
- `min_length` / `max_length`: Enforce length restrictions for strings.
- `ge` / `le` / `gt` / `lt`: Enforce numeric constraints
- `description`: Adds documentation that appears automatically in the interactive API docs.


## Depends

When building APIs, it’s common to need reusable logic across multiple endpoints: authentication, database sessions, pagination parameters, etc. FastAPI provides a powerful system called ***dependency injection***, and the key tool is `Depends`.

When we specify `Depends`, we tell FastAPI that a parameter’s value should **come from another function**, not directly from the request.

For example:

```py
from typing import Annotated
from fastapi import FastAPI, Depends

app = FastAPI()

def common_parameters(q: str | None = None, skip: int = 0, limit: int = 100):
    return {"q": q, "skip": skip, "limit": limit}

@app.get("/items/")
def read_items(commons: Annotated[dict, Depends(common_parameters)]):
    return commons
```

The endpoint `read_items` receives `commons` as the dictionary returned by `common_parameters` function.

A common use case for `Depends` is **Authentication&Authorization** and **Database Session Management**

```py
# Authentication&Authorization

def get_current_user(token: str):
    if token != "secret":
        raise HTTPException(status_code=401, detail="Unauthorized")
    return {"username": "admin"}

@app.get("/profile/")
def read_profile(user: Annotated[dict, Depends(get_current_user)]):
    return user
```

```py
# Database Session Management

def get_db():
    db = create_session()
    try:
        yield db
    finally:
        db.close()

@app.get("/products/")
def get_products(db: Annotated[Session, Depends(get_db)]):
    return db.query(Product).all()
```


## References

https://fastapi.tiangolo.com/tutorial/query-params-str-validations/

https://fastapi.tiangolo.com/tutorial/path-params-numeric-validations/

https://fastapi.tiangolo.com/reference/dependencies/?h=depen#fastapi.Depends

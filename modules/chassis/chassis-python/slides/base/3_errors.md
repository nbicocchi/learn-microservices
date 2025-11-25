# Errors

When building web applications, it is crucial to handle errors in a way that provides useful feedback to the client without exposing unnecessary internal details. FastAPI provides a simple and powerful way to manage errors through exceptions.

The most common tool is the `HTTPException` class, which allows us to return HTTP error responses with custom status codes and messages.

In order to raise an HTTP exception you must simple `raise HTTPException(...)` providing at least `status_code` and `detail`:

```py
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
    
    if product_id >= len(products):     # check and return 404 Not Found
        raise HTTPException(status_code=404, detail="Product not found")

    return products[product_id]
```

When a client requests `/products/3`, FastAPI automatically generates a response like this:

```
{
  "detail": "Product not found"
}
```

You can also create custom exception handlers. For example:

```py
from fastapi.responses import JSONResponse
from fastapi.requests import Request

@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": f"Oops! {exc.detail}"}
    )
```

## References

https://fastapi.tiangolo.com/tutorial/handling-errors/?h=err















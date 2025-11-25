from fastapi import FastAPI, HTTPException, Response
from pydantic import BaseModel
from typing import Optional, List


class Product(BaseModel):
    """
    Model which represent a product
    """

    name: str
    price: float
    description: Optional[str] = None


app = FastAPI()     # FastAPI instance


@app.post("/products/")
async def create_product(product: Product) -> Product:
    """
    Should create a product based on input, returns created product
    """

    return product

@app.get("/products/")
async def read_products() -> list[Product]:
    """
    Return list of available products
    """

    return [
        Product(
            name="Keyboard",
            price=42.0,
        ),
        Product(
            name="Mouse",
            price=32.0,
            description="It is not the animal!"
        ),
    ]

@app.get("/products/{product_id}", status_code=200)
async def read_product(product_id: int) -> Product:
    
    products = [
        Product(
            name="Keyboard",
            price=42.0,
        ),
        Product(
            name="Mouse",
            price=32.0,
            description="It is not the animal!"
        ),
    ]
    
    if product_id >= len(products):
        raise HTTPException(status_code=404, detail="Product not found")

    return products[product_id]



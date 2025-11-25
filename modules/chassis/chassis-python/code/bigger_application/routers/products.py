from fastapi import APIRouter, Depends
from typing import List, Optional

from pydantic import BaseModel


router = APIRouter(
    prefix="/products",
    tags=["products"],
    responses={404: {"description": "Not found"}},
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
    
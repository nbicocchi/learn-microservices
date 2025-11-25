from fastapi import APIRouter, Depends
from typing import List, Optional

from pydantic import BaseModel, EmailStr


router = APIRouter(
    prefix="/users",
    tags=["users"],
    responses={404: {"description": "Not found"}},
)

class User(BaseModel):
    """
    Model which represent an user
    """

    email: EmailStr


DATABASE: List[User] = [] # Mock database


@router.get("/")
async def read_all() -> List[User]:
    return DATABASE


@router.post("/", status_code=201)
async def create(product: User) -> User:
    DATABASE.append(product)

    return product
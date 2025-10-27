from pydantic import BaseModel
from uuid import UUID
from typing import Optional

class Product(BaseModel):
    id: int
    uuid: UUID
    name: str
    weight: float

    class Config:
        orm_mode = True
        
class ProductCreate(BaseModel):
    name: str
    weight: float
    uuid: Optional[str] = None  # Optional UUID for testing purposes
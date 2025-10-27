import strawberry
from uuid import UUID

@strawberry.type
class Product:
    id: int
    uuid: UUID
    name: str
    weight: float
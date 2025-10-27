from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Float
from uuid import uuid4

Base = declarative_base()

class ProductORM(Base):
    __tablename__ = "products"

    id = Column(Integer, primary_key=True, autoincrement=True)
    uuid = Column(String, default=lambda: str(uuid4()), nullable=False, unique=True)
    name = Column(String)
    weight = Column(Float)

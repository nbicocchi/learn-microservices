from product_schema import ProductORM, Base
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool
from typing import List, Optional
from uuid import uuid4

class ProductRepository:
    
    def __init__(self):
        self.engine = create_engine("sqlite://", connect_args={"check_same_thread": False}, echo=True, poolclass=StaticPool)
        self.Session = sessionmaker(bind=self.engine)
        Base.metadata.create_all(self.engine)
        self.seed_db()

    def seed_db(self):
        session = self.Session()
        products = [
            ProductORM(uuid="171f5df0-b213-4a40-8ae6-fe82239ab660", name="Laptop", weight=2.2),
            ProductORM(uuid="f89b6577-3705-414f-8b01-41c091abb5e0", name="Bike", weight=5.5),
            ProductORM(uuid="b1f4748a-f3cd-4fc3-be58-38316afe1574", name="Shirt", weight=0.2)
        ]
        session.add_all(products)
        session.commit()
        session.close()

    def get_all_products(self) -> List[ProductORM]:
        with self.Session() as session:
            return session.query(ProductORM).all()

    def get_product_by_uuid(self, uuid: str) -> Optional[ProductORM]:
        with self.Session() as session:
            return session.query(ProductORM).filter(ProductORM.uuid == uuid).first()

    def add_product(self, name: str, weight: float, uuid: Optional[str] = None):
        # Use provided UUID if available, otherwise generate one
        product_uuid = uuid if uuid else str(uuid4())
        new_product = ProductORM(uuid=product_uuid, name=name, weight=weight)
        with self.Session() as session:
            session.add(new_product)
            session.commit()
            session.refresh(new_product)
            return new_product

    def delete_product(self, uuid: str) -> bool:
        with self.Session() as session:
            product = session.query(ProductORM).filter(ProductORM.uuid == uuid).first()
            if product:
                session.delete(product)
                session.commit()
                return True
            return False

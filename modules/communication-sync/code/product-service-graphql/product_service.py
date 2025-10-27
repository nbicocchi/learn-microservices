from product_repository import ProductRepository
from typing import Optional

class ProductService:
    def __init__(self):
        self.product_repository = ProductRepository()

    def get_all_products(self):
        return self.product_repository.get_all_products()

    def get_product_by_uuid(self, uuid: str):
        return self.product_repository.get_product_by_uuid(uuid)

    def add_product(self, name: str, weight: float, uuid: Optional[str] = None):
        return self.product_repository.add_product(name, weight, uuid)

    def delete_product(self, uuid: str):
        return self.product_repository.delete_product(uuid)


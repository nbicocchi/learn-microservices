from product_repository import ProductRepository


class ProductService:

    def __init__(self):
        self.product_repository = ProductRepository()
    
    def get_all_products(self):
        return self.product_repository.get_all_products()

    def get_product_by_uuid(self, uuid: str):
        product = self.product_repository.get_product_by_uuid(uuid)
        if not product:
            return None
        return product

    def add_product(self, product_data):
        return self.product_repository.add_product(product_data)

    def delete_product(self, uuid: str):
        success = self.product_repository.delete_product(uuid)
        if not success:
            return None
        return {"message": "Product deleted successfully"}

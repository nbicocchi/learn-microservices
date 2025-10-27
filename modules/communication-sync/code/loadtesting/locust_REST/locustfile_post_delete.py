from locust import HttpUser, task, between
import uuid

class ProductUser(HttpUser):
    @task
    def create_and_delete_product(self):
        product_uuid = str(uuid.uuid4())
        product_data = {
            "uuid": product_uuid,
            "name": "Kiwi",
            "weight": 12.5
        }

        # Create product (grouped as POST)
        create_response = self.client.post("/products", json=product_data, name="/products (POST)")

        # Delete product (grouped as DELETE)
        if create_response.status_code == 200:
            self.client.delete(f"/products/{product_uuid}", name="/products/{uuid} (DELETE)")

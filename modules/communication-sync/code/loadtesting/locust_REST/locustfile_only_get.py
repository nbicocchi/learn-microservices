from locust import HttpUser, task, between

class ProductUser(HttpUser):
    @task
    def get_all_products(self):
        self.client.get("/products")

    @task
    def get_single_product(self):
        self.client.get("/products/171f5df0-b213-4a40-8ae6-fe82239ab660")
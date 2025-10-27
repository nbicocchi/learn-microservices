from locust import HttpUser, task
import json


class GraphQLUser(HttpUser):

    @task
    def get_all_products(self):
        query = """
        query {
            getAllProducts {
                id
                uuid
                name
                weight
            }
        }
        """
        self.client.post(
            "/graphql",
            headers={"Content-Type": "application/json"},
            data=json.dumps({"query": query}),
        )

    @task
    def get_product_by_uuid(self):
        query = f"""
        query {{
            getProductByUuid(uuid: "171f5df0-b213-4a40-8ae6-fe82239ab660") {{
                id
                uuid
                name
                weight
            }}
        }}
        """
        self.client.post(
            "/graphql",
            headers={"Content-Type": "application/json"},
            data=json.dumps({"query": query}),
        )
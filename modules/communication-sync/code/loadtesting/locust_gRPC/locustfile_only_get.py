import grpc
import time
from locust import User, task, events
from google.protobuf.empty_pb2 import Empty

import product_pb2, product_pb2_grpc


class GRPCClient:
    def __init__(self, host="localhost:8004"):
        self.channel = grpc.insecure_channel(host)
        self.stub = product_pb2_grpc.ProductServiceStub(self.channel)

    def get_all_products(self):
        return self.stub.GetAllProducts(Empty())

    def get_product_by_uuid(self, uuid):
        request = product_pb2.ProductUUID(uuid=uuid)
        return self.stub.GetProductByUUID(request)


class GRPCUser(User):

    def on_start(self):
        self.client = GRPCClient()
        try:
            response = self.client.get_all_products()
            self.uuids = [product.uuid for product in response.products]
        except Exception as e:
            print(f"Error during startup UUID fetch: {e}")
            self.uuids = []

    def record_request(self, name, func, *args, **kwargs):
        start_time = time.time()
        try:
            result = func(*args, **kwargs)
            total_time = (time.time() - start_time) * 1000
            events.request.fire(
                request_type="gRPC",
                name=name,
                response_time=total_time,
                response_length=len(result.SerializeToString()) if result else 0
            )
        except Exception as e:
            total_time = (time.time() - start_time) * 1000
            events.request.fire(
                request_type="gRPC",
                name=name,
                response_time=total_time,
                exception=e
            )

    @task
    def task_get_all(self):
        self.record_request("GetAllProducts", self.client.get_all_products)

    @task
    def task_get_by_uuid(self):
        uuid = "171f5df0-b213-4a40-8ae6-fe82239ab660"
        self.record_request("GetProductByUUID", self.client.get_product_by_uuid, uuid)
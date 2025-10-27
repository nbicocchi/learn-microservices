import grpc
import time
import product_pb2
import product_pb2_grpc
from locust import User, task, events
from uuid import uuid4

class GRPCClient:
    def __init__(self):
        self.channel = grpc.insecure_channel("localhost:8004")
        self.stub = product_pb2_grpc.ProductServiceStub(self.channel)

    def add_product(self, uuid, name, weight):
        return self.stub.AddProduct(
            product_pb2.ProductRequest(name=name, weight=weight, uuid=uuid) 
        )

    def delete_product(self, uuid):
        return self.stub.DeleteProduct(product_pb2.ProductUUID(uuid=uuid))

class GRPCUser(User):

    def on_start(self):
        self.client = GRPCClient()

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
    def add_and_delete(self):
        uuid = str(uuid4())
        self.record_request("AddProduct", self.client.add_product, uuid, "Kiwi", 12.5)
        self.record_request("DeleteProduct", self.client.delete_product, uuid)

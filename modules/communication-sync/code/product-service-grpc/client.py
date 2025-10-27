import grpc
from proto import product_pb2, product_pb2_grpc

def run():
    channel = grpc.insecure_channel('localhost:8004') # change to localhost:50051 if running locally
    stub = product_pb2_grpc.ProductServiceStub(channel)

    response = stub.AddProduct(product_pb2.ProductRequest(name="Kiwi", weight=1.5))
    print("Added Product:", response)

    all_products = stub.GetAllProducts(product_pb2.google_dot_protobuf_dot_empty__pb2.Empty())
    print("All Products:", all_products)

    product_by_uuid = stub.GetProductByUUID(product_pb2.ProductUUID(uuid=response.uuid))
    print("Product by UUID:", product_by_uuid)

    delete_response = stub.DeleteProduct(product_pb2.ProductUUID(uuid=response.uuid))
    print("Delete Result:", delete_response.success)

if __name__ == '__main__':
    run()

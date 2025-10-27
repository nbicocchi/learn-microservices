from fastapi import FastAPI, HTTPException
from typing import List
from uuid import UUID
from product_service import ProductService
from product_model import Product, ProductCreate
import uvicorn

app = FastAPI()

@app.get("/products", response_model=List[Product])
def get_all_products():
    return product_service.get_all_products()

@app.get("/products/{uuid}", response_model=Product)
def get_product_by_uuid(uuid: UUID):
    product = product_service.get_product_by_uuid(str(uuid))
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return product

@app.post("/products", response_model=Product)
def add_product(product_data: ProductCreate):
    return product_service.add_product(product_data)

@app.delete("/products/{uuid}", response_model=dict)
def delete_product(uuid: UUID):
    response = product_service.delete_product(str(uuid))
    if not response:
        raise HTTPException(status_code=404, detail="Product not found")
    return response

if __name__ == "__main__":
    product_service = ProductService()
    uvicorn.run(app, host="0.0.0.0", port=8000)
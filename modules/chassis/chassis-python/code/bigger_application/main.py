from fastapi import FastAPI
from .routers import products, users


app = FastAPI()

app.include_router(products.router)
app.include_router(users.router)



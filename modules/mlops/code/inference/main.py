import os

import mlflow
import pandas as pd
import logging
import numpy as np

from fastapi import FastAPI, Request, HTTPException
from pydantic import BaseModel
from datetime import datetime
from datetime import date
from typing import Literal

class HealthCheck(BaseModel):
    status: str

class SpaceflightInput(BaseModel):
    engines: float
    passenger_capacity: int
    crew: float
    d_check_complete: bool
    moon_clearance_complete: bool
    iata_approved: bool
    company_rating: float
    review_scores_rating: float

class ModelURI(BaseModel):
    uri: str # format: runs:/runid/name

# Logging
LOG = logging.getLogger('uvicorn.info')

app = FastAPI()

# Set our tracking server uri for logging
mlflow_host = os.getenv("MLFLOW_MODEL_REGISTRY_HOST", "127.0.0.1")
mlflow_port = os.getenv("MLFLOW_MODEL_REGISTRY_PORT", "5000")
mlflow_uri = "http://" + mlflow_host + ":" + mlflow_port
LOG.info("Connecting to MLFlow at (%s)", mlflow_uri)
mlflow.set_tracking_uri(uri=mlflow_uri)

@app.get("/health")
def get_health():
    return HealthCheck(status='OK')

@app.post("/model")
def model(data: ModelURI, request: Request):
    try:
        request.app.state.loaded_model = mlflow.pyfunc.load_model(data.uri)
        return {"model": "loaded"}
    except:
        raise HTTPException(status_code=500, detail="No model is loaded.")

@app.post("/predict")
def predict(data: SpaceflightInput, request: Request):
    model = request.app.state.loaded_model
    if not model:
        raise HTTPException(status_code=500, detail="No model is loaded.")

    # Convert to DataFrame with a single row
    df = pd.DataFrame([data.dict()])

    # Make prediction
    try:
        prediction = model.predict(df)
        return {"prediction": prediction.tolist()}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")

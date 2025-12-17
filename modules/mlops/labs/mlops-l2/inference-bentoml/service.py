import bentoml
import mlflow
import os
import pandas as pd
import logging
from pydantic import BaseModel, ValidationError
from datetime import datetime
from typing import Optional

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

ARTIFACT_MODEL_NAME = "model"
MLFLOW_TRACKING_URI = os.environ.get("MLFLOW_TRACKING_URI", "http://mlflow:5000")

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
    model_name: str
    model_version: int


@bentoml.service()
class SpaceflightService:
    def __init__(self):
        self.bento_model = None
        mlflow.set_tracking_uri(MLFLOW_TRACKING_URI)

    @bentoml.api(route="/import_model")
    async def import_model(self, model_uri: ModelURI):
        try:
            await self._import_load_model(model_uri)
            return {"message": f"Model {model_uri.model_name}/{model_uri.model_version} is being imported and loaded."}
        except Exception as e:
            logger.error(f"Error importing model: {e}")
            return {"error": str(e)}

    async def _import_load_model(self, model_uri: ModelURI):
        imported_model = bentoml.mlflow.import_model(
            name=f"{model_uri.model_name}",
            model_uri=f"models:/{model_uri.model_name}/{model_uri.model_version}"
        )
        self.bento_model = bentoml.mlflow.load_model(imported_model)

    @bentoml.api(route="/predict")
    def predict(self, input_data: SpaceflightInput):
        try:
            if self.bento_model is None:
                raise ValueError("No model is loaded. Please load a model before making predictions.")

            df = pd.DataFrame([input_data.dict()])
            result = self.bento_model.predict(df)
            return {"prediction": result.tolist()}
        except ValidationError as ve:
            logger.error(f"Validation error: {ve}")
            return {"error": "Invalid input data"}
        except Exception as e:
            logger.error(f"Error during prediction: {e}")
            return {"error": str(e)}

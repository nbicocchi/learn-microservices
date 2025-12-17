from fastapi import FastAPI, HTTPException
from datetime import datetime
import pandas as pd
import numpy as np
import requests
import os
import logging
from pydantic import BaseModel

from utils.minIO_comunication import upload_to_minio
from utils.dataset_creation import model_predictions, generate_stable, generate_drift
from utils.evidently_integration import project_setup, data_drift_check, model_performance_check

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger(__name__)

BENTO_URL = os.getenv("BENTO_URL", "http://host.docker.internal:3000/predict")
KEDRO_API_URL = os.getenv("KEDRO_API_URL", "http://host.docker.internal:8005/run-pipeline")

REFERENCE_PATH = "/app/data/reference.csv"
REFERENCE_TARGET_PATH = "/app/data/reference_target.csv"
CURRENT_PATH = "/app/data/current.csv"
CURRENT_TARGET_PATH = "/app/data/current_target.csv"

MINIO_REFERENCE_OBJ = "reference_target"
MINIO_CURRENT_OBJ = "current_target"


np.random.seed(42)

app = FastAPI()

class CurrentRequest(BaseModel):
    drift: bool = False

def evidently_warnings(data_failed):
    logger.warning(f"{len(data_failed)} failed tests detected:")
    for t in data_failed:
        logger.warning(f"ID: {t.id}, \n"
                       f"Name: {t.name}, \n"
                       f"Column: {t.metric_config.params.get('column')},\n"
                       f"Metric type: {t.metric_config.params.get('type')},\n"
                       f"Threshold (drift/fixed test): {t.metric_config.params.get('drift_share') or t.test_config.get('threshold')}\n"
                       f"Critical test? {t.test_config.get('is_critical')}\n\n")

def run_generation_pipeline(
    *,
    df: pd.DataFrame,
    local_output_path: str,
    minio_object_name: str,
):
    """
    Common pipeline for running model predictions, saving CSV, and uploading to MinIO.
    """
    logger.info("Running model predictions...")
    df_pred = model_predictions(df, BENTO_URL)

    # --- Save CSV locally ---
    df_pred.to_csv(local_output_path, index=False)
    logger.info(f"Saved dataset to {local_output_path}")

    # --- Upload to MinIO ---
    upload_to_minio(df_pred, minio_object_name)
    logger.info(f"Uploaded dataset to MinIO as '{minio_object_name}'")

    return df_pred


@app.get("/reference")
def reference():
    logger.info(f"Loading reference dataset: {REFERENCE_PATH}")
    df_ref = pd.read_csv(REFERENCE_PATH)

    run_generation_pipeline(
        df=df_ref,
        local_output_path=REFERENCE_TARGET_PATH,
        minio_object_name=MINIO_REFERENCE_OBJ,
    )

    return {
        "status": "OK",
        "message": "Reference dataset uploaded to MinIO",
        "file_uploaded": MINIO_REFERENCE_OBJ,
    }


@app.post("/current")
def generate(req: CurrentRequest):
    logger.info(f"Starting dataset generation (drift={req.drift})")

    logger.info("Loading base dataset")
    df_ref = pd.read_csv(REFERENCE_PATH)

    # --- Generate stable or drifted dataset ---
    logger.info("Applying drift" if req.drift else "Generating stable dataset")
    df_curr = generate_drift(df_ref) if req.drift else generate_stable(df_ref)

    # --- Save locally before predictions ---
    df_curr.to_csv(CURRENT_PATH, index=False)
    logger.info(f"Saved generated dataset to {CURRENT_PATH}")

    # --- Prepare MinIO object name with timestamp ---
    minio_object_name = f"{MINIO_CURRENT_OBJ}_{datetime.now().strftime('%Y%m%d_%H%M%S')}"

    # --- Run predictions and upload ---
    run_generation_pipeline(
        df=df_curr,
        local_output_path=CURRENT_TARGET_PATH,
        minio_object_name=minio_object_name,
    )

    return {
        "status": "OK",
        "message": "Current dataset uploaded to MinIO",
        "file_uploaded": minio_object_name,
    }



@app.get("/analyze")
def analyze():
    logger.info("Starting Evidently analysis...")
    project = project_setup()

    # --- Data drift and model performance checks ---
    logger.info("Checking data drift...")
    failed_data_tests = data_drift_check(REFERENCE_PATH, CURRENT_PATH, project)
    if failed_data_tests:
        evidently_warnings(failed_data_tests)

    logger.info("Checking model performance...")
    failed_model_tests = model_performance_check(REFERENCE_TARGET_PATH, CURRENT_TARGET_PATH, project)
    if failed_model_tests:
        evidently_warnings(failed_model_tests)

    # --- Trigger Kedro pipeline if any test failed ---
    if failed_data_tests or failed_model_tests:
        logger.info("Drift detected — trigger Kedro pipeline")

    return {
        "status": "OK",
        "message": "Evidently analysis completed",
        "failed_data_tests": len(failed_data_tests),
        "failed_model_tests": len(failed_model_tests),
    }

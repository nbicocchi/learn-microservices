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

MINIO_REFERENCE_OBJ = "reference_table_and_target"

BENTO_URL = os.getenv("BENTO_URL", "http://bentoml:3000/predict")
KEDRO_API_URL = os.getenv("KEDRO_API_URL", "http://host.docker.internal:8005/run-pipeline")

ORIGINAL_DIR = os.getenv("REFERENCE_PATH", "/app/data")
REFERENCE_PATH = "/app/data/reference/reference_table_and_target.csv"
CURRENT_PATH = "/app/data/current/dataset_curr.csv"
CURRENT_TARGET_PATH = "/app/data/current/dataset_curr_and_target.csv"

np.random.seed(42)

app = FastAPI()


class GenerateRequest(BaseModel):
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


def run_kedro_pipeline():
    try:
        result = requests.post(KEDRO_API_URL)
        logger.info(f"Kedro pipeline triggered successfully: {result.json()}")
    except requests.RequestException as e:
        logger.error(f"Failed to trigger Kedro pipeline: {e}")


def get_dataset_path():
    if not os.path.isdir(ORIGINAL_DIR):
        raise HTTPException(status_code=500, detail=f"The directory {ORIGINAL_DIR} does not exist!")

    csv_files = [f for f in os.listdir(ORIGINAL_DIR) if f.lower().endswith(".csv")]

    if not csv_files:
        raise HTTPException(status_code=500, detail="No CSV files found")
    elif len(csv_files) > 1:
        logger.info(f"Multiple CSV files found in {ORIGINAL_DIR}, using the first one: {csv_files[0]}")

    logger.info(f"Using dataset file: {csv_files}")
    return os.path.join(ORIGINAL_DIR, csv_files[0])


@app.get("/reference")
def reference():
    # --- Load local dataset ---
    dataset_path = get_dataset_path()
    logger.info(f"Loading reference dataset: {dataset_path}")
    df_ref = pd.read_csv(dataset_path)

    # --- Make predictions with the model ---
    logger.info("Running model predictions...")
    df_pred = model_predictions(df_ref, BENTO_URL)

    # --- Save reference dataset locally ---
    df_pred.to_csv(REFERENCE_PATH, index=False)
    logger.info(f"Saved generated dataset to {REFERENCE_PATH}")

    # --- Upload to MinIO ---
    upload_to_minio(df_pred, MINIO_REFERENCE_OBJ)
    logger.info(f"Uploaded reference predictions to MinIO as '{MINIO_REFERENCE_OBJ}'")

    return {
        "status": "OK",
        "message": "Predictions loaded on MinIO",
        "file_uploaded": dataset_path
    }


@app.post("/generate")
def generate(req: GenerateRequest):
    logger.info(f"Starting dataset generation (drift={req.drift})")

    # --- Load local dataset ---
    dataset_path = get_dataset_path()
    logger.info(f"Base dataset loaded")
    df_ref = pd.read_csv(dataset_path)

    # -- Generate current dataset ---
    logger.info("Applying drift" if req.drift else "Generating stable dataset")
    df_curr = generate_drift(df_ref) if req.drift else generate_stable(df_ref)

    # --- Save current dataset locally ---
    df_curr.to_csv(CURRENT_PATH, index=False)
    logger.info(f"Saved generated dataset to {CURRENT_PATH}")

    # --- Make predictions with the model ---
    df_pred = model_predictions(df_curr, BENTO_URL)
    logger.info("Running model predictions on generated dataset")
    df_pred.to_csv(CURRENT_TARGET_PATH, index=False)

    # --- Upload to MinIO ---
    upload_to_minio(df_pred, f"current_table_and_target_{datetime.now().strftime('%Y%m%d_%H%M%S')}")
    logger.info(f"Uploaded generated dataset to MinIO")

    return {
        "status": "OK",
        "message": "Current dataset generated and uploaded to MinIO",
        "drift": req.drift
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
    failed_model_tests = model_performance_check(REFERENCE_PATH, CURRENT_TARGET_PATH, project)
    if failed_model_tests:
        evidently_warnings(failed_model_tests)

    # --- Trigger Kedro pipeline if any test failed ---
    if failed_data_tests or failed_model_tests:
        logger.info("Drift detected — triggering Kedro pipeline")
        run_kedro_pipeline()

    logger.info("Evidently analysis completed")

    return {
        "status": "OK",
        "message": "Evidently analysis completed",
        "failed_data_tests": len(failed_data_tests),
        "failed_model_tests": len(failed_model_tests),
        "kedro_pipeline_triggered": bool(failed_data_tests or failed_model_tests)
    }

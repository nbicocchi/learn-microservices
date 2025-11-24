"""
This script uploads a reference dataset to a MinIO bucket and uses a BentoML model
to generate predictions on the dataset. The resulting dataset with predictions is then uploaded back to MinIO.
"""

import pandas as pd
import requests
import os
import logging

from pydantic import BaseModel

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger(__name__)

logging.getLogger("botocore").setLevel(logging.WARNING)
logging.getLogger("aiobotocore").setLevel(logging.WARNING)
logging.getLogger("s3transfer").setLevel(logging.WARNING)
logging.getLogger("urllib3").setLevel(logging.WARNING)
logging.getLogger("asyncio").setLevel(logging.WARNING)

MINIO_ROOT_USER = os.getenv("MINIO_ROOT_USER", "minioadmin")
MINIO_ROOT_PASSWORD = os.getenv("MINIO_ROOT_PASSWORD", "minioadmin")
MINIO_URL = os.getenv("MINIO_URL", "http://minio:9000")
BENTO_URL = os.getenv("BENTO_URL", "http://inference:3000/predict")

class SpaceflightInput(BaseModel):
    engines: float
    passenger_capacity: int
    crew: float
    d_check_complete: bool
    moon_clearance_complete: bool
    iata_approved: bool
    company_rating: float
    review_scores_rating: float

def upload_to_minio(df, object_name):
    url = f"s3://datasets/{object_name}.csv"

    logger.debug(f"Uploading to MinIO at: {url}")

    df.to_csv(
        url,
        index=False,
        storage_options={
            "key": MINIO_ROOT_USER,
            "secret": MINIO_ROOT_PASSWORD,
            "client_kwargs": {"endpoint_url": MINIO_URL}
        }
    )

def use_model(df):
    logger.info(f"Making predictions with model at: {BENTO_URL}...")

    required_columns = [
        "engines",
        "passenger_capacity",
        "crew",
        "d_check_complete",
        "moon_clearance_complete",
        "iata_approved",
        "company_rating",
        "review_scores_rating"
    ]
    input_df = df[required_columns].copy()

    predictions = []

    for _, row in input_df.iterrows():
        spaceflight_input = SpaceflightInput(**row.to_dict())
        logger.info(spaceflight_input)
        payload = {"input_data": spaceflight_input.dict()}
        response = requests.post(BENTO_URL, json=payload)

        if response.status_code == 200:
            result = response.json()
            predictions.append(result["prediction"][0])
        else:
            logger.error(f"Error {_}: {response.text}")
            predictions.append(None)

    input_df["prediction"] = predictions
    input_df["price"] = df["price"]

    logger.debug("Predictions processed and added to dataframe.")
    return input_df


if __name__ == "__main__":
    reference_path = os.getenv("REFERENCE_PATH", "/app/data")
    logger.info(f"Reference path inside container: {reference_path}")

    if not os.path.isdir(reference_path):
        logger.warning(f"The directory {reference_path} does not exist!")
        exit(1)

    csv_files = [f for f in os.listdir(reference_path) if f.lower().endswith(".csv")]
    if not csv_files:
        logger.error(f"No CSV files found in {reference_path}")
        exit(1)
    elif len(csv_files) > 1:
        logger.info(f"Multiple CSV files found in {reference_path}, using the first one: {csv_files[0]}")

    csv_path = os.path.join(reference_path, csv_files[0])
    logger.info(f"Loading csv file: {csv_path}...")

    df_ref = pd.read_csv(csv_path)
    df_pred = use_model(df_ref)

    object_name = "reference_table_and_target"
    upload_to_minio(df_pred, object_name)

    logger.info(f"Upload {object_name} on MinIO completed successfully.")

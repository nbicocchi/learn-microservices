"""
This is a simulation script for a production environment that performs the following tasks:
1. Downloads a reference dataset from a MinIO object storage.
2. Simulates data drift by applying random changes to the reference dataset.
3. Uses a deployed machine learning model to make predictions on the drifted dataset.
4. Uploads the predictions along with the drifted dataset back to MinIO for monitoring purposes.

It assumes the presence of a BentoML service endpoint for predictions and MinIO for data storage.
"""

import datetime
import pandas as pd
import numpy as np
import requests
import os
import logging

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger(__name__)

logging.getLogger("botocore").setLevel(logging.WARNING)
logging.getLogger("boto3").setLevel(logging.WARNING)
logging.getLogger("aiobotocore").setLevel(logging.WARNING)

MINIO_ROOT_USER = os.getenv("MINIO_ROOT_USER", "minioadmin")
MINIO_ROOT_PASSWORD = os.getenv("MINIO_ROOT_PASSWORD", "minioadmin")
MINIO_URL = os.getenv("MINIO_URL", "http://minio:9000")

BENTO_URL = os.getenv("BENTO_URL", "http://bentoml:3000/predict")

np.random.seed(42)


def download_from_minio(object_name):
    url = f"s3://datasets/{object_name}.csv"
    logger.debug(f"Download from MinIO at: {url}")

    df = pd.read_csv(
        url,
        storage_options={
            "key": MINIO_ROOT_USER,
            "secret": MINIO_ROOT_PASSWORD,
            "client_kwargs": {"endpoint_url": MINIO_URL}
        }
    )
    return df


def upload_to_minio(df, object_name):
    url = f"s3://datasets/current/{object_name}.csv"
    logger.info(f"Uploading to MinIO at: {url}")

    df.to_csv(
        url,
        index=False,
        storage_options={
            "key": MINIO_ROOT_USER,
            "secret": MINIO_ROOT_PASSWORD,
            "client_kwargs": {"endpoint_url": MINIO_URL}
        }
    )


def dataset_stable(df):
    """
    Simulate stable production data (minimal drift).
    """
    logger.info("Simulating stable dataset (no drift)...")
    df_curr = df.sample(n=1000).copy()

    # Stable data: minimal changes
    df_curr["engines"] = (df_curr["engines"] + np.random.choice([1, 0], size=len(df_curr), p=[0.02, 0.98])).clip(lower=0)
    df_curr["passenger_capacity"] = df_curr["passenger_capacity"] + np.random.choice([-1, 0], size=len(df_curr), p=[0.05, 0.95])
    df_curr["crew"] = df_curr["crew"] + np.random.choice([-1, 0], size=len(df_curr), p=[0.02, 0.98])
    df_curr["company_rating"] = (df_curr["company_rating"] + np.random.normal(0, 0.01, size=len(df_curr))).clip(0, 1)
    df_curr["price"] = (df_curr["price"] * np.random.uniform(0.99, 1.01, size=len(df_curr))).clip(lower=1)
    df_curr["d_check_complete"] = df_curr["d_check_complete"]
    df_curr["moon_clearance_complete"] = df_curr["moon_clearance_complete"]
    df_curr["iata_approved"] = df_curr["iata_approved"]

    logger.debug("Stable dataset generated.")
    return df_curr


def dataset_drifting(df):
    """
    Simulate strong production data drift by applying larger random changes.
    """
    logger.info("Simulating data drift (strong)...")
    df_curr = df.sample(n=1000).copy()

    df_curr["engines"] = (df_curr["engines"] + np.random.randint(-1, 1, size=len(df_curr))).clip(lower=0)
    df_curr["passenger_capacity"] = (df_curr["passenger_capacity"] + np.random.randint(-1, 2, size=len(df_curr))).clip(
        lower=1)
    df_curr["crew"] = (df_curr["crew"] + np.random.randint(-1, 1, size=len(df_curr))).clip(lower=1)
    df_curr["d_check_complete"] = df_curr["d_check_complete"]
    df_curr["moon_clearance_complete"] = np.random.choice([False, True], size=len(df_curr), p=[0.45, 0.55])
    df_curr["iata_approved"] = np.random.choice([False, True], size=len(df_curr), p=[0.45, 0.55])
    df_curr["company_rating"] = (df_curr["company_rating"] + np.random.normal(0, 0.05, size=len(df_curr))).clip(0, 1)
    df_curr["review_scores_rating"] = (
                df_curr["review_scores_rating"] + np.random.choice([-1, 0, 1], size=len(df_curr))).clip(0, 100)
    df_curr["price"] = (df_curr["price"] * np.random.uniform(0.9, 1.1, size=len(df_curr))).clip(lower=1)

    logger.debug("Strong drift simulation complete.")
    return df_curr


def use_model(df):
    """
    Simulate using the deployed model by sending each row to the prediction endpoint.
    """
    logger.debug(f"Using model at: {BENTO_URL}")

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
        payload = {"input_dict": row.to_dict()}
        response = requests.post(BENTO_URL, json=payload)

        if response.status_code == 200:
            result = response.json()
            predictions.append(result["prediction"][0])
        else:
            logger.error(f"Error {_}: {response.text}")
            predictions.append(None)

    input_df["prediction"] = predictions
    input_df["price"] = df["price"]

    logger.info("Predictions processed and added to dataframe.")
    return input_df


if __name__ == "__main__":
    scenario = os.getenv("SCENARIO", "stable")
    logger.info(f"Starting production simulation with scenario {scenario}...")

    df_ref = download_from_minio("reference_table_and_target")
    df_ref.to_csv("./data/reference_table_and_target.csv", index=False)

    if scenario == "stable":
        df_curr = dataset_stable(df_ref)
    else:
        df_curr = dataset_drifting(df_ref)
    df_curr.to_csv("./data/dataset_curr.csv", index=False)

    df_pred = use_model(df_curr)
    df_pred.to_csv("./data/dataset_curr_and_target.csv", index=False)

    upload_to_minio(df_pred, f"current_table_and_target_{datetime.datetime.now().strftime('%Y%m%d_%H%M%S')}")

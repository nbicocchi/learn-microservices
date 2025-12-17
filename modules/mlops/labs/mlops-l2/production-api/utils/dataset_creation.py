import numpy as np
import requests

from pydantic import BaseModel


class SpaceflightInput(BaseModel):
    engines: float
    passenger_capacity: int
    crew: float
    d_check_complete: bool
    moon_clearance_complete: bool
    iata_approved: bool
    company_rating: float
    review_scores_rating: float


def generate_stable(df):
    """
    Simulate stable production data (minimal drift).
    """
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

    return df_curr


def generate_drift(df):
    """
    Simulate strong production data drift by applying larger random changes.
    """
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

    return df_curr


def model_predictions(df, bento_url):
    """
    Simulate using the deployed model by sending each row to the prediction endpoint.
    """
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
        payload = {"input_data": row.to_dict()}
        response = requests.post(bento_url, json=payload)

        if response.status_code == 200:
            result = response.json()
            predictions.append(result["prediction"][0])
        else:
            predictions.append(None)

    input_df["prediction"] = predictions
    input_df["price"] = df["price"]

    return input_df

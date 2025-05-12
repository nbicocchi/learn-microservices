import logging

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from sklearn.metrics import max_error, mean_absolute_error, mean_squared_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import PolynomialFeatures
from sklearn.linear_model import LinearRegression

import mlflow
from mlflow.models import infer_signature

def split_data(data: pd.DataFrame, parameters: dict) -> tuple:
    """Splits data into features and targets training and test sets.

    Args:
        data: Data containing features and target.
        parameters: Parameters defined in parameters/data_science.yml.
    Returns:
        Split data.
    """
    X = data[parameters["features"]]
    y = data["price"]
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=parameters["test_size"], random_state=parameters["random_state"]
    )
    return X_train, X_test, y_train, y_test


def train_model(X_train: pd.DataFrame, y_train: pd.Series) -> LinearRegression:
    """Trains the linear regression model.

    Args:
        X_train: Training data of independent features.
        y_train: Training data for price.

    Returns:
        Trained model.
    """
    regressor = make_pipeline(
        PolynomialFeatures(degree=1, include_bias=False),
        LinearRegression()
    )
    regressor.fit(X_train, y_train)
    return regressor


def evaluate_model_and_log_to_mlflow(
    parameters: dict,
    regressor: LinearRegression,
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series
):
    """Calculates and logs model details.

    Args:
        parameters: Parameters defined in parameters/data_science.yml.
        regressor: Trained model.
        X_train: Training data of independent features.
        y_train: Training data for price.
        X_test: Testing data of independent features.
        y_test: Testing data for price.
    """

    # Set our tracking server uri for logging
    mlflow.set_tracking_uri(uri="http://127.0.0.1:5000")

    # Create a new MLflow Experiment
    mlflow.set_experiment("spaceflights")

    # Start an MLflow run
    with mlflow.start_run(run_name="__default__"):

        # Log the hyperparameters
        mlflow.log_params(parameters)

        # Log metrics
        y_pred = regressor.predict(X_test)
        mlflow.log_metric("r2", r2_score(y_test, y_pred))
        mlflow.log_metric("mae", mean_absolute_error(y_test, y_pred))
        mlflow.log_metric("rmse", mean_squared_error(y_test, y_pred))
        mlflow.log_metric("max_error", max_error(y_test, y_pred))

        # Log a chart
        fig, ax = plt.subplots()
        ax.scatter(y_test, y_pred, marker='o')
        plt.xlabel("True")
        plt.ylabel("Predicted")
        mlflow.log_figure(fig, "true_vs_predicted.png")

        # Set a tag that we can use to remind ourselves what this run was for
        mlflow.set_tag("Training Info", "LR model for spaceship data")

        # Infer the model signature
        signature = infer_signature(X_train, regressor.predict(X_train))

        print(X_train)

        # Log the model
        model_info = mlflow.sklearn.log_model(
            sk_model=regressor,
            artifact_path="model",
            signature=signature,
            input_example=X_train,
            registered_model_name="spaceflights-pandas",
        )

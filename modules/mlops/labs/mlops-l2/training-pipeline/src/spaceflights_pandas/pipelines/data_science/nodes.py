
import pandas as pd
import os
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor

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
    MLFLOW_TRACKING_URI = os.getenv("MLFLOW_TRACKING_URI", "http://localhost:5000")

    mlflow.set_tracking_uri(uri=MLFLOW_TRACKING_URI)
    mlflow.set_experiment("spaceflights-kedro")
    mlflow.start_run(run_name="__default__")
    mlflow.set_tag("Training Info", "RandomForest model for spaceship data")
    mlflow.log_params(parameters)

    csv_path = "./data/05_model_input/final_input_table.csv"
    data.to_csv(csv_path, index=False)
    mlflow.log_artifact(csv_path, artifact_path="datasets")

    X = data[parameters["features"]]
    y = data["price"]
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=parameters["test_size"], random_state=parameters["random_state"]
    )

    return X_train, X_test, y_train, y_test


def train_model(X_train: pd.DataFrame, y_train: pd.Series, parameters: dict) -> RandomForestRegressor:
    """Trains the linear regression model.

    Args:
        X_train: Training data of independent features.
        y_train: Training data for price.
        parameters: Parameters defined in parameters/data_science.yml.

    Returns:
        Trained model.
    """

    regressor = RandomForestRegressor(**parameters['regressor'])
    regressor.fit(X_train, y_train)

    signature = infer_signature(X_train, regressor.predict(X_train))
    mlflow.sklearn.log_model(
        sk_model=regressor,
        name="model",
        signature=signature,
        input_example=X_train,
        registered_model_name="spaceflights-kedro",
    )

    return regressor


def evaluate_model(
    regressor: RandomForestRegressor,
    X_test: pd.DataFrame,
    y_test: pd.Series,
):
    """Calculates and logs model details.

    Args:
        regressor: Trained model.
        X_train: Training data of independent features.
        y_train: Training data for price.
        X_test: Testing data of independent features.
        y_test: Testing data for price.
        parameters: Parameters defined in parameters/data_science.yml.
    """

    # Log metrics
    y_pred = regressor.predict(X_test)
    mlflow.log_metric("r2", r2_score(y_test, y_pred))
    mlflow.log_metric("mae", mean_absolute_error(y_test, y_pred))
    mlflow.log_metric("rmse", mean_squared_error(y_test, y_pred))
    mlflow.end_run()

"""
def export_model_to_bentoml(regressor):
    Exports the trained model to BentoML.

    Args:
        regressor: Trained model.

    Returns:
        Info about the saved model in BentoML.
    model_name = "spaceflights-pandas"
    model_info = bentoml.sklearn.save_model(
        name=model_name,
        model=regressor,
        signatures={"predict": {"batchable": True}},
    )
    return model_info"""

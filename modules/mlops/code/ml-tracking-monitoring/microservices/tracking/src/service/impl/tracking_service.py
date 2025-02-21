import logging
import pandas as pd
import numpy as np
import psutil
import time
from datetime import datetime

from mlflow.models import infer_signature
from sklearn.metrics import (
    accuracy_score,
    f1_score,
    precision_score,
    recall_score,
)
from sklearn.model_selection import GridSearchCV, cross_val_predict
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC

import mlflow
import mlflow.sklearn

from src.service.i_service import IService
from src.persistence.model.i_model import IModel
from src.persistence.repository.impl.query import Query


class TrackingService(IService):
    def __init__(self, tracking_model: IModel, db=None):
        """
        Initialize TrackingService instance.
        
        :param tracking_model: Model instance implementing IModel interface for tracking
        :param db: Database connection object, defaults to None
        """

        self.tracking_model = tracking_model
        self.db = db
        self.mlflow = None
        self.logger = None
        self.query = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        self.init_query()
        self.mlflow = mlflow

    def init_query(self):
        self.query = Query(self.db, self.mlflow)

    def track_optimization_info(self, models, hyperparameters, scoring, cv, experiment_name, description, owner):
        """
        Track model optimization information and results.
        
        :param models: List of model names to optimize
        :param hyperparameters: Dictionary of hyperparameter grids for each model
        :param scoring: Scoring metric for optimization
        :param cv: Number of cross-validation folds
        :param experiment_name: Name of the MLflow experiment
        :param description: Description of the experiment
        :param owner: Name of experiment owner
        :return: List of MLflow run IDs for the experiment
        :raises MLflowException: If there's an error logging to MLflow
        """

        training_records = self.query.select_joined_conditioned_value("samples", "targets", "sample_index",
                                                                      "sample_index", 1)
        X, y = self.define_set(training_records)
        results = self.perform_optimization(X, y, models, hyperparameters, scoring, cv)
        id_runs = self.log_to_mlflow(results, experiment_name, X, y, scoring, cv, hyperparameters, description, owner)
        return id_runs

    def compute_metrics(self, y_true, y_pred):
        """
        Compute classification metrics for model evaluation.
        
        :param y_true: Array-like of true labels
        :param y_pred: Array-like of predicted labels
        :return: Dictionary containing accuracy, f1_score, precision, and recall metrics
        """

        metrics = {'accuracy': accuracy_score(y_true, y_pred), 'f1_score': f1_score(y_true, y_pred, average='weighted'),
                   'precision': precision_score(y_true, y_pred, average='weighted'),
                   'recall': recall_score(y_true, y_pred, average='weighted')}
        return metrics

    def define_set(self, records):
        """
        Define the set.
        :param records: The records of the training set
        :return X: The features
        :return y: The target
        """

        records = pd.DataFrame(records)
        self.prediction = records["sample_index"]

        # List of columns to drop
        columns_to_drop = [
            "dataset_id", "sample_id", "target_id",
            "sample_index", "target_index", "targets.sample_index"
        ]

        # Drop only the columns that exist in the DataFrame
        records = records.drop(columns=[col for col in columns_to_drop if col in records.columns], errors='ignore')

        records.replace({"null": np.nan}, inplace=True) 
        X = records.drop(columns=["class"])
        y = records["class"]
        return X, y

    def perform_optimization(self, X, y, models, hyperparameters, scoring, cv):
        """
        Perform the optimization.
        :param X: The features
        :param y: The target
        :param models: The models to optimize
        :param hyperparameters: The hyperparameters for the models
        :param scoring: The scoring method
        :param cv: The cross-validation folds
        :return optimized_results: The optimized results
        """

        optimized_results = {}
        for model_name in models:
            start_time = time.time()
            cpu_usage_before = psutil.cpu_percent(interval=None)

            model = get_model_by_name(model_name)
            grid_search = GridSearchCV(estimator=model, param_grid=hyperparameters[model_name],
                                       scoring=scoring, cv=cv)
            grid_search.fit(X, y)
            best_model = grid_search.best_estimator_
            y_pred = cross_val_predict(best_model, X, y, cv=cv)

            end_time = time.time()
            cpu_usage_after = psutil.cpu_percent(interval=None)
            metrics = self.compute_metrics(y, y_pred)

            optimization_time = end_time - start_time
            cpu_usage_diff = max(0.0, cpu_usage_after - cpu_usage_before)

            optimized_results[model_name] = {
                "best_model": best_model,
                "best_params": grid_search.best_params_,
                "metrics": metrics,
                "execution_time": optimization_time,
                "cpu_usage": cpu_usage_diff
            }
        return optimized_results

    def log_to_mlflow(self, models_results, experiment_name, X_train, y_train, scoring_grid_search, cv, param_grid,
                      description, owner):
        """
        Log to MLFlow.
        :param models_results: The optimized results
        :param experiment_name: The experiment name to track
        :param X_train: The features of the training set
        :param y_train: The target of the training set
        :param scoring_grid_search: The scoring method
        :param cv: The cross-validation folds
        :param param_grid: The hyperparameters for the models
        :param description: The description of the experiment
        :param owner: The owner of the experiment
        """

        experiment_id = self.create_mlflow_experiment(experiment_name, tag={"description": description, "owner": owner})

        run_id_dict = {
            "experiment_id": experiment_id,
            "run_ids": {}
        }

        for model_name, result in models_results.items():
            best_model = result.get("best_model")
            best_params = result["best_params"]
            metrics = result["metrics"]

            run_name = f"{model_name} Grid_Search_Opt and CV_Performance"

            with mlflow.start_run(experiment_id=experiment_id, run_name=run_name) as run:
                run_id = run.info.run_id
                mlflow.set_tag("experiment_type", "GridSearch_CrossValidation")
                mlflow.set_tag("model_type", model_name)

                if best_model and X_train is not None:
                    signature = infer_signature(X_train, best_model.predict(X_train))
                    input_example = X_train.iloc[:5]

                    mlflow.sklearn.log_model( sk_model=best_model, artifact_path="model",
                                              signature=signature, input_example=input_example)

                    model_uri = f"runs:/{run_id}/model"
                    mlflow.register_model(model_uri, model_name)

                self.logger.info(f"MlFlow Logging for run_id: {run_id}")

                mlflow.log_params({f"model_{k}": v for k, v in best_params.items()})
                mlflow.log_param("data_samples", X_train.shape[0])
                mlflow.log_param("data_features", X_train.shape[1])
                mlflow.log_param("data_classes", len(set(y_train)))

                mlflow.log_params({f"grid_search_{k}": v for k, v in param_grid[model_name].items()})
                mlflow.log_param("grid_search_scoring", scoring_grid_search)
                mlflow.log_param("grid_search_cv_folds", cv)

                mlflow.log_metrics({f"model_{k}": v for k, v in metrics.items()})
                mlflow.log_metric("system_cpu_usage", result['cpu_usage'])
                mlflow.log_metric("system_execution_time", result['execution_time'])

                run_id_dict["run_ids"][model_name] = run_id

        return run_id_dict

    def create_mlflow_experiment(self, experiment_name, tag):
        """
        Create the MLFlow experiment.
        :param experiment_name: The experiment name to track
        :param tag: The tag of the experiment
        :return experiment_id: The id of the experiment
        """

        self.logger.info(f"Creating experiment: {experiment_name}")

        try:
            existing_experiment = self.mlflow.get_experiment_by_name(experiment_name)
            if existing_experiment is not None:
                experiment_id = existing_experiment.experiment_id
                self.logger.info(f"Experiment already exists with ID: {experiment_id}")
                return experiment_id

            experiment_id = self.mlflow.create_experiment(experiment_name, tags=tag)
            self.logger.info(f"Experiment created with ID: {experiment_id}")
            return experiment_id

        except Exception as e:
            raise RuntimeError(f"Error in Experiment Creation: {e}")

    def get_experiment_info(self, experiment, experiment_runs):
        """
        Get the experiment info.
        :param experiment: The experiment
        :param experiment_runs: The runs of the experiment
        :return experiment_info: The experiment info
        """

        experiment_info = {
            "experiment_info": {
                "experiment_id": experiment.experiment_id,
                "experiment_name": experiment.name,
                "artifact_location": experiment.artifact_location,
                "lifecycle_stage": experiment.lifecycle_stage,
                "tags": experiment.tags,
                "data_creation": datetime.fromtimestamp(experiment.creation_time / 1000).strftime(
                    '%Y-%m-%d %H:%M:%S') if experiment.creation_time else None,
                "last_updated": datetime.fromtimestamp(experiment.last_update_time / 1000).strftime(
                    '%Y-%m-%d %H:%M:%S') if experiment.last_update_time else None,
            },
            "experiment_runs": {
                "number_of_runs": len(experiment_runs),
                "completed_runs": sum(run["status"] == "FINISHED" for _, run in experiment_runs.iterrows()),
                "failed_runs": sum(run["status"] == "FAILED" for _, run in experiment_runs.iterrows()),
                "active_runs": sum(run["status"] == "RUNNING" for _, run in experiment_runs.iterrows()),
                "stopped_runs": sum(run["status"] == "STOPPED" for _, run in experiment_runs.iterrows()),
                "first_run_completed": min(
                    run["start_time"].strftime('%Y-%m-%d %H:%M:%S') for _, run in experiment_runs.iterrows()),
                "last_run_completed": max(
                    run["end_time"].strftime('%Y-%m-%d %H:%M:%S') for _, run in experiment_runs.iterrows()),
            }
        }
        return experiment_info

    def get_best_model(self, experiment_runs, filter_req):
        """
        Get the best model.
        :param experiment_runs: The runs of the experiment
        :param filter_req: The filter request
        :return best_run_info: The best run info
        """

        if not filter_req or len(filter_req) == 0:
            raise ValueError("At least one filter (metric) is required.")

        best_metric_value = float('-inf')
        best_metrics = {}
        best_run_id = None

        for _, run in experiment_runs.iterrows():
            run_id = run["run_id"]
            run = self.mlflow.get_run(run_id)
            run_metrics = run.data.metrics
            valid_metrics = {k: v for k, v in run_metrics.items() if k in filter_req}

            for metric_name, metric_value in valid_metrics.items():
                if metric_value > best_metric_value:
                    best_metric_value = metric_value
                    best_run_id = run_id
                    best_metrics = valid_metrics

        if not best_run_id:
            return None

        best_run = self.mlflow.get_run(best_run_id)
        best_run_info = {
            "run_id": best_run_id,
            "run_name": best_run.data.tags.get('mlflow.runName', 'Unnamed Run'),
            "model": best_run.data.tags.get("model_type", "Unknown Model Type"),
            "metrics": best_metrics
        }

        return best_run_info

    def calculate_statistics(self, data):
        """
        Calculate the statistics.
        :param data: The data
        :return statistics: The statistics
        """

        if not data:
            return {
                "mean": 0,
                "median": 0,
                "std_dev": 0,
                "variance": 0,
                "min": 0,
                "max": 0,
                "mode": 0,
                "iqr": 0
            }
        n = len(data)
        mean = sum(data) / n
        sorted_data = sorted(data)

        median = (sorted_data[n // 2] if n % 2 != 0 else (sorted_data[n // 2 - 1] + sorted_data[n // 2]) / 2)
        std_dev = (sum((x - mean) ** 2 for x in data) / n) ** 0.5
        variance = std_dev ** 2
        min_value = min(data)
        max_value = max(data)
        mode = max(set(data), key=data.count)

        q1 = sorted_data[n // 4]
        q3 = sorted_data[(3 * n) // 4]
        iqr = q3 - q1

        return {
            "mean": mean,
            "median": median,
            "std_dev": std_dev,
            "variance": variance,
            "min": min_value,
            "max": max_value,
            "mode": mode,
            "iqr": iqr
        }

    def get_statistics(self, experiment_runs, filter_req):
        """
        Get the statistics.
        :param experiment_runs: The runs of the experiment
        :param filter_req: The filter request
        :return statistics: The statistics
        """

        durations, accuracies, f1_scores, precisions, recalls = [], [], [], [], []

        for _, run_series in experiment_runs.iterrows():
            run_id = run_series["run_id"]
            run = self.mlflow.get_run(run_id)
            run_metrics = run.data.metrics

            if not filter_req or "all" in filter_req:
                durations.append(run_metrics.get("system_execution_time", 0))
                accuracies.append(run_metrics.get("model_accuracy", 0))
                f1_scores.append(run_metrics.get("model_f1_score", 0))
                precisions.append(run_metrics.get("model_precision", 0))
                recalls.append(run_metrics.get("model_recall", 0))
            else:
                if "system_execution_time" in filter_req:
                    durations.append(run_metrics.get("system_execution_time", 0))
                if "model_accuracy" in filter_req:
                    accuracies.append(run_metrics.get("model_accuracy", 0))
                if "model_f1_score" in filter_req:
                    f1_scores.append(run_metrics.get("model_f1_score", 0))
                if "model_precision" in filter_req:
                    precisions.append(run_metrics.get("model_precision", 0))
                if "model_recall" in filter_req:
                    recalls.append(run_metrics.get("model_recall", 0))

        stats = {}
        if durations:
            stats["duration"] = self.calculate_statistics(durations)
        if accuracies:
            stats["accuracy"] = self.calculate_statistics(accuracies)
        if f1_scores:
            stats["f1_score"] = self.calculate_statistics(f1_scores)
        if precisions:
            stats["precision"] = self.calculate_statistics(precisions)
        if recalls:
            stats["recall"] = self.calculate_statistics(recalls)

        return stats

    def get_experiment_runs(self, experiment_id):
        """
        Get the experiment runs.
        :param experiment_id: The id of the experiment
        :return runs_list: The runs list
        """

        runs_list = []

        experiment_runs = self.mlflow.search_runs([experiment_id])

        for _, run in experiment_runs.iterrows():
            run_info = {
                "run_id": run["run_id"],
                "status": run["status"],
                "start_time": run["start_time"].strftime('%Y-%m-%d %H:%M:%S'),
                "end_time": run["end_time"].strftime('%Y-%m-%d %H:%M:%S'),
                "run_name": run.get("tags.mlflow.runName", "Unnamed Run"),
                "tags": {
                    "model_type": run.get("tags.model_type", "Unknown"),
                    "experiment_type": run.get("tags.experiment_type", "Unknown")
                }
            }
            runs_list.append(run_info)

        return runs_list if runs_list else None

    def get_run_parameters(self, run, parameter_type):
        """
        Extract parameters of specified type from MLflow run.
        
        :param run: MLflow Run object
        :param parameter_type: Type of parameters to extract ('data', 'grid_search', or 'model')
        :return: Dictionary of filtered parameters
        :raises ValueError: If parameter_type is invalid
        """

        run_params = run.data.params
        return {
            key.replace(f"{parameter_type}_", ""): value
            for key, value in run_params.items()
            if key.startswith(parameter_type)
        }

    def get_run_metrics(self, run, metric_type):
        """
        Extract metrics of specified type from MLflow run.
        
        :param run: MLflow Run object
        :param metric_type: Type of metrics to extract ('system' or 'model')
        :return: Dictionary of filtered metrics
        :raises ValueError: If metric_type is invalid
        """

        run_metrics = run.data.metrics
        return {
            key.replace(f"{metric_type}_", ""): value
            for key, value in run_metrics.items()
            if key.startswith(metric_type)
        }

    def track_model_setting(self, run):
        """
        Track the model setting.
        :param run: The run
        :return model_info: The model info
        """

        parameters = self.get_run_parameters(run, "model")
        hyperparameters = {
            k: identify_value_type(v)
            for k, v in parameters.items()
        }
        model_name = run.data.tags.get("model_type", "Unknown")
        return {
            "model_name": model_name,
            "hyperparameters": hyperparameters
        }


def identify_value_type(value):
    """
    Identify the value type.
    :param value: The value
    :return value: The value
    """

    if value in ['null', 'None']:
        return None
    if isinstance(value, str):
        if value.isdigit():
            return int(value)
        try:
            float_val = float(value)
            return float_val
        except ValueError:
            return value
    return value

def get_model_by_name(model_name):
    """
    Get the model by name.
    :param model_name: The name of the model
    :return model: The model
    """

    models_dict = {
        "LogisticRegression": LogisticRegression(),
        "RandomForest": RandomForestClassifier(),
        "SVC": SVC(),
    }
    return models_dict.get(model_name)



import datetime
import hashlib
import logging
import os, json
from typing import Dict
from typing import List
from typing import Optional

import dataclasses
import flask
import pandas as pd
import prometheus_client
from flask import Flask, render_template
from werkzeug.middleware.dispatcher import DispatcherMiddleware

from evidently.model_monitoring import CatTargetDriftMonitor
from evidently.model_monitoring import ClassificationPerformanceMonitor
from evidently.model_monitoring import DataDriftMonitor
from evidently.model_monitoring import DataQualityMonitor
from evidently.model_monitoring import ModelMonitoring
from evidently.model_monitoring import NumTargetDriftMonitor
from evidently.model_monitoring import ProbClassificationPerformanceMonitor
from evidently.model_monitoring import RegressionPerformanceMonitor
from evidently.pipeline.column_mapping import ColumnMapping
from evidently.runner.loader import DataLoader


app = Flask(__name__)
app.config["TEMPLATES_AUTO_RELOAD"] = True

#dataset_path = "datasets"
f = open(os.path.join("parameters", "params.json"))
params = json.load(f)
ff = open(os.path.join("parameters", "header_params.json"))
header_params = json.load(ff)

# Add prometheus wsgi middleware to route /metrics requests
app.wsgi_app = DispatcherMiddleware(app.wsgi_app, {"/metrics": prometheus_client.make_wsgi_app()})

@dataclasses.dataclass
class MonitoringServiceOptions:
    min_reference_size: int
    use_reference: bool
    moving_reference: bool
    window_size: int
    calculation_period_sec: int

@dataclasses.dataclass
class LoadedDataset:
    name: str
    references: pd.DataFrame
    monitors: List[str]
    column_mapping: ColumnMapping

EVIDENTLY_MONITORS_MAPPING = {
    "cat_target_drift": CatTargetDriftMonitor,
    "data_drift": DataDriftMonitor,
    "data_quality": DataQualityMonitor,
    "num_target_drift": NumTargetDriftMonitor,
    "regression_performance": RegressionPerformanceMonitor,
    "classification_performance": ClassificationPerformanceMonitor,
    "prob_classification_performance": ProbClassificationPerformanceMonitor,
}

class MonitoringService:
    # names of monitoring datasets
    datasets: LoadedDataset
    metric: prometheus_client.Gauge
    # collection of reference data
    reference: pd.DataFrame
    # collection of current data
    current: Dict[str, pd.DataFrame]
    # collection of monitoring objects
    monitoring: ModelMonitoring
    calculation_period_sec: float = 15
    window_size: int

    def __init__(self, datasets: LoadedDataset, window_size: int):
        target = "Quality"    
        self.reference = datasets.references
        self.current = {}
        self.current["model_input_table"] = pd.read_csv(os.path.join("datasets", "current.csv"))
        self.column_mapping = {}

        self.window_size = window_size
        self.metrics = {}

        self.monitoring = ModelMonitoring(
            monitors=[EVIDENTLY_MONITORS_MAPPING[k]() for k in datasets.monitors], options=[]
        )
        self.column_mapping[datasets.name] = datasets.column_mapping

        self.hash = hashlib.sha256(pd.util.hash_pandas_object(self.reference).values).hexdigest()
        self.hash_metric = prometheus_client.Gauge("evidently:reference_dataset_hash", "", labelnames=["hash"])

    def iterate(self, new_rows: pd.DataFrame):
        """Add data to current dataset for specified dataset"""
        window_size = self.window_size
        
        dataset_name = 'model_input_table'
        if dataset_name in self.current:
            current_data = self.current[dataset_name].append(new_rows, ignore_index=True)
        else:
            current_data = new_rows
        
        current_size = current_data.shape[0]

        if current_size > self.window_size:
            # cut current_size by window size value
            current_data.drop(index=list(range(0, current_size - self.window_size)), inplace=True)
            current_data.reset_index(drop=True, inplace=True)

        self.current[dataset_name] = current_data

        current_data['Distance (km)'] = current_data['Distance (km)'].astype('float64')
        current_data['Average Speed (km/h)'] = current_data['Average Speed (km/h)'].astype('float64')
        current_data['Calories Burned'] = current_data['Calories Burned'].astype('int64')
        current_data['Climb (m)'] = current_data['Climb (m)'].astype('int64')
        current_data['Average Heart rate (tpm)'] = current_data['Average Heart rate (tpm)'].astype('int64')
        current_data['Quality'] = current_data['Quality'].astype('float64')

        for column in current_data.columns:
            if column not in header_params["header"]:
                current_data.drop(column, axis=1, inplace=True)
    
        current_data.to_csv(os.path.join("datasets", "current.csv"), index = False)  

        self.next_run_time = datetime.datetime.now() + datetime.timedelta(
            seconds=self.calculation_period_sec
        )
        self.monitoring.execute(self.reference, current_data, self.column_mapping[dataset_name])
        self.hash_metric.labels(hash=self.hash).set(1)

        for metric, value, labels in self.monitoring.metrics():
            metric_key = f"evidently:{metric.name}"
            found = self.metrics.get(metric_key)

            if not labels:
                labels = {}

            labels["dataset_name"] = 'model_input_table'

            if isinstance(value, str):
                continue

            if found is None:
                found = prometheus_client.Gauge(metric_key, "", list(sorted(labels.keys())))
                self.metrics[metric_key] = found

            try:
                found.labels(**labels).set(value)

            except ValueError as error:
                # ignore errors sending other metrics
                logging.error("Value error for metric %s, error: ", metric_key, error)

SERVICE: Optional[MonitoringService] = None


@app.before_first_request
def configure_service():
    # pylint: disable=global-statement
    global SERVICE
    
    options = MonitoringServiceOptions(
        calculation_period_sec = 2, 
        min_reference_size = 30, 
        moving_reference = False, 
        use_reference = True, 
        window_size = params["window_size"]
    )
    loader = DataLoader()

    logging.info(f"Load reference data")
    reference_path = os.path.join("datasets", params["file_name_training_data_clean"])

    reference_data = pd.read_csv(reference_path, parse_dates=True)
    datasets = LoadedDataset(
        name = 'model_input_table',
        references = reference_data,
        monitors = params["monitors_name"],
        column_mapping=ColumnMapping(target = 'Quality', numerical_features = params["numerical_features_names"])
    )
    logging.info("Reference is loaded: %s rows", len(reference_data))

    SERVICE = MonitoringService(datasets=datasets, window_size=options.window_size)


@app.route("/")
def home():
    return render_template("request_page.html")

@app.route("/drift_report")
def drift_report():
    return render_template("drift_report.html")

@app.route("/data_stability")
def data_stability():
    return render_template("data_stability.html")

@app.route("/data_drift_tests")
def data_drift_tests():
    return render_template("data_drift_tests.html")
   

@app.route("/iterate", methods=["POST"])
def iterate():
    item = flask.request.json

    global SERVICE
    if SERVICE is None:
        return "Internal Server Error: service not found", 500

    SERVICE.iterate(new_rows=pd.DataFrame.from_dict(item))
    return "ok"


if __name__ == "__main__":
    app.run(debug=True)
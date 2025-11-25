"""
This script performs data and model drift detection using Evidently AI.
It creates a workspace and project, takes reference and current datasets,
generates reports, checks for failed tests, and conditionally triggers a Kedro pipeline if drifts are detected.
"""

import datetime
import pandas as pd
import os
import sys
import requests
from evidently.ui.workspace import Workspace, Snapshot
from evidently import Report, DataDefinition, Dataset, Regression
from evidently.presets import DataDriftPreset, DataSummaryPreset, RegressionPreset
from evidently.metrics import MAE
from evidently.sdk.models import PanelMetric
from evidently.sdk.panels import DashboardPanelPlot
import logging
import warnings

warnings.simplefilter(action='ignore', category=FutureWarning)

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger(__name__)

logging.getLogger("botocore").setLevel(logging.WARNING)
logging.getLogger("aiobotocore").setLevel(logging.WARNING)
logging.getLogger("s3transfer").setLevel(logging.WARNING)
logging.getLogger("urllib3").setLevel(logging.WARNING)
logging.getLogger("asyncio").setLevel(logging.WARNING)

os.environ["AWS_ACCESS_KEY_ID"] = os.getenv("MINIO_ROOT_USER", "minioadmin")
os.environ["AWS_SECRET_ACCESS_KEY"] = os.getenv("MINIO_ROOT_PASSWORD", "minioadmin")
os.environ["AWS_ENDPOINT_URL"] = os.getenv("MINIO_URL", "http://minio:9000")

KEDRO_API_URL = os.getenv("KEDRO_API_URL", "http://host.docker.internal:8005/run-pipeline")

# Workspace and project setup
logger.info("Creating workspace and project...")
ws = Workspace.create(path="s3://evidently-ai/workspace")
project = ws.create_project(f"My Project {datetime.datetime.now().strftime('%Y %m %d - %H %M %S')}")
project.description = "My production monitoring simulation project."
project.save()


def load_dataset(file_path: str, schema: DataDefinition) -> Dataset:
    df = pd.read_csv(file_path)
    logger.debug(f"Loaded dataset from {file_path} with {len(df)} rows")
    return Dataset.from_pandas(df, data_definition=schema)


def report_data_drift(ref_file: str, curr_file: str, schema: DataDefinition, project: any) -> Snapshot:
    logger.info("Running data drift report...")
    eval_data_ref = load_dataset(ref_file, schema)
    eval_data_prod = load_dataset(curr_file, schema)

    report_drift = Report([DataDriftPreset(drift_share=0.7)], include_tests=True)
    eval_drift = report_drift.run(reference_data=eval_data_ref, current_data=eval_data_prod,
                                  tags=["Data drift present", "tests included"])
    ws.add_run(project.id, eval_drift, include_data=False)

    report_summary = Report([DataSummaryPreset()])
    eval_summary = report_summary.run(reference_data=eval_data_ref, current_data=eval_data_prod,
                                      tags=["Data summary present"])
    ws.add_run(project.id, eval_summary, include_data=False)

    # Dashboard panels
    project.dashboard.add_panel(
        DashboardPanelPlot(
            title="Price and passenger capacity drift",
            subtitle="Drift numerico su prezzo e capacità passeggeri",
            size="half",
            values=[
                PanelMetric(legend="Price drift", metric="ValueDrift", metric_labels={"column": "price"}),
                PanelMetric(legend="Capacity drift", metric="ValueDrift",
                            metric_labels={"column": "passenger_capacity"}),
            ],
            plot_params={"plot_type": "bar"},
        ),
        tab="Data Drift",
    )

    project.dashboard.add_panel(
        DashboardPanelPlot(
            title="Drift on categorical features",
            subtitle="Comparazione su colonne categoriche binarie",
            size="half",
            values=[
                PanelMetric(legend="D-Check Complete", metric="ValueDrift",
                            metric_labels={"column": "d_check_complete"}),
                PanelMetric(legend="Moon Clearance", metric="ValueDrift",
                            metric_labels={"column": "moon_clearance_complete"}),
                PanelMetric(legend="IATA Approved", metric="ValueDrift",
                            metric_labels={"column": "iata_approved"}),
            ],
            plot_params={"plot_type": "bar"},
        ),
        tab="Data Drift",
    )

    return eval_drift


def report_model_drift(ref_file: str, curr_file: str, schema: DataDefinition, project: any) -> Snapshot:
    logger.info("Running data drift report...")
    eval_data_ref = load_dataset(ref_file, schema)
    eval_data_prod = load_dataset(curr_file, schema)

    report = Report([RegressionPreset()], include_tests=False)
    my_eval = report.run(reference_data=eval_data_ref, current_data=eval_data_prod, tags=["Regression present"])
    ws.add_run(project.id, my_eval, include_data=False)

    report = Report([MAE()], include_tests=True)
    mape = report.run(reference_data=eval_data_ref, current_data=eval_data_prod, tags=["MAE", "tests included"])
    ws.add_run(project.id, mape, include_data=False)

    return mape


def check_failed_tests(my_eval: Snapshot) -> list:

    failed = [t for t in my_eval.tests_results if t.status == "FAIL"]
    if failed:
        logger.warning(f"{len(failed)} failed tests detected:")
        for t in failed:
            logger.warning(f"ID: {t.id}, \n"
                           f"Name: {t.name}, \n"
                           f"Column: {t.metric_config.params.get('column')},\n"
                           f"Metric type: {t.metric_config.params.get('type')},\n"
                           f"Threshold (drift/fixed test): {t.metric_config.params.get('drift_share') or t.test_config.get('threshold')}\n"
                           f"Critical test? {t.test_config.get('is_critical')}\n\n")
    return failed


def run_kedro_pipeline():
    try:
        result = requests.post(KEDRO_API_URL)
        logger.info(f"Kedro pipeline triggered successfully: {result.json()}")
    except requests.RequestException as e:
        logger.error(f"Failed to trigger Kedro pipeline: {e}")
        sys.exit(1)


def main():
    ##########################
    # Data Drift Check
    ##########################
    logger.debug("Creating data drift report...")
    schema_drift = DataDefinition(
        numerical_columns=[
            "engines",
            "passenger_capacity",
            "crew",
            "price",
            "company_rating"
        ],
        categorical_columns=[
            "d_check_complete",
            "moon_clearance_complete",
            "iata_approved",
        ]
    )

    eval_drift = report_data_drift(
        ref_file="./data/reference_table_and_target.csv",
        curr_file="./data/dataset_curr.csv",
        schema=schema_drift,
        project=project
    )

    failed_data_tests = check_failed_tests(eval_drift)

    #########################
    # Model Performance Check
    #########################
    logger.info("Creating model performance report...")
    schema_model = DataDefinition(
        regression=[Regression(name="default", target="price", prediction="prediction")]
    )

    model_eval = report_model_drift(
        ref_file="./data/reference_table_and_target.csv",
        curr_file="./data/dataset_curr_and_target.csv",
        schema=schema_model,
        project=project
    )

    failed_model_tests = check_failed_tests(model_eval)

    ##############################
    # Conditional pipeline trigger
    ##############################
    if failed_data_tests or failed_model_tests:
        logger.warning("Failed tests detected, restarting the pipeline...")
        run_kedro_pipeline()
        sys.exit(1)
    else:
        logger.info("No failed tests detected. No important drift detected.")
        sys.exit(0)


if __name__ == "__main__":
    main()

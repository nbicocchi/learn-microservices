import datetime
import os
import pandas as pd
from evidently.ui.workspace import Workspace, Snapshot
from evidently import Report, DataDefinition, Dataset, Regression
from evidently.presets import DataDriftPreset, DataSummaryPreset, RegressionPreset
from evidently.metrics import MAE
from evidently.sdk.models import PanelMetric
from evidently.sdk.panels import DashboardPanelPlot

os.environ["AWS_ACCESS_KEY_ID"] = os.getenv("MINIO_ROOT_USER", "minioadmin")
os.environ["AWS_SECRET_ACCESS_KEY"] = os.getenv("MINIO_ROOT_PASSWORD", "minioadmin")
os.environ["AWS_ENDPOINT_URL"] = os.getenv("MINIO_URL", "http://minio:9000")

ws = Workspace.create(path="s3://evidently-ai/workspace")


def project_setup() -> tuple:
    project = ws.create_project(f"My Project {datetime.datetime.now().strftime('%Y %m %d - %H %M %S')}")
    project.description = "My production monitoring simulation project."
    project.save()

    return project


def load_dataset(file_path: str, schema: DataDefinition) -> Dataset:
    df = pd.read_csv(file_path)
    return Dataset.from_pandas(df, data_definition=schema)


def report_data_drift(ref_file: str, curr_file: str, schema: DataDefinition, project: any) -> Snapshot:
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
    return failed


def data_drift_check(ref_file, curr_file, project):
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
        ref_file=ref_file,
        curr_file=curr_file,
        schema=schema_drift,
        project=project
    )

    return check_failed_tests(eval_drift)


def model_performance_check(ref_file, curr_file, project):
    schema_model = DataDefinition(
        regression=[Regression(name="default", target="price", prediction="prediction")]
    )

    model_eval = report_model_drift(
        ref_file=ref_file,
        curr_file=curr_file,
        schema=schema_model,
        project=project
    )

    return check_failed_tests(model_eval)

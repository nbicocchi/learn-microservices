# Evidently AI Overview

[Evidently AI](https://docs.evidentlyai.com/introduction) is an open-source Python library designed to monitor and evaluate data and machine learning models in production.

## Key Capabilities
**Evidently AI** provides several key features:
- **Data Drift Detection**: Identify changes in input data distributions over time.
- **Model Performance Monitoring**: Track model accuracy, precision, recall, and other performance metrics.
- **Visualization**: Generate interactive reports and dashboards to visualize data and model performance.
- **Custom Metrics**: Define and monitor custom metrics relevant to your specific use case.

## Key components

**Evidently AI** main components:
- **Reports**: Generate detailed reports that summarize data and model performance.
- **Dashboards**: Create interactive dashboards for real-time monitoring.

## Organize your work with Workspaces and Projects

**Evidently AI** allows you to organize your monitoring activities using Workspaces and Projects:
- **Workspaces**: High-level containers for organizing multiple projects. Each workspace can contain several projects, making it easier to manage related monitoring tasks.
- **Projects**: Within a workspace, projects group related reports and analyses. Each project can have its own set of reports, dashboards and metrics.

---

## Project Highlights

### Evidently AI Installation

**Evidently AI** can be installed via pip. Use the following command to install the library:

```bash
pip install evidently
```

### Self-Hosting the Evidently UI service

**Evidently AI** provides to set up **Evidently Cloud Platform** or **self-host** the Evidently UI service.
We opted to self-host the Evidently UI service using `Docker Compose`, alongside `MinIO` for storage.

1. **Create a Workspace and Project**: 
    In our example, we created a workspace on MinIO bucket, by specifying the S3 path:

    ```yml    
    environment:
      - FSSPEC_S3_KEY=${MINIO_ROOT_USER}
      - FSSPEC_S3_SECRET=${MINIO_ROOT_PASSWORD}
      - FSSPEC_S3_ENDPOINT_URL=http://minio:9000
    command: [ "--workspace", "s3://evidently-ai/workspace" ]
    ```
   
2. **Load datasets**:
   `reference` and `current` (to evaluate data and model drift) datasets.
3. Set schema for your data using `DataDefinition`.
4. Create a **Report** to evaluate data drift and model performance.
      - **Metric Presets** - pre-configured evaluation metrics.
        - `Data Drift` and `Data Summary`
        - `Classification Performance` and `Regression Performance`
        - For more details, see [All Presets](https://docs.evidentlyai.com/metrics/all_presets)
      - **Metrics** - individual metrics to include in your own report. 
        - Column-level metrics, Data-level metrics, Classification metrics, Regression metrics and more.
        - For more details, see [All metrics](https://docs.evidentlyai.com/metrics/)
      - **Tests** - by adding a condition parameter to a Metric.
        - `Data Quality`, `Data Drift`, `Model Performance` and more.
        - For more details, see [All Test Suites](https://docs.evidentlyai.com/metrics/test_suites)
5. [optional] Set up a **Dashboard** - create custom panels.
      - Text panel, Counter panel, Plot panel
      - For more details, see [Dashboards](https://docs.evidentlyai.com/docs/platform/dashboard_add_panels)

#### Jupiter Notebook
You can find a Jupyter Notebook `./notebooks/evidently_ai.ipynb` demonstrating how to use Evidently AI for monitoring data and model performance.

### Run
Follow these steps to start and run the deployment-monitoring environment:
1. Build and start the core services:
   ```bash
   docker compose build bentoml, minio, createbuckets, evidently-service
   docker compose up bentoml, minio, createbuckets, evidently-service
   ```
2. Load the reference dataset into the MinIO bucket `evidently-ai/datasets`.
   - You need to set the reference dataset path in the environment variable `REFERENCE_DATA_PATH` in the `docker-compose.yml` file.
   - Note: the reference dataset you can find and download from **MLflow** UI about the model you want to deploy and monitor.
      ```bash
      docker compose build reference
      docker compose up reference
      ```
3. Simulate model usage over time using a scheduled process (every 5 minutes / 300 seconds).
This step:
   - generates a synthetic `current` dataset:
     - you can set the SCENARIO type (`stable` - generating a dataset similar to the reference dataset - or *drift* - generating a dataset that have data drift and model drift) by `docker-compose.yml` file: 
       ```yaml
       environment:
         SCENARIO=stable # or 'drift'
       ```
   - makes predictions and uploads to MinIO bucket `evidently-ai/datasets`
   - generates Evidently monitoring reports and uploads to MinIO bucket `evidently-ai/workspace`
   - if data drift or model performance degradation is detected, triggers kedro pipeline to retrain the model.
     - Attention: this step requires the **Kedro-api** container to be running.
     ```bash
     docker compose build monitoring
     docker compose up monitoring
     ```

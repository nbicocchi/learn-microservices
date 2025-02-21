# Tools for MLOps
This slide presents useful tools that can help automate various processes in the MLOps pipeline,
enhancing efficiency and collaboration among teams.
Among these tools, MLflow and Evidently AI stand out for their capabilities in experiment tracking and model monitoring.

## MLflow

MLflow's primary feature is **experiment tracking**. This functionality enables users to log and track parameters, metrics, and artifacts for each experiment. Users can effectively monitor model performance and compare different configurations, maintaining transparency in the development process and facilitating team collaboration through shared results.

### 1. Metadata and Artifacts in MLflow

MLflow distinguishes between two main types of information: **metadata** and **artifacts**. This distinction is crucial for understanding how experiment data is managed and stored.

#### Metadata
Metadata encompasses all structured information related to experiments and runs. This data is stored in the MLflow-configured **database** and includes:

- **Parameters**: Model configuration settings, such as training hyperparameters
- **Metrics**: Performance measurements calculated during or after training (e.g., accuracy, F1 score, execution time)
- **Tags**: Labels assigned to experiments and runs for descriptive purposes (e.g., experiment type, owner)
- **Run Information**: Execution details, including experiment ID, timestamp, configuration, and final status

This data is stored in a **relational database** (such as MySQL, PostgreSQL, or SQLite), enabling efficient searching, filtering, and comparison of runs and their results.

#### Artifacts
Artifacts are files generated during runs and include larger, unstructured items such as:

- **Trained Models**: Saved model files
- **Graphs**: Performance visualizations, such as accuracy or loss curves
- **Configuration Files**: Scripts and other files needed to reproduce the experiment

Artifacts are stored separately from the database due to their size and management requirements. They reside in dedicated storage space, either in a local folder or cloud storage service.

![](images/mlflow-metadata.webp)

This separation between metadata and artifacts optimizes MLflow's efficiency and enables scalable experiment data management.

---

### 2. Setup

#### 1. Automatic Database Creation

Before starting experiments and runs, MLflow requires a database to store all metadata related to the experiments. MLflow can be configured to automatically generate this database, ensuring that all necessary tables for tracking executions and associated information are created.

To enable automatic database creation, simply configure MLflow to use an external database, such as MySQL.

```python
mlflow.set_tracking_uri("mysql+pymysql://username:password@host:port/db_name")
``` 

In the code above:

- `mysql` indicates the type of database to connect to, in this case, MySQL. MLflow uses SQLAlchemy, a Python toolkit for managing database connections, and specifies `mysql` as the database type.

- `pymysql` is the specific driver for connecting Python to MySQL. It is a library that handles communication between Python and the MySQL database, enabling Python code to send and receive data from the database.

Upon first request, MLflow checks for the database and required tables. If they don't exist, MLflow automatically creates them in the specified database. Here's an overview of the main tables:

---

#### 2. Creating Experiments

In MLflow, an **experiment** represents a working context for a set of related **runs**. Each experiment has a unique ID that provides access to all associated runs and related information.

To create an experiment, use the following function:

```python
experiment_id = mlflow.create_experiment(
    experiment_name, 
    tag={"description": description, "owner": owner}
) 
```

The parameters of the function are:

- **name**: The name of the experiment, which must be a unique string.
- **artifact_location**: The location where run artifacts are stored. If not provided, the server will choose an appropriate default value.
- **tags**: An optional dictionary of keys and string values to be set as tags on the experiment.

In the example, the experiment is created with a specific name and enriched with useful tags, such as the description and owner of the experiment. These tags are crucial for facilitating the search and organization of experiments within the platform.

After creating the experiment, MLflow assigns it a unique ID. This ID is essential to retain as it provides access to all runs and associated information for that experiment.

#### 3. Creation of Runs
In MLflow, a **run** represents a single execution of an activity related to a machine learning model within an experiment.
This activity may include:
- training a model
- evaluating its performance  
- tuning hyperparameters
- generating predictions

Each run is associated with a specific experiment and records all details related to that activity.

![](images/mlflow-exp-run.webp)

To create a run, you use the `mlflow.start_run()` function; An example of how to start a run is shown below:

```python
with mlflow.start_run(experiment_id=experiment_id, run_name="name_of_the_run") as run:
```

---
### 3. Logging Run Information

After starting a run, you can log several types of information that are critical for analyzing and comparing model performance. In MLflow, you can log various types of data during a run, including:

- **Log Types**:
  - **Parameters**: Hyperparameters, dataset parameters, process configurations.
  - **Metrics**: Model performance, contextual execution metrics.
  - **Artifacts**: Files/objects from training.
    - **Includes**:
      - Serialized Model
      - Model Dependencies (`conda.yaml`, `requirements.txt`)
      - Signature and Input Example


The MLflow database also stores the model's **metadata**, including:

- **Run ID**: A unique identifier linking the model to its associated experiment
- **Artifact Path**: The model's storage location reference (e.g., `runs:/<run_id>/model`), required for model retrieval
- **Parameters and Metrics**: Configuration and performance data from the model's training run, if logged
- **Time and Logging Information**: Timestamps and configuration details for model context and versioning

While this metadata doesn't contain the model itself, it provides all necessary information to identify and load the correct model from the artifact directory.

---

### 4. Information Retrieval

MLflow provides APIs for accessing information about experiments and runs, enabling retrieval of parameters, metrics, tags, and context for analysis and comparison. Key functions include:

- **`mlflow.search_experiments()`**: Retrieves a list of experiments based on specified criteria.
- **`mlflow.search_runs()`**: Searches for runs within specified experiments, returning details about each run.
- **`mlflow.get_experiment()`**: Retrieves detailed information about a specific experiment using its ID.
- **`mlflow.get_run()`**: Retrieves detailed information about a specific run using its run ID.

These functions support filtering, sorting, and detailed data retrieval for effective experiment and run management.

---
## Evidently AI

### Overview

Evidently AI is a powerful tool designed for monitoring and evaluating machine learning models in production environments.  
It provides a comprehensive suite of features that enable data scientists and machine learning engineers to ensure the quality and performance of their models over time.  
The platform focuses on observability, allowing users to track **data drift**, **model performance**, and **data quality**, which are critical for maintaining robust machine learning systems.

---

### Reports

Evidently allows users to generate detailed **Reports** that provide insights into various aspects of their data and models. These reports help identify issues like data drift, data quality problems, or performance degradation, offering a clear overview of your ML pipeline’s health.  
You can think of reports as combinations of the metrics and statistical tests that are grouped together. Evidently includes a set of pre-built Reports, but users can also create custom reports tailored to their specific needs.
Each of the predefined reports addresses a specific aspect of the data or model performance. 

![Evidently Reports](images/evidently_reports.webp)

#### 1. **Data Drift Reports**
- **Definition**: Data drift occurs when the distribution of features in the current data differs significantly from the training data.
- **Use Case**: Detect shifts in feature distributions that may impact model predictions.
- **Example Insights**:  
  - Which features have changed the most?  
  - Quantitative metrics, such as the Kolmogorov-Smirnov statistic, to measure drift.

#### 2. **Data Quality Reports**
- **Definition**: Data quality refers to the consistency, completeness, and validity of input data.
- **Use Case**: Identify anomalies, missing values, or invalid data that could disrupt model performance.
- **Example Insights**:  
  - Percentage of missing data.  
  - Detection of outliers or unexpected categorical values.

#### 3. **Model Performance Reports**
- **Definition**: Assess how well the model is performing using key metrics. There are different types of model performance reports, such as classification, probababilistic classification or regression.
- **Use Case**: Track metrics like accuracy, precision, recall, or regression errors over time.
- **Example Insights**:  
  - Trends in model performance.  
  - Comparison of predictions against ground truth.

#### 4. **Target Drift Reports**
- **Definition**: Target drift occurs when the distribution of the predicted target variable changes over time. You can find numerical and categorical target drift reports.
- **Use Case**: Monitor changes in the target variable that may affect model predictions.
- **Example Insights**:  
  - Distribution of the target variable.  
  - Statistical tests to detect significant changes.

Reports are generated in various formats, such as interactive HTML files, making them easy to share with stakeholders.

---

### Tests

Evidently also provides a framework for **Tests**, which are automated checks designed to validate the data and model performance. These tests allow users to establish thresholds for specific metrics and trigger alerts when these thresholds are exceeded.

#### Example Test Categories:
1. **Data Drift Tests**: Check if data distributions have changed beyond acceptable limits.  
2. **Data Quality Tests**: Validate the integrity of the incoming data (e.g., ensuring no null values in critical columns).  
3. **Model Performance Tests**: Ensure the model maintains a specified accuracy or error threshold.

When performing tests in Evidently, the results provide clear feedback on the status of your data or model's conditions. The possible outcomes of a test are:

- **Success**: The test condition is satisfied, indicating everything is functioning as expected.
- **Fail**: The test condition is not satisfied, and the test has the highest priority for immediate attention.  
- **Warning**: The test condition is not satisfied but has secondary priority. (Note: Test importance parameters will be available in a future release.)  
- **Error**: The test execution itself failed, indicating a problem with the test process.

A **test suite** is a collection of individual checks grouped together to address a specific use case. Instead of running tests individually, a test suite enables you to execute multiple tests simultaneously and receive a summarized overview of all results.  

---

### Visualization and Monitoring

Evidently offers flexible ways to visualize and monitor data and model performance, ensuring actionable insights are accessible in multiple formats.

![Evidently Visualization](images/evidently_monitoring.webp)

#### 1. **Dashboards**
- Dashboards help visually explore and evaluate data and model performance.  
- You can generate dashboards in supported notebook environments (e.g., Jupyter, Colab) or through the command-line interface.  
- Dashboards can be displayed directly in notebooks or exported as standalone HTML files for sharing.

#### 2. **Profiles**
- Evidently also generates JSON Profiles, which are the "JSON versions" of dashboards.  
- JSON Profiles enable seamless integration of Evidently into ML pipelines, such as:  
  - Performing scheduled batch checks of model health.  
  - Logging results for further analysis or building workflows, e.g., triggering alerts or retraining models.  
- To create a JSON Profile, select the desired sections, which determine the analysis performed and metrics calculated.  
- JSON Profiles can be generated in notebooks or via the command-line interface, using the same inputs and configurations as dashboards.

#### 3. **Real-Time Monitoring**
- Evidently provides Monitors to collect data and model metrics from deployed ML services.  
- These metrics can be used to build live monitoring dashboards or log data for further use.  
- Evidently includes a lightweight integration with Prometheus and Grafana, featuring pre-built dashboards for quick deployment.  

---

## Sources

- [Official MLflow Documentation](https://mlflow.org/docs/latest/index.html)
- [MLflow Tutorials](https://mlflow.org/docs/latest/getting-started/index.html)
- [Evidently AI Documentation](https://docs.evidentlyai.com/)
















# PostgreSQL DB

This section provides a detailed description of the PostgreSQL database schema used in the MLOps project.

The database schema consists of two databases: a Dataset Database for storing the actual data and an MLflow Metadata Database for tracking experiments and model information.

## Index
- [Dataset Database](#1-dataset-database)
- [MLflow Metadata Database](#2-mlflow-metadata-database)

## 1. Dataset Database

### Datasets table

| **Fields**   | **Type**       | **Attributes** |
|--------------|----------------|----------------|
| *dataset_id* | `int`          | primary key    |
| *name*       | `varchar(255)` |                |

### Samples table

|**Fields**                    |**Type**      |**Attributes**|
|------------------------------|--------------|--------------|
|*sample_id*                   |`int`         |primary key   |
|*dataset_id*                  |`int`         |foreign key   |
|*sample_index*                |`int`         |              |
|*Alcohol*                     |`float`       |              |
|*Malicacid*                   |`float`       |              |
|*Ash*                         |`float`       |              |
|*Alcalinity_of_ash*           |`float`       |              |
|*Magnesium*                   |`float`       |              |
|*Total_phenols*               |`float`       |              |
|*Flavanoids*                  |`float`       |              |
|*Nonflavanoid_phenols*        |`float`       |              |
|*Proanthocyanins*             |`float`       |              |
|*Color_intensity*             |`float`       |              |
|*Hue*                         |`float`       |              |
|*0D280_0D315_of_diluted_wines*|`float`       |              |
|*Proline*                     |`float`       |              |

### Predictions table

|**Fields**        |**Type**|**Attributes**|
|------------------|--------|--------------|
|*prediction_id*   |`int`   |primary key   |
|*sample_index*    |`int`   |foreign key   |
|*prediction_index*|`int`   |              |
|*class*           |`int`   |              |

### Targets table

|**Fields**    |**Type**|**Attributes**|
|--------------|--------|--------------|
|*target_id*   |`int`   |primary key   |
|*sample_index*|`int`   |foreign key   |
|*target_index*|`int`   |              |
|*class*       |`int`   |              |

## 2. MLflow Metadata Database

### Alembic Version Table

| **Fields**       | **Type**        | **Attributes**    |
|------------------|-----------------|-------------------|
| *version_num*    | `varchar(32)`   | primary key       |


### Datasets Table

| **Fields**            | **Type**       | **Attributes** |
|-----------------------|----------------|----------------|
| *dataset_uuid*        | `varchar(36)`  |                |
| *experiment_id*       | `int`          | primary key    |
| *name*                | `varchar(500)` | primary key    |
| *digest*              | `varchar(36)`  | primary key    |
| *dataset_source_type* | `varchar(36)`  |                |
| *dataset_source*      | `text`         |                |
| *dataset_schema*      | `mediumtext`   |                |
| *dataset_profile*     | `mediumtext`   |                |


### Experiment Tags Table

| **Fields**        | **Type**        | **Attributes**    |
|-------------------|-----------------|-------------------|
| *key*             | `varchar(250)`  | primary key       |
| *value*           | `varchar(5000)` |                   |
| *experiment_id*   | `int`           | primary key       |


### Experiments Table

| **Fields**           | **Type**        | **Attributes** |
|----------------------|-----------------|----------------|
| *experiment_id*      | `int`           | primary key    |
| *name*               | `varchar(256)`  |                |
| *artifact_location*  | `varchar(256)`  |                |
| *lifecycle_stage*    | `varchar(32)`   |                |
| *creation_time*      | `bigint`        |                |
| *last_update_time*   | `bigint`        |                |


### Input Tags Table

| **Fields**        | **Type**        | **Attributes**    |
|-------------------|-----------------|-------------------|
| *input_uuid*      | `varchar(36)`   | primary key       |
| *name*            | `varchar(255)`  | primary key       |
| *value*           | `varchar(500)`  |                   |


### Inputs Table

| **Fields**         | **Type**        | **Attributes**    |
|--------------------|-----------------|-------------------|
| *input_uuid*       | `varchar(36)`   |                   |
| *source_type*      | `varchar(36)`   | primary key       |
| *source_id*        | `varchar(36)`   | primary key       |
| *destination_type* | `varchar(36)`   | primary key       |
| *destination_id*   | `varchar(36)`   | primary key       |


### Latest Metrics Table

| **Fields**       | **Type**        | **Attributes**    |
|------------------|-----------------|-------------------|
| *key*            | `varchar(250)`  | primary key       |
| *value*          | `double`        |                   |
| *timestamp*      | `bigint`        |                   |
| *step*           | `bigint`        |                   |
| *is_nan*         | `tinyint(1)`    |                   |
| *run_uuid*       | `varchar(32)`   | primary key       |


### Metrics Table

| **Fields**      | **Type**        | **Attributes**   |
|------------------|-----------------|-------------------|
| *key*            | `varchar(250)`  | primary key       |
| *value*          | `double`        | primary key       |
| *timestamp*      | `bigint`        | primary key       |
| *run_uuid*       | `varchar(32)`   | primary key       |
| *step*           | `bigint`        | primary key       |
| *is_nan*         | `tinyint(1)`    | primary key       |


### Model Version Tags Table

| **Fields**        | **Type**        | **Attributes**    |
|-------------------|-----------------|-------------------|
| *key*             | `varchar(250)`  | primary key       |
| *value*           | `varchar(5000)` |                   |
| *name*            | `varchar(256)`  | primary key       |
| *version*         | `int`           | primary key       |


### Model Versions Table

| **Fields**           | **Type**        | **Attributes**   |
|----------------------|-----------------|-------------------|
| *name*               | `varchar(256)`  | primary key       |
| *version*            | `int`           | primary key       |
| *creation_time*      | `bigint`        |                   |
| *last_updated_time*  | `bigint`        |                   |
| *description*        | `varchar(5000)` |                   |
| *user_id*            | `varchar(256)`  |                   |
| *current_stage*      | `varchar(20)`   |                   |
| *source*             | `varchar(500)`  |                   |
| *run_id*             | `varchar(32)`   |                   |
| *status*             | `varchar(20)`   |                   |
| *status_message*     | `varchar(500)`  |                   |
| *run_link*           | `varchar(500)`  |                   |
| *storage_location*    | `varchar(500)`  |                   |


### Params Table

| **Fields**      | **Type**        | **Attributes**   |
|------------------|-----------------|-------------------|
| *key*            | `varchar(250)`  | primary key       |
| *value*          | `varchar(8000)` |                   |
| *run_uuid*       | `varchar(32)`   | primary key       |


### Registered Model Aliases Table

| **Fields**         | **Type**        | **Attributes**   |
|--------------------|-----------------|-------------------|
| *alias*            | `varchar(256)`  | primary key       |
| *version*          | `int`           |                   |
| *name*             | `varchar(256)`  | primary key       |


### Registered Model Tags Table

| **Fields**        | **Type**        | **Attributes**   |
|-------------------|-----------------|-------------------|
| *key*             | `varchar(250)`  | primary key       |
| *value*           | `varchar(5000)` |                   |
| *name*            | `varchar(256)`  | primary key       |


### Registered Models Table

| **Fields**           | **Type**        | **Attributes**   |
|----------------------|-----------------|-------------------|
| *name*               | `varchar(256)`  | primary key       |
| *creation_time*      | `bigint`        |                   |
| *last_updated_time*  | `bigint`        |                   |
| *description*        | `varchar(5000)` |                   |


### Runs Table

| **Fields**           | **Type**        | **Attributes**   |
|----------------------|-----------------|-------------------|
| *run_uuid*           | `varchar(32)`   | primary key       |
| *name*               | `varchar(250)`  |                   |
| *source_type*        | `varchar(20)`   |                   |
| *source_name*        | `varchar(500)`  |                   |
| *entry_point_name*   | `varchar(50)`   |                   |
| *user_id*            | `varchar(256)`  |                   |
| *status*             | `varchar(9)`    |                   |
| *start_time*         | `bigint`        |                   |
| *end_time*           | `bigint`        |                   |
| *source_version*     | `varchar(50)`   |                   |
| *lifecycle_stage*    | `varchar(20)`   |                   |
| *artifact_uri*       | `varchar(200)`  |                   |
| *experiment_id*      | `int`           |                   |
| *deleted_time*       | `bigint`        |                   |


### Tags Table

| **Fields**        | **Type**        | **Attributes**   |
|-------------------|-----------------|-------------------|
| *key*             | `varchar(250)`  | primary key       |
| *value*           | `varchar(5000)` |                   |
| *run_uuid*        | `varchar(32)`   | primary key       |


### Trace Info Table

| **Fields**        | **Type**        | **Attributes**   |
|-------------------|-----------------|-------------------|
| *request_id*      | `varchar(50)`   | primary key       |
| *experiment_id*   | `int`           |                   |
| *timestamp_ms*    | `bigint`        |                   |
| *execution_time_ms*| `bigint`       |                   |
| *status*          | `varchar(50)`   |                   |


### Trace Request Metadata Table

| **Fields**        | **Type**        | **Attributes**   |
|-------------------|-----------------|-------------------|
| *key*             | `varchar(250)`  | primary key       |
| *value*           | `varchar(8000)` |                   |
| *request_id*      | `varchar(50)`   | primary key       |


### Trace Tags Table

| **Fields**        | **Type**        | **Attributes**   |
|-------------------|-----------------|-------------------|
| *key*             | `varchar(250)`  | primary key       |
| *value*           | `varchar(8000)` |                   |
| *request_id*      | `varchar(50)`   | primary key       |


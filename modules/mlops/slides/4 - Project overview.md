# Tracking and Monitoring ML-enabled Microservices

## Overview
This project leverages MLflow for tracking machine learning experiments within a distributed microservices architecture, and uses EvidentlyAI for comprehensive monitoring.

Specifically, it performs the following:
- Grid search optimization of various machine learning models with different hyperparameters.
- Tracking and logging results as metadata.
- Custom retrieval of information about experiments and runs according to specific needs.
- Setting a chosen model as a microservice.
- Training and testing with this model.
- Simulating production data.
- Monitoring of model performance and data quality.

The system is composed of 5 fundamental containerized *microservices*:
- **PostgreSQL DB**: to *store* and *query* necessary data;
- **simulator**: to simulate *training*, *testing* and *production* data;
- **ML model**: a trainable model that can be tested on *data batches*;
- **tracking**: a service to log, retrieve, and filter metadata and results from experiments, providing flexible access to metrics, parameters, and performance data. 
- **monitoring**: to observe the model, allowing to compute reports and tests and trigger model re-training.



Each component is identified by a **containerized micro-service**, with its own dependencies and environment, following the typical *application layers* structure (except the *PostgreSQL DB*).

```
src
|-- config
|-- persistence
    |-- model
    |-- repository
|-- service
|-- utils
|-- web
    |-- controller
```

All *microservices* are developed using `Python`, based on [`Flask`](https://flask.palletsprojects.com/en/3.0.x/) framework and the [`Flask RESTful`](https://flask-restful.readthedocs.io/en/latest/) library to provide *REST APIs*.

```
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_sqlalchemy import SQLAlchemy
```

For each component, a `query.py` script (under the `src/persistence/repository` folder) is provided to *query* the *PostgresQL DB* for *storing* and *requesting* the proper data.

To reproduce the results, a `random seed` of [42](https://en.wikipedia.org/wiki/Phrases_from_The_Hitchhiker%27s_Guide_to_the_Galaxy) is set.

Each component can be finally run with a `Dockerfile` as an independent `Docker` *container*.


## PostgreSQL DB

The system uses two PostgreSQL databases to manage data and metadata:

1. **Dataset Database**: Stores the datasets required for ML model prediction tasks, including:
   - Training and validation data for optimization
   - Testing data for model evaluation  
   - Production simulation data

   The database contains tables for:
   - Datasets: Stores dataset metadata
   - Samples: Stores the actual wine samples with 13 features
   - Predictions: Records model predictions
   - Targets: Stores ground truth labels

2. **MLflow Metadata Database**: Automatically managed by MLflow to track experiment metadata:
   - Model information and versioning
   - Hyperparameters
   - Metrics and results
   - Run history and artifacts

   Key tables include:
   - Experiments: Tracks ML experiments
   - Runs: Individual training runs
   - Metrics: Performance metrics
   - Parameters: Model hyperparameters
   - Model Versions: Model versioning info

For full database schema details, see [Database.md](extras/Database.md)


## Simulator

The focus of this *micro-service* is to extrapolate and simulate:
- *training* set: 60% of full *dataset*;
- *testing* set: 20% of full *dataset*;
- *production* set: 20% of full *dataset*, where a fouling process is applied to simulate data with *missing value* and different *distribution*, causing a performance drop.

Starting from a *dataset*, this component allows loading to the **DB** the chosen set to simulate the different phases of a *ML model* life-cycle.

### Wine Dataset

The chosen *dataset* is the **Wines Dataset**, directly provided by the [`Scikit-Learn`](https://scikit-learn.org/stable/) (`SK-Learn`) library.
The [*Wine Dataset*](https://scikit-learn.org/stable/modules/generated/sklearn.datasets.load_wine.html) is a classic *toy dataset* that can be used for *multi-class classification* problems.

```
from sklearn.datasets import load_wine
```

All 13 *features* are *real* and *positive*, with no *missing values*, for a total of 178 *samples* and 3 different *classes* to predict.

About *Wine Dataset*:
> These data are the results of a chemical analysis of wines grown in the same region in Italy but derived from three different cultivars. The analysis determined the quantities of 13 constituents found in each of the three types of wines.

### Code Explanation

#### Simulator Model
```
import logging

from sklearn.datasets import load_wine

from persistence.model.i_model import IModel


class SimulatorModel(IModel):
    def __init__(self):
        self.logger = None
        self.df = None
        self.target_name = None
        self.columns_name = None
        self.X = None
        self.y = None
        self.name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        data, label = load_wine(return_X_y=True, as_frame=True)
        df = data.join(label.to_frame())
        self.target_name = "target"
        df.columns = [c.replace("/", "__") for c in df.columns]
        self.columns_name = df.drop([self.target_name], axis=1).columns
        self.y = df[self.target_name]
        self.X = df.drop(columns=self.target_name, axis=1)
        self.name = "DataSimulator"

```

The *Simulator model* performs the following operations:
- Loads the *Wine Dataset* into a [`Pandas`](https://pandas.pydata.org/) `DataFrame`, separating the *features* `X` and the *target* `y`
- Applies transformations to the `DataFrame` to prepare the data
- Sets the final `X` and `y` attributes with the processed data


#### Simulator Service
```
import logging
import numpy as np
import pandas as pd

from sklearn.model_selection import train_test_split

from service.i_service import IService
from persistence.model.i_model import IModel
from persistence.repository.impl.query import Query


class SimulatorService(IService):
    def __init__(self, simulator_model: IModel, db=None, seed=42):
        self.simulator_model = simulator_model
        self.db = db
        self.seed = seed

        self.logger = None
        self.query = None
        self.X_train = None
        self.X_test = None
        self.y_train = None
        self.y_test = None
        self.X_test_reference = None
        self.X_test_current = None
        self.y_test_reference = None
        self.y_test_current = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        np.random.seed(self.seed)
        self.init_query()

    def init_query(self):
        self.query = Query(self.db)
        self.query.samples_columns_name = self.simulator_model.columns_name

    def create_tables(self):
        self.query.create_datasets_table()
        self.query.create_samples_table()
        self.query.create_predictions_table()
        self.query.create_targets_table()

        self.query.describe_table("datasets")
        self.query.describe_table("samples")
        self.query.describe_table("targets")
        self.query.describe_table("predictions")

    def delete_records(self):
        self.query.delete_values("targets")
        self.query.delete_values("predictions")
        self.query.delete_values("samples")
        self.query.delete_values("datasets")

        self.query.select_value("datasets")
        self.query.select_value("samples")
        self.query.select_value("targets")
        self.query.select_value("predictions")

    def insert_records(self, step):
        if step == "dataset":
            self.datasets()
        elif step == "training":
            self.training()
        elif step == "testing":
            self.testing()
        elif step == "production":
            self.production()

    def datasets(self):
        self.define_datasets()

    def training(self):
        self.define_training_set()
        self.load_training_set()

    def testing(self):
        self.define_testing_set()
        self.load_testing_set()

    def production(self):
        self.define_production_set()
        self.load_production_set()

    def define_datasets(self):
        records = {"dataset_id": [1, 2, 3], "name": ["training", "testing", "production"]}
        records = pd.DataFrame(data=records)
        self.query.insert_dataset_records(records)

        self.query.select_value("datasets")

    def define_training_set(self):
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(
            self.simulator_model.X, self.simulator_model.y,
            train_size=0.6,
            random_state=self.seed,
            stratify=self.simulator_model.y
        )

    def define_testing_set(self):
        self.X_test_reference, self.X_test_current, self.y_test_reference, self.y_test_current = train_test_split(
            self.X_test, self.y_test,
            train_size=0.5,
            random_state=self.seed,
            stratify=self.y_test
        )

    def define_production_set(self):
        self.X_test_current = self.X_test_current + np.random.normal(0, 0.3)
        for col in self.X_test_current.columns:
            self.X_test_current.loc[self.X_test_current.sample(frac=0.03).index, col] = "null"

    def load_training_set(self):
        records = self.X_train
        records["dataset_id"] = 1
        records["sample_index"] = records.index
        self.query.insert_samples_records(records)

        records = self.y_train.to_frame(name="class")
        records["sample_index"] = records.index
        records["target_index"] = records.index
        self.query.insert_targets_records(records)

        self.query.select_condition_value("samples", 1)

    def load_testing_set(self):
        records = self.X_test_reference
        records["dataset_id"] = 2
        records["sample_index"] = records.index
        self.query.insert_samples_records(records)

        records = self.y_test_reference.to_frame(name="class")
        records["sample_index"] = records.index
        records["target_index"] = records.index
        self.query.insert_targets_records(records)

        self.query.select_condition_value("samples", 2)

    def load_production_set(self):
        records = self.X_test_current
        records["dataset_id"] = 3
        records["sample_index"] = records.index
        self.query.insert_samples_records(records)

        records = self.y_test_current.to_frame(name="class")
        records["sample_index"] = records.index
        records["target_index"] = records.index
        self.query.insert_targets_records(records)

        self.query.select_condition_value("samples", 3)

```

The *Simulator service* allows to:
- create the different *tables* inside the *PostgreSQL DB*, specifically the **datasets**, **samples**, **targets** and **predictions** tables;
- load immediately the *dataset records* on startup;
- wait for *requests* to insert *samples* and *targets*, which define the *training*, *testing* or *production* sets in the *PostgreSQL DB*.

#### Simulator Controller
```
import logging
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_sqlalchemy import SQLAlchemy

from service.i_service import IService
from web.controller.i_controller import IController


class SimulatorController(IController):
    def __init__(
            self,
            simulator_service: IService,
            app_host="127.0.0.1", app_port=5004, app_debug=False, base_url="/root", service_url="/simulator",
            db_host="127.0.0.1", db_port=5432, db_name="", db_user="root", db_password="pass"
    ):
        self.simulator_service = simulator_service
        self.app_host = app_host
        self.app_port = app_port
        self.app_debug = app_debug
        self.base_url = base_url
        self.service_url = service_url
        self.db_host = db_host
        self.db_port = db_port
        self.db_name = db_name
        self.db_user = db_user
        self.db_password = db_password

        self.logger = None
        self.app = None
        self.api = None
        self.parser = None
        self.db = None

        self.initialize()

    def initialize(self):
        self.app = Flask(__name__)
        self.app.config["SQLALCHEMY_DATABASE_URI"] = f"postgresql://{self.db_user}:{self.db_password}@{self.db_host}:{self.db_port}/{self.db_name}"
        self.app.app_context().push()
        self.api = Api(self.app)
        self.parser = reqparse.RequestParser()
        self.db = SQLAlchemy(self.app)
        self.logger = logging.getLogger("werkzeug")

        self.simulator_service.db = self.db
        self.simulator_service.init_query()

        self.add_resource()
        self.set_db()

        '''self.simulator_service.insert_records("training")
        self.simulator_service.insert_records("testing")
        self.simulator_service.insert_records("production")'''

    def add_resource(self):
        self.api.add_resource(
            Simulator,
            "".join([self.base_url, self.service_url]),
            resource_class_kwargs={
                "parser": self.parser,
                "db": self.db,
                "simulator_service": self.simulator_service,
            }
        )

    def set_db(self):
        self.simulator_service.create_tables()
        self.simulator_service.delete_records()

        self.simulator_service.insert_records("dataset")

    def run(self):
        self.app.run(host=self.app_host, port=self.app_port, debug=self.app_debug)


class Simulator(Resource):
    def __init__(self, parser, db, simulator_service):
        self.parser = parser
        self.db = db
        self.simulator_service = simulator_service

        self.initialization()

    def initialization(self):
        self.parser.add_argument("set_name")

    def get(self):
        payload = {
            "name": self.simulator_service.simulator_model.name,
            "message": "Simulator"
        }
        return payload

    def post(self):
        args = self.parser.parse_args()
        set_name = args["set_name"]
        self.simulator_service.insert_records(set_name)
        payload = {
            "message": "Load {} set".format(set_name)
        }
        return payload

```

The *Simulator controller* allows:
- building the `Flask` *App* and the *REST API*
- instantiating a connection to the *PostgreSQL DB*
- mapping the `Resource` to the correct *endpoint*, allowing definition of proper behavior for every **CRUD** operation
- defining the arguments to *parse* inside the *request* body for each `Resource`


## ML Model

The focus of this *microservice* is to have an *ML model* capable of being trained and tested over new data.


### Model

Initially, no specific ML model is set. However, a model will be configured later through the tracking service via an API. We have selected three potential models that can be implemented:

-  [`RandomForestClassifier`](https://scikit-learn.org/stable/modules/generated/sklearn.ensemble.RandomForestClassifier.html)
-  [`SVC`](https://scikit-learn.org/stable/modules/generated/sklearn.svm.SVC.html)
-  [`LogisticRegression`](https://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LogisticRegression.html)

This selection is extendable, allowing for the incorporation of additional models as needed.


```
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC
from sklearn.linear_model import LogisticRegression
```

### Data Pre-Processing

The only data pre-processing step is applying a [`Standard Scaler`](https://scikit-learn.org/stable/modules/generated/sklearn.preprocessing.StandardScaler.html), fitted on the *training* set, to transform the data before passing it to the *ML model*.

```
from sklearn.preprocessing import StandardScaler
```

### Code Explanation

#### ML Model Model
```
import logging
from sklearn.tree import DecisionTreeClassifier

from persistence.model.i_model import IModel


class MLModel(IModel):
    def __init__(self, seed=42):
        self.seed = seed

        self.logger = None
        self.model = None
        self.name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        
```


#### ML Model Service
```
import logging
import numpy as np
import pandas as pd

from sklearn.preprocessing import StandardScaler
from sklearn.metrics import accuracy_score

from service.i_service import IService
from persistence.model.i_model import IModel
from persistence.repository.impl.query import Query


class MLService(IService):
    def __init__(self, ml_model: IModel, db=None, seed=42):
        self.ml_model = ml_model
        self.db = db
        self.seed = seed
        self.available_models = {
            "RandomForest": RandomForestClassifier,
            "SVC": SVC,
            "LogisticRegression": LogisticRegression
        }

        self.logger = None
        self.query = None
        self.standard_scaler = None
        self.prediction = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        self.standard_scaler = StandardScaler()
        self.init_query()

    def init_query(self):
        self.query = Query(self.db)
        #self.query.samples_columns_name = self.ml_model.columns_name

    def select_records(self, step):
        score = 0
        if step == "training":
            score = self.training()
        elif step == "testing":
            score = self.testing()
        elif step == "production":
            score = self.production()

        return score

    def training(self):
        records = self.query.select_joined_conditioned_value(
            "samples", "targets", "sample_index", "sample_index", 1
        )

        X, y, = self.define_set(records)
        self.pre_processing(X)
        self.train(X, y)
        y_pred = self.test(X)
        score = self.score(y, y_pred)
        print("score train", score*100)

        self.load_prediction(y_pred)

        return score

    def testing(self):
        records = self.query.select_joined_conditioned_value(
            "samples", "targets", "sample_index", "sample_index", 2
        )

        X, y, = self.define_set(records)
        self.pre_processing(X, fit=False)
        y_pred = self.test(X)
        score = self.score(y, y_pred)
        print("score test", score*100)

        self.load_prediction(y_pred)

        return score

    def production(self):
        records = self.query.select_joined_conditioned_value(
            "samples", "targets", "sample_index", "sample_index", 3
        )

        X, y, = self.define_set(records)
        self.pre_processing(X, fit=False)
        y_pred = self.test(X)
        score = self.score(y, y_pred)
        print("score prod", score*100)

        self.load_prediction(y_pred)

        return score

    def define_set(self, records):
        records = pd.DataFrame(records)
        columns = records.columns.to_list()
        # Rename the second occurrence of sample_index to avoid duplication
        sample_index_idx = [i for i, s in enumerate(columns) if "sample_index" in s][1]
        columns[sample_index_idx] = "targets.sample_index"
        records.columns = columns

        self.prediction = records[["sample_index"]].copy()
        records = records.drop(columns=[
            "dataset_id", "sample_id", "target_id",
            "sample_index", "target_index", "targets.sample_index"
        ])
        records.fillna(records.mean(), inplace=True)
        X = records.drop(columns=["class"])
        y = records["class"]
        return X, y

    def pre_processing(self, X, fit=True):
        if fit:
            self.standard_scaler.fit(X)
        X_np = self.standard_scaler.transform(X)
        return X_np

    def train(self, X, y):
        self.ml_model.model.fit(X, y)

    def test(self, X):
        y = self.ml_model.model.predict(X)
        return y

    @staticmethod
    def score(target, prediction):
        score = accuracy_score(target, prediction)
        return score

    def load_prediction(self, y_pred):
        records = self.prediction
        records["prediction_index"] = records["sample_index"]
        records["class"] = y_pred
        self.query.insert_predictions_records(records)
```

The *ML Model service*:
- Waits for requests to train or test the *ML model* on the requested dataset, properly selecting samples and targets from the *PostgreSQL DB*
- Returns the accuracy score of predictions made on the requested dataset and loads them into the *PostgreSQL DB*

#### ML Model Controller
```
import logging
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_sqlalchemy import SQLAlchemy

from service.i_service import IService
from web.controller.i_controller import IController


class MLController(IController):
    def __init__(
            self,
            ml_service: IService,
            app_host="127.0.0.1", app_port=5004, app_debug=False, base_url="/root", service_url="/model",
            db_host="127.0.0.1", db_port=5432, db_name="", db_user="root", db_password="pass"
    ):
        self.ml_service = ml_service
        self.app_host = app_host
        self.app_port = app_port
        self.app_debug = app_debug
        self.base_url = base_url
        self.service_url = service_url
        self.db_host = db_host
        self.db_port = db_port
        self.db_name = db_name
        self.db_user = db_user
        self.db_password = db_password

        self.logger = None
        self.app = None
        self.api = None
        self.parser = None
        self.db = None

        self.initialize()

    def initialize(self):
        self.app = Flask(__name__)
        self.app.config["SQLALCHEMY_DATABASE_URI"] = f"postgresql://{self.db_user}:{self.db_password}@{self.db_host}:{self.db_port}/{self.db_name}"

        self.app.app_context().push()
        self.api = Api(self.app)
        self.parser = reqparse.RequestParser()
        self.db = SQLAlchemy(self.app)
        self.logger = logging.getLogger("werkzeug")

        self.ml_service.db = self.db
        self.ml_service.init_query()

        self.add_resource()


    def add_resource(self):
        self.api.add_resource(
            Model,
            "".join([self.base_url, self.service_url]),
            resource_class_kwargs={
                "parser": self.parser,
                "db": self.db,
                "ml_service": self.ml_service,
            }
        )

        self.api.add_resource(
            SetModel,
            "".join([self.base_url, self.service_url, "/set_model"]),
            resource_class_kwargs={
                "parser": self.parser,
                "ml_service": self.ml_service,
            }
        )

    def run(self):
        self.app.run(host=self.app_host, port=self.app_port, debug=self.app_debug)


class Model(Resource):
    def __init__(self, parser, db, ml_service: IService):
        self.parser = parser
        self.db = db
        self.ml_service = ml_service

        self.initialization()

    def initialization(self):
        self.parser.add_argument("dataset")

    def get(self):
        payload = {
            "name": self.ml_service.ml_model.name,
            "message": "ML model"
        }
        return payload

    def post(self):
        args = self.parser.parse_args()
        dataset = args["dataset"]
        score = self.ml_service.select_records(dataset)
        payload = {
            "message": "Score on {} set: {}%".format(dataset, score*100)
        }
        return payload
```

The *ML Model controller* allows us to:
- Build the `Flask` *App* and *REST API*
- Instantiate a connection to the *PostgreSQL DB* 
- Map the `Resource` to the correct *endpoint*, defining proper behavior for every **CRUD** operation
- Define arguments to parse from the request body for each `Resource`

Additionally, a class was created to set specific models and parameters through the request body, as shown below:
```
class SetModel(Resource):
    def __init__(self, parser, ml_service: IService):
        self.parser = parser
        self.ml_service = ml_service

        self.initialization()

    def initialization(self):
        self.parser.add_argument("model_name", type=str, location='json', help="Name of ML model")
        self.parser.add_argument("hyperparameters", type=dict, location='json', help="hyperparameters of model")

    def post(self):
        args = self.parser.parse_args()
        model_name = args["model_name"]
        hyperparameters = args["hyperparameters"]

        try:
            model = self.ml_service.load_model(model_name, hyperparameters)
            if model is None:
                return {
                    "error": "Model not Found",
                    "message": "Please Specify 'RandomForest', 'SVC' or 'LogisticRegression'"
                }, 400

            return {
                "message": "Model successfully uploaded"
            }, 200

        except KeyError as ke:
            missing_param = str(ke).strip("'")
            return {
                "error": f"Missing parameter: {missing_param}",
                "message": f"The parameter '{missing_param}' is required but was not provided"
            }, 400

        except ValueError as ve:
            return {
                "error": str(ve),
                "message": "Invalid value provided"
            }, 400

        except Exception as e:
            return {
                "error": str(e),
                "message": "An unexpected error occurred"
            }, 500
```

The request will simply contain the name of the model and the hyperparameters to be used.

## Tracking

The *Tracking* microservice was developed for:

1. **Hyperparameter optimization**.
2. **Cross-validated** model performance evaluation
3. Creation of Machine learning **Experiments**
4. **Logging** of Runs Metadata and artifact in MLflow Database
5. **Retrieving** filtered information from MLflow Database

### **MLflow**

MLflow provides **functions** and **APIs** for easily logging all metadata and artifacts associated with the ML experiments.

Each experiment tracked with **MLflow** is stored in the centralized **MLflow Database** where the following can be logged:

- **Experiments Data**
- **Run Details**
- **Parameters** such as:
  - **Best Model Parameters** from grid search hyperparameter tuning process
  - **Grid Search Parameters** including configurations like:
    - `cv`: cross-validation splits
    - `scoring`: to evaluate each combination of parameters.
  
  - **Dataset Parameters**
    - Number of classes
    - Number of samples
    - Number of features


- **Metrics** such as:
  - **Cross-Validation Performance Metrics**:
    - Accuracy
    - Precision 
    - Recall, 
    - F1 Score
  
  - **System Performance Metrics**:
    - CPU Consumption
    - Execution Time

  

### Code Explanation

#### Tracking Service

1. **Imports and Dependencies**

```
import logging
import psutil
import time
from datetime import datetime

import pandas as pd
import numpy as np
import mlflow
import mlflow.sklearn

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

from src.service.i_service import IService
from src.persistence.model.i_model import IModel
from src.persistence.repository.impl.query import Query

```
2. **Class Initialization**
```
class TrackingService(IService):
    def __init__(self, tracking_model: IModel, db=None):

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
```
- Sets up a logger for event tracking
- Assigns the MLflow object for tracking experiments 
- Initializes the query configuration by calling init_query()
- Stores a PostgreSQL database connection instance in self.db

3. **Model Optimization and Tracking**
```
    def track_optimization_info(self, models, hyperparameters, scoring, cv, experiment_name, description, owner):

        training_records = self.query.select_joined_conditioned_value("samples", "targets", "sample_index",
                                                                      "sample_index", 1)
        X, y = self.define_set(training_records)
        results = self.perform_optimization(X, y, models, hyperparameters, scoring, cv)
        id_runs = self.log_to_mlflow(results, experiment_name, X, y, scoring, cv, hyperparameters, description, owner)
        return id_runs
```
This function performs one of the main tasks. The operations performed are:
- 3.1 **Selection of trainig data records**
- 3.2 **Splits data into X (features) and y (target)**
```
    def define_set(self, records):
        
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
```

- 3.3 **Executes grid search for model tuning and returns metrics and parameters**

```
    def perform_optimization(self, X, y, models, hyperparameters, scoring, cv):
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
        
    def compute_metrics(self, y_true, y_pred):
    metrics = {'accuracy': accuracy_score(y_true, y_pred), 'f1_score': f1_score(y_true, y_pred, average='weighted'),
               'precision': precision_score(y_true, y_pred, average='weighted'),
               'recall': recall_score(y_true, y_pred, average='weighted')}
    return metrics
```
- Sets up a grid search for each model
- Trains the model using the provided data and hyperparameters
- Identifies the best-performing model
- Evaluates the best model's performance through cross-validation predictions
- Computes performance metrics


- 3.4 **Logging in MLflow Database**

```
    def log_to_mlflow(self, models_results, experiment_name, X_train, y_train, scoring_grid_search, cv, param_grid,
                      description, owner):

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
```

- Creates MLflow Experiment (Checks for Existing Experiment)

- For Each Best Model starts MLflow Run
  - Sets tags for the experiment type and model type

- Logs Model:
    - Infers the model's signature and logs the model

- Logs various parameters, including:
  - Best parameters for the model
  - Data shape and classes
  - Grid search parameters and scoring method

- Logs metrics related to: model performance, CPU usage, and execution time

- Stores Run ID in `run_id_dict`
- Returns the `run_id_dict` containing all run IDs


4. **Information retrieval**

```
    def get_experiment_info(self, experiment, experiment_runs):
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
```
- Gathers experiment details and statistics
```
    def get_best_model(self, experiment_runs, filter_req):
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
```
- Identifies the best model among runs of an experiment based on specified metrics in `filter_req`
- returning the ID and metrics of the optimal run.
```
    def get_statistics(self, experiment_runs, filter_req):
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
        
    def calculate_statistics(self, data):
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
```
- Calculates descriptive statistics (mean, median, standard deviation, variance, minimum, maximum, mode, interquartile range)
- Returns a dictionary containing the calculated statistics for the collected metrics

```
    def get_experiment_runs(self, experiment_id):
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
```
 - Retrieves runs associated with the provided experiment ID and extracts key information from each run.

```
    def get_run_parameters(self, run, parameter_type):
        run_params = run.data.params
        return {
            key.replace(f"{parameter_type}_", ""): value
            for key, value in run_params.items()
            if key.startswith(parameter_type)
        }
```
- Extracts parameters from a specific run
- Returns a dictionary with the filtered parameters, removing the prefix specified by `parameter_type`
```
    def get_run_metrics(self, run, metric_type):
        run_metrics = run.data.metrics
        return {
            key.replace(f"{metric_type}_", ""): value
            for key, value in run_metrics.items()
            if key.startswith(metric_type)
        }
```
- Extracts metrics from a specific run.
- Returns a dictionary with the filtered metrics, removing the prefix specified by `metric_type`.

```
    def track_model_setting(self, run):
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
```

```

def identify_value_type(value):
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
        models_dict = {
            "LogisticRegression": LogisticRegression(),
            "RandomForest": RandomForestClassifier(),
            "SVC": SVC(),
        }
        return models_dict.get(model_name)

```
Additional functions used to set model in the **ML** microservice

#### Tracking Controller
```
import logging
import json
import requests
from flask import Flask, request
from flask_restful import Api, Resource, reqparse
from flask_sqlalchemy import SQLAlchemy
from mlflow import MlflowException

from src.service.i_service import IService
from src.web.controller.i_controller import IController


class TrackingController(IController):
    def __init__(self,
                 tracking_service: IService,
                 app_host="127.0.0.1", app_port=5003, app_debug=False, base_url="/root", service_url="/tracking",
                 db_dataset_host="127.0.0.1", db_dataset_port=5432, db_dataset_name="", db_dataset_user="root",
                 db_dataset_password=None,
                 db_mlflow_host="127.0.0.1", db_mlflow_port=5432, db_mlflow_name="", db_mlflow_user="root",
                 db_mlflow_password=None,
                 ml_host="127.0.0.1", ml_port=5001, ml_service_url="/ml"):

        self.tracking_service = tracking_service
        self.app_host = app_host
        self.app_port = app_port
        self.app_debug = app_debug
        self.base_url = base_url
        self.service_url = service_url

        self.db_dataset_host = db_dataset_host
        self.db_dataset_port = db_dataset_port
        self.db_dataset_user = db_dataset_user
        self.db_dataset_password = db_dataset_password
        self.db_dataset_name = db_dataset_name

        self.db_mlflow_host = db_mlflow_host
        self.db_mlflow_port = db_mlflow_port
        self.db_mlflow_user = db_mlflow_user
        self.db_mlflow_password = db_mlflow_password
        self.db_mlflow_name = db_mlflow_name

        self.ml_host = ml_host
        self.ml_service_url = ml_service_url
        self.ml_port = ml_port
        self.ml_service_uri = None

        self.app_host = app_host
        self.app_port = app_port
        self.app_debug = app_debug
        self.base_url = base_url
        self.service_url = service_url

        self.logger = None
        self.app = None
        self.api = None
        self.parser = None
        self.db_dataset = None

        self.initialize()

    def initialize(self):
        self.app = Flask(__name__)

        self.app.config["SQLALCHEMY_DATABASE_URI"] = f"postgresql://{self.db_dataset_user}:{self.db_dataset_password}@{self.db_dataset_host}:{self.db_dataset_port}/{self.db_dataset_name}"

        self.app.app_context().push()
        self.api = Api(self.app)
        self.parser = reqparse.RequestParser()

        self.db_dataset = SQLAlchemy(self.app)
        self.logger = logging.getLogger("werkzeug")

        self.tracking_service.db = self.db_dataset

        mlflow_uri = f"postgresql://{self.db_mlflow_user}:{self.db_mlflow_password}@{self.db_mlflow_host}:{self.db_mlflow_port}/{self.db_mlflow_name}"
        self.tracking_service.mlflow.set_tracking_uri(mlflow_uri)

        self.ml_service_uri = f"http://{self.ml_host}:{self.ml_port}{self.base_url}{self.ml_service_url}"

        self.tracking_service.init_query()

        self.add_resource()
```

- **Import Libraries:** Import modules for logging, request handling, PostgreSQL interaction, and MLflow.

- **Initialize Parameters:**
  - Set host, ports, database credentials, and ML service settings.

- **Configure Flask App:**
  - Create a Flask application and set up the PostgreSQL database configuration.

- **Initialize API and Parser:**
  - Set up a RESTful API and a request parser.

- **Set Up Database:**
  - Create an instance of the PostgreSQL database and link it to the tracking service.

- **Configure MLflow:**
  - Set the tracking URI for MLflow using the provided credentials.

- **Set Up ML Service:**
  - Construct the URI to access the machine learning service.

- **Initialize Query in Tracking Service:**
  - Invoke a method to initialize queries in the tracking service.

- **Add Resources to the API**

```
    def add_resource(self):
        self.api.add_resource(
            TrackingExperiment,
            "".join([self.base_url, self.service_url, "/experiments"]),
            resource_class_kwargs={
                "parser": self.parser,
                "tracking_service": self.tracking_service,
            }
        )

        self.api.add_resource(
            TrackingRuns,
            "".join([self.base_url, self.service_url, "/runs"]),
            resource_class_kwargs={
                "parser": self.parser,
                "tracking_service": self.tracking_service,
            }
        )

        self.api.add_resource(
            Optimization,
            "".join([self.base_url, self.service_url, "/model_management"]),
            resource_class_kwargs={
                "parser": self.parser,
                "db_dataset": self.db_dataset,
                "tracking_service": self.tracking_service,
                "ml_service_uri": self.ml_service_uri
            }
        )

    def run(self):
        self.app.run(host=self.app_host, port=self.app_port, debug=self.app_debug)
```
  - Adds three resources:
    - TrackingExperiment Resource
    - TrackingRuns Resource  
    - Optimization Resource

  - Passes required dependencies to each resource

  - Starts the Flask application with the `run` method:
    - Makes the application accessible on the configured host and port

```
class Optimization(Resource):
    def __init__(self, parser, db_dataset, ml_service_uri, tracking_service: IService):

        self.parser = parser
        self.db_dataset = db_dataset
        self.tracking_service = tracking_service
        self.ml_service_uri = ml_service_uri

        self.initialization()

    def initialization(self):

        self.parser.add_argument("models", type=list, location='json', help="List of models")
        self.parser.add_argument("hyperparameters", type=dict, location='json', help="Hyperparameters for models")
        self.parser.add_argument("scoring", type=str, location='json', help="Scoring metric")
        self.parser.add_argument("cv", type=int, location='json', help="Number of cross-validation folds")
        self.parser.add_argument("experiment_name", type=str, location='json', help="Name of the experiment")
        self.parser.add_argument("experiment_description", type=str, location='json',
                                 help="Description of the experiment")
        self.parser.add_argument("experiment_owner", type=str, location='json', help="Owner of the experiment")

        self.parser.add_argument("experiment_id", type=str, location='json', help="Experiment Id")
        self.parser.add_argument("run_id", type=str, location='json', help="Run Id")
        self.parser.add_argument("request_information", type=str, location='json', help="Type of Request")

    def post(self):
        try:
            args = self.parser.parse_args()
            models = args["models"]
            hyperparameters = args["hyperparameters"]
            scoring = args["scoring"]
            cv = args["cv"]
            request_info = args.get("request_information")

            experiment_name = args["experiment_name"]
            experiment_description = args["experiment_description"]
            experiment_owner = args["experiment_owner"]

            if request_info == "setting":
                run_id = args.get("run_id")

                if not run_id:
                    return {
                        "error": "Run ID is Missing.",
                        "message": "Please Specify run_id Parameters."
                    }, 400

                run = self.tracking_service.mlflow.get_run(run_id)
                if run.info.lifecycle_stage == "deleted":
                    return {"error": f"Run with ID {run_id} has been deleted."}, 404

                model_info = self.tracking_service.track_model_setting(run)

                response = requests.post(self.ml_service_uri + "/set_model", json.dumps(model_info),
                                         headers={"Content-Type": "application/json"})

                key, value = next(iter(response.json().items()))

                return {
                    key: value
                }, response.status_code


            elif request_info == "optimization":

                ids_models = self.tracking_service.track_optimization_info(models, hyperparameters, scoring, cv,
                                                                           experiment_name, experiment_description,
                                                                           experiment_owner)
                return {
                    "message": "Experiments retrieved successfully",
                    "data": ids_models
                }, 201

            else:
                return {
                    "error": "",
                    "message": "Invalid request_info provided"
                }, 400

        except MlflowException as mlflow_error:
            return {"error": "MLflow error", "message": str(mlflow_error)}, 500

        except ValueError as ve:
            return {
                "error": str(ve),
                "message": "Invalid value provided"
            }, 400

        except KeyError as ke:
            return {
                "error": f"Missing parameter: {str(ke)}",
                "message": "Missing parameter"
            }, 400

        except Exception as e:
            return {
                "error": str(e),
                "message": "An unexpected error occurred"
            }, 500

```
The `Optimization` endpoint is designed for model management and optimization and includes two main operations:

- **setting**: Allows selecting a model via `run_id` and setting it in the machine learning service
- **optimization**: Manages optimization information for different models, parameters, and scoring metrics and records new optimization experiments

```
class TrackingExperiment(Resource):
    def __init__(self, parser, tracking_service):
        self.parser = parser
        self.tracking_service = tracking_service

        self.initialization()

    def initialization(self):

        self.parser.add_argument("experiment_id", type=str, location='json', help="Experiment Id")
        self.parser.add_argument("request_information", type=str, location='json', help="Type of Request")
        self.parser.add_argument("filter", type=list, location='json', help="Filter for Request Information")

    def get(self):

        try:
            experiments = self.tracking_service.mlflow.search_experiments()
            experiments_list = []

            for exp in experiments:
                runs = self.tracking_service.mlflow.search_runs(experiment_ids=[exp.experiment_id])
                run_count = len(runs)

                experiments_list.append({
                    "experiment_id": exp.experiment_id,
                    "name": exp.name,
                    "lifecycle_stage": exp.lifecycle_stage,
                    "run_count": run_count
                })

            return {
                "message": "Experiments retrieved successfully",
                "data": experiments_list
            }, 200

        except RuntimeError as e:
            return {
                "error": str(e),
                "message": "Failed to retrieve experiments."
            }, 500

        except MlflowException as mlflow_error:
            return {"error": "MLflow error", "message": str(mlflow_error)}, 500

        except Exception as e:
            return {
                "error": str(e),
                "message": "An unexpected error occurred while retrieving experiments."
            }, 500

    def post(self):
        args = self.parser.parse_args()
        experiment_id = args.get("experiment_id")
        request_info = args.get("request_information")
        filter_req = args.get("filter")

        if not experiment_id or not request_info:
            return {
                "error": "Experiment ID and/or Request Info are Missing",
                "message": "Please Specify experiment_id and request_info Parameters"
            }, 400

        try:

            experiment = self.tracking_service.mlflow.get_experiment(experiment_id)
            if not experiment:
                return {
                    "error": "Experiment not found",
                    "message": f"No experiment found with the ID {experiment_id}"
                }, 400

            experiment_runs = self.tracking_service.mlflow.search_runs([experiment_id])
            if experiment_runs.empty:
                return {
                    "error": "No runs found",
                    "message": f"No runs associated with the experiment ID {experiment_id}"
                }, 404

            if request_info == "general":
                experiment_info = self.tracking_service.get_experiment_info(experiment, experiment_runs)

                return {
                    "message": "Information Retrieved",
                    "data": experiment_info
                }, 200

            elif request_info == "best_model":
                if not filter_req or len(filter_req) == 0:
                    return {
                        "error": "At least one filter (metric) is required.",
                        "message": "Please specify one of these: 'model_accuracy', 'model_f1_score', 'model_precision',"
                                   "'model_recall', 'system_cpu_usage', 'system_execution_time'"
                    }, 400

                best_run_info = self.tracking_service.get_best_model(experiment_runs, filter_req)

                if not best_run_info:
                    return {
                        "message": "Best Run Not Found"
                    }, 404

                return {
                    "message": "Best model and run retrieved successfully.",
                    "data": best_run_info
                }, 200

            elif request_info == "statistics":
                stats = self.tracking_service.get_statistics(experiment_runs, filter_req)
                return {"message": "Statistics Retrieved", "data": stats}, 200

            else:
                return {
                    "error": "Invalid request_information",
                    "message": "Please specify 'parameters', 'metrics', or 'best_model'."
                }, 400

        except MlflowException as mlflow_error:
            return {"error": "MLflow error", "message": str(mlflow_error)}, 500

        except KeyError as ke:
            missing_param = str(ke).strip("'")
            return {
                "error": f"Missing parameter: {missing_param}",
                "message": f"The parameter '{missing_param}' is required but was not provided"
            }, 400

        except ValueError as ve:
            return {
                "error": str(ve),
                "message": "Invalid value provided"
            }, 400

        except Exception as e:
            return {
                "error": str(e),
                "message": "An unexpected error occurred"
            }, 500

    def delete(self):
        try:
            experiment_id = request.args.get("experiment_id")
            experiment = self.tracking_service.mlflow.get_experiment(experiment_id)
            if not experiment:
                return {"error": f"Experiment with ID {experiment_id} does not exist."}, 404

            self.tracking_service.mlflow.delete_experiment(experiment_id)
            return {"message": f"Experiment {experiment_id} deleted successfully."}, 200

        except MlflowException as mlflow_error:
            return {"error": "MLflow error", "message": str(mlflow_error)}, 500

        except Exception as e:
            return {"error": "An unexpected error occurred.", "message": str(e)}, 500
```

The `TrackingExperiment` endpoint manages operations on experiments. It provides endpoints for:

- **GET**: Retrieves a list of experiments, including basic information such as `experiment_id`, `name`, and `run_count`.
- **POST**: Retrieves details of a specific experiment, such as general information, the best model, or aggregate statistics, 
depending on the type of request.
- **DELETE**: Deletes an experiment specified by `experiment_id`.
```

class TrackingRuns(Resource):
    def __init__(self, parser, tracking_service):
        self.parser = parser
        self.tracking_service = tracking_service
        self.initialization()

    def initialization(self):
        self.parser.add_argument("experiment_id", type=str, location='json', help="Experiment Id")
        self.parser.add_argument("run_id", type=str, location='json', help="Run Id")
        self.parser.add_argument("request_information", type=str, location='json', help="Type of Request")
        self.parser.add_argument("type", type=str, location='json',
                                 help="Type of Data required: 'data', 'grid_search', 'model' for Parameters and "
                                      "'system', 'model' for Metrics")

    def get(self):
        experiment_id = request.args.get("experiment_id")

        if not experiment_id:
            return {
                "error": "Experiment ID is Missing",
                "message": "Please Specify 'experiment_id' Parameter."
            }, 400

        try:
            runs_list = self.tracking_service.get_experiment_runs(experiment_id)

            if not runs_list:
                return {
                    "error": f"No runs found for experiment ID: {experiment_id}."
                }, 404

            return {
                "message": "Experiment Runs Retrieved",
                "data": runs_list
            }, 200

        except MlflowException as mlflow_error:
            return {"error": "MLflow error",
                    "message": str(mlflow_error)}, 500

        except Exception as e:
            return {
                "error": str(e),
                "message": "An unexpected error occurred"
            }, 500

    def post(self):

        args = self.parser.parse_args()
        experiment_id = args.get("experiment_id")
        run_id = args.get("run_id")
        request_info = args.get("request_information")

        if not experiment_id or not run_id or not request_info:
            return {
                "error": "Experiment ID and/or Request Info are Missing.",
                "message": "Please Specify experiment_id and request_info Parameters."
            }, 400

        try:
            run = self.tracking_service.mlflow.get_run(run_id)
            if run.info.lifecycle_stage == "deleted":
                return {"error": f"Run with ID {run_id} has been deleted."}, 404

            if request_info == "parameters":
                parameter_type = args.get("type")
                if parameter_type not in ["data", "grid_search", "model"]:
                    return {
                        "error": "Invalid value for 'type' Parameter.",
                        "message": "Please specify 'data', 'grid_search' or 'model'"
                    }, 400

                filtered_params = self.tracking_service.get_run_parameters(run, parameter_type)

                return {
                    "message": "Parameters Retrieved",
                    "data": filtered_params,
                }, 200

            elif request_info == "metrics":
                metric_type = args.get("type")
                if metric_type not in ["system", "model"]:
                    return {
                        "error": "Invalid value for 'type' Parameter.",
                        "message": "Please specify 'system' or 'model'"
                    }, 400

                filtered_metrics = self.tracking_service.get_run_metrics(run, metric_type)

                return {
                    "message": "Parameters Retrieved",
                    "data": filtered_metrics
                }, 200

            else:
                return {
                    "error": "Invalid info type. Please specify 'parameters' or 'metrics'."
                }, 400

        except KeyError as ke:
            missing_param = str(ke).strip("'")
            return {
                "error": f"Missing parameter: {missing_param}",
                "message": f"The parameter '{missing_param}' is required but was not provided"
            }, 400

        except MlflowException as mlflow_error:
            return {"error": "MLflow error", "message": str(mlflow_error)}, 500

        except ValueError as ve:
            return {
                "error": str(ve),
                "message": "Invalid value provided"
            }, 400

        except Exception as e:
            return {
                "error": str(e),
                "message": "An unexpected error occurred"
            }, 500

    def delete(self):
        run_id = request.args.get("run_id")
        experiment_id = request.args.get("experiment_id")

        try:
            experiment = self.tracking_service.mlflow.get_experiment(experiment_id)
            if not experiment:
                return {"error": f"Experiment with ID {experiment_id} does not exist."}, 404

            run = self.tracking_service.mlflow.get_run(run_id)

            if run.info.lifecycle_stage == "deleted":
                return {"error": f"Run with ID {run_id} has already been  deleted."}, 404

            self.tracking_service.mlflow.delete_run(run_id)
            return {"message": f"Run {run_id} deleted successfully."}, 200

        except MlflowException as mlflow_error:
            return {"error": "MLflow error", "message": str(mlflow_error)}, 500

        except Exception as e:
            return {"error": "An unexpected error occurred.", "message": str(e)}, 500

```

`TrackingRuns` Handles operations on executions (`runs`) of experiments. It offers the following methods:

- **GET**: Retrieves all runs for a specific experiment, identified by `experiment_id`
- **POST**: Retrieves the parameters and metrics of a specific run (`run_id`) based on the type of request (e.g., `parameters` or `metrics`)
- **DELETE**: Deletes a specific run using `run_id`

### Error Handling

Each resource handles common errors via `try-except` blocks, returning detailed error messages for:
- Missing required parameters (`KeyError`)
- Invalid value errors (`ValueError`) 
- MLflow-specific errors (`MlflowException`)
- Generic errors (`Exception`)


## Monitoring

The focus of this *microservice* is to observe and monitor the *ML model* in a *production environment*, reporting important signals that can trigger an *ML model re-training*.

### Evidently AI

[`Evidently AI`](https://www.evidentlyai.com/) allows testing and monitor data and the *ML model* in a *production environment*, evaluating and tracking the quality of data and *predictions*.

The main idea is to perform a comparison between:
- **reference dataset**: the *baseline*, typically the *dataset* on which the *ML model* was trained
- **current dataset**: the *production data*, new (and potentially dirty) unseen data that comes during the *inference* phase

```
from evidently import ColumnMapping
from evidently.options import ColorOptions

from evidently.metrics import *
from evidently.report import Report
from evidently.metric_preset import DataDriftPreset, DataQualityPreset, TargetDriftPreset
from evidently.metric_preset import ClassificationPreset

from evidently.tests import *
from evidently.test_suite import TestSuite
from evidently.test_preset import DataDriftTestPreset, DataQualityTestPreset, DataStabilityTestPreset, NoTargetPerformanceTestPreset
from evidently.test_preset import MulticlassClassificationTestPreset
```

#### Reports

**Reports** are composed of a combination of multiple **metrics**. **Presets** are pre-built *metric* combinations that can be used inside a *report*.

*Reports* can be used in many different steps, and are typically suitable for:
- *visual analysis*
- *debugging* and *exploration*
- *logging*
- *documentation*

It is convenient to use *reports* with small *datasets*, focusing on *interactive visualization*.

```
report = Report(metrics=[
    DataDriftPreset(),
    DataQualityPreset(),
    TargetDriftPreset(),
    ClassificationPreset()
])
```

#### Tests

**Tests** are *metrics* with conditions that return a *pass* or *fail* result. *Tests* can be combined inside a **Test Suite**. In this case, some *Presets* are also provided.

*Tests* can be used when performance checks are needed. Each *test* directly verifies a specific *expectation* on data or the *ML model*. They are typically suitable for:
- *test-based monitoring* of *production* *ML models*;
- performing *batch checks* on data or *ML models*;
- triggering *failure alerts*.

It is convenient to use *tests* with larger *datasets*, especially when an *expectation* needs to be checked.

```
tests = TestSuite(tests=[
    DataDriftTestPreset(),
    DataQualityTestPreset(),
    DataStabilityTestPreset(),
    NoTargetPerformanceTestPreset(),
    MulticlassClassificationTestPreset()
])
```

#### Customization
*Evidently AI* allows us to rely on:
- **local library**
- **local dashboard** 
- **online platform**

The results of *reports* and *tests* are provided in several different formats:
- `Python dictionary`
- `JSON`
- `HTML file`

```
report.as_dict()
report.json()
report.save_html("report.html")
```
```
tests.as_dict()
tests.json()
tests.save_html("tests.html")
```

Finally, it is possible to build *custom* *reports* and *tests* that can be specifically adapted to your *use-case*.


### Code Explanation

#### Monitoring Model
```
import logging

from evidently import ColumnMapping
from evidently.options import ColorOptions

from evidently.metrics import *
from evidently.report import Report
from evidently.metric_preset import DataDriftPreset, DataQualityPreset, TargetDriftPreset
from evidently.metric_preset import ClassificationPreset

from evidently.tests import *
from evidently.test_suite import TestSuite
from evidently.test_preset import DataDriftTestPreset, DataQualityTestPreset, DataStabilityTestPreset, NoTargetPerformanceTestPreset
from evidently.test_preset import MulticlassClassificationTestPreset

from persistence.model.i_model import IModel


class MonitoringModel(IModel):
    def __init__(self, columns):
        self.columns = columns

        self.logger = None
        self.column_mapping = None
        self.report = None
        self.tests = None
        self.summary = None
        self.summary_tests = None
        self.name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)

        self.define_column_mapping()
        self.define_report()
        self.define_test()
        self.define_summary()
        self.name = "EvidentlyAI"

        self.summary_tests = {
            "data_drift": DataDriftTestPreset(),
            "data_quality": DataQualityTestPreset(),
            "data_stability": DataStabilityTestPreset(),
            "no_target_performance": NoTargetPerformanceTestPreset(),
            "multi_class_classification": MulticlassClassificationTestPreset()
        }

    def define_column_mapping(self):
        self.column_mapping = ColumnMapping()
        self.column_mapping.target = "target"
        # column_mapping.target_names = {0: "first_region", 1: "second_region", 2: "third_region"}
        self.column_mapping.prediction = "prediction"
        self.column_mapping.numerical_features = self.columns
        self.column_mapping.task = "classification"

    def define_report(self):
        self.report = Report(metrics=[
            DataDriftPreset(),
            DataQualityPreset(),
            TargetDriftPreset(),
            ClassificationPreset()
        ])

    def define_test(self):
        self.tests = TestSuite(tests=[
            DataDriftTestPreset(),
            DataQualityTestPreset(),
            DataStabilityTestPreset(),
            NoTargetPerformanceTestPreset(),
            MulticlassClassificationTestPreset()
        ])

    def define_summary(self, tests=None):
        applied_tests = [
            DataDriftTestPreset(),
            DataQualityTestPreset(),
            DataStabilityTestPreset(),
            NoTargetPerformanceTestPreset(),
            MulticlassClassificationTestPreset()
        ]

        if tests is not None:
            applied_tests = []
            for k, v in tests.items():
                if v:
                    applied_tests.append(self.summary_tests[k])
        self.summary = TestSuite(tests=applied_tests)
```

The *Monitoring model* allows users to:
- define the correct *column mapping* associated with the *Wine Dataset*, considering the task that needs to be solved;
- define *reports*, *tests*, and *summaries* with default *metrics* and *Presets*;
- check for potential changes in the *summary* definition.


#### Monitoring Service
```
import logging
import pandas as pd
from json import loads

from service.i_service import IService
from persistence.model.i_model import IModel
from persistence.repository.impl.query import Query


class MonitoringService(IService):
    def __init__(self, monitoring_model: IModel, db=None):
        self.monitoring_model = monitoring_model
        self.db = db

        self.logger = None
        self.query = None

        self.reference = None
        self.current = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        self.init_query()

    def init_query(self):
        self.query = Query(self.db)
        #self.query.samples_columns_name = self.monitoring_model.columns_name

    def compute_metric(self, step, tests=None):
        result = {}
        if step == "report":
            result = self.report()
        elif step == "tests":
            result = self.tests()
        elif step == "summary":
            result = self.summary(tests)

        return result

    def report(self):
        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        result = self.compute_report(self.reference, self.current)

        return result

    def tests(self):
        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        result = self.compute_tests(self.reference, self.current)

        return result

    def summary(self, tests):
        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        self.monitoring_model.define_summary(tests)
        result = self.compute_summary(self.reference, self.current)

        return result

    def define_set(self, dataset_id):
        records = self.query.select_joined_conditioned_value(
            "samples", "targets", "predictions",
            "sample_index", "sample_index", "sample_index",
            dataset_id
        )
        records = pd.DataFrame(records)

        columns = records.columns.to_list()
        # Rename the second occurrence of sample_index to avoid duplication
        sample_index_idx = [i for i, s in enumerate(columns) if "sample_index" in s]
        columns[sample_index_idx[1]] = "targets.sample_index"
        columns[sample_index_idx[2]] = "predictions.sample_index"

        class_idx = [i for i, s in enumerate(columns) if "class" in s]
        columns[class_idx[0]] = "target"
        columns[class_idx[1]] = "prediction"

        records.columns = columns
        records = records.drop(columns=[
            "dataset_id", "sample_id", "target_id",
            "sample_index", "target_index", "targets.sample_index",
            "prediction_id", "prediction_index", "predictions.sample_index"
        ])
        return records

    def compute_report(self, reference, current):
        self.monitoring_model.report.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.report.json()
        result = loads(result)

        return result

    def compute_tests(self, reference, current):
        self.monitoring_model.tests.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.tests.json()
        result = loads(result)

        return result

    def compute_summary(self, reference, current):
        self.monitoring_model.summary.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.summary.json()
        result = loads(result)
        result = result["summary"]

        return result
```

The *Monitoring service* allows:
- Handling requests to compute *reports*, *tests*, or *summaries* by properly selecting *samples*, *targets*, and *predictions* from the *PostgreSQL DB*, and loading the results back into the *PostgreSQL DB*.


#### Monitoring Controller
```
import logging
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_sqlalchemy import SQLAlchemy

from service.i_service import IService
from web.controller.i_controller import IController


class MonitoringController(IController):
    def __init__(
            self,
            monitoring_service: IService,
            app_host="127.0.0.1", app_port=5004, app_debug=False, base_url="/root", service_url="/monitoring",
            db_host="127.0.0.1", db_port=5432, db_name="", db_user="root", db_password="pass"
    ):
        self.monitoring_service = monitoring_service
        self.app_host = app_host
        self.app_port = app_port
        self.app_debug = app_debug
        self.base_url = base_url
        self.service_url = service_url
        self.db_host = db_host
        self.db_port = db_port
        self.db_name = db_name
        self.db_user = db_user
        self.db_password = db_password

        self.logger = None
        self.app = None
        self.api = None
        self.parser = None
        self.db = None

        self.initialize()

    def initialize(self):
        self.app = Flask(__name__)
        self.app.config["SQLALCHEMY_DATABASE_URI"] = f"postgresql://{self.db_user}:{self.db_password}@{self.db_host}:{self.db_port}/{self.db_name}"
        self.app.app_context().push()
        self.api = Api(self.app)
        self.parser = reqparse.RequestParser()
        self.db = SQLAlchemy(self.app)
        self.logger = logging.getLogger("werkzeug")

        self.monitoring_service.db = self.db
        self.monitoring_service.init_query()

        self.add_resource()

        '''from time import sleep
        sleep(5)
        self.monitoring_service.compute_metric("report")
        self.monitoring_service.compute_metric("tests")
        self.monitoring_service.compute_metric("summary")'''

    def add_resource(self):
        self.api.add_resource(
            Monitoring,
            "".join([self.base_url, self.service_url]),
            resource_class_kwargs={
                "parser": self.parser,
                "db": self.db,
                "monitoring_service": self.monitoring_service,
            }
        )

    def run(self):
        self.app.run(host=self.app_host, port=self.app_port, debug=self.app_debug)


class Monitoring(Resource):
    def __init__(self, parser, db, monitoring_service):
        self.parser = parser
        self.db = db
        self.monitoring_service = monitoring_service

        self.initialization()

    def initialization(self):
        self.parser.add_argument("metric")
        self.parser.add_argument("tests", type=dict)

    def get(self):
        payload = {
            "name": self.monitoring_service.monitoring_model.name,
            "message": "Monitoring"
        }
        return payload

    def post(self):
        args = self.parser.parse_args()
        metric = args["metric"]
        tests = args["tests"]
        result = self.monitoring_service.compute_metric(metric, tests)
        payload = {
            "message": result
        }
        return payload
```

The *Monitoring Controller* allows you to:
- Build the `Flask` *App* and *REST API*
- Instantiate a connection to the *PostgreSQL DB* 
- Map the `Resource` to the correct *endpoint*, defining proper behavior for each **CRUD** operation
- Define the arguments to parse from the request body for each `Resource`



## Run Microservices

To run all the [`Docker`](https://www.docker.com/) *containers*, it is sufficient to run the command `./launch.sh` following the [README.md](../code/ml-tracking-monitoring/README.md). This will build every container based on the `docker-compose.yml` file.
All *containers* can run simultaneously in the background. The *simulator*, *ML model* and *tracking* *microservices* are mapped to different *ports* and rely on the *health check* performed on the *PostgreSQL DB* *container*.


## Future Work

There are many other (*open-source*) tools that can be useful in a *MLOps* pipeline:
- [`Feast`](https://feast.dev/): a *feature* store for storing *raw data* to pass to the *ML model*
- [`Seldon Core`](https://github.com/SeldonIO/seldon-core): a tool that helps with the deployment of *ML models*
- [`Kubeflow`](https://www.kubeflow.org/): a platform to build the entire *infrastructure* to integrate and orchestrate all the different tools
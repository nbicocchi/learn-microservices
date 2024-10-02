# USE CASE

## Overview

This work allows to emulate and monitor an *ML model* in a distributed *production environment*, simulating *training*, *testing* and *production* data.

The system is composed by 4 fundamental containerized *micro-services*:
- **MySQL DB**: to *store* and *query* necessary data;
- **simulator**: to simulate *training*, *testing* and *production* data;
- **ML model**: a trainable model that can be tested on *data batches*;
- **monitoring**: to observe the model, allowing to compute *reports* and *tests* and trigger model *re-training*.


Each component is identified by a **containerized micro-service**, with its own dependencies and environment, following the typical *application layers* structure (except the *MySQL DB*).

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

All *micro-services* are developed using `Python`, basing on [`Flask`](https://flask.palletsprojects.com/en/3.0.x/) framework and the [`Flask RESTful`](https://flask-restful.readthedocs.io/en/latest/) library to provide *REST API*.

```
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_mysqldb import MySQL
```

For each component a `query.py` script (under `src/persistence/repository` folder) is provided, useful to *query* the *MYSQL DB* to *store* and *request* the proper data.

To reproduce the results a `random seed`, equal to [42](https://en.wikipedia.org/wiki/Phrases_from_The_Hitchhiker%27s_Guide_to_the_Galaxy), is set.

Each component can be finally runned with a `Dockerfile` as an indipendent `Docker` *container*.


## MySQL DB

### Datasets table

|**Fields**|**Type**|**Attributes**|
|-|-|-|
|*dataset_id*|`int`|primary key|
|*name*|`varchar(255)`||

### Samples table

|**Fields**|**Type**|**Attributes**|
|-|-|-|
|*sample_id*|`int`|primary key|
|*dataset_id*|`int`|foreign key|
|*sample_index*|`int`||
|*Alcohol*|`float`||
|*Malicacid*|`float`||
|*Ash*|`float`||
|*Alcalinity_of_ash*|`float`||
|*Magnesium*|`float`||
|*Total_phenols*|`float`||
|*Flavanoids*|`float`||
|*Nonflavanoid_phenols*|`float`||
|*Proanthocyanins*|`float`||
|*Color_intensity*|`float`||
|*Hue*|`float`||
|*0D280_0D315_of_diluted_wines*|`float`||
|*Proline*|`float`||

### Predictions table

|**Fields**|**Type**|**Attributes**|
|-|-|-|
|*prediction_id*|`int`|primary key|
|*sample_index*|`int`|foreign key|
|*prediction_index*|`int`||
|*class*|`int`||

### Targets table

|**Fields**|**Type**|**Attributes**|
|-|-|-|
|*target_id*|`int`|primary key|
|*sample_index*|`int`|foreign key|
|*target_index*|`int`||
|*class*|`int`||


## Simulator

The focus of this *micro-service* is to extrapolate and simulate:
- *training* set: 60% of full *dataset*;
- *testing* set: 20% of full *dataset*;
- *production* set: 20% of full *dataset*, where a fouling process is applied to simulate data with *missing value* and different *distribution*, causing a performance drop.

Starting from a *dataset*, this component allows to load on the **DB** the chosen set to simulate the different phase of a *ML model* life-cycle.

### Wine Dataset

The chosen *dataset* is the **Wines Dataset**, directly provided by the [`Scikit-Learn`](https://scikit-learn.org/stable/) (`SK-Learn`) library.
The [*Wine Dataset*](https://scikit-learn.org/stable/modules/generated/sklearn.datasets.load_wine.html) is a classic *toy dataset* that can be used for *multi-class classification* problems.

```
from sklearn.datasets import load_wine
```

All the 13 *features* are *real* and *positive*, presenting no *missing value*, for a total of 178 *samples*, with 3 different *classes* to predict.

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

The *Simulator model* allows to:
- load the *Wine Dataset* in a [`Pandas`](https://pandas.pydata.org/) `Dataframe`, considering the *features* `X` and the *target* `y`;
- then some operations are applied on the `DataFrame`, to finally set the attribute `X` and `y`.


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
- create the different *tables* inside the *MySQL DB*, specifically the **datasets**, **samples**, **targets** and **predictions** tables;
- load immediatly the *dataset records* on starting;
- waiting for a *request* to insert *samples* and *targets*, therefore the *training*, *testing* or *production* set is defined into the *MySQL DB*.


#### Simulator Controller
```
import logging
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_mysqldb import MySQL

from service.i_service import IService
from web.controller.i_controller import IController


class SimulatorController(IController):
    def __init__(
            self,
            simulator_service: IService,
            app_host="127.0.0.1", app_port=5000, app_debug=False, base_url="/root", service_url="/simulator",
            db_host="127.0.0.1", db_port=3306, db_name="", db_user="root", db_password="pass"
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
        self.app.config["MYSQL_HOST"] = self.db_host
        self.app.config["MYSQL_PORT"] = self.db_port
        self.app.config["MYSQL_DB"] = self.db_name
        self.app.config["MYSQL_USER"] = self.db_user
        self.app.config["MYSQL_PASSWORD"] = self.db_password
        self.app.config["MYSQL_CURSORCLASS"] = "DictCursor"
        self.app.app_context().push()
        self.api = Api(self.app)
        self.parser = reqparse.RequestParser()
        self.db = MySQL(self.app)
        self.logger = logging.getLogger("werkzeug")
        # self.log.setLevel(logging.ERROR)

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

The *Simulator controller* allows to:
- build the `Flask` *App* and the *REST API*;
- instantiate a connection to the *MySQL DB*;
- mapping the `Resource` to the correct *endpoint*, allowing to define the proper behaviour for every **CRUD** operation;
- for each `Resource` it is needed to define the arguments to *parse* inside the *request* body.


## ML Model

The focus of this *micro-service* is to have an *ML model* capable to be trained and tested over new data.


### Decision Tree Model

The chosen *ML model* is a [`Decision Tree Classifier`](https://scikit-learn.org/stable/modules/generated/sklearn.tree.DecisionTreeClassifier.html) model, directly provided by the `SK-Learn` library.
To facilitate the understanding the *hyper-parameters* of the *ML model* have not been set, in fact, the default *ML model* configuration is taken into account.

```
from sklearn.tree import DecisionTreeClassifier
```

### Data Pre-Processing

The only precaution taken with respect to the data is to apply a [`Standard Scaler`](https://scikit-learn.org/stable/modules/generated/sklearn.preprocessing.StandardScaler.html), fitted on the *training* set, to transform the data before passing to the *ML model*.

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
        self.model = DecisionTreeClassifier(random_state=self.seed)
        self.name = type(self.model).__name__
```

The *ML Model model* allows to:
- define the `Decision Tree Classifier` *model*;


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
        self.prediction = records["sample_index"].to_frame(name="sample_index")
        records = records.drop(columns=[
            "dataset_id", "sample_id", "target_id",
            "sample_index", "target_index", "targets.sample_index"
        ])
        records.replace({"null", np.nan})
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

The *ML Model service* allows to:
- waiting for a *request* to *train* or *test* the *ML model* on the requested set, properly selecting the *samples* and *targets* from the *MySQL DB*;
- returns the *accuracy score* of the *predictions* made on the requested set, and finally loads them into the *MySQL DB*.

#### ML Model Controller
```
import logging
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_mysqldb import MySQL

from service.i_service import IService
from web.controller.i_controller import IController


class MLController(IController):
    def __init__(
            self,
            ml_service: IService,
            app_host="127.0.0.1", app_port=5000, app_debug=False, base_url="/root", service_url="/model",
            db_host="127.0.0.1", db_port=3306, db_name="", db_user="root", db_password="pass"
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
        self.app.config["MYSQL_HOST"] = self.db_host
        self.app.config["MYSQL_PORT"] = self.db_port
        self.app.config["MYSQL_DB"] = self.db_name
        self.app.config["MYSQL_USER"] = self.db_user
        self.app.config["MYSQL_PASSWORD"] = self.db_password
        self.app.config["MYSQL_CURSORCLASS"] = "DictCursor"
        self.app.app_context().push()
        self.api = Api(self.app)
        self.parser = reqparse.RequestParser()
        self.db = MySQL(self.app)
        self.logger = logging.getLogger("werkzeug")
        # self.log.setLevel(logging.ERROR)

        self.ml_service.db = self.db
        self.ml_service.init_query()

        self.add_resource()

        '''from time import sleep
        sleep(2)
        score = self.ml_service.select_records("training")
        score = self.ml_service.select_records("testing")
        score = self.ml_service.select_records("production")'''

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

The *ML Model controller* allows to:
- build the `Flask` *App* and the *REST API*;
- instantiate a connection to the *MySQL DB*;
- mapping the `Resource` to the correct *endpoint*, allowing to define the proper behaviour for every **CRUD** operation;
- for each `Resource` it is needed to define the arguments to *parse* inside the *request* body.


## Monitoring

The focus of this *micro-service* is to observe and monitor the *ML model* in a *production environment*, reporting important signal that can trigger an *ML model re-training*.

### Evidently AI

[`Evidently AI`](https://www.evidentlyai.com/) allows to test and monitor data and the *ML model* in a *production environment*, evaluating and tracking the quality of data and *predictions*.

The main idea is to perform a comparison between:
- **reference dataset**: the *baseline*, typically the *dataset* on which the *ML model* was trained;
- **current dataset**: the *production data*, new (and probably dirty) unseen data that come during the *inference* phase.

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

**Reports** are composed by a combination of multiple **metrics**. **Presets** are a pre-built *metrics* combination that can be used inside a *report*.

*Reports* can be used in many different step, typically they are suitable for:
- *visual analysis*;
- *debugging* and *exploration*;
- *logging*;
- *documentation*.

It is convenient to use *reports* with small *datasets*, with a focus on *interactive visualization*.

```
report = Report(metrics=[
    DataDriftPreset(),
    DataQualityPreset(),
    TargetDriftPreset(),
    ClassificationPreset()
])
```

#### Tests

**Tests** are *metrics* with a condition, that return a *pass* or *fail* result. *Tests* can be combined inside a **Test Suite**. Also in this case some *Presets* are provided.

*Tests* can be used when performance checks are requested. Every *test* directly verify a precise *expectation* on data or *ML model*, typically they are suitable for:
- *test-based monitoring* of *production* *ML model*;
- perform *batch checks* on data or *ML model*;
- trigger *failure alert*.

It is convenient to use *tests* also with larger *datasets*, specially when an *expectation* must be checked.

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
*Evidently AI* allows to rely on:
- **local library**;
- **local dashboard**;
- **online platform**.

The results of *reports* and *tests* are proveded in many different format:
- `Python dictionary`;
- `JSON`;
- `HTML file`.

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

Finally, it is also possible to build *custom* *reports* and *tests*, that can be specifically adapted to the considered *use-case*.


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

The *Monitoring model* allows to:
- define the correct *column mapping* associated to the *Wine Dataset*, also considering the task that needs to be solved;
- define *reports*, *tests* and *summary*, with some default *metrics* and *Presets*;
- check eventually changes in the *summary* definition.


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
        records = records.drop(columns=[
            "dataset_id", "sample_id", "target_id",
            "sample_index", "target_index", "targets.sample_index",
            "prediction_id", "prediction_index", "predictions.sample_index"
        ])
        records = records.rename(columns={"class": "target", "predictions.class": "prediction"})
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

The *Monitoring service* allows to:
- waiting for a *request* to compute *reports*, *tests* or *summary*, properly selecting *samples*, *targets* and *predictions* from the *MySQL DB*, and finally loads the results into the *MySQL DB*.


#### Monitoring Controller
```
import logging
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_mysqldb import MySQL

from service.i_service import IService
from web.controller.i_controller import IController


class MonitoringController(IController):
    def __init__(
            self,
            monitoring_service: IService,
            app_host="127.0.0.1", app_port=5000, app_debug=False, base_url="/root", service_url="/monitoring",
            db_host="127.0.0.1", db_port=3306, db_name="", db_user="root", db_password="pass"
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
        self.app.config["MYSQL_HOST"] = self.db_host
        self.app.config["MYSQL_PORT"] = self.db_port
        self.app.config["MYSQL_DB"] = self.db_name
        self.app.config["MYSQL_USER"] = self.db_user
        self.app.config["MYSQL_PASSWORD"] = self.db_password
        self.app.config["MYSQL_CURSORCLASS"] = "DictCursor"
        self.app.app_context().push()
        self.api = Api(self.app)
        self.parser = reqparse.RequestParser()
        self.db = MySQL(self.app)
        self.logger = logging.getLogger("werkzeug")
        # self.log.setLevel(logging.ERROR)

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

The *Monitoring controller* allows to:
- build the `Flask` *App* and the *REST API*;
- instantiate a connection to the *MySQL DB*;
- mapping the `Resource` to the correct *endpoint*, allowing to define the proper behaviour for every **CRUD** operation;
- for each `Resource` it is needed to define the arguments to *parse* inside the *request* body.


## Run Micro-Services

To run all the [`Docker`](https://www.docker.com/) *container* it is sufficient to run the command `docker-compose up -d`, based on the `docker-compose.yml` file.
In this way all the *containers* can be runmed simultaneously in background. The *simulator*, *ML model* and *monitoring* *micro-services* are mapped on different *port*, rely on the *health check* performed on the *MySQL DB* *container*.

```
version: "3.7"

services:
  db:
    image: mysql:5.7
    container_name: ml_model_monitoring_db
    restart: always
    environment:
      MYSQL_DATABASE: "wines_database"
      MYSQL_ROOT_PASSWORD: "pass"
      MYSQL_PASSWORD: "pass"
    ports:
      - "3306:3306"
    healthcheck:
      test: "/usr/bin/mysql --user=root --password=pass --execute \"SHOW DATABASES;\""
      interval: 5s
      timeout: 2s
      retries: 60

  simulator:
    build: ./microservices/simulator
    container_name: ml_model_monitoring_simulator
    ports:
      - "5000:5000"
    links:
      - db
    depends_on:
      db:
        condition: service_healthy

  ml:
    build: ./microservices/ml
    container_name: ml_model_monitoring_ml
    ports:
      - "5001:5000"
    links:
      - db
    depends_on:
      db:
        condition: service_healthy

  monitoring:
    build: ./microservices/monitoring
    container_name: ml_model_monitoring_monitoring
    ports:
      - "5002:5000"
    links:
      - db
    depends_on:
      db:
        condition: service_healthy
```


## Emulate ML Model Life Cycle

The idea of this work is to emulate the life-cicle of a *ML model* in a *production environment*.
Using [`Postman`](https://www.postman.com/) different requests to each *micro-service* can be sent.

### ML Model Training and Testing

First of all the *training* and *testing* set must be loaded on the *MySQL DB*, in order to train and test the *ML model* before to put it in a *production environment*.

```
POST request on http://127.0.0.1:5000/ml_model_monitoring/simulator
Request body: {
  "set_name": "training"
}
```
```
POST request on http://127.0.0.1:5000/ml_model_monitoring/simulator
Request body: {
  "set_name": "testing"
}
```

Then the *ML model* can be trained on the *training* set.

```
POST request on http://127.0.0.1:5001/ml_model_monitoring/model
Request body: {
  "dataset": "training"
}
```

Now, the *ML model* can be tested on the *testing* set.

```
POST request on http://127.0.0.1:5001/ml_model_monitoring/model
Request body: {
  "dataset": "testing"
}
```

### ML Model in a Production Environment

Having a well performing *ML model*, it is feasible to put it in a *production environment*.
It is now possible also load the *production* set, that is the final *batch* of dirty data. This data simulate the new and unseen data that come to the *ML model* once it is put in a *production environment*.
```
POST request on http://127.0.0.1:5000/ml_model_monitoring/simulator
Request body: {
  "set_name": "production"
}
```

The *ML model* can be tested on the *production* set. Very often *production* data, over time, tend to differ from the data on which the *ML model* was trained, thus causing a decline in performance.
```
POST request on http://127.0.0.1:5001/ml_model_monitoring/model
Request body: {
  "dataset": "production"
}
```

The expected performance of the *ML model* are:
- *training* set: 100% *accuracy*;
- *testing* set: 97.2% *accuracy*;
- *production* set: 88.9% *accuracy*.

### ML Model Monitoring

Finally, through *Evidently AI* it is possible to compute *reports* and *tests*, to detect the data *drift* and many others problems that could be present on *production* data. Specifically, *summary* can be used to check if a *re-training* is necessary. It is therefore allowed to choose the specific *tests* to perform.
```
POST request on http://127.0.0.1:5002/ml_model_monitoring/monitoring
Request body: {
  "metric": "report"
}
```
```
POST request on http://127.0.0.1:5002/ml_model_monitoring/monitoring
Request body: {
  "metric": "tests"
}
```
```
POST request on http://127.0.0.1:5002/ml_model_monitoring/monitoring
Request body: {
  "metric": "summary",
  "tests": {
    "data_drift": true,
    "data_quality": true,
    "data_stability": true,
    "no_target_performance": true,
    "multi_class_classification": true
  }
}
```

## Future Integration

Like `Evidently AI` there are many other (*open-source*) tools that can be very useful to be used in a *MLOps* pipeline:
- [`Feast`](https://feast.dev/): a *feature* store used to store *raw data* to pass to the *ML model*;
- [`MLflow`](https://mlflow.org/): a simple tool to keep track of experiments and *ML model* versions;
- [`Seldon Core`](https://github.com/SeldonIO/seldon-core): a tool that helps in the deployment of *ML model*;
- [`Kubeflow`](https://www.kubeflow.org/): a platform to build the entire *infra-structure* to integrate and orchestrate all the different tools.

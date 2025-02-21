import logging
import numpy as np
import pandas as pd

from sklearn.model_selection import train_test_split

from src.service.i_service import IService
from src.persistence.model.i_model import IModel
from src.persistence.repository.impl.query import Query


class SimulatorService(IService):
    def __init__(self, simulator_model: IModel, db=None, seed=42):
        """
        Initialize SimulatorService instance.
        
        :param simulator_model: Model instance implementing IModel interface
        :param db: Database connection object, defaults to None
        :param seed: Random seed for reproducibility, defaults to 42
        """
        self.simulator_model = simulator_model
        self.db = db
        self.seed = seed

        self.logger = None
        self.query = None
        self.X_train = None
        self.X_val = None  #
        self.X_test = None
        self.y_train = None
        self.y_val = None  #
        self.y_test = None
        self.X_test_reference = None
        self.X_test_current = None
        self.y_test_reference = None
        self.y_test_current = None

        self.initialize()

    def initialize(self):
        """
        Insert records into database based on specified step.
        
        :param step: Type of records to insert, one of: 'dataset', 'training', 'testing', 'production'
        :raises ValueError: If step is not one of the valid options
        """
        self.logger = logging.getLogger(__name__)
        np.random.seed(self.seed)
        self.init_query()

    def init_query(self):
        self.query = Query(self.db)
        self.query.samples_columns_name = self.simulator_model.columns_name

    def create_tables(self):
        """
        Create the tables.
        """

        self.query.create_datasets_table()
        self.query.create_samples_table()
        self.query.create_predictions_table()
        self.query.create_targets_table()

        self.query.describe_table("datasets")
        self.query.describe_table("samples")
        self.query.describe_table("targets")
        self.query.describe_table("predictions")

    def delete_records(self):
        """
        Delete the records.
        """

        self.query.delete_values("targets")
        self.query.delete_values("predictions")
        self.query.delete_values("samples")
        self.query.delete_values("datasets")

        self.query.select_value("datasets")
        self.query.select_value("samples")
        self.query.select_value("targets")
        self.query.select_value("predictions")

    def insert_records(self, step):
        """
        Insert the records.
        :param step: The step to insert the records for
        """

        if step == "dataset":
            self.datasets()
        elif step == "training":
            self.training()
        elif step == "testing":
            self.testing()
        elif step == "production":
            self.production()

    def datasets(self):
        """
        Define the datasets.
        """

        self.define_datasets()

    def training(self):
        """
        Define the training set.
        """

        self.define_training_set()
        self.load_training_set()

    def testing(self):
        """
        Define the testing set.
        """

        self.define_testing_set()
        self.load_testing_set()

    def production(self):
        """
        Define the production set.
        """

        self.define_production_set()
        self.load_production_set()

    def define_datasets(self):
        """
        Define the datasets.
        """

        records = {"dataset_id": [1, 2, 3], "name": ["training", "testing", "production"]}
        records = pd.DataFrame(data=records)
        self.query.insert_dataset_records(records)

        self.query.select_value("datasets")

    def define_training_set(self):
        """
        Define the training set by splitting the data.
        
        Splits simulator model data into training and test sets using stratified sampling.
        Sets X_train, X_test, y_train, y_test class attributes.
        """

        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(
            self.simulator_model.X, self.simulator_model.y,
            train_size=0.6,
            random_state=self.seed,
            stratify=self.simulator_model.y
        )


    def define_testing_set(self):
        """
        Define the testing set by splitting test data.
        
        Splits existing test data into reference and current sets using stratified sampling.
        Sets X_test_reference, X_test_current, y_test_reference, y_test_current class attributes.
        """

        self.X_test_reference, self.X_test_current, self.y_test_reference, self.y_test_current = train_test_split(
            self.X_test, self.y_test,
            train_size=0.5,
            random_state=self.seed,
            stratify=self.y_test
        )

    def define_production_set(self):
        """
        Define the production set by adding noise to test data.
        
        Adds random normal noise to current test set and introduces null values
        to simulate production data conditions.
        :return: None
        """

        self.X_test_current = self.X_test_current + np.random.normal(0, 0.3)
        for col in self.X_test_current.columns:
            self.X_test_current.loc[self.X_test_current.sample(frac=0.03).index, col] = "null"

    def load_training_set(self):
        """
        Load training set into database.
        
        Inserts training features and targets into respective database tables
        with appropriate dataset_id and index mappings.
        :raises DatabaseError: If database insertion fails
        """

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
        """
        Load the testing set.
        """

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
        """
        Load the production set.
        """

        records = self.X_test_current
        records["dataset_id"] = 3
        records["sample_index"] = records.index
        self.query.insert_samples_records(records)

        records = self.y_test_current.to_frame(name="class")
        records["sample_index"] = records.index
        records["target_index"] = records.index
        self.query.insert_targets_records(records)

        self.query.select_condition_value("samples", 3)

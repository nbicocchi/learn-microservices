import logging
import pandas as pd

from sklearn.preprocessing import StandardScaler
from sklearn.metrics import accuracy_score
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC
from sklearn.linear_model import LogisticRegression

from src.service.i_service import IService
from src.persistence.model.i_model import IModel
from src.persistence.repository.impl.query import Query


class MLService(IService):
    def __init__(self, ml_model: IModel, db=None, seed=42):
        """
        Initialize MLService instance.
        
        :param ml_model: Model instance implementing IModel interface
        :param db: Database connection object, defaults to None
        :param seed: Random seed for reproducibility, defaults to 42
        """
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
        """
        Initialize the MLService instance.
        
        Sets up logging, standard scaler, and database query components.
        No parameters or return values.
        """

        self.logger = logging.getLogger(__name__)
        self.standard_scaler = StandardScaler()
        self.init_query()

    def init_query(self):
        """
        Initialize the query object.
        
        This method creates an instance of the Query class using the provided database connection.
        """

        self.query = Query(self.db)

    def select_records(self, step):
        """
        Select records based on the specified step and compute model score.
        
        :param step: Dataset type to use, must be one of: 'training', 'testing', or 'production'
        :return: Model accuracy score for the specified dataset
        :raises ValueError: If step is not one of the valid options
        """


        score = 0
        if step == "training":
            score = self.training()
        elif step == "testing":
            score = self.testing()
        elif step == "production":
            score = self.production()

        return score

    def training(self):
        """
        Perform training and return the score.
        
        This method selects training records from the database, preprocesses the data, trains the model, tests the model, and returns the score.
        :return: the score of the model on the training set
        """

        records = self.query.select_joined_conditioned_value(
            "samples", "targets", "sample_index", "sample_index", 1
        )

        X, y, = self.define_set(records)
        self.pre_processing(X)
        self.train(X, y)
        y_pred = self.test(X)
        score = self.score(y, y_pred)
        print("score train", score * 100)
        self.load_prediction(y_pred)

        return score


    def testing(self):
        """
        Perform testing and return the score.
        
        This method selects testing records from the database, preprocesses the data, tests the model, and returns the score.
        :return: the score of the model on the testing set
        """

        records = self.query.select_joined_conditioned_value(
            "samples", "targets", "sample_index", "sample_index", 2
        )

        X, y, = self.define_set(records)
        self.pre_processing(X, fit=False)
        y_pred = self.test(X)
        score = self.score(y, y_pred)
        print("score test", score * 100)

        self.load_prediction(y_pred)

        return score

    def production(self):
        """
        Perform production and return the score.
        
        This method selects production records from the database, preprocesses the data, tests the model, and returns the score.
        :return: the score of the model on the production set
        """

        records = self.query.select_joined_conditioned_value(
            "samples", "targets",
            "sample_index", "sample_index",
            3
        )

        X, y, = self.define_set(records)
        self.pre_processing(X, fit=False)
        y_pred = self.test(X)
        score = self.score(y, y_pred)
        print("score prod", score * 100)

        self.load_prediction(y_pred)

        return score

    def define_set(self, records):
        """
        Define the feature and target sets from database records.

        :param records: Dictionary containing database records
        :return: Tuple of (X, y) where X is features DataFrame and y is target Series
        """

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
        """
        Preprocess the data using standard scaling.

        :param X: Feature DataFrame to preprocess
        :param fit: Whether to fit the scaler on this data, defaults to True
        :return: Scaled numpy array of features
        """

        if fit:
            self.standard_scaler.fit(X)
        X_np = self.standard_scaler.transform(X)
        return X_np

    def train(self, X, y):
        """
        Train the model.
        
        This method fits the model to the data.
        :param X: The data to train on
        :param y: The target
        """

        self.ml_model.model.fit(X, y)

    def test(self, X):
        """
        Test the model.
        
        This method predicts the target for the given data.
        :param X: The data to test on
        :return: The predicted target
        """

        y = self.ml_model.model.predict(X)
        return y


    @staticmethod
    def score(target, prediction):
        """
        Calculate the accuracy score.
        
        This method calculates the accuracy score between the target and prediction.
        :param target: The target
        :param prediction: The prediction
        :return: The accuracy score
        """

        score = accuracy_score(target, prediction)
        return score

    def load_prediction(self, y_pred):
        """
        Load the prediction into the database.
        
        This method inserts the prediction into the database.
        :param y_pred: The predicted target
        """

        records = self.prediction
        records["prediction_index"] = records["sample_index"]
        records["class"] = y_pred
        self.query.insert_predictions_records(records)

    def load_model(self, model_name: str, hyperparameters: dict):
        """
        Load and initialize a machine learning model.
        
        :param model_name: Name of the model to load ('RandomForest', 'SVC', or 'LogisticRegression')
        :param hyperparameters: Dictionary of model-specific parameters
        :return: Model name if successful, None if model_name is invalid
        :raises ValueError: If hyperparameters are invalid for the specified model
        """

        modelClass = self.available_models.get(model_name)
        if modelClass is None:
            return None

        self.ml_model.name = model_name
        self.ml_model.model = modelClass(**hyperparameters, random_state=self.seed)
        return self.ml_model.name

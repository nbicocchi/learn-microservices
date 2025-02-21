import logging
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_sqlalchemy import SQLAlchemy

from src.service.i_service import IService
from src.web.controller.i_controller import IController


class MLController(IController):
    def __init__(
            self,
            ml_service: IService,
            app_host="127.0.0.1", app_port=5000, app_debug=False, base_url="/root", service_url="/ml",
            db_host="127.0.0.1", db_port=5432, db_name="", db_user="postgres", db_password="pass"
    ):
        """
        Initialize MLController instance.
        
        :param ml_service: Service implementing IService interface
        :param app_host: Flask application host, defaults to "127.0.0.1"
        :param app_port: Flask application port, defaults to 5000
        :param app_debug: Enable Flask debug mode, defaults to False
        :param base_url: Base URL for API endpoints, defaults to "/root"
        :param service_url: Service-specific URL path, defaults to "/ml"
        :param db_host: Database host address, defaults to "127.0.0.1"
        :param db_port: Database port, defaults to 5432
        :param db_name: Database name, defaults to ""
        :param db_user: Database username, defaults to "root"
        :param db_password: Database password, defaults to "pass"
        """
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
        """
        Initialize Flask application and configure database connection.
        
        Sets up Flask app configuration, creates API instance, initializes parser,
        establishes database connection, and configures logging.
        """

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
        """
        Add API resources to Flask application.
        
        Registers the Model and SetModel resources with their respective endpoints
        and configures their dependencies.
        """

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
        """
        Run the Flask application.
        """

        self.app.run(host=self.app_host, port=self.app_port, debug=self.app_debug)


class SetModel(Resource):
    def __init__(self, parser, ml_service: IService):
        self.parser = parser
        self.ml_service = ml_service

        self.initialization()

    def initialization(self):
        self.parser.add_argument("model_name", type=str, location='json', help="Name of ML model")
        self.parser.add_argument("hyperparameters", type=dict, location='json', help="hyperparameters of model")

    def post(self):
        """
        Handle POST request to set model configuration.
        
        :return: Tuple of (response_dict, http_status_code)
        :raises KeyError: If required parameters are missing
        :raises ValueError: If invalid parameter values are provided
        """

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


class Model(Resource):
    def __init__(self, parser, db, ml_service: IService):
        self.parser = parser
        self.db = db
        self.ml_service = ml_service

        self.initialization()

    def initialization(self):
        self.parser.add_argument("dataset")

    def get(self):
        """
        Get the model from the database.
        :return payload of the response with the name and message
        """

        payload = {
            "name": self.ml_service.ml_model.name,
            "message": "ML model"
        }
        return payload

    def post(self):
        """
        Post the score of the model on the dataset.
        :return payload of the response with the message
        """

        args = self.parser.parse_args()
        dataset = args["dataset"]

        try:
            score = self.ml_service.select_records(dataset)
            payload = {
                "message": "Score on {} set: {}%".format(dataset, score * 100)
            }
            return payload, 200

        except Exception as e:
            print(f"Error occurred: {e}")
            return {
                "message": "An error occurred while processing the request.",
                "error": str(e)
            }, 500

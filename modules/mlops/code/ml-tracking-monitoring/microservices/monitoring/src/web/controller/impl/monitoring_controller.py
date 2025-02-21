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
            app_host="127.0.0.1", app_port=5000, app_debug=False, base_url="/root", service_url="/monitoring",
            db_host="127.0.0.1", db_port=5432, db_name="", db_user="root", db_password="pass"
    ):
        """
        Initialize MonitoringController instance.
        
        :param monitoring_service: Service implementing IService interface
        :param app_host: Flask application host, defaults to "127.0.0.1"
        :param app_port: Flask application port, defaults to 5000
        :param app_debug: Enable Flask debug mode, defaults to False
        :param base_url: Base URL for API endpoints, defaults to "/root"
        :param service_url: Service-specific URL path, defaults to "/monitoring"
        :param db_host: Database host address, defaults to "127.0.0.1"
        :param db_port: Database port, defaults to 5432
        :param db_name: Database name, defaults to ""
        :param db_user: Database username, defaults to "root"
        :param db_password: Database password, defaults to "pass"
        """
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

        self.monitoring_service.db = self.db
        self.monitoring_service.init_query()

        self.add_resource()


    def add_resource(self):
        """
        Add the resource to the API.
        """
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
        """
        Run the Flask application.
        """

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
        """
        Get monitoring model information.
        
        :return: Dictionary containing model name and status message
        :rtype: dict
        """

        payload = {
            "name": self.monitoring_service.monitoring_model.name,
            "message": "Monitoring"
        }
        return payload

    def post(self):
        """
        Compute monitoring metrics based on request parameters.
        
        :return: Dictionary containing computed metrics or test results
        :rtype: dict
        :raises ValueError: If invalid metric type or test configuration is provided
        """

        args = self.parser.parse_args()
        metric = args["metric"]
        tests = args["tests"]
        result = self.monitoring_service.compute_metric(metric, tests)
        payload = {
            "message": result
        }
        return payload

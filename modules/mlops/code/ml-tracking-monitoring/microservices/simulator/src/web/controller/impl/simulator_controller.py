import logging
from flask import Flask
from flask_restful import Api, Resource, reqparse
from flask_sqlalchemy import SQLAlchemy

from src.service.i_service import IService
from src.web.controller.i_controller import IController


class SimulatorController(IController):
    def __init__(
            self,
            simulator_service: IService,
            app_host="127.0.0.1", app_port=5000, app_debug=False, base_url="/root", service_url="/simulator",
            db_host="127.0.0.1", db_port=5432, db_name="", db_user="root", db_password="pass"
    ):
        """
        Initialize SimulatorController instance.
        
        :param simulator_service: Service implementing IService interface
        :param app_host: Flask application host address
        :param app_port: Flask application port number
        :param app_debug: Enable Flask debug mode
        :param base_url: Base URL for API endpoints
        :param service_url: Service-specific URL path
        :param db_host: Database host address
        :param db_port: Database port number
        :param db_name: Database name
        :param db_user: Database username
        :param db_password: Database password
        """
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

        self.simulator_service.db = self.db
        self.simulator_service.init_query()

        self.add_resource()
        self.set_db()


    def add_resource(self):
        """
        Add the resource.
        """
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
        """
        Set up and initialize the database.
        
        Creates required database tables, clears existing records,
        and inserts initial dataset records.
        :raises DatabaseError: If database operations fail
        """
        self.simulator_service.create_tables()
        self.simulator_service.delete_records()

        self.simulator_service.insert_records("dataset")

    def run(self):
        """
        Run the Flask application server.
        
        :param self: Instance of SimulatorController
        :return: None
        """
        self.app.run(host=self.app_host, port=self.app_port, debug=self.app_debug)


class Simulator(Resource):
    def __init__(self, parser, db, simulator_service):
        self.parser = parser
        self.db = db
        self.simulator_service = simulator_service

        self.initialization()

    def initialization(self):
        """
        Initialize the parser.
        """
        self.parser.add_argument("set_name")

    def get(self):
        """
        Get the simulator name.
        :return payload of the response with the name and message
        """
        payload = {
            "name": self.simulator_service.simulator_model.name,
            "message": "Simulator"
        }
        return payload

    def post(self):
        """
        Post the simulator name.
        :return payload of the response with the name and message
        """
        try:
            args = self.parser.parse_args()
            set_name = args["set_name"]
            self.simulator_service.insert_records(set_name)
            payload = {
                "message": "Load {} set".format(set_name)
            }
            return payload

        except Exception as e:
            return {
                "message": "An error occurred: {}".format(str(e))
            }, 500

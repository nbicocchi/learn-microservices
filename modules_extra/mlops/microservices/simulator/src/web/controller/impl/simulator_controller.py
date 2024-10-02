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

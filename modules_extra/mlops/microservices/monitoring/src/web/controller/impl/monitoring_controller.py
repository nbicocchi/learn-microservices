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

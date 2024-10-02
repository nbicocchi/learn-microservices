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

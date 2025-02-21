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
        """
        Initialize TrackingController instance.
        
        :param tracking_service: Service implementing IService interface
        :param app_host: Flask application host address, defaults to "127.0.0.1"
        :param app_port: Flask application port number, defaults to 5003
        :param app_debug: Enable Flask debug mode, defaults to False
        :param base_url: Base URL for API endpoints, defaults to "/root"
        :param service_url: Service-specific URL path, defaults to "/tracking"
        :param db_dataset_host: Dataset database host address
        :param db_dataset_port: Dataset database port
        :param db_dataset_name: Dataset database name
        :param db_dataset_user: Dataset database username
        :param db_dataset_password: Dataset database password
        :param db_mlflow_host: MLflow database host address
        :param db_mlflow_port: MLflow database port
        :param db_mlflow_name: MLflow database name
        :param db_mlflow_user: MLflow database username
        :param db_mlflow_password: MLflow database password
        :param ml_host: ML service host address
        :param ml_port: ML service port number
        :param ml_service_url: ML service URL path
        """

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
        """
        Initialize Flask application and configure database connections.
        
        Sets up Flask app configuration, creates API instance, initializes parsers,
        establishes database connections, and configures MLflow tracking URI.
        :raises MLflowException: If unable to set up MLflow tracking
        """
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

    def add_resource(self):
        """
        Add API resources and endpoints for the tracking service.
        
        Registers the following resources:
        - TrackingExperiment: Handles experiment CRUD operations at /experiments endpoint
        - TrackingRuns: Manages experiment runs at /runs endpoint 
        - Optimization: Handles model optimization at /model_management endpoint
        
        Each resource is initialized with required dependencies like the request parser,
        tracking service, and database connections.
        """

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
        """
        Run the Flask app.
        """

        self.app.run(host=self.app_host, port=self.app_port, debug=self.app_debug)


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
        """
        Post the request.
        """

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
        """
        Get experiments.
        """

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
        """
        Post the request to get experiment info, best model and statistics.
        """

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
        """
        Delete experiment.
        """

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
        """
        Get the experiment runs.
        """

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
        """
        Post the request to get run info, parameters and metrics.
        """

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
        """
        Delete run.
        """

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

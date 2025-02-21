import os
import sys
import argparse

sys.path.append(os.getcwd())

from src.utils.config.yaml_config import yaml_config
from src.persistence.model.impl.tracking import TrackingModel
from src.service.impl.tracking_service import TrackingService
from src.web.controller.impl.tracking_controller import TrackingController


def main():
    config = yaml_config(args.config_file_path, True)

    tracking_model = TrackingModel(config["general"]["seed"])

    tracking_service = TrackingService(
        tracking_model
    )

    tracking_controller = TrackingController(
        tracking_service,
        config["controller"]["app"]["host"],
        config["controller"]["app"]["port"],
        config["controller"]["app"]["debug"],
        config["controller"]["app"]["base_url"],
        config["controller"]["app"]["service_url"],

        config["controller"]["db_dataset"]["host"],
        config["controller"]["db_dataset"]["port"],
        config["controller"]["db_dataset"]["name"],
        config["controller"]["db_dataset"]["user"],
        config["controller"]["db_dataset"]["password"],

        config["controller"]["db_mlflow"]["host"],
        config["controller"]["db_mlflow"]["port"],
        config["controller"]["db_mlflow"]["name"],
        config["controller"]["db_mlflow"]["user"],
        config["controller"]["db_mlflow"]["password"],

        config["controller"]["ml"]["host"],
        config["controller"]["ml"]["port"],
        config["controller"]["ml"]["service_url"]
    )
    tracking_controller.run()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-cfg_pth", "--config_file_path", required=True, help="Path to .yml config file")
    args = parser.parse_args()
    main()

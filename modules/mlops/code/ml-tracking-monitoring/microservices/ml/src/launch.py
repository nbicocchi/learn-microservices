import os
import sys
import argparse

sys.path.append(os.getcwd())

from src.utils.config.yaml_config import yaml_config
from src.persistence.model.impl.ml import MLModel
from src.service.impl.ml_service import MLService
from src.web.controller.impl.ml_controller import MLController


def main():
    config = yaml_config(args.config_file_path, True)

    ml_model = MLModel(
        config["general"]["seed"]
    )
    ml_service = MLService(
        ml_model,
        config["general"]["seed"]
    )
    ml_controller = MLController(
        ml_service,
        config["controller"]["app"]["host"],
        config["controller"]["app"]["port"],
        config["controller"]["app"]["debug"],
        config["controller"]["app"]["base_url"],
        config["controller"]["app"]["service_url"],
        config["controller"]["db"]["host"],
        config["controller"]["db"]["port"],
        config["controller"]["db"]["name"],
        config["controller"]["db"]["user"],
        config["controller"]["db"]["password"],
    )
    ml_controller.run()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-cfg_pth", "--config_file_path", required=True, help="Path to .yml config file")
    args = parser.parse_args()
    main()

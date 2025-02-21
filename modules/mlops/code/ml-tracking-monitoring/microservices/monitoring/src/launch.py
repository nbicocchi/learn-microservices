import os
import sys
import argparse

sys.path.append(os.getcwd())

from utils.config.yaml_config import yaml_config
from persistence.model.impl.monitoring import MonitoringModel
from service.impl.monitoring_service import MonitoringService
from web.controller.impl.monitoring_controller import MonitoringController


def main():
    config = yaml_config(args.config_file_path, True)

    monitoring_model = MonitoringModel([
        "alcohol", "malic_acid",
        "ash", "alcalinity_of_ash", "magnesium", "total_phenols", "flavanoids",
        "nonflavanoid_phenols", "proanthocyanins", "color_intensity", "hue",
        "od280__od315_of_diluted_wines", "proline"
    ])
    monitoring_service = MonitoringService(
        monitoring_model
    )
    monitoring_controller = MonitoringController(
        monitoring_service,
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
    )
    monitoring_controller.run()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-cfg_pth", "--config_file_path", required=True, help="Path to .yml config file")
    args = parser.parse_args()
    main()

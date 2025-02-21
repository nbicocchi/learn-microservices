import os
import sys
import argparse

sys.path.append(os.getcwd())

from src.utils.config.yaml_config import yaml_config
from src.persistence.model.impl.simulator import SimulatorModel
from src.service.impl.simulator_service import SimulatorService
from src.web.controller.impl.simulator_controller import SimulatorController


def main():
    config = yaml_config(args.config_file_path, True)

    simulator_model = SimulatorModel()
    simulator_service = SimulatorService(
        simulator_model,
        config["general"]["seed"]
    )
    simulator_controller = SimulatorController(
        simulator_service,
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
    simulator_controller.run()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-cfg_pth", "--config_file_path", required=True, help="Path to .yml config file")
    args = parser.parse_args()
    main()

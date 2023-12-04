import argparse
import logging
import os
import shutil
import subprocess
import json

import pandas as pd

def setup_logger():
    logging.basicConfig(
        level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s", handlers=[logging.StreamHandler()]
    )


def check_docker_installation():
    logging.info("Check docker version")
    docker_version_result = os.system("docker -v")

    if docker_version_result:
        exit("Docker was not found. Try to install it with https://www.docker.com")


def check_dataset(
    force: bool,
    datasets_path: str,
    dataset_name: str,
) -> None:
    logging.info("Check dataset %s", dataset_name)
    dataset_path = os.path.join(datasets_path, dataset_name)

    if os.path.exists(dataset_path):
        if force:
            logging.info("Remove dataset directory %s", dataset_path)
            shutil.rmtree(dataset_path)
            os.makedirs(dataset_path)

        else:
            logging.info("Dataset %s already exists", dataset_name)
            return

    logging.info("Download dataset %s", dataset_name)

    os.system("python scripts/prepare_data.py")

def prepare_data():
    dataset_path = "datasets"
    logging.info("Generate test data for dataset.")
    f = open(os.path.join("parameters", "params.json"))
    params = json.load(f)

    logging.info("Take datasets")
    reference_data = pd.read_csv(os.path.join(dataset_path, params["file_name_training_data"]))
    production_data = pd.read_csv(os.path.join(dataset_path, params["file_name_request_data"]))

    print("reference_data\n", reference_data)
    print("production_data\n", production_data)

    logging.info("Reference dataset was created with %s rows", reference_data.shape[0])
    logging.info("Production dataset was created with %s rows", production_data.shape[0])

    return reference_data, production_data


def download_test_datasets(force: bool):
    datasets_path = os.path.abspath("datasets")
    logging.info("Check datasets directory %s", datasets_path)

    if not os.path.exists(datasets_path):
        logging.info("Create datasets directory %s", datasets_path)
        os.makedirs(datasets_path)

    else:
        logging.info("Datasets directory already exists")

    dataset_name = "model_input_table"
    check_dataset(force, datasets_path, dataset_name)


def run_docker_compose():
    logging.info("Run docker compose")
    run_script(cmd=["docker", "compose", "up", "-d"], wait=True)


def run_script(cmd: list, wait: bool) -> None:
    logging.info("Run %s", " ".join(cmd))
    script_process = subprocess.Popen(" ".join(cmd) , stdout=subprocess.PIPE, shell=True)

    if wait:
        script_process.wait()

        if script_process.returncode != 0:
            exit(script_process.returncode)


def send_data_requests():
    logging.info("Run scripts/example_run_request.py")
    os.system("python scripts/example_run_request.py")


def run_monitoring_html():
    logging.info("Run scripts/monitoring.py")
    os.system("python scripts/monitoring.py")

def stop_docker_compose():
    logging.info("Run docker compose down")
    os.system("docker compose down")

def run_streamlit():
    logging.info("Run streamlit")
    os.system("streamlit run streamlit_app.py")


def main(force: bool):
    setup_logger()
    check_docker_installation()
    download_test_datasets(force=force)
    send_data_requests()
    run_monitoring_html()
    logging.info("Terminate. Attend next request...")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Script for data and config generation for demo Evidently metrics integration with Grafana"
    )
    parser.add_argument(
        "-f",
        "--force",
        action="store_true",
        help="Remove and download again test datasets",
    )
    parameters = parser.parse_args()
    main(force=parameters.force)
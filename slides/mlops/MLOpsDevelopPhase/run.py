import logging
import os
import sys
from typing import List
import shutil
import pandas as pd

def setup_logger():
    logging.basicConfig(
        level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s", handlers=[logging.StreamHandler()]
    )

def update_data(dataset: pd.DataFrame):
    logging.info("Load new dataset in project.")
    dataset.to_csv('./data/01_raw/DATA.csv', index=False)

def update_data_url(url: str):
    logging.info("Load new dataset in project.")
    shutil.copyfile(url, './data/01_raw/DATA.csv')

def dvc_run():
    logging.info("Run dvc to push dataset on Google Drive.")
    os.system("git rm -r --cached data/01_raw/DATA.csv")
    os.system("dvc remove data/01_raw/DATA.csv.dvc")
    #os.system("git commit -m stop")
    os.system("dvc add data/01_raw/DATA.csv")
    os.system("dvc push data/01_raw/DATA.csv")

    os.system("git add data/01_raw/DATA.csv -f")
    #os.system("git commit -m start")

def kedro_docker_set():
    logging.info("Create docker image of Kedro pipeline.")
    os.system("kedro docker build --image pipeline-ml")

def mlflow_run_onWindows():
    logging.info("Run mlflow command.")
    os.system("mlflow run . --experiment-name activities-example --no-conda")

def bentoml_set():
    logging.info("Create Bento model and dockerize it.")
    os.system("bentoml build")
    os.system("bentoml containerize activities_model:latest")

def run_retrain():
    dvc_run()
    mlflow_run_onWindows()
    bentoml_set()

def kedro_ui():
    logging.info("Run kedro ui.")
    os.system("kedro viz")

def mlflow_ui():
    logging.info("Run mlflow ui.")
    os.system("mlflow ui")


def main(arg: List):

    if arg[1] == 'pipeline':
        kedro_ui()
    elif arg[1] == 'exp':
        mlflow_ui()
    elif arg[1] == 'pipeline-docker':
        kedro_docker_set()
    elif arg[1] == 'run':
        run_retrain()
    elif arg[1] == 'new-dataset':
        if len(arg) != 3:
            logging.info("Error!! Need the global url.")
            return
        update_data_url(arg[2])
    else:
        logging.info("Error!!\n     Arg can be:\n\t---------------------------------------\n\tpython run.py pipeline\n\tpython run.py exp\n\tpython run.py pipeline-docker\n\tpython run.py run\n\tpython run.py new-dataset <global_url>\n\t---------------------------------------")
        return

    logging.info("Terminated successfully.")


if __name__ == "__main__":
    setup_logger()

    if len(sys.argv) != 2 and len(sys.argv) != 3:
        logging.info("Error!! Need args.\n\t---------------------------------------\n\tpython run.py pipeline\n\tpython run.py exp\n\tpython run.py pipeline-docker\n\tpython run.py run\n\tpython run.py new-dataset <global_url>\n\t---------------------------------------")
    else:
        main(sys.argv)
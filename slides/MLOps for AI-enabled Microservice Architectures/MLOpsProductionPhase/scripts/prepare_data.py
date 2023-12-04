import logging
import os
import json
import pandas as pd

dataset_path = "datasets"
f = open(os.path.join("parameters", "params.json"))
params = json.load(f)
ff = open(os.path.join("parameters", "header_params.json"))
header_params = json.load(ff)

def setup_logger() -> None:
    logging.basicConfig(
        level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s", handlers=[logging.StreamHandler()]
    )

def take_datasets():
    logging.info("Take datasets")
    reference_data = pd.read_csv(os.path.join(dataset_path, params["file_name_training_data"]))
    production_data = pd.read_csv(os.path.join(dataset_path, params["file_name_request_data"]))
    
    logging.info("Reference dataset was created with %s rows", reference_data.shape[0])
    logging.info("Production dataset was created with %s rows", production_data.shape[0])

    for single in [reference_data, production_data]:
        for column in single.columns:
            if column not in header_params["header"]:
                single.drop(column, axis=1, inplace=True)

    logging.info("Save datasets to %s", dataset_path)
    reference_data.to_csv(os.path.join(dataset_path, params["file_name_training_data_clean"]), index=False)
    production_data.to_csv(os.path.join(dataset_path, params["file_name_request_data_clean"]), index=False)

def main():
    take_datasets()


if __name__ == "__main__":
    setup_logger()
    main()
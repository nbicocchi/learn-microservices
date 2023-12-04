"""
This is a boilerplate pipeline 'data_science'
generated using Kedro 0.18.2
"""
from kedro.io import *
import logging

from typing import Dict, Tuple
import os, json, pickle

import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.model_selection import GridSearchCV
from sklearn import metrics

import matplotlib.pyplot as plt
from datetime import datetime
import seaborn as sns
import numpy as np
import mlflow
from mlflow import sklearn

import bentoml

def split_data(data: pd.DataFrame, parameters: Dict) -> Tuple:
    """Splits data into features and targets training (60%), test (20%) and validation (20%) sets.
    Args:
        data: Data containing features and target.
        parameters: Parameters defined in parameters/data_science.yml.
    Returns:
        Split data.
    """
    X = data[parameters["features"]]
    y = data["Quality"]

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=parameters["test_size"], random_state=parameters["random_state"])
    X_train, X_val, y_train, y_val = train_test_split(X_train, y_train, test_size=parameters["val_size"], random_state=parameters["random_state"])

    return X_train, X_test, X_val, y_train, y_test, y_val


def train_model(X_train: pd.DataFrame, y_train: pd.Series, parameters: Dict) -> RandomForestRegressor:
    """Trains the model.
    Args:
        X_train: Training data of independent features.
        y_train: Training data for price.
    Returns:
        Trained model.
    """
    mlflow.log_artifact(local_path=os.path.join("data", "01_raw", "DATA.csv"))

    regressor = RandomForestRegressor(max_depth=parameters["max_depth"], random_state=parameters["random_state"])
    regressor.fit(X_train, y_train)

    ############################
    ## Hyperparameters Tuning ##
    ############################

    # define search space
    space = dict()
    space['max_depth'] = [1,2,3]
    space['random_state'] = [41,42,43,44]

    # define search
    search = GridSearchCV(regressor, space, scoring='neg_mean_absolute_error')
    # execute search
    result = search.fit(X_train, y_train)

    # summarize result
    logger = logging.getLogger(__name__)
    logger.info("Hyperparameters tuning. Best Score: %s", result.best_score_)
    logger.info("Hyperparameters tuning. Best Hyperparameters: %s", result.best_params_)
    
    ####################
    ## Training model ##
    ####################

    # update parameters
    parameters["max_depth"] = result.best_params_["max_depth"]
    parameters["random_state"] = result.best_params_["random_state"]

    #update model
    regressor = RandomForestRegressor(max_depth=parameters["max_depth"], random_state=parameters["random_state"])
    regressor.fit(X_train, y_train)

    # saving model
    sklearn.log_model(sk_model=regressor, artifact_path="model")

    # logging params
    mlflow.log_param('test_size', parameters["test_size"])
    mlflow.log_param('val_size', parameters["val_size"])
    mlflow.log_param('max_depth', parameters["max_depth"])
    mlflow.log_param('random_state', parameters["random_state"])

    # logging name reference dataset (DATA.csv) in Google Drive
    with open(os.path.join("files", os.getcwd(),'data','01_raw','DATA.csv.dvc', ), "r") as f:
        lines = f.readlines()
        count = 0
        for line in lines:
            if (count == 1):             
                mlflow.set_tag('dataset_original', str(line)[7:])
                break
            count += 1

    # Report training set score
    train_score = regressor.score(X_train, y_train) * 100

    logger.info("Model has a accurancy of %.3f on train data.", train_score)
    
    return regressor, {"test_size": parameters["test_size"], "val_size": parameters["val_size"], "max_depth": parameters["max_depth"], "random_state": parameters["random_state"]}


def evaluate_model(regressor: RandomForestRegressor, X_val: pd.DataFrame, y_val: pd.Series) -> Dict[str, float]:
    """Calculates and logs the coefficient of determination.
    Args:
        regressor: Trained model.
        X_val: Valuate data of independent features.
        y_val: Valuate data for quality.
    Returns:
        Values from predict.
    """
    # score returns the coefficient of determination of the prediction. Best possible score is 1.0, lower values are worse.
    # we multiply it * 100, so the best score is 100.
    score = regressor.score(X_val, y_val) * 100

    y_pred = regressor.predict(X_val)
    # MAE to measure errors between the predicted value and the true value.
    mae = metrics.mean_absolute_error(y_val, y_pred)
    # MSE to average squared difference between the predicted value and the true value.
    mse = metrics.mean_squared_error(y_val, y_pred)
    # ME to capture the worst-case error between the predicted value and the true value.
    me = metrics.max_error(y_val, y_pred)
    
    logger = logging.getLogger(__name__)
    logger.info("Model has a accurancy of %.3f on validation data.", score)

    # logging evaluation metrics
    mlflow.log_metric("accuracy", score)
    mlflow.log_metric("mean_absolute_erro", mae)
    mlflow.log_metric("mean_squared_error", mse)
    mlflow.log_metric("max_error", me)
    mlflow.log_param("time of prediction", str(datetime.now()))
    mlflow.set_tag("Model Type", "Random Forest")
    
    return {"accurancy": score, "mean_absolute_error": mae, "mean_squared_error": mse, "max_error": me}


def testing_model(regressor: RandomForestRegressor, X_test: pd.DataFrame, y_test: pd.Series) -> RandomForestRegressor:
    """Diagnose the source issue when they fail. Testing code, data, models.
        Unit testing, integration testing.
        Performing the final “Model Acceptance Test” by using the hold backtest dataset to estimate the generalization error
        compare the model with its previous version
     Args:
        regressor: Trained model.
        X_test: Test data of independent features.
        y_test: Test data for quality.
    Returns:
        Values from testing versions.
    """
    test_accuracy = regressor.score(X_test, y_test) * 100
    y_pred = regressor.predict(X_test)

    # See older versions data
    best_version = 'new version'

    versions_differnce = {}

    for root, dirnames, filenames in os.walk(os.path.join("files", os.getcwd(),'data','09_tracking','metrics.json')):
        for dirname in dirnames:
            with open(os.path.join("files", os.getcwd(),'data','09_tracking','metrics.json', dirname , 'metrics.json'), "r") as f:
                old_data = json.load(f)
                versions_differnce[dirname] = old_data['accurancy']
                
                if (old_data['accurancy'] > test_accuracy):
                    test_accuracy = old_data['accurancy']
                    best_version = dirname
    
    # Write directory name last version 
    with open(os.path.join(os.getcwd(), 'data', "last_version.txt"), 'w') as outfile:
        outfile.write(dirname)
    
    versions_differnce[dirname] = test_accuracy

    mlflow.log_artifact(local_path=os.path.join("data", "02_intermediate", "exploration_activities.json", dirname ,"exploration_activities.json"))
    mlflow.log_artifact(local_path=os.path.join("data", "03_primary", "preprocessed_activities.csv", dirname ,"preprocessed_activities.csv"))
    mlflow.log_artifact(local_path=os.path.join("data", "04_feature", "model_input_table.csv", dirname ,"model_input_table.csv"))
    mlflow.set_tag("Model Version", dirname)
    mlflow.set_tag("mlflow.runName", dirname)
    mlflow.sklearn.save_model(regressor, os.path.join(os.getcwd(), 'my_model', dirname))
    with open(os.path.join(os.getcwd(), 'my_model', dirname, "MLmodel"), 'a') as model_file:        
        model_file.write("model_version: {}".format(dirname))
     
    bentoml.mlflow.import_model("my_model", model_uri= os.path.join(os.getcwd(), 'my_model', dirname))

    logger = logging.getLogger(__name__)
    if (best_version != 'new version'):
        logger.info("ATTENTION!!!\nMODEL VERSION  %s have best metrics.", best_version)
        mlflow.set_tag("Is Model Version Best", "No")
        mlflow.set_tag("Model Version with best metrics", best_version)
    else:
        mlflow.set_tag("Is Model Version Best", "Yes")
        mlflow.set_tag("Model Version with best metrics", "-")
        logger.info("This model version is the best.")

    return versions_differnce


def plot_feature_importance(regressor: RandomForestRegressor, data: pd.DataFrame) -> pd.DataFrame:
    """Create plot of feature importance and save into png
     Args:
        regressor: Trained model.
        data: Data containing features and target.
    """
    # Calculate feature importance in random forest
    importances = regressor.feature_importances_
    labels = data.columns
    feature_data = pd.DataFrame(list(zip(labels, importances)), columns = ["feature","importance"])
    feature_data = feature_data.sort_values(by='importance', ascending=False,)
    
    # image formatting
    axis_fs = 16 #fontsize
    title_fs = 22 #fontsize
    sns.set(style="whitegrid")

    ax = sns.barplot(x="importance", y="feature", data=feature_data)
    ax.set_xlabel('Importance',fontsize = axis_fs) 
    ax.set_ylabel('Feature', fontsize = axis_fs)#ylabel
    ax.set_title('Random forest\nfeature importance', fontsize = title_fs)

    plt.tight_layout()
    plt.savefig(os.path.join("files", os.getcwd(),'data','08_reporting','feature_importance.png'), dpi=120)
    plt.close()

    return feature_data


def plot_residuals(regressor: RandomForestRegressor, X_test: pd.DataFrame, y_test: pd.Series) -> pd.DataFrame:
    """Create plot of residuals and save into png
    A residual is a measure of how far away a point is vertically from the regression line. 
    Simply, it is the error between a predicted value and the observed actual value.
     Args:
        regressor: Trained model.
        X_test: Testing data of independent features.
        y_test: Testing data for price.
    """
    y_pred = regressor.predict(X_test) + np.random.normal(0,0.25,len(y_test))
    y_jitter = y_test + np.random.normal(0,0.25,len(y_test))
    res_df = pd.DataFrame(list(zip(y_jitter,y_pred)), columns = ["true_","pred"])

    axis_fs = 16 #fontsize
    title_fs = 22 #fontsize

    ax = sns.scatterplot(x="true_", y="pred",data=res_df)
    ax.set_aspect('equal')
    ax.set_xlabel('True activities quality',fontsize = axis_fs) 
    ax.set_ylabel('Predicted activities quality', fontsize = axis_fs)#ylabel
    ax.set_title('Residuals', fontsize = title_fs)

    # Make it pretty- square aspect ratio
    ax.plot()
    #plt.ylim((3,7))
    #plt.xlim((-2,12))

    plt.tight_layout()
    plt.savefig(os.path.join("files", os.getcwd(),'data','08_reporting','residuals.png'), dpi=120)
    plt.close()

    mlflow.log_artifact(local_path=os.path.join("data", "08_reporting", "feature_importance.png"))
    mlflow.log_artifact(local_path=os.path.join("data", "08_reporting", "residuals.png"))

    return res_df

def plot_differences(test_difference: json) -> pd.DataFrame:
    """Create plot of residuals and save into png
    A residual is a measure of how far away a point is vertically from the regression line. 
    Simply, it is the error between a predicted value and the observed actual value.
     Args:
        regressor: Trained model.
        X_test: Testing data of independent features.
        y_test: Testing data for price.
    """

    xAxis = [key for key, value in test_difference.items()]
    yAxis = [value for key, value in test_difference.items()]
    diff_df = pd.DataFrame(list(zip(xAxis,yAxis)), columns = ["versions","accurancy"])

    ## BAR GRAPH ##
    fig = plt.figure()
    plt.bar(xAxis,yAxis, color='green')
    plt.xlabel('versions')
    plt.ylabel('accurancy')
    plt.title('Accurancies between versions')
    plt.xticks(rotation=45, ha='right')
    plt.tight_layout()
    plt.savefig(os.path.join("files", os.getcwd(),'data','10_testing','plot_difference.png'), dpi=120)

    mlflow.log_artifact(local_path=os.path.join("data", "10_testing", "plot_difference.png"))

    return diff_df

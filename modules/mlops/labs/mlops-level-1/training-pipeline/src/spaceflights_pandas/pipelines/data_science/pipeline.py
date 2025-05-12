from kedro.pipeline import Pipeline, node, pipeline

from .nodes import evaluate_model_and_log_to_mlflow, split_data, train_model


def create_pipeline(**kwargs) -> Pipeline:
    return pipeline(
        [
            node(
                func=split_data,
                inputs=["model_input_table", "params:model_options"],
                outputs=["X_train", "X_test", "y_train", "y_test"],
                name="split_data_node",
            ),
            node(
                func=train_model,
                inputs=["X_train", "y_train"],
                outputs="regressor",
                name="train_model_node",
            ),
            node(
                func=evaluate_model_and_log_to_mlflow,
                inputs=["params:model_options", "regressor", "X_train", "y_train", "X_test", "y_test"],
                outputs=None,
                name="evaluate_model_and_log_to_mlflow",
            ),
        ]
    )

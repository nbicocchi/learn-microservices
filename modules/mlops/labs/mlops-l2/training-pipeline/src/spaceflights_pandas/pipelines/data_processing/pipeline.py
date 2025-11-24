from kedro.pipeline import Pipeline, node, pipeline

from .nodes import create_model_input_table, preprocess_companies, preprocess_shuttles
from .nodes import load_and_merge_from_minio, merge_with_model_input


def create_pipeline(**kwargs) -> Pipeline:
    return pipeline(
        [
            node(
                func=preprocess_companies,
                inputs="companies",
                outputs="preprocessed_companies",
                name="preprocess_companies_node",
            ),
            node(
                func=preprocess_shuttles,
                inputs="shuttles",
                outputs="preprocessed_shuttles",
                name="preprocess_shuttles_node",
            ),
            node(
                func=create_model_input_table,
                inputs=["preprocessed_shuttles", "preprocessed_companies", "reviews"],
                outputs="model_input_table",
                name="create_model_input_table_node",
            ),
            node(
                func=load_and_merge_from_minio,
                inputs=["params:minio_bucket"],
                outputs="collected_data",
                name="load_collected_from_minio_node",
            ),
            node(
                func=merge_with_model_input,
                inputs=["model_input_table", "collected_data"],
                outputs="final_input_table",
                name="merge_final_table_node",
            ),
        ]
    )

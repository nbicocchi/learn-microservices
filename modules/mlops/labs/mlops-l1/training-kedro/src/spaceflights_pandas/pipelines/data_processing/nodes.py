import pandas as pd
import s3fs
import os


def _is_true(x: pd.Series) -> pd.Series:
    return x == "t"


def _parse_percentage(x: pd.Series) -> pd.Series:
    x = x.str.replace("%", "")
    x = x.astype(float) / 100
    return x


def _parse_money(x: pd.Series) -> pd.Series:
    x = x.str.replace("$", "").str.replace(",", "")
    x = x.astype(float)
    return x


def preprocess_companies(companies: pd.DataFrame) -> pd.DataFrame:
    """Preprocesses the data for companies.

    Args:
        companies: Raw data.
    Returns:
        Preprocessed data, with `company_rating` converted to a float and
        `iata_approved` converted to boolean.
    """
    companies["iata_approved"] = _is_true(companies["iata_approved"])
    companies["company_rating"] = _parse_percentage(companies["company_rating"])
    return companies


def preprocess_shuttles(shuttles: pd.DataFrame) -> pd.DataFrame:
    """Preprocesses the data for shuttles.

    Args:
        shuttles: Raw data.
    Returns:
        Preprocessed data, with `price` converted to a float and `d_check_complete`,
        `moon_clearance_complete` converted to boolean.
    """
    shuttles["d_check_complete"] = _is_true(shuttles["d_check_complete"])
    shuttles["moon_clearance_complete"] = _is_true(shuttles["moon_clearance_complete"])
    shuttles["price"] = _parse_money(shuttles["price"])
    return shuttles


def create_model_input_table(
    shuttles: pd.DataFrame, companies: pd.DataFrame, reviews: pd.DataFrame
) -> pd.DataFrame:
    """Combines all data to create a model input table.

    Args:
        shuttles: Preprocessed data for shuttles.
        companies: Preprocessed data for companies.
        reviews: Raw data for reviews.
    Returns:
        Model input table.
    """

    rated_shuttles = shuttles.merge(reviews, left_on="id", right_on="shuttle_id")
    rated_shuttles = rated_shuttles.drop("id", axis=1)
    model_input_table = rated_shuttles.merge(
        companies, left_on="company_id", right_on="id"
    )
    model_input_table = model_input_table.dropna()
    return model_input_table


def load_and_merge_from_minio(bucket: str) -> pd.DataFrame:
    """
    Load all csv files from a given MinIO bucket and prefix, and merge them into a single DataFrame.
    """
    MINIO_ROOT_USER = os.getenv("MINIO_ROOT_USER", "minioadmin")
    MINIO_ROOT_PASSWORD = os.getenv("MINIO_ROOT_PASSWORD", "minioadmin")
    MINIO_URL = os.getenv("MINIO_URL", "http://localhost:9000")

    try:
        fs = s3fs.S3FileSystem(
            key=MINIO_ROOT_USER,
            secret=MINIO_ROOT_PASSWORD,
            client_kwargs={"endpoint_url": MINIO_URL}
        )
        files = fs.glob(f"{bucket}/current/*.csv")
    except Exception as e:
        print(f"Unable to connect to MinIO at {MINIO_URL}: {e}")
        return pd.DataFrame()

    if not files:
        print("No files found in the specified bucket and prefix.")
        return pd.DataFrame()

    print(f"Found {len(files)} files.")

    dfs = []
    for file in files:
        try:
            with fs.open(file, "rb") as f:
                df = pd.read_csv(f)
                if 'prediction' in df.columns:
                    df = df.drop(columns=['prediction'])
                dfs.append(df)
        except Exception as e:
            print(f"Failed to read file {file}: {e}")
    if dfs:
        return pd.concat(dfs, ignore_index=True)
    else:
        return pd.DataFrame()


def merge_with_model_input(model_input_table: pd.DataFrame, collected: pd.DataFrame) -> pd.DataFrame:
    """
    Merge the model input table with newly collected data.
    """
    return pd.concat([model_input_table, collected], ignore_index=True)

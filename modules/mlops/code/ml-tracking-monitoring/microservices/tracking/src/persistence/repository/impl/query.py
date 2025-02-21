import logging
from sqlalchemy import text
from src.persistence.repository.i_query import IQuery

class Query(IQuery):
    def __init__(self, data_db, mlflow):

        self.data_db = data_db
        self.mlflow = mlflow

        self.logger = None
        self.samples_columns_name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)

    def select_joined_conditioned_value(self, table_1, table_2, on_1, on_2, condition):
        """
        Select the joined conditioned value.
        :param table_1: The first table
        :param table_2: The second table
        :param on_1: The first on
        :param on_2: The second on
        :param condition: The condition
        :return records: The records
        """

        result = self.data_db.session.execute(text('''
        SELECT * FROM {} join {}
        on ({}.{} = {}.{})
        where {} = {};
        '''.format(
            table_1, table_2,
            table_1, on_1,
            table_2, on_2,
            "dataset_id", condition
        )))
        records = result.fetchall()
        print(len(records))
        return records

    def select_value(self, table):
        """
        Select the value.
        :param table: The table
        :return records: The records
        """

        result = self.data_db.session.execute(text(f"SELECT * FROM {table};"))
        records = result.fetchall()
        self.logger.info(f"Records fetched from {table}: {len(records)}")
        return records

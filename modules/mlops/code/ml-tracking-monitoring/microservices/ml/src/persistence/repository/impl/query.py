import logging
from sqlalchemy import text

from src.persistence.repository.i_query import IQuery


class Query(IQuery):
    def __init__(self, db):
        self.db = db

        self.logger = None

        self.samples_columns_name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)

    def insert_predictions_records(self, records):
        """
        Insert or update prediction records in the predictions table.
        
        :param records: DataFrame containing prediction records with columns matching the predictions table
        """

        for r in records.iterrows():
            self.db.session.execute(text('''
            INSERT INTO {}({}, {}, {})
            VALUES ({}, {}, {})
            ON CONFLICT (sample_index) DO UPDATE SET
                class = EXCLUDED.class;
            '''.format(
                "predictions",
                *records.columns,
                *r[1]
            )))
        self.db.session.commit()

    def select_joined_conditioned_value(self, table_1, table_2, on_1, on_2, condition):
        """
        Select joined conditioned value from two tables.
        
        :param table_1: First table name
        :param table_2: Second table name
        :param on_1: First table column to join on
        :param on_2: Second table column to join on
        :param condition: Condition to filter the joined records
        :return: List of tuples containing the joined records
        """

        result = self.db.session.execute(text('''
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
        Select all records from a table.
        
        :param table: Table name
        :return: List of tuples containing the records
        """

        result = self.db.session.execute(text('''
        SELECT * FROM {};
        '''.format(
            table
        )))
        records = result.fetchall()
        print(len(records))
        for i, r in enumerate(records):
            print(i, type(r), r)

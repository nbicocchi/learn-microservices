import logging
from sqlalchemy import text

from persistence.repository.i_query import IQuery


class Query(IQuery):
    def __init__(self, db):
        self.db = db

        self.logger = None

        self.samples_columns_name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)

    def select_joined_conditioned_value(self, table_1, table_2, table_3, on_1, on_2, on_3, condition):
        """
        Select joined conditioned value from three tables.
        
        :param table_1: First table name
        :param table_2: Second table name
        :param table_3: Third table name
        :param on_1: First table column to join on
        :param on_2: Second table column to join on
        :param on_3: Third table column to join on
        :param condition: Condition to filter the joined records
        :return: List of tuples containing the joined records
        """

        result = self.db.session.execute(text('''
        SELECT * FROM {} join {}
        on ({}.{} = {}.{})
        join {}
        on ({}.{} = {}.{})
        where {} = {};
        '''.format(
            table_1, table_2,
            table_1, on_1,
            table_2, on_2,
            table_3,
            table_1, on_1,
            table_3, on_3,
            "dataset_id", condition
        )))
        records = result.fetchall()
        print(len(records))
        return records

import logging

from persistence.repository.i_query import IQuery


class Query(IQuery):
    def __init__(self, db):
        self.db = db

        self.logger = None
        self.cursor = None

        self.samples_columns_name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)

    def select_joined_conditioned_value(self, table_1, table_2, table_3, on_1, on_2, on_3, condition):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
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
        ))
        records = self.cursor.fetchall()
        self.cursor.close()
        print(len(records))
        '''for i, r in enumerate(records):
            print(i, type(r), r)'''
        return records

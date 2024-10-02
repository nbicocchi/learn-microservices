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

    def insert_predictions_records(self, records):
        self.cursor = self.db.connection.cursor()
        for r in records.iterrows():
            self.cursor.execute('''
            insert into {}({}, {}, {})
            values ({}, {}, {});
            '''.format(
                "predictions",
                *records.columns,
                *r[1]
            ))
        self.db.connection.commit()
        self.cursor.close()

    def select_joined_conditioned_value(self, table_1, table_2, on_1, on_2, condition):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
        SELECT * FROM {} join {}
        on ({}.{} = {}.{})
        where {} = {};
        '''.format(
            table_1, table_2,
            table_1, on_1,
            table_2, on_2,
            "dataset_id", condition
        ))
        records = self.cursor.fetchall()
        self.cursor.close()
        print(len(records))
        '''for i, r in enumerate(records):
            print(i, type(r), r)'''
        return records

    def select_value(self, table):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
        select * from {};
        '''.format(
            table
        ))
        records = self.cursor.fetchall()
        self.cursor.close()
        print(len(records))
        for i, r in enumerate(records):
            print(i, type(r), r)

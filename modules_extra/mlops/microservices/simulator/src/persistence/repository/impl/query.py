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

    def create_datasets_table(self):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
                create table if not exists {}(
                {} int not null unique,
                {} varchar(255) unique,
                primary key ({})
                );
                '''.format(
            "datasets",
            "dataset_id",
            "name",
            "dataset_id"
        ))
        self.db.connection.commit()
        self.cursor.close()

    def create_samples_table(self):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
                create table if not exists {}(
                {} int not null unique auto_increment,
                {} int not null,
                {} int not null unique,
                {} float, {} float, {} float, {} float, {} float, {} float,
                {} float, {} float, {} float, {} float, {} float, {} float, {} float,
                primary key ({}),
                foreign key ({}) references {}({}) on delete cascade on update cascade
                );
                '''.format(
            "samples",
            "sample_id",
            "dataset_id",
            "sample_index",
            *self.samples_columns_name,
            "sample_id",
            "dataset_id", "datasets", "dataset_id"
        ))
        self.db.connection.commit()
        self.cursor.close()

    def create_predictions_table(self):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
                create table if not exists {}(
                {} int not null unique auto_increment,
                {} int not null unique,
                {} int not null unique,
                {} int,
                primary key ({}),
                foreign key ({}) references {}({}) on delete cascade on update cascade
                );
                '''.format(
            "predictions",
            "prediction_id",
            "sample_index",
            "prediction_index",
            "class",
            "prediction_id",
            "sample_index", "samples", "sample_index"
        ))
        self.db.connection.commit()
        self.cursor.close()

    def create_targets_table(self):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
                create table if not exists {}(
                {} int not null unique auto_increment,
                {} int not null unique,
                {} int not null unique,
                {} int,
                primary key ({}),
                foreign key ({}) references {}({}) on delete cascade on update cascade
                );
                '''.format(
            "targets",
            "target_id",
            "sample_index",
            "target_index",
            "class",
            "target_id",
            "sample_index", "samples", "sample_index"
        ))
        self.db.connection.commit()
        self.cursor.close()

    def insert_dataset_records(self, records):
        self.cursor = self.db.connection.cursor()
        for r in records.iterrows():
            self.cursor.execute('''
            insert into {}({}, {})
            values ({}, '{}');
            '''.format(
                "datasets",
                *records.columns,
                *r[1]
            ))
        self.db.connection.commit()
        self.cursor.close()

    def insert_samples_records(self, records):
        self.cursor = self.db.connection.cursor()
        for r in records.iterrows():
            self.cursor.execute('''
            insert into {}({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
            values ({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {});
            '''.format(
                "samples",
                *records.columns,
                *r[1]
            ))
        self.db.connection.commit()
        self.cursor.close()

    def insert_predictions_records(self, records):
        self.cursor = self.db.connection.cursor()
        self.db.connection.commit()
        self.cursor.close()

    def insert_targets_records(self, records):
        self.cursor = self.db.connection.cursor()
        for r in records.iterrows():
            self.cursor.execute('''
            insert into {}({}, {}, {})
            values ({}, {}, {});
            '''.format(
                "targets",
                *records.columns,
                *r[1]
            ))
        self.db.connection.commit()
        self.cursor.close()

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
        '''for i, r in enumerate(records):
            print(i, type(r), r)'''

    def select_condition_value(self, table, condition):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
        select * from {}
        where dataset_id = {};
        '''.format(
            table, condition
        ))
        records = self.cursor.fetchall()
        self.cursor.close()
        print(len(records))

    def select_joined_value(self, table_1, table_2, on_1, on_2):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
        SELECT * FROM {} join {}
        on ({}.{} = {}.{});
        '''.format(
            table_1, table_2,
            table_1, on_1,
            table_2, on_2
        ))
        records = self.cursor.fetchall()
        self.cursor.close()
        print(len(records))
        for i, r in enumerate(records):
            print(i, type(r), r)

    def delete_values(self, table):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
        delete from {};
        '''.format(
            table
        ))
        self.db.connection.commit()
        self.cursor.close()

    def describe_table(self, table):
        self.cursor = self.db.connection.cursor()
        self.cursor.execute('''
        describe {};
        '''.format(
            table
        ))
        records = self.cursor.fetchall()
        self.cursor.close()
        print(table)
        for i, r in enumerate(records):
            print(r)

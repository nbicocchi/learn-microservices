import logging
import pandas as pd
from json import loads

from service.i_service import IService
from persistence.model.i_model import IModel
from persistence.repository.impl.query import Query


class MonitoringService(IService):
    def __init__(self, monitoring_model: IModel, db=None):
        self.monitoring_model = monitoring_model
        self.db = db

        self.logger = None
        self.query = None

        self.reference = None
        self.current = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        self.init_query()

    def init_query(self):
        self.query = Query(self.db)
        #self.query.samples_columns_name = self.monitoring_model.columns_name

    def compute_metric(self, step, tests=None):
        result = {}
        if step == "report":
            result = self.report()
        elif step == "tests":
            result = self.tests()
        elif step == "summary":
            result = self.summary(tests)

        return result

    def report(self):
        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        result = self.compute_report(self.reference, self.current)

        return result

    def tests(self):
        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        result = self.compute_tests(self.reference, self.current)

        return result

    def summary(self, tests):
        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        self.monitoring_model.define_summary(tests)
        result = self.compute_summary(self.reference, self.current)

        return result

    def define_set(self, dataset_id):
        records = self.query.select_joined_conditioned_value(
            "samples", "targets", "predictions",
            "sample_index", "sample_index", "sample_index",
            dataset_id
        )
        records = pd.DataFrame(records)
        records = records.drop(columns=[
            "dataset_id", "sample_id", "target_id",
            "sample_index", "target_index", "targets.sample_index",
            "prediction_id", "prediction_index", "predictions.sample_index"
        ])
        records = records.rename(columns={"class": "target", "predictions.class": "prediction"})
        return records

    def compute_report(self, reference, current):
        self.monitoring_model.report.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.report.json()
        result = loads(result)

        return result

    def compute_tests(self, reference, current):
        self.monitoring_model.tests.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.tests.json()
        result = loads(result)

        return result

    def compute_summary(self, reference, current):
        self.monitoring_model.summary.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.summary.json()
        result = loads(result)
        result = result["summary"]

        return result

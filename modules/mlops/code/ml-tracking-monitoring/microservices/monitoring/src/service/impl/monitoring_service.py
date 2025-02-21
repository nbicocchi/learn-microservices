import logging
import pandas as pd
from json import loads

from service.i_service import IService
from persistence.model.i_model import IModel
from persistence.repository.impl.query import Query


class MonitoringService(IService):
    def __init__(self, monitoring_model: IModel, db=None):
        """
        Initialize MonitoringService instance.
        
        :param monitoring_model: Model instance implementing IModel interface for monitoring
        :param db: Database connection object, defaults to None
        """
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

    def compute_metric(self, step, tests=None):
        """
        Compute monitoring metrics based on specified step.
        
        :param step: Type of metric to compute. Must be one of: 'report', 'tests', or 'summary'
        :param tests: Dictionary of test configurations when step is 'summary'
        :return: Dictionary containing computed metrics or test results
        :raises ValueError: If step is not one of the valid options
        """

        result = {}
        if step == "report":
            result = self.report()
        elif step == "tests":
            result = self.tests()
        elif step == "summary":
            result = self.summary(tests)

        return result

    def report(self):
        """
        Compute the report.
        :return: The report
        """

        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        result = self.compute_report(self.reference, self.current)

        return result

    def tests(self):
        """
        Compute the tests.
        :return: The tests
        """

        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        result = self.compute_tests(self.reference, self.current)

        return result

    def summary(self, tests):
        """
        Compute the summary.
        :param tests: The tests to compute the summary for
        :return: The summary
        """

        self.reference = self.define_set(2)
        self.current = self.define_set(3)
        self.monitoring_model.define_summary(tests)
        result = self.compute_summary(self.reference, self.current)

        return result

    def define_set(self, dataset_id):
        """
        Define and prepare dataset for monitoring.
        
        :param dataset_id: ID of the dataset to process
        :return: Preprocessed DataFrame containing samples, targets, and predictions
        :raises ValueError: If dataset_id is invalid
        """

        records = self.query.select_joined_conditioned_value(
            "samples", "targets", "predictions",
            "sample_index", "sample_index", "sample_index",
            dataset_id
        )
        records = pd.DataFrame(records)

        columns = records.columns.to_list()
        # Rename the second occurrence of sample_index to avoid duplication
        sample_index_idx = [i for i, s in enumerate(columns) if "sample_index" in s]
        columns[sample_index_idx[1]] = "targets.sample_index"
        columns[sample_index_idx[2]] = "predictions.sample_index"

        class_idx = [i for i, s in enumerate(columns) if "class" in s]
        columns[class_idx[0]] = "target"
        columns[class_idx[1]] = "prediction"

        records.columns = columns

        records = records.drop(columns=[
            "dataset_id", "sample_id", "target_id",
            "sample_index", "target_index", "targets.sample_index",
            "prediction_id", "prediction_index", "predictions.sample_index"
        ])
        return records

    def compute_report(self, reference, current):
        """
        Compute monitoring report comparing reference and current datasets.
        
        :param reference: Reference dataset DataFrame
        :param current: Current dataset DataFrame
        :return: Dictionary containing monitoring report results
        """

        self.monitoring_model.report.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.report.json()
        result = loads(result)

        return result

    def compute_tests(self, reference, current):
        """
        Run monitoring tests comparing reference and current datasets.
        
        :param reference: Reference dataset DataFrame
        :param current: Current dataset DataFrame
        :return Dictionary containing test results
        """

        self.monitoring_model.tests.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.tests.json()
        result = loads(result)

        return result

    def compute_summary(self, reference, current):
        """
        Generate monitoring summary comparing reference and current datasets.
        
        :param reference: Reference dataset DataFrame
        :param current: Current dataset DataFrame
        :return: Dictionary containing summary metrics
        """

        self.monitoring_model.summary.run(
            reference_data=reference,
            current_data=current,
            column_mapping=self.monitoring_model.column_mapping
        )
        result = self.monitoring_model.summary.json()
        result = loads(result)
        result = result["summary"]

        return result

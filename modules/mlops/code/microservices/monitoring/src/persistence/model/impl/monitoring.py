import logging

from evidently import ColumnMapping
from evidently.options import ColorOptions

from evidently.metrics import *
from evidently.report import Report
from evidently.metric_preset import DataDriftPreset, DataQualityPreset, TargetDriftPreset
from evidently.metric_preset import ClassificationPreset

from evidently.tests import *
from evidently.test_suite import TestSuite
from evidently.test_preset import DataDriftTestPreset, DataQualityTestPreset, DataStabilityTestPreset, NoTargetPerformanceTestPreset
from evidently.test_preset import MulticlassClassificationTestPreset

from persistence.model.i_model import IModel


class MonitoringModel(IModel):
    def __init__(self, columns):
        self.columns = columns

        self.logger = None
        self.column_mapping = None
        self.report = None
        self.tests = None
        self.summary = None
        self.summary_tests = None
        self.name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)

        self.define_column_mapping()
        self.define_report()
        self.define_test()
        self.define_summary()
        self.name = "EvidentlyAI"

        self.summary_tests = {
            "data_drift": DataDriftTestPreset(),
            "data_quality": DataQualityTestPreset(),
            "data_stability": DataStabilityTestPreset(),
            "no_target_performance": NoTargetPerformanceTestPreset(),
            "multi_class_classification": MulticlassClassificationTestPreset()
        }

    def define_column_mapping(self):
        self.column_mapping = ColumnMapping()
        self.column_mapping.target = "target"
        # column_mapping.target_names = {0: "first_region", 1: "second_region", 2: "third_region"}
        self.column_mapping.prediction = "prediction"
        self.column_mapping.numerical_features = self.columns
        self.column_mapping.task = "classification"

    def define_report(self):
        self.report = Report(metrics=[
            DataDriftPreset(),
            DataQualityPreset(),
            TargetDriftPreset(),
            ClassificationPreset()
        ])

    def define_test(self):
        self.tests = TestSuite(tests=[
            DataDriftTestPreset(),
            DataQualityTestPreset(),
            DataStabilityTestPreset(),
            NoTargetPerformanceTestPreset(),
            MulticlassClassificationTestPreset()
        ])

    def define_summary(self, tests=None):
        applied_tests = [
            DataDriftTestPreset(),
            DataQualityTestPreset(),
            DataStabilityTestPreset(),
            NoTargetPerformanceTestPreset(),
            MulticlassClassificationTestPreset()
        ]

        if tests is not None:
            applied_tests = []
            for k, v in tests.items():
                if v:
                    applied_tests.append(self.summary_tests[k])
        self.summary = TestSuite(tests=applied_tests)

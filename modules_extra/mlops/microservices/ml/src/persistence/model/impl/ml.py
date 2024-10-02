import logging
from sklearn.tree import DecisionTreeClassifier

from persistence.model.i_model import IModel


class MLModel(IModel):
    def __init__(self, seed=42):
        self.seed = seed

        self.logger = None
        self.model = None
        self.name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        self.model = DecisionTreeClassifier(random_state=self.seed)
        self.name = type(self.model).__name__

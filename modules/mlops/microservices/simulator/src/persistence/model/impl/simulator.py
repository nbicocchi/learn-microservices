import logging

from sklearn.datasets import load_wine

from persistence.model.i_model import IModel


class SimulatorModel(IModel):
    def __init__(self):
        self.logger = None
        self.df = None
        self.target_name = None
        self.columns_name = None
        self.X = None
        self.y = None
        self.name = None

        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)
        data, label = load_wine(return_X_y=True, as_frame=True)
        df = data.join(label.to_frame())
        self.target_name = "target"
        df.columns = [c.replace("/", "__") for c in df.columns]
        self.columns_name = df.drop([self.target_name], axis=1).columns
        self.y = df[self.target_name]
        self.X = df.drop(columns=self.target_name, axis=1)
        self.name = "DataSimulator"

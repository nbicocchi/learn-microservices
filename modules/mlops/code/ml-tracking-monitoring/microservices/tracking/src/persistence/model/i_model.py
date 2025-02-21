import abc


class IModel(abc.ABC):

    def __init__(self):
        self.experiment_name = None

    @abc.abstractmethod
    def initialize(self):
        pass

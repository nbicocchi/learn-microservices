import abc


class IService(abc.ABC):

    def __init__(self):
        self.mlflow = None

    @abc.abstractmethod
    def initialize(self):
        pass

    def init_query(self):
        pass







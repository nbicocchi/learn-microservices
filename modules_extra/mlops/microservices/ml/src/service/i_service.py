import abc


class IService(abc.ABC):

    @abc.abstractmethod
    def initialize(self):
        pass

    @abc.abstractmethod
    def pre_processing(self, X, fit=True):
        pass

    @abc.abstractmethod
    def train(self, X, y):
        pass

    @abc.abstractmethod
    def test(self, X):
        pass

    @abc.abstractmethod
    def score(self):
        pass

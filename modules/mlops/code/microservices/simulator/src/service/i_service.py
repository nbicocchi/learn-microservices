import abc


class IService(abc.ABC):
    @abc.abstractmethod
    def initialize(self):
        pass

    @abc.abstractmethod
    def load_training_set(self):
        pass

    @abc.abstractmethod
    def load_testing_set(self):
        pass

    @abc.abstractmethod
    def load_production_set(self):
        pass

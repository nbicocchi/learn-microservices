import abc


class IModel(abc.ABC):
    @abc.abstractmethod
    def initialize(self):
        pass

import abc


class IQuery(abc.ABC):
    @abc.abstractmethod
    def initialize(self):
        pass

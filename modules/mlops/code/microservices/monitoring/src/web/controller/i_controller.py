import abc


class IController(abc.ABC):
    def initialize(self):
        pass

    @abc.abstractmethod
    def run(self):
        pass

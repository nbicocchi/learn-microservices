import abc


class IService(abc.ABC):

    @abc.abstractmethod
    def initialize(self):
        pass

    @abc.abstractmethod
    def compute_report(self, reference, current):
        pass

    @abc.abstractmethod
    def compute_tests(self, reference, current):
        pass

    @abc.abstractmethod
    def compute_summary(self, reference, current):
        pass

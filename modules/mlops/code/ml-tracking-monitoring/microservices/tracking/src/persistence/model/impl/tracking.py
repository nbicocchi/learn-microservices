import logging
from src.persistence.model.i_model import IModel

class TrackingModel(IModel):

    def __init__(self, seed=42 ):
        self.seed = seed
        self.logger = None
        self.initialize()
        self.initialize()

    def initialize(self):
        self.logger = logging.getLogger(__name__)



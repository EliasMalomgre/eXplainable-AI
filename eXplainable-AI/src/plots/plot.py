from abc import abstractmethod

from matplotlib.figure import Figure


class Plot:
    """
    Base class for all plots
    """
    @abstractmethod
    def explain(self, to_explain, explainer, **kwargs) -> [Figure]:
        pass

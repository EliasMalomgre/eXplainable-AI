from abc import abstractmethod


class Explainer:
    def __init__(self, explainer, **kwargs):
        self.explainer = explainer

    @abstractmethod
    def explain_image(self, to_explain, **kwargs):
        pass

    @abstractmethod
    def explain_csv(self, to_explain, **kwargs):
        pass

from abc import ABC

import shap

from src.explainers.explainer import Explainer


class GradientExplainer(Explainer, ABC):
    def __init__(self, model, background, batch_size=50, local_smoothing=0):
        super().__init__(shap.GradientExplainer(model, background, batch_size=batch_size,
                                                local_smoothing=local_smoothing))

    def explain_image(self, to_explain: [], ranked_outputs: int = None):
        return self.explainer.shap_values(to_explain, ranked_outputs=ranked_outputs)

    def explain_csv(self):
        pass

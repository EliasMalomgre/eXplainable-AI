from abc import ABC

import shap

from src.explainers.explainer import Explainer

import tensorflow as tf
# Without disabling DeepExplainer doesn't work
tf.compat.v1.disable_v2_behavior()


class DeepExplainer(Explainer, ABC):
    def __init__(self, model, background):
        super().__init__(shap.DeepExplainer(model, background))

    def explain_image(self, to_explain, ranked_outputs=None):
        return self.explainer.shap_values(to_explain, check_additivity=False, ranked_outputs=ranked_outputs)

    def explain_csv(self, to_explain, **kwargs):
        return self.explainer.shap_values(to_explain.values, check_additivity=False)

import shap

from config.shap_properties import ShapProperties
from src.explainers.explainer import Explainer


class KernelExplainer(Explainer):
    def __init__(self, model, background):
        super().__init__(shap.KernelExplainer(model.predict, background))
        self.properties: ShapProperties = ShapProperties()

    def explain_image(self, to_explain, **kwargs):
        pass

    def explain_csv(self, to_explain, **kwargs):
        return self.explainer.shap_values(to_explain, nsamples=self.properties.shap_kernel_n_samples)

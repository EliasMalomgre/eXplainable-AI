import numpy as np
import shap
from matplotlib import pyplot as plt
from matplotlib.figure import Figure
from shap import Explanation

from src.plots.plot import Plot


class HeatmapPlot(Plot):

    def explain(self, to_explain, explainer, class_names=None, **kwargs) -> [Figure]:
        shap_values = explainer.explain_csv(to_explain)
        plt.figure()
        if len(shap_values) == 1:
            plt.title(f'Heatmap ({str(len(to_explain))} entries)', loc='center')
            return [self._explain_single_dimensional_heatmap_plot(shap_values[0], to_explain, explainer)]
        else:
            return self._explain_multi_dimensional_heatmap_plot(shap_values, to_explain, explainer, class_names)

    @staticmethod
    def _explain_single_dimensional_heatmap_plot(shap_values, to_explain, explainer):
        explanation = Explanation(shap_values, data=to_explain.values,
                                  # A list of feature names
                                  feature_names=to_explain.columns.values.tolist(),
                                  # For each entry we need a list of expected_values
                                  base_values=np.asarray([explainer.explainer.expected_value for _ in
                                                          range(len(to_explain))]).flatten())
        shap.plots.heatmap(explanation, show=False)
        return plt.gcf()

    def _explain_multi_dimensional_heatmap_plot(self, shap_values, to_explain, explainer, class_names):
        plots: [Figure] = []

        # Generate titles for all single class plots
        titles = [f'Heatmap ({str(len(to_explain))} entries): {str(class_names[i]).capitalize()}' for i in range(len(shap_values))] \
            if class_names is not None else [f'Heatmap ({str(len(to_explain))} entries): Class {i + 1}'
                                             for i in range(len(shap_values))]

        # Plot a single heatmap plot for each class
        for i in range(len(shap_values)):
            plt.figure()
            plt.title(titles[i], loc='center')
            plots.append(self._explain_single_dimensional_heatmap_plot(shap_values[i], to_explain, explainer))

        return plots

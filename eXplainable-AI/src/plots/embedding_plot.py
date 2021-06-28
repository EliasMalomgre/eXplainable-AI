import numpy as np
import shap
from matplotlib import pyplot as plt
from matplotlib.figure import Figure

from src.plots.plot import Plot


class EmbeddingPlot(Plot):

    def explain(self, to_explain, explainer, class_names=None, **kwargs) -> [Figure]:
        """
        Creates a decision plot with incoming data

        :param to_explain:      Data to explain
        :param explainer:       The explainer to use
        :param class_names:     The names of all output names
        :return: A list of all generated plots
        """
        shap_values = explainer.explain_csv(to_explain)
        # Switch between single and multi dimensional decision plot
        if len(shap_values) == 1:
            return self._explain_single_dimensional_embedding_plot(shap_values, to_explain)
        else:
            return self._explain_multi_dimensional_embedding_plot(shap_values, explainer, to_explain, class_names)

    @staticmethod
    def _explain_single_dimensional_embedding_plot(shap_values, to_explain):
        plt.figure()
        plots: [Figure] = []

        # Get feature names
        feature_names = to_explain.columns.values.tolist()

        # Create a embedding plot for each feature name
        for i in range(len(feature_names)):
            plt.figure()
            shap.plots.embedding(feature_names[i], shap_values[0], feature_names=feature_names, show=False)
            plt.title(f'Embedding ({str(len(to_explain))} entries): {str(feature_names[i]).capitalize()}', loc='center')
            plots.append(plt.gcf())
        return plots

    @staticmethod
    def _explain_multi_dimensional_embedding_plot(shap_values, explainer, to_explain, class_names):
        plt.figure()
        plots: [Figure] = []

        # get the expected value of the explainer
        expected_value = explainer.explainer.expected_value

        # Get feature names
        feature_names = to_explain.columns.values.tolist()

        # Generate a title for each class
        titles = class_names if class_names is not None else [f"Class {i + 1}" for i in range(len(expected_value))]

        for i in range(len(titles)):
            # Create a embedding plot for each feature name
            for j in range(len(feature_names)):
                plt.figure()
                shap.plots.embedding(feature_names[j], shap_values[i], feature_names=feature_names, show=False)
                plt.title(f'Embedding ({str(len(to_explain))} entries): {str(titles[i]).capitalize()} - {feature_names[j]}', loc='center')
                plots.append(plt.gcf())
        return plots
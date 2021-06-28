import numpy as np
import shap
from matplotlib import pyplot as plt
from matplotlib.figure import Figure
from shap import Explanation

from src.plots.plot import Plot


class ScatterPlot(Plot):

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
            return self._explain_single_dimensional_scatter_plot(shap_values, explainer, to_explain)
        else:
            return self._explain_multi_dimensional_scatter_plot(shap_values, explainer, to_explain, class_names)

    @staticmethod
    def _explain_single_dimensional_scatter_plot(shap_values, explainer, to_explain):
        plt.figure()
        plots: [Figure] = []

        # Get feature names
        feature_names = to_explain.columns.values.tolist()

        explanation = Explanation(shap_values[0], data=to_explain.values,
                                  # For each entry we need a list of feature names
                                  feature_names=[feature_names for _ in range(len(to_explain))],
                                  # For each entry we need a list of expected_values
                                  base_values=np.asarray([explainer.explainer.expected_value for _ in
                                                          range(len(to_explain))]).flatten())

        # For each entry create a plot
        for i in range(len(feature_names)):
            plt.figure()
            exp = explanation[:, feature_names[i]]
            exp.data = np.array(exp.data)
            exp.values = np.array(exp.values)
            shap.plots.scatter(exp, show=False)
            plt.title(f'Scatter ({str(len(to_explain))} entries): {str(feature_names[i])}', loc='center')
            plots.append(plt.gcf())
        return plots

    @staticmethod
    def _explain_multi_dimensional_scatter_plot(shap_values, explainer, to_explain, class_names):
        plt.figure()
        plots: [Figure] = []

        # get the expected value of the explainer
        expected_value = explainer.explainer.expected_value

        # Get feature names
        feature_names = to_explain.columns.values.tolist()

        # Generate a title for each class
        titles = class_names if class_names is not None else [f'Class {i + 1}' for i in range(len(expected_value))]

        for i in range(len(titles)):
            # Create shap Explanation object
            explanation = Explanation(shap_values[i], data=to_explain.values,
                                      # For each entry we need a list of feature names
                                      feature_names=[feature_names for _ in range(len(to_explain))],
                                      # For each entry we need a list of expected_values
                                      base_values=np.asarray([expected_value for _ in
                                                              range(len(to_explain))]).flatten())
            # Create a scatter plot for each feature name
            for j in range(len(feature_names)):
                plt.figure()
                exp = explanation[:, feature_names[j]]
                exp.data = np.array(exp.data)
                exp.values = np.array(exp.values)
                shap.plots.scatter(exp, show=False)
                plt.title(f'Scatter ({str(len(to_explain))} entries): {titles[i].capitalize()} - '
                          f'{str(feature_names[j])}', loc='center')
                plots.append(plt.gcf())
        return plots

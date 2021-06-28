import shap
from matplotlib import pyplot as plt
from matplotlib.figure import Figure

from src.plots.plot import Plot

import tensorflow as tf

tf.compat.v1.disable_v2_behavior()


class SummaryPlot(Plot):
    """
    All logic to calculate shap values and use them in a summary plot
    """

    def explain(self, to_explain, explainer, class_names=None, **kwargs) -> [Figure]:
        """
        Creates a summary plot of calculated shap values

        :param to_explain:      The dataframe to explain
        :param explainer:       The explainer which has to be used
        :param class_names:     The names of all the outputs
        :param kwargs:          Extra params
        :return: A figure of the summary plot
        """
        # Calculate shap values
        shap_values = explainer.explain_csv(to_explain)
        plt.figure()
        if len(shap_values) == 1:
            plt.title("Summary (" + str(len(to_explain)) + " entries)", loc='center')
            return [self._explain_single_dimensional_summary_plot(shap_values[0], to_explain)]
        else:
            return self._explain_multi_dimensional_summary_plot(shap_values, to_explain, class_names)

    @staticmethod
    def _explain_single_dimensional_summary_plot(shap_values, to_explain):
        shap.summary_plot(shap_values, features=to_explain,
                          feature_names=to_explain.columns.values, show=False)
        return plt.gcf()

    def _explain_multi_dimensional_summary_plot(self, shap_values, to_explain, class_names, ):
        plots: [Figure] = []

        # Plot all classes in the same bar plot
        if class_names is None:
            shap.summary_plot(shap_values, feature_names=to_explain.columns.values, show=False,
                              color=plt.get_cmap("tab20c"))

        else:
            shap.summary_plot(shap_values, feature_names=to_explain.columns.values, class_names=class_names,
                              show=False, color=plt.get_cmap("tab20c"))

        plt.title("Summary (" + str(len(to_explain)) + " entries): Combined", loc='center')
        plots.append(plt.gcf())

        # Generate titles for all single class plots
        titles = [f'Summary ({str(len(to_explain))} entries): {str(class_names[i]).capitalize()}' for i in range(len(shap_values))] \
            if class_names is not None else [f'Summary ({str(len(to_explain))} entries): Class {i + 1}'
                                             for i in range(len(shap_values))]

        # Plot a single summary plot for each class
        for i in range(len(shap_values)):
            plt.figure()
            plt.title(titles[i], loc='center')
            plots.append(self._explain_single_dimensional_summary_plot(shap_values[i], to_explain))

        return plots

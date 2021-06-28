import matplotlib.pyplot as plt
import numpy as np
import shap
from matplotlib.figure import Figure
from shap import Explanation

from src.plots.plot import Plot


class WaterFallPlot(Plot):
    def explain(self, to_explain, explainer, model=None, class_names=None, **kwargs) -> [Figure]:
        """
        Creates a decision plot with incoming data

        :param to_explain:      Data to explain
        :param explainer:       The explainer to use
        :param class_names:     The names of all output names
        :param model:           The model to use
        :return: A list of all generated plots
        """
        shap_values = explainer.explain_csv(to_explain)
        # Switch between single and multi dimensional decision plot
        if len(shap_values) == 1:
            return self._explain_single_dimensional_waterfall_plot(shap_values, explainer, to_explain)
        else:
            return self._explain_multi_waterfall_decision_plot(shap_values, explainer, to_explain, model, class_names)

    @staticmethod
    def _explain_single_dimensional_waterfall_plot(shap_values, explainer, to_explain) -> [Figure]:
        plt.figure()
        plots: [Figure] = []

        # Create shap Explanation object
        explanation = Explanation(shap_values[0], data=to_explain.values,
                                  # For each entry we need a list of feature names
                                  feature_names=[to_explain.columns.values.tolist() for _ in range(len(to_explain))],
                                  # For each entry we need a list of expected_values
                                  base_values=np.asarray([explainer.explainer.expected_value for _ in
                                                          range(len(to_explain))]).flatten())

        # For each entry create a plot
        for i in range(len(to_explain)):
            plt.figure()
            shap.plots.waterfall(explanation[i], show=False)
            plt.title("Waterfall: Entry " + str(i+1), loc='center')
            plots.append(plt.gcf())
        return plots

    @staticmethod
    def _explain_multi_waterfall_decision_plot(shap_values, explainer, to_explain, model, class_names):
        plots: [Figure] = []
        # Get the prediction for each entry
        predictions = model.predict(to_explain) if model is not None else None
        # get the expected value of the explainer
        expected_value = explainer.explainer.expected_value

        # Create a plot for each entry
        for row_index in range(len(to_explain)):
            # Create titles with the predictions of the model and if provided the class names
            titles = [f'Waterfall: Entry {row_index + 1}: {str(class_names[i]).capitalize()} ({(predictions[row_index, i]*100).round(1)}%)'
                             for i in range(len(expected_value))] if class_names is not None else \
                [f'Waterfall: Entry {row_index + 1}: Class {i + 1} ({(predictions[row_index, i]*100).round(1)}%)'
                 for i in range(len(expected_value))]

            # Create shap Explanation object
            # Get the entry from each class array in the shap values using row index
            explanation = Explanation(np.asarray(shap_values)[:, row_index, :],
                                      # For class create a list of the to explain values
                                      data=[to_explain.iloc[row_index,:].values for _ in range(len(expected_value))],
                                      # For each entry we need a list of feature names
                                      feature_names=[to_explain.columns.values.tolist() for _ in
                                                     range(len(expected_value))]
                                      ,
                                      base_values=np.asarray(expected_value))

            # For each entry create a plot
            for i in range(len(expected_value)):
                plt.figure()
                plt.title(titles[i], loc='center')
                shap.waterfall_plot(explanation[i], show=False)
                plt.tight_layout()
                plots.append(plt.gcf())
        return plots

import matplotlib.pyplot as plt
import numpy as np
import shap.plots
from matplotlib.figure import Figure

from src.plots.plot import Plot


class DecisionPlot(Plot):

    def explain(self, to_explain, explainer, class_names=None, model=None, **kwargs) -> [Figure]:
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
            return self._explain_single_dimensional_decision_plot(shap_values, explainer, to_explain)
        else:
            return self._explain_multi_dimensional_decision_plot(shap_values, explainer, to_explain, class_names, model)

    @staticmethod
    def _explain_single_dimensional_decision_plot(shap_values, explainer, to_explain) -> [Figure]:
        plt.figure()
        plots: [Figure] = []
        # get the expected value of the explainer
        expected_value = explainer.explainer.expected_value
        # Create a plot of all entries
        shap.plots.decision(expected_value, shap_values[0], to_explain, show=False)
        plt.title(f'Decision ({str(len(to_explain))} entries)', loc='center')
        plots.append(plt.gcf())
        # Make a plot for each entry
        for i in range(len(to_explain)):
            plt.figure()
            shap.plots.decision(expected_value, shap_values[0][i], to_explain.iloc[i, :], show=False)
            plt.title("Decision: Entry " + str(i+1), loc='center')
            plots.append(plt.gcf())

        return plots

    @staticmethod
    def _explain_multi_dimensional_decision_plot(shap_values, explainer, to_explain, class_names, model):
        plots: [Figure] = []
        # Get the prediction for each entry
        predictions = model.predict(to_explain) if model is not None else None
        # get the expected value of the explainer
        expected_value = explainer.explainer.expected_value

        # Create a plot for each entry
        for row_index in range(len(to_explain)):
            # Create legend labels with the predictions of the model and if provided the class names
            legend_labels = [f'{class_names[i]} ({(predictions[row_index, i]*100).round(1)}%)'
                             for i in range(len(expected_value))] if class_names is not None else \
                [f'Class {i + 1} ({(predictions[row_index, i]*100).round(1)}%)'
                 for i in range(len(expected_value))]

            plt.figure()
            # Create feature names by combining the input values and the column names
            feature_values = to_explain.iloc[row_index, :].values
            column_names = to_explain.columns.values.tolist()
            feature_names = [f'{feature_values[i]} = {column_names[i]}' for i in range(len(column_names))]
            shap.multioutput_decision_plot(expected_value.tolist(), shap_values, row_index=row_index,
                                           legend_labels=legend_labels, legend_location='lower right',
                                           highlight=[np.argmax(predictions[row_index])],
                                           feature_names=feature_names, show=False)
            plt.title("Decision: Entry " + str(row_index+1), loc='center')
            plots.append(plt.gcf())
        return plots

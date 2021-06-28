import numpy as np
import shap
from matplotlib import pyplot as plt
from tensorflow.keras.models import Model

from src.explainers.explainer import Explainer
from src.plots.plot import Plot
from config.shap_properties import ShapProperties
from src.utilities.shap_utilities import ShapUtilities


class ImagePlot(Plot):
    """
    Uses the calculated shap values to create an image plot
    """

    def __init__(self):
        self.properties: ShapProperties = ShapProperties()
        self.utilities: ShapUtilities = ShapUtilities()

    def explain(self, to_explain, explainer, model: Model = None, class_names=None, ranked_outputs=None,
                normalize=True):
        """
        Generates a image plot of incoming images

        :param to_explain:      The images to explain
        :param explainer:       The explainer that has to be used
        :param class_names:     The names for all the outputs of the model
        :param ranked_outputs:  The number of outputs shown in the plot
        :param model:           The model which is used by the explainer
        :param normalize:       Normalize shap values between -1 and 1

        :return: A figure of the generated plot
        """
        plt.figure()
        if ranked_outputs is not None and class_names is not None:
            # Generate shap values for the amount of ranked outputs and use them to explain the images
            # shap_values, indexes = explainer.shap_values(to_explain, ranked_outputs=ranked_outputs)

            shap_values, indexes = explainer.explain_image(to_explain, ranked_outputs=ranked_outputs)
            if normalize:
                self.utilities.normalize_shap_values(shap_values)
            predictions = model.predict(to_explain)

            # Get the correct class labels
            index_names = self.utilities.add_predicted_values_class_names(predictions, indexes, class_names)

            shap.image_plot(shap_values, to_explain, np.asarray(index_names), show=False,
                            hspace=self.properties.shap_image_plot_hspace)

        elif class_names is not None and ranked_outputs is None:
            # Generate shap values and use them to explain the images
            shap_values = explainer.explain_image(to_explain)
            if normalize:
                self.utilities.normalize_shap_values(shap_values)
            predictions = model.predict(to_explain)

            # Get the correct class labels
            index_names = self.utilities.add_predicted_values_class_names(predictions,
                                                                          self.utilities.get_indexes_all_class_names(
                                                                              len(to_explain), len(class_names)
                                                                          ), class_names)
            shap.image_plot(shap_values, to_explain, np.asarray(index_names), show=False,
                            hspace=self.properties.shap_image_plot_hspace)
        else:
            # Generate shap values and use them to explain the images
            shap_values, indexes = explainer.explain_image(to_explain,
                                                           ranked_outputs=self.properties.shap_default_output_limit)
            if normalize:
                self.utilities.normalize_shap_values(shap_values)
            predictions = model.predict(to_explain)

            # Get the correct class labels
            class_names = [f'Class {i + 1}' for i in range(len(predictions[0]))]
            index_names = self.utilities.add_predicted_values_class_names(predictions, indexes, class_names)

            shap.image_plot(shap_values, to_explain, np.asarray(index_names), show=False,
                            hspace=self.properties.shap_image_plot_hspace)

        return plt.gcf()

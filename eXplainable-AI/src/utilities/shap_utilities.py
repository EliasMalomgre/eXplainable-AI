import numpy as np


class ShapUtilities:

    @staticmethod
    def normalize_shap_values(shap_values):
        """
        Normalizes the shap values between -1 and 1

        :param shap_values: The shap values to normalize
        :return: The normalized shap values
        """
        # Loop through all the images in the result and normalize them by diving with the max/absolute min value
        for i in range(len(shap_values)):
            for j in range(len(shap_values[i])):
                max = np.max(np.asarray([np.max(shap_values[i][j]), abs(np.min(shap_values[i][j]))]))
                if max != 0:
                    shap_values[i][j] = shap_values[i][j] / max

    @staticmethod
    def add_predicted_values_class_names(predicted_values, indexes, class_names=None):
        """
        Adds the predicted values to the class names

        :param predicted_values:    The predicted values
        :param indexes:             The indexes of the values to use
        :param class_names:         The class name to which the predictions have to e added
        :return: Class names and predictions combined
        """
        index_names = []

        # Loop through the indexes and use them to combine the correct class name and prediction
        for i in range(len(indexes)):
            row = []
            for j in range(len(indexes[i])):
                index = indexes[i][j]
                predicted = str(round(predicted_values[i][index]*100, 1)) + "%"
                row.append(("(" + predicted + ")")
                           if class_names is None else
                           (class_names[index] + " (" + predicted + ")"))
            index_names.append(row)

        return index_names

    @staticmethod
    def get_indexes_all_class_names(number_to_explain, number_class_names):
        """
        Get indexes when there are no indexes returned by the explainer

        :param number_to_explain:
        :param number_class_names:
        :return:
        """
        indexes = []
        # For each image to explain add values in a range from 0 to the number of class names
        for _ in range(number_to_explain):
            indexes.append(list(range(number_class_names)))
        return indexes

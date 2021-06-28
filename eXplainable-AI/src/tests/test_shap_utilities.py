import unittest

from src.utilities.shap_utilities import ShapUtilities


class TestShapUtilities(unittest.TestCase):

    def setUp(self) -> None:
        self.utilities: ShapUtilities = ShapUtilities()

    def test_get_all_indexes(self):
        indexes = self.utilities.get_indexes_all_class_names(1, 3)
        self.assertEqual([[0, 1, 2]], indexes)
        indexes = self.utilities.get_indexes_all_class_names(3, 5)
        self.assertEqual([[0, 1, 2, 3, 4], [0, 1, 2, 3, 4], [0, 1, 2, 3, 4]], indexes)

    def test_add_predicted_to_class_names(self):
        predicted_values = [[0.1, 0.2, 0.3, 0.4]]
        indexes = [[3, 1]]
        class_names = ["a", "b", "c", "d"]

        index_names = self.utilities.add_predicted_values_class_names(predicted_values, indexes, class_names)
        self.assertEqual([["d (40.0%)", "b (20.0%)"]], index_names)

        predicted_values = [[0.1, 0.2, 0.3, 0.4], [0.5, 0.6, 0.7, 0.8]]
        indexes = [[3, 1], [0, 2]]
        class_names = ["a", "b", "c", "d"]

        index_names = self.utilities.add_predicted_values_class_names(predicted_values, indexes, class_names)
        self.assertEqual([["d (40.0%)", "b (20.0%)"], ["a (50.0%)", "c (70.0%)"]], index_names)

        predicted_values = [[0.1, 0.2, 0.3, 0.4]]
        indexes = [[3, 1]]

        index_names = self.utilities.add_predicted_values_class_names(predicted_values, indexes)
        self.assertEqual([["(40.0%)", "(20.0%)"]], index_names)

        predicted_values = [[0.1, 0.2, 0.3, 0.4], [0.5, 0.6, 0.7, 0.8]]
        indexes = [[3, 1], [0, 2]]

        index_names = self.utilities.add_predicted_values_class_names(predicted_values, indexes)
        self.assertEqual([["(40.0%)", "(20.0%)"], ["(50.0%)", "(70.0%)"]], index_names)


if __name__ == '__main__':
    unittest.main()

import unittest

from src.services.explanation_service import ExplanationService


class MyTestCase(unittest.TestCase):

    def setUp(self) -> None:
        self.service = ExplanationService()

    def test_validation_ranked_outputs(self):
        model_outputs = 10
        self.assertEqual(None, self.service.validate_ranked_outputs(None, model_outputs))
        self.assertEqual(None, self.service.validate_ranked_outputs(0, model_outputs))
        self.assertEqual(1, self.service.validate_ranked_outputs(1, model_outputs))
        self.assertEqual(7, self.service.validate_ranked_outputs(7, model_outputs))
        self.assertEqual(10, self.service.validate_ranked_outputs(10, model_outputs))
        self.assertEqual(10, self.service.validate_ranked_outputs(11, model_outputs))

    def test_validate_class_names(self):
        model_outputs = 2
        self.assertEqual(None, self.service.validate_class_names(None, model_outputs))
        self.assertEqual(None, self.service.validate_class_names([], model_outputs))
        self.assertEqual(None, self.service.validate_class_names(["1"], model_outputs))
        self.assertEqual(["1", "3"], self.service.validate_class_names(["1", "3"], model_outputs))
        self.assertEqual(None, self.service.validate_class_names(["1", "2", "3"], model_outputs))


if __name__ == '__main__':
    unittest.main()

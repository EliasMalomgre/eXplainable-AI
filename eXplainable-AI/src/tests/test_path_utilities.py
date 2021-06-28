import unittest

from src.utilities.path_utilities import PathUtilities


class TestPathUtilities(unittest.TestCase):

    def setUp(self) -> None:
        self.utilities: PathUtilities = PathUtilities()

    def test_correct_extraction(self):
        self.assertEqual(".h5", self.utilities.get_file_extension("test.h5"))
        self.assertEqual(".onnx", self.utilities.get_file_extension("test.onnx"))
        self.assertEqual(".h5", self.utilities.get_file_extension("test.test.h5"))
        self.assertEqual(".h5", self.utilities.get_file_extension("/test/test.h5"))
        self.assertEqual(".h5", self.utilities.get_file_extension("test/test.h5"))
        self.assertEqual(".h5", self.utilities.get_file_extension("./test/test.h5"))
        self.assertEqual(".h5", self.utilities.get_file_extension("../test/test.h5"))
        self.assertEqual(".h5", self.utilities.get_file_extension("C:\test\test.h5"))







if __name__ == '__main__':
    unittest.main()

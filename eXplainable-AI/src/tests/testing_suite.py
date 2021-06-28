import unittest

from config.testing_properties import TestingProperties

if __name__ == '__main__':
    loader = unittest.TestLoader()
    suite = loader.discover(TestingProperties().suite_discover_directory)
    runner = unittest.TextTestRunner()
    runner.run(suite)

import os
import unittest

import PIL.Image

from src.services.image_handler import ImageHandler


class TestImageHandler(unittest.TestCase):

    def setUp(self):
        self.handler = ImageHandler()

    def test_correct_rgb(self):
        script_dir = os.path.realpath(os.path.join(__file__, '..', ))
        file_path = os.path.join(script_dir, "test_data/15-20-3.png")
        with PIL.Image.open(file_path) as image:
            image.load()
            converted = self.handler.reshape_images([image, image], (None, 20, 15, 3))
            self.assertEqual(converted.shape, (2, 20, 15, 3))

    def test_correct_gray(self):
        script_dir = os.path.realpath(os.path.join(__file__, '..', ))
        file_path = os.path.join(script_dir, "test_data/28-28-1.png")
        with PIL.Image.open(file_path) as image:
            image.load()
            converted = self.handler.reshape_images([image, image], (None, 28, 28, 1))
            self.assertEqual(converted.shape, (2, 28, 28, 1))

    def test_incorrect_rgb(self):
        script_dir = os.path.realpath(os.path.join(__file__, '..', ))
        file_path = os.path.join(script_dir, "test_data/15-20-3.png")
        with PIL.Image.open(file_path) as image:
            image.load()
            # Wrong first dimension
            with self.assertRaises(ValueError):
                self.handler.reshape_images([image, image], (None, 16, 20, 3))
            # Wrong second dimension
            with self.assertRaises(ValueError):
                self.handler.reshape_images([image, image], (None, 15, 21, 3))
            # Wrong third dimension
            with self.assertRaises(ValueError):
                self.handler.reshape_images([image, image], (None, 20, 15, 1))

    def test_incorrect_gray(self):
        script_dir = os.path.realpath(os.path.join(__file__, '..', ))
        file_path = os.path.join(script_dir, "test_data/28-28-1.png")
        with PIL.Image.open(file_path) as image:
            image.load()
            # Wrong first dimension
            with self.assertRaises(ValueError):
                self.handler.reshape_images([image, image], (None, 25, 28, 1))
            # Wrong second dimension
            with self.assertRaises(ValueError):
                self.handler.reshape_images([image, image], (None, 28, 21, 1))
            # Wrong third dimension
            with self.assertRaises(ValueError):
                self.handler.reshape_images([image, image], (None, 28, 28, 3))

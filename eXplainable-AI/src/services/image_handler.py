import numpy as np
from PIL.Image import Image
from tensorflow.keras.preprocessing.image import img_to_array
from numpy import ndarray


class ImageHandler:

    @staticmethod
    def reshape_images(images: [Image], input_shape):
        """
        Converts images to arrays according to the input shape of a model

        :param images:          The images to convert
        :param input_shape:     The input shape of the model
        :return: An array of the converted images
        """
        try:
            converted = []
            for image in images:
                img_array = img_to_array(image)
                assert img_array.shape == tuple(input_shape[-3:])
                converted.append(img_array)

            return np.array(converted)
        except:
            raise ValueError("Image can't be reshaped into input shape")

import onnx
from onnx2keras import onnx_to_keras


class ONNXService:

    @staticmethod
    def onnx_to_keras(onnx_model):
        """
        Converts a ONNX model to a keras model

        :param onnx_model: The model to convert
        :return: A converted keras model
        """
        keras_model = onnx_to_keras(onnx_model, [onnx_model.graph.input[0].name], change_ordering=True)
        return keras_model

import logging

import flask
import numpy as np
from flask import Blueprint, jsonify
from flask_restful import reqparse
from tensorflow.python.keras.models import Model
import tensorflow as tf

from config.api_properties import ApiProperties
from src.services.azure_storage_service import AzureStorageService

model_controller = Blueprint('model_controller', __name__)
controller_prefix = ApiProperties().api_model_prefix


@model_controller.route(controller_prefix + "getmodelinfo", methods=["POST"])
def get_model_input_shape():
    logging.info("Model input shape is requested")
    # Create a parser for all parameters of the incoming api request
    parser = reqparse.RequestParser()
    parser.add_argument('model', type=dict)
    args = parser.parse_args()

    try:
        with tf.device('/cpu:0'):
            model: Model = AzureStorageService().download_model(args['model'])

            # Map useful model information to json objects
            layers = []
            for layer, position in zip(model.layers, range(len(model.layers))):
                layers.append({"layerPosition": position, "name": layer.name, "className": layer.__class__.__name__,
                               "inputShape": list(np.asarray(layer.output_shape).flatten()), "outputShape": list(np.asarray(layer.output_shape).flatten())})

            # Return the useful model information
            return flask.make_response(jsonify(inputShape=list(model.input_shape), outputShape=list(model.output_shape),
                                               layers=layers))
    except:
        return '', 400

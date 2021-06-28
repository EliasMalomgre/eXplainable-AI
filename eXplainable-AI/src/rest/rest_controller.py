from flask import Flask
from flask_cors import CORS
from flask_restful import Api

from config.api_properties import ApiProperties
from src.rest.explananation_controller import explanation_controller
from src.rest.model_controller import model_controller

# Create a api app and configure all controllers
app = Flask(__name__)
app.register_blueprint(model_controller)
app.register_blueprint(explanation_controller)

api = Api(app)

cors = CORS(app, resources={r"/*": {"origins": "*"}})


class RestController:

    @staticmethod
    def get_api():
        return api

    @staticmethod
    def run():
        app.run(debug=ApiProperties.api_server_debug, host=ApiProperties.api_server_host,
                port=ApiProperties.api_server_port)

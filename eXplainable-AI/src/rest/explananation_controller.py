import json
import logging
from threading import Thread

import flask
from flask import Blueprint
from flask_restful import reqparse

from config.api_properties import ApiProperties
from src.domain.explanation_error import ExplanationError
from src.services.explanation_service import ExplanationService

explanation_controller = Blueprint('explanation_controller', __name__)
controller_prefix = ApiProperties().api_explanation_prefix


@explanation_controller.route(controller_prefix + "startexplanation", methods=["POST"])
def start_explanation():
    logging.info("Explanation is requested")
    # Create a parser for all parameters of the incoming api request
    parser = reqparse.RequestParser()
    parser.add_argument('taskId', type=str)
    parser.add_argument('model', type=dict)
    parser.add_argument('data', type=dict, action='append')
    parser.add_argument('toexplain', type=dict, action='append')
    parser.add_argument('classLabels', type=list, action='append')
    parser.add_argument('parameters', type=dict)
    parser.add_argument('explainerType', type=str)
    parser.add_argument('plotType', type=str)
    args = parser.parse_args()

    try:
        explanation_service: ExplanationService = ExplanationService()

        # Start the explanation on a different thread
        thread = Thread(target=explanation_service.explanation,
                        args=(args['model'], args['data'], args['toexplain'], args['taskId'],
                              args['classLabels'], args['parameters'], args['explainerType'], args['plotType']))
        thread.setDaemon(True)
        thread.start()

        logging.info(f"Explanation has started generating for task: {args['taskId']}")
        return flask.make_response(json.dumps(True), 200)

    except:
        logging.error("An error occurred while starting the explanation thread")
        explanation_service: ExplanationService = ExplanationService()
        explanation_service.spring_core_service.post_error(args['taskId'], ExplanationError.UNKNOWN)
        return flask.make_response(json.dumps(False), 400)

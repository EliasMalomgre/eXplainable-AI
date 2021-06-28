import logging
import tempfile
import uuid
import os
import requests
from matplotlib.figure import Figure

from config.api_properties import ApiProperties
from src.domain.explanation_error import ExplanationError


class SpringCoreService:
    def __init__(self):
        self.properties = ApiProperties()

    def send_result_image(self, images: [Figure], task_id: str):
        """
        Sends the result figure to the Spring core project

        :param images:       The figure of the explanation result
        :param task_id:     The task to which the result belongs
        """
        # Create a temp directory for temporary saving of the result image
        with tempfile.TemporaryDirectory() as tmp_dir:
            results = []
            files_to_close = []
            for image in images:
                filename = os.path.join(tmp_dir, str(uuid.uuid4()) + ".png")
                image.savefig(filename, format="PNG", transparent=True,  bbox_inches='tight')

                # with open(filename, "rb") as file:
                #     results.append(('results', file))
                file = open(filename, 'rb')
                files_to_close.append(file)
                results.append(('results', (filename, file, 'image/png')))

            response = requests.post(self.properties.api_spring_core_base_url + 'api/task/finishtask',
                                     data={"taskId": task_id}, files=results)

            for file in files_to_close:
                file.close()

            if response.ok:
                logging.info(f"Explanation result for task with id {task_id} has been posted to Spring core")
            else:
                logging.error(f"Explanation result request for task with id {task_id} to Spring core failed"
                              f" with status code {response.status_code}")

    def post_error(self, task_id: str, error: ExplanationError):
        response = requests.post(self.properties.api_spring_core_base_url + 'api/task/postExplanationError',
                                 data={"taskId": task_id, "explanationError": error.name})
        if response.ok:
            logging.info(f"Explanation error for task with id {task_id} has been posted to Spring core")
        else:
            logging.error(f"Post explanation error request for task with id {task_id} to Spring core failed "
                         f"with status code {response.status_code}")

    def cancel_task(self, task_id: str):
        """
        Attempts to cancel the task corresponding to the given id

        :param task_id:     The task to cancel
        """
        # Create a temp directory for temporary saving of the result image
        response = requests.post(self.properties.api_spring_core_base_url + 'api/task/cancelTask',
                                 data={"taskId": task_id})
        if response.ok:
            logging.info(f"Task with id {task_id} is being cancelled")
        else:
            logging.error(f"Task with id {task_id} cancel request for to Spring core failed with status code"
                         f" {response.status_code}")

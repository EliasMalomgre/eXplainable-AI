import logging

from src.explainers.kernel_explainer import KernelExplainer
from src.plots.decision_plot import DecisionPlot
from src.plots.embedding_plot import EmbeddingPlot
from src.plots.heatmap_plot import HeatmapPlot
from src.plots.scatter_plot import ScatterPlot
from src.plots.summary_plot import SummaryPlot
from PIL.Image import Image
from tensorflow.python.keras.models import Model
from src.domain.explanation_error import ExplanationError
from src.explainers.deep_explainer import DeepExplainer
from src.explainers.explainer import Explainer
from src.explainers.gradient_explainer import GradientExplainer
from src.plots.image_plot import ImagePlot
from src.plots.waterfall_plot import WaterFallPlot
from src.services.azure_storage_service import AzureStorageService
from src.services.image_handler import ImageHandler
from src.services.spring_core_service import SpringCoreService

import tensorflow as tf
# Without disabling DeepExplainer doesn't work
tf.compat.v1.disable_v2_behavior()


class ExplanationService:
    def __init__(self):
        self.image_handler: ImageHandler = ImageHandler()
        self.spring_core_service: SpringCoreService = SpringCoreService()
        self.storage_service: AzureStorageService = AzureStorageService()

    def explanation(self, model_reference: dict, data_references: [dict], to_explain_references: [dict],
                    task_id: str, class_names: [str], parameters: dict, explainer_type: str, plot_type: str):
        """
        Switches to the correct method based on incoming request

        :param plot_type:               The type of plot which has to be used
        :param explainer_type:          The type of explainer which has to be used
        :param task_id:                 The id of the task to which the explanation belongs
        :param model_reference:         Reference to the to model in Azure file storage
        :param data_references:         References to the images in Azure file storage
        :param to_explain_references:   References to the to explain data in Azure file storage
        :param class_names:             The names for all the outputs of the model
        :param parameters:              Extra parameters given based on what task is expected to run
        """
        try:
            # Download model from Azure file storage
            model = self.storage_service.download_model(model_reference)
            class_names = self.validate_class_names(class_names[0], model.output_shape[1])

            # Check the parameterTypes to determine which method to start
            if parameters['parameterType'] == 'IMAGEPLOT':

                data = self.storage_service.download_images(data_references)
                to_explain = self.storage_service.download_images(to_explain_references)

                return self.image_explanation(model, data, to_explain,
                                              task_id, class_names, parameters['rankedOutputs'],
                                              parameters['normalize'], explainer_type)

            elif parameters['parameterType'] == 'GENERIC':
                data = self.storage_service.download_csv(data_references[0])
                to_explain = self.storage_service.download_csv(to_explain_references[0])

                self.csv_explanation(model, data, to_explain, task_id, explainer_type, plot_type, class_names)

        except Exception:
            self.spring_core_service.post_error(task_id, ExplanationError.UNKNOWN)
            logging.exception("An error occurred while downloading the data.")

    def image_explanation(self, model: Model, data_download: [Image], to_explain_download: [Image],
                          task_id: str, class_names: [str], ranked_outputs: int, normalize: bool, explainer_type):
        """
        Handles the explanation and sends the result to Spring core

        :param explainer_type:          The type of explainer that has to be used
        :param model:                   The downloaded model
        :param data_download:           References to the images in Azure file storage
        :param to_explain_download:     References to the to explain data in Azure file storage
        :param class_names:             The names for all the outputs of the model
        :param ranked_outputs:          The number of outputs shown in the plot
        :param task_id:                 The task of the explanation
        :param normalize:               Normalize shap values between -1 and 1
        """
        to_explain = None
        images = None
        try:
            # Download all images form Azure file storage and reshape them to the correct array
            logging.info(f"Task({task_id}): Reshaping data")
            images = self.image_handler.reshape_images(data_download, model.input_shape)
            # Download all images form Azure file storage and reshape them to the correct array
            to_explain = self.image_handler.reshape_images(to_explain_download, model.input_shape)

            # Validate ranked outputs and class names
            ranked_outputs = self.validate_ranked_outputs(ranked_outputs, model.output_shape[1])

            self.start_image_explanation(explainer_type, task_id, model, to_explain, images, class_names,
                                         ranked_outputs, normalize)

        # When explanation fails due to not having enough VRAM restart with lower batch size
        except tf.errors.ResourceExhaustedError:
            try:
                logging.warning("An error occurred while explaining the data on GPU. "
                                "Trying to run on a smaller batch size")
                self.spring_core_service.post_error(task_id, ExplanationError.LOW_BATCH_SIZE)
                self.start_image_explanation(explainer_type, task_id, model, to_explain, images, class_names,
                                             ranked_outputs,
                                             normalize, batch_size=1)
            # When explanation fails due to not having enough VRAM restart on CPU
            except tf.errors.ResourceExhaustedError:
                try:
                    logging.warning("An error occurred while explaining the data on GPU. Trying to run on CPU")
                    self.spring_core_service.post_error(task_id, ExplanationError.CPU_SWITCH)
                    with tf.device('/cpu:0'):
                        self.start_image_explanation(explainer_type, task_id, model, to_explain, images, class_names,
                                                     ranked_outputs)
                except Exception:
                    self.spring_core_service.post_error(task_id, ExplanationError.CPU_FAILURE)
                    logging.exception("An error occurred while explaining the data on CPU. Explanation aborted!")
            except Exception:
                self.spring_core_service.post_error(task_id, ExplanationError.UNKNOWN)
                logging.exception("An error occurred while explaining the data.")

        except Exception:
            self.spring_core_service.post_error(task_id, ExplanationError.UNKNOWN)
            logging.exception("An error occurred while explaining the data.")

        else:
            logging.info("Explanation has successfully generated and is being sent to Spring")

    def start_image_explanation(self, explainer_type: str, task_id, model, to_explain, images, class_names,
                                ranked_outputs,
                                normalize=True, batch_size=50, local_smoothing=100):
        """
        Starts an image explanation

        :param explainer_type:      The type explainer which has to be used
        :param task_id:             The id of task to which the explanation belongs
        :param model:               The keras model
        :param to_explain:          The images to explain
        :param images:              The images used for the background for the explainer
        :param class_names:         The names of the outputs of the model
        :param ranked_outputs:      The amount of ranked outputs shown in the plot
        :param normalize:           Whether the shap values have to be normalized
        :param batch_size:          The batch sire used for the Gradient explainer
        :param local_smoothing:     How hard local smoothing is used in he image plot
        """
        # Create the correct explainer
        explainer: Explainer
        if explainer_type == "GRADIENT":
            explainer = GradientExplainer(model, images, batch_size=batch_size, local_smoothing=local_smoothing)
        elif explainer_type == "DEEP":
            explainer = DeepExplainer(model, images)
        else:
            raise Exception(f"Explainer type '{explainer_type}' is not implemented")

        # Start the explanation and send the result
        logging.info(f"Task({task_id}): SHAP starting up")
        result = ImagePlot().explain(to_explain, explainer, model, class_names, ranked_outputs, normalize)
        # Transmitting result back to Orchestrator/Core
        logging.info(f"Task({task_id}): Explanation completed, transmitting result")
        self.spring_core_service.send_result_image([result], task_id)

    def csv_explanation(self, model, data, to_explain, task_id, explainer_type, plot_type, class_names=None):
        """
        Explains values of an csv

        :param model:           The keras model
        :param data:            The dataframe used as background
        :param to_explain:      The dataframe to explain
        :param task_id:         The id of task to which the explanation belongs
        :param explainer_type:  The type explainer which has to be used
        :param plot_type:       The type of plot which has to be used
        :param class_names:     The names of the outputs of the model
        """
        explainer: Explainer
        if explainer_type == "KERNEL":
            explainer = KernelExplainer(model, data)
        elif explainer_type == "DEEP":
            explainer = DeepExplainer(model, data)
        else:
            raise Exception(f"Explainer type '{explainer_type}' is not implemented")

        # Start the explanation and send the result
        logging.info(f"Task({task_id}): SHAP starting up")
        if plot_type == "SUMMARY":
            result = SummaryPlot().explain(to_explain, explainer, class_names=class_names)
        elif plot_type == "DECISION":
            result = DecisionPlot().explain(to_explain, explainer, class_names=class_names, model=model)
        elif plot_type == "WATERFALL":
            result = WaterFallPlot().explain(to_explain, explainer, class_names=class_names, model=model)
        elif plot_type == "SCATTER":
            result = ScatterPlot().explain(to_explain, explainer, class_names=class_names)
        elif plot_type == "HEATMAP":
            result = HeatmapPlot().explain(to_explain, explainer, class_names=class_names)
        elif plot_type == "EMBEDDING":
            result = EmbeddingPlot().explain(to_explain, explainer, class_names=class_names)
        else:
            raise Exception(f"Plot type '{plot_type}' is not implemented")

        # Transmitting result back to Orchestrator/Core
        logging.info(f"Task({task_id}): Explanation completed, transmitting result")
        self.spring_core_service.send_result_image(result, task_id)

    @staticmethod
    def validate_ranked_outputs(ranked_outputs, model_outputs: int):
        """
        Validates ranked outputs

        :param ranked_outputs:  The ranked outputs to validate
        :param model_outputs:   The number of outputs of the model
        :return: The new ranked outputs value
        """
        # If ranked outputs is None or 0 return None so all outputs are shown without ranking them
        if ranked_outputs is None or ranked_outputs == 0:
            return None
        # If ranked outputs is greater than amount return model outputs so all outputs are shown
        elif ranked_outputs > model_outputs:
            return model_outputs
        # Else return ranked outputs
        return ranked_outputs

    @staticmethod
    def validate_class_names(class_names: [str], model_outputs: int):
        """
        Validate the class names

        :param class_names:     The class names to validate
        :param model_outputs:   The number of outputs o the model
        :return:
        """
        # If class names is not None or the len of the class names does not match the number of outputs,
        # the class names are invalid
        if class_names is not None and len(class_names) == model_outputs:
            return class_names
        else:
            return None

    def cancel_task(self, task_id: str) -> None:
        """
        Cancels the task

        :param task_id:      The id of the task being cancelled

        """
        # Start the explanation and send the result
        self.spring_core_service.cancel_task(task_id)

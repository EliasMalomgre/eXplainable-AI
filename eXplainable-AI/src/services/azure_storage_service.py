import logging
import os
from io import BytesIO, StringIO

import PIL
import tempfile
import uuid
import csv

import onnx
import pandas as pd
import tensorflow as tf
from PIL.Image import Image
from azure.storage.blob import BlobClient, StorageStreamDownloader
from tensorflow.python.keras.models import Model

from config.azure_storage_properties import AzureStorageProperties
from src.services.onnx_service import ONNXService
from src.utilities.path_utilities import PathUtilities


class AzureStorageService:
    def __init__(self):
        self.properties: AzureStorageProperties = AzureStorageProperties()
        self.onnx_service: ONNXService = ONNXService()
        self.path_utilities: PathUtilities = PathUtilities()

    def download_file(self, file_path: str) -> BytesIO:
        """
        Download a file from Azure File Storage

        :param file_path:   The path to the file
        :return: The bytes of the file in a buffer
        """
        # Create a temp folder
        with tempfile.TemporaryDirectory() as tmp_dir:
            # Create a temp file and write the bytes int the temp file
            tmp_file = os.path.join(tmp_dir, str(uuid.uuid4()))
            with open(tmp_file, "wb") as download_file:
                blob_client: BlobClient = BlobClient.from_connection_string(self.properties.connection_string,
                                                                            "xai-opdracht",
                                                                            file_path)

                blob_data: StorageStreamDownloader = blob_client.download_blob()
                blob_data.readinto(download_file)
            # Load the bytes of the file in a buffer
            with open(tmp_file, "rb") as file:
                return BytesIO(file.read())

    def download_images(self, images_references: [dict]) -> [Image]:
        """
        Downloads all images of a list of references to the location of images in Azure file storage

        :param images_references:   The references to the location of all the images
        :return: A list of PIL images
        """
        data: [Image] = []
        # Download the image for each reference and covert it to a PIL image
        for reference in images_references:
            buffer = self.download_file(reference['storagePath'])
            with PIL.Image.open(buffer) as image:
                image.load()
                data.append(image)
        return data

    def download_csv(self, csv_reference):
        buffer = self.download_file(csv_reference['storagePath'])

        with tempfile.TemporaryDirectory() as tmp_dir:
            filename = tmp_dir + "/" + str(uuid.uuid4()) + ".csv"
            # Write bytes in a temp file
            with open(filename, "wb") as file:
                file.write(buffer.read())
            return pd.read_csv(filename)

    def download_model(self, model_reference: dict) -> Model:
        """
        Downloads a model using a reference to the location of images in Azure file storage

        :param model_reference:   A reference to the location of the model
        :return: A loaded keras model
        """
        # Save the model bytes into a temp file and load the temp file with keras
        with tempfile.TemporaryDirectory() as tmp_dir:
            # Download the model with the storage path
            storage_path = model_reference['storagePath']
            buffer: BytesIO = self.download_file(storage_path)
            # Get the extension and use it to load the correct model
            extension = self.path_utilities.get_file_extension(storage_path)
            if extension == ".h5":
                filename = tmp_dir + "/" + str(uuid.uuid4()) + extension
                # Write bytes in a temp file
                with open(filename, "wb") as file:
                    file.write(buffer.read())
                model = tf.keras.models.load_model(filename, compile=False)
                return model
            elif extension == ".onnx":
                filename = tmp_dir + "/" + str(uuid.uuid4()) + extension
                # Write bytes in a temp file
                with open(filename, "wb") as file:
                    file.write(buffer.read())
                # Load and convert the ONNX model
                onnx_model = onnx.load(filename)
                return self.onnx_service.onnx_to_keras(onnx_model)
            else:
                logging.error("File ({}) has invalid extension!".format(storage_path))
                raise Exception("File ({}) has invalid extension!".format(storage_path))



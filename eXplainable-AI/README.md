# Stage Info Support - 2020-2021 - Front End - XAII
The 'eXplainable AI Interface' application attempts to pry open the black box of Artificial Intelligence. Imagined by
two students at the Karel de Grote University College, in collaboration with Info Support.

## Contributors
- Elias Malomgré
- Vink Van den Bosch
- 
## Introduction
This project houses all logic to create explanations and to handle model information. Completly written in Python

## Project Overview
The XAI project is used to process model information and create explanations for models. SHAP is used to explain the model. At the moment image and CSV explanations are supported and it is easy to implement NLP or other explanation libraries. Explanation can be started by sending an api call to XAI service containing references to the files needed for the explanation, id of the task to which the explanation belongs and settings for plots. The XAI project will send an api call with the result to Spring core to process the result. When something goes wrong during the explanation, the XAI project will notify Spring core.

This project can either be ran with docker or locally. Note that when using docker, core-spring service also has to be ran dockerized. 


### Used technologies
The project is written entirely in Python.

- **Azure File Storage**: Uploaded models, upload data, data to explain and results are stored in Azure File storage and can be retrieved with a storage path. These storage paths are stored in a MongoDb. Azure File storage is accessed from Spring core and XAI project. The connection string has to be placed in application.properties in Spring core and azure_storage_properties.py in XAI project. By creating an interface of the file storage classes, other file storages can be used; for example saving to local storage or any other cloud service. 
- **Flask Restful**: Flask Restful is used for the API endpoints.
- **SHAP**: SHAP is a library which can be used to create model explanations.
- **Tensorflow Keras**: Is the main model technology used in the project.
- **ONNX**: ONNX models can also be uploaded and are converted to Tensorflow Keras models when loaded.

## Settings
The project has several configurations you can alter, these can be found in the properties files in the config folder. Note that some settings have to be different when using docker. To work around this a separate properties file can be created and during the building of the container the original file can be overridden with the new docker properties file. the api properties file can be used as an example.

We advise using the default values, as bad settings can and will lead to unintended behaviour and potential crashes.

## Setup for docker
1. In this directory build a docker image with: `docker build -t xai-python . `
2. Run de docker image with (replace {placeholders}): `docker run --rm -p 4080:4080 -v {full-path-to-the-src-directory}:/app/src xai-python`

## Setup locally 
### Requirements
- [Python 3.8](https://www.python.org/downloads/release/python-387/)

### Setup
1. (Optional) If you wish to use a virtual environment, you can follow the instructions for setting one up for your specific platform [here](https://packaging.python.org/guides/installing-using-pip-and-virtual-environments/).

2. In the root directory, run `pip3 install -r requirements.txt`

### Run
In the root directory, run `rest_server.py` using the command `python rest_server.py` (or `python3 rest_server.py` depending on platform), or trough the IDE of your choice.

The application will start an API server on `http://0.0.0.0:4080`.

### NVIDIA CUDA
Installing NVIDIA CUDA is recommended because it will speed up the explanation process. CUDA will only work if you have a NVIDIA GPU. The following process can be follow [here](https://www.tensorflow.org/install/gpu).

## Azure file storage
Connection string: `DefaultEndpointsProtocol=https;AccountName=xaiopdracht;AccountKey=s/pdGm26F2szwKgjBIR1lJsX17ru02QAJWLx3kE9UfYBcnzRzg4FO9HZh0mYM+EBJ959uqAdlElQ/QQLpRCptQ==;EndpointSuffix=core.windows.net`
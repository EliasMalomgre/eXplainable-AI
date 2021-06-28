# Installation manual

This project can either be run dockerized or locally. As a user, I would recommend using the application dockerized because you don't need to install as much and as a developer locally because starting the docker containers can take some time and using a debugger tool isn't possible.

You will need to install docker to be able to run the project. You can refer to these guides to install docker
- On a Linux device: https://docs.docker.com/engine/install/ubuntu/
- On a Windows device: https://docs.docker.com/docker-for-windows/install/
- On a Mac OS device:https://docs.docker.com/docker-for-mac/install/

## Project components

### MongoDB
MongoDB is used to store references to and information about files in the file storage. At the moment a MongoDB docker container is used but by changing the connection string in the application.properties and application.docker.properties in Spring core, a cloud database can be used.

### Azure File Storage
Uploaded models, upload data, data to explain and results are stored in Azure File storage and can be retrieved with a storage path. These storage paths are stored in MongoDB. Azure File storage is accessed from Spring core and the XAI project. The connection string has to be placed in application.properties and application.docker.properties in Spring core and azure_storage_properties.py in the XAI project. 

If you want a different storage service or local storage you can create an interface of the file storage classes and create a new implementation.

### Spring core
The Spring Core project is written in Java 11 using the Spring framework and is responsible for orchestrating tasks and maintaining data consistency. It receives and validates incoming uploads and explanation requests, before transmitting them to their designated services.

### XAI project 
The XAI project is written in Python 3.8 and is used to process model information and create explanations for models. SHAP is used to explain the model. At the moment image and CSV explanations are supported and it is easy to implement NLP or other explanation libraries. An explanation can be started by sending an API call to the XAI service containing references to the files needed for the explanation, the id of the task to which the explanation belongs, and settings for plots. The XAI project will send an API call with the result to Spring core to process the result. When something goes wrong during the explanation, the XAI project will notify Spring core.

### Vue front end
The front end is written in Vue.js 2.6 using the Quasar framework and is the sole UI responsible for allowing the user to run the application. It allows the user to upload trained artificial intelligence models and associated data. Once uploaded the user can request explanation plots for the given model, this request will be handled by the spring core. Once processed the user will be able to view the requested plot, along with others on the corresponding page. The front end also contains informative pages to educate the user on how to use the application and interpret the results.

## Running the project dockerized

### Requirements
- Docker

### Startup process
To start the project execute docker-compose command in the root directory
```
docker-compose up --build -d
```
It will take some time to build the first time and to start all services.

Afterwards, you can visit the frontend at http:/<span>/localhost:8080, the java backend at http:/<span>/localhost:7080, the python processing at http:/<span>/localhost:4080, and the MongoDB server is running at http:/<span>/localhost:27017. 

## Running the project locally

You have the choice whether to use all services locally or run both Spring core and the XAI project dockerized and run the front end locally. This allows you to make use of the auto-refresh function when developing. If you choose to use a cloud MongoDB you don't need docker at all. When not running XAI project dockerized it is recommended to also install CUDA. This allows Python to make use of the GPU and will make the explanation process much faster. You can use [this tutorial](https://www.tensorflow.org/install/gpu) to install it.

### Requirements
#### Vue frontend
- npm
#### Spring core
- Java 11
#### XAI project
- Python 3.8
- CUDA
#### MongoDB
- Docker

### Startup process
#### Vue frontend
Run locally by either using your IDE of choice or by running the following command in the `frontend-vue` folder:
```
npm install
npm run serve
```
Run dockerized by executing the following commands in the `frontend-vue` folder
```
docker run -v "$PWD":/usr/src/app -w /usr/src/app node:latest npm install
docker run -p 8080:8080 -v "$PWD":/usr/src/app -w /usr/src/app node:latest npm run serve
```

#### Spring core
Run locally 
Run locally by either using your IDE of choice or by running the following command in the `core-spring` folder:
```
gradlew bootRun
```
Run dockerized by executing the following commands in the `core-spring` folder

```
docker build -t java-backend . 
docker run --rm -p 7080:7080 java-backend
```
#### XAI project
Run locally by either using your IDE of choice or by running the following command in the `eXplainable-AI` folder:
```
pip3 install -r requirements.txt
python src/rest_server.py
```
Run dockerized by executing the following commands in the `eXplainable-AI` folder (replace {placeholders})
```
docker build -t xai-python .
docker run --rm -p 4080:4080 -v {full-path-to-the-src-directory}:/app/src xai-python
```
#### MongoDB
When using the docker version make sure to install, the instructions are mentioned above.

Run dockerized by executing the following commands in the `mongodb` folder
```
docker-compose up -d
```
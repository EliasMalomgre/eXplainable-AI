# Stage Info Support - 2020-2021 - Front End - XAII

The 'eXplainable AI Interface' application attempts to pry open the black box of Artificial Intelligence. Imagined by
two students at the Karel de Grote University College, in collaboration with Info Support.

## Contributors

- Elias Malomgré
- Vink Van den Bosch

## Introduction

This project houses the core for the XAII application. Utilising the Java Spring framework.

## Project Overview

The Spring Core is responsible for orchestrating tasks and maintaining data consistency. It receives and validates incoming uploads and explanation requests, before transmitting them to their designated services. 

Notable linked services are:
- **Frontend-Vue**: The UI layer for the user, that allows the user to load in images and neural nets to explain
- **eXplainable-AI**: The processing component that analyses loaded neural nets+images and returns visual explanations

### Used technologies

The project is written entirely in Java and utilizes the Java Spring framework.

- **MongoDB**: The MongoDb is used to store references to and information about files in the file storage. At the moment a MongoDb docker container is used but by changing the connection string in Spring core, a cloud database can be used.
    
- **Azure File Storage**: Uploaded models, upload data, data to explain and results are stored in Azure File storage and can be retrieved with a storage path. These storage paths are stored in a MongoDb. Azure File storage is accessed from Spring core and XAI project. The connection string has to be placed in application.properties in Spring core and azure_storage_properties.py in XAI project. By creating an interface of the file storage classes, other file storages can be used; for example saving to local storage or any other cloud service. 

## Settings

The project has several configurations you can alter, these can be found in `./src/main/resources/application.properties`. Note that the same changes have to be made in `./src/main/resources/application.docker.properties`. Note that some settings have to be different when using docker.

We advise using the default values, as bad settings can and will lead to unintended behaviour and potential crashes.

## Project Run

To run the project use following commands:

```
gradlew bootRun
```

## Setup (dockerized)
When not having gradle and jdk installed. Note that eXplainable-AI service also has to be ran dockerized. 

Build application docker image: ` docker build -t java-backend . `

Run application `docker run --rm -p 7080:7080 java-backend`

The project is hosted by default on http://localhost:7080

## Connect to Mongo database
First execute the command `docker-compose up -d` in the folder `mongodb`.You can get access to the database by using the following connection string:
- `mongodb://XAI:InfoSupportXAI@localhost:27017/?authSource=admin`

## Connect to Azure file storage
Connection string: `DefaultEndpointsProtocol=https;AccountName=xaiopdracht;AccountKey=s/pdGm26F2szwKgjBIR1lJsX17ru02QAJWLx3kE9UfYBcnzRzg4FO9HZh0mYM+EBJ959uqAdlElQ/QQLpRCptQ==;EndpointSuffix=core.windows.net`
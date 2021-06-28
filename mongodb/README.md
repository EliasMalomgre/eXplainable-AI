# Stage Info Support - 2020-2021 - Front End - XAII

The 'eXplainable AI Interface' application attempts to pry open the black box of Artificial Intelligence. Imagined by
two students at the Karel de Grote University College, in collaboration with Info Support.

## Contributors
- Elias Malomgré
- Vink Van den Bosch

## Overview
The MongoDb is used to store references to and information about files in the file storage. At the moment a MongoDb docker container is used but by changing the connection string in Spring core, a cloud database can be used.

## Run Mongo database
Execute the command `docker-compose up -d` in the folder `mongodb`. After executing this command a MongoDB will start on `port 27017`.

## Connect to Mongo database
You can get access to the database by using the following the following credentials:
- Username: `XAI`
- Password: `InfoSupportXAI`
- Hostname: `localhost`
- Port: `27017`
- Authentication: `Username / Password`
- Authentication Database: `admin`

Or use the following connection string:
- `mongodb://XAI:InfoSupportXAI@localhost:27017/?authSource=admin`

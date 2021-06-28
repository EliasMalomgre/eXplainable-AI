# Stage Info Support - 2020-2021 - Front End - XAII

The 'eXplainable AI Interface' application attempts to pry open the black box of Artificial Intelligence. Imagined by
two students at the Karel de Grote University College, in collaboration with Info Support.

## Contributors
- Elias Malomgré
- Vink Van den Bosch

## Introduction
This project houses the front end for the XAII application. Utilising the Vue framework.

## Project Overview
The vue front end is the sole UI responsible for allowing the user to run the application. It allows the user to upload trained artificial intelligence models and associated data. Once uploaded the user can request explanation plots for the given model, this request will be handled by the spring core. Once processed the user will be able to view the requested plot, along with others on the corresponding page. The front end also contains informative pages to educate the user on how to use the application and interpret the results. This application is also PWA compatible.

### Used technologies
- **Quasar** -> Component framework used throughout the entire project
- **ThreeJS** -> WebGL framework used for the visualisation of the AI's inner structure

## Project setup
You can run the project with the following commands:

```
npm install
```

### Project setup dockerized
`docker run -v "$PWD":/usr/src/app -w /usr/src/app node:latest npm install`

### Compiles and hot-reloads for development
By default runs on http://localhost:8080

```
npm run serve
```

### Compiles and hot-reloads for development (dockerized)
`docker run -p 8080:8080 -v "$PWD":/usr/src/app -w /usr/src/app node:latest npm run serve`

### Compiles and minifies for production
```
npm run build
```

Use the following instructions to deploy the build:
https://cli.vuejs.org/guide/deployment.html


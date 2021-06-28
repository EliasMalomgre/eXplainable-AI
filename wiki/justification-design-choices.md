# Justification of technology choices

## UI Front-End (Vue)
### Web Application vs. Desktop Application
Initially, the plan was to make a desktop application. Hosting a website with many users can be expensive and because making explanations requires a lot of GPU power, the explanation queue would be long during peak hours. We explored many GUI technologies and mainly Desktop Compose, Vaadin, KVision, and Electron.

After further research, an on-premise web server seemed to be the best option. The application won't be used that often by each user so it is beneficial to equip one server with a good GPU instead of equipping every device.

Therefore we researched for possible UI frameworks. The main contenders were Thymeleaf, Angular, and Vue.js wrapped in a PWA. Eventually, we decided to use Vue.js. Mainly because Vue.js is easier to learn compared to Angular and isn't as limited as Thymeleaf.

## Explainable AI Microservice (Python)
### Why Python
Python has a very active Artificial Intelligence-focused ecosystem, with most related code being written specifically for Python. We considered using Jython to reduce the number of services we'd need to run. But ultimately decided against doing this, since it didn't seem as a stable option, coming with odd Gradle additions to properly compile everything, weird interpreters and it all seemed a bit tacky. We also have some previous Spring microservices for user authentication, api-gateway, and websocket handling, and decided if we'd eventually add these, that a complete microservice structure would be preferable, over a bloated core service.

### SHAP vs. LIME
SHAP can create more accurate explanations but is really slow in some situations and LIME is faster in general. In our application, we valued accuracy over speed and therefore we decided to use SHAP. However it is still possible to add LIME to the application. 

## Core / Glue (Spring Boot)
### Why Spring Boot
Mainly because we both are most productive in Spring and have a lot of existing to rest upon, whenever needed. This includes extensive testing examples, controllers, a complete authentication microservice, api-gateway, and websocket handler.

#Justification of other choices

## Communication
### Why does XAI processing have direct access to Azure File Storage
* XAI processing has access to Azure File Storage because otherwise, files of large size have to be sent over HTTP calls which isn't ideal.
* A downside is that XAI processing now has a dependency on Azure file storage dependencies

## Base64 vs raw file transmission
* The only real downside to sending files is their complexity
* A base64 string of a file is larger than the file itself
* The string has to be decoded en encoded in the Spring core and XAI project
* Decoding from base64 requires saving and can lead to additional compression which might lead to data loss
* Azure file storage is used so that means we pull files out of the storage to then encode them


## Persistence

### Why MongoDB?
SQL would have been perfectly valid here, but we figured it would be slightly cleaner to use mongo's document storage, to have a storage string of a model paired to an array of the sent-in training images/data, all tied up in one neat document.
We also aim to one day add the ability to display a time-lapse, by sending in trained networks every X episodes, which would mean having an array of neural networks linked to an array of images, which could be done in sql with an associative class and a couple joins, but mongo offers more flexibility for this case, allowing us to easily do this, without impacting the readability/complexity of our database structure. 

### Why Azure File storage
We decided to use a cloud-based storage service because the files would be easily accessible from anywhere. We chose Azure File Storage because we already made use of Azure DevOps. However, the storage service can easily be replaced with another storage service or local storage by creating a new storage class in Spring core and XAI processing. 

### Why use UUID's in the storage strings?
A sequence could be used instead and would be less "overkill". We decided against this to avoid sequence-related problems that could arise if mongo and azure would end up desynced. The way it's implemented now we use mongo's object key to name the top folder and random uuid's to rename images and models, to avoid overlaps in the names due to user error.

## Deployment
### Why Docker
Due to our varying technologies, we have several major dependencies: Node, Python, Java, Gradle, CUDA, and a mongo shell. Without docker this would be a very complicated installation procedure, which would depend on the user installing the correct versions for each one, to ensure compatibility. Failing to do so could lead to unexpected errors and instability.
We were initially hesitant towards Docker, due to the complicated setup, but it's more important to guarantee a smooth installation for the client and can speed up the setup for code reviews.
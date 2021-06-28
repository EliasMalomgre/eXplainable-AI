#Glossary for the XAI project

|Term|Explanation|Alias|
|--|--|--|
| XAII | Name of the application, eXplainable Artificial Intelligence Interface |  |
| Vue front-end | The front-end/UI webpack service running in vue | Front-end, Vue |
| Spring core | The spring microservice that orchestrates the application | Spring, Java, Orchestrator |
| XAI processing | The python backend that makes explanations and processes model information | Python, XAI project |
| PWA | Progressive Web App, a web application that can be "downloaded" or ran on mobile devices |  |
| Model | Trained neural network uploaded by the user, which he wishes to analyse | Neural net, Net, neural network, NN |
| Data | Data which is used during the explanation. This can be images, CSV's or text. | image, CSV |
| Deserialized neural net | An object which has information about a model which is used to visualise the model structure | |
| Layer | A layer in the neural network, NN's generally contain several layers |  |
| Training set | All images required to analyze the model, to properly explain future images | background |
| AI Bundle | The bundled data of the model, training set, and extra information | Upload, Bundle |
| Explainer | An object which is used to explain how a model performs using input data (CSV, images, or text) | |
| Explanation Task | A request made by the user to explain a neural net | Task |
| Explanation Task Queue | A queue for explanation tasks to make sure XAI processing isn't overloaded | Queue |
| Explanation | The result of the XAI process for a given neural net and data  | Result |
| dto | Data Transfer Object |  |



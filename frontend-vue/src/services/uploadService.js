import axios from 'axios'

class UploadService {

    /**
     * Transmits a rest call to the Spring Core backend, to upload the model and data into
     * an aggregated object of both, which will then be stored in azure file storage and mongodb
     *
     * @param name          The name for the neural network
     * @param description   The description for the neural network
     * @param model         The pretrained model of the neural network
     * @param data          The training set, which we'll use to analyse the model
     * @param classLabels   The labels associated with the output layer of the model
     * @param dataType      What kind of data the model uses to feed their input layer
     * @returns {Promise<T | boolean>} whether the upload succeeded or not
     */
    uploadModelAndData(name, description, model, data, classLabels, dataType) {
        let formData = new FormData();

        formData.append('model', model);

        if (!Array.isArray(data)) {
            formData.append('data', data);
        } else {
            for (let i = 0; i < data.length; i++) {
                formData.append('data', data[i]);
            }
        }

        formData.append('name', name)
        if (description != null) {
            formData.append('description', description)
        }

        if (classLabels != null) {
            formData.append('classLabels', classLabels)
        }

        formData.append('dataType', dataType)

        return axios.post(process.env.VUE_APP_BACKEND + '/aibundle/uploadModelAndData',
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(result => {
            return result;
        }).catch(function () {
            console.error("Error while attempting to connect to aibundle/uploadModelAndData");
            return false;
        });
    }

    /**
     * Requests the backend to generate an explanation for the given uploadid,
     * backend will create a queued task based on the given request
     *
     * @param uploadId      the model and training we wish to generate an explanation for
     * @param taskType      the type of explanation we wish to receive
     * @param dataType      the type of the data we're transmitting
     * @param plotType      the type of plot we wish to see
     * @param data          the data we wish to see explained with the model
     * @param outputs       the number of outputs shown in the result
     * @param normalize     whether the output should be normalized
     * @returns {Promise<T | boolean>}
     */
    requestExplanation(uploadId, taskType, plotType, dataType, data, outputs, normalize) {
        let formData = new FormData();

        formData.append('aiBundleId', uploadId);
        formData.append('taskType', taskType);
        formData.append('plotType', plotType);
        formData.append('dataType', dataType);
        if (!Array.isArray(data)) {
            formData.append('data', data);
        } else {
            for (let i = 0; i < data.length; i++) {
                formData.append('data', data[i]);
            }
        }
        formData.append('rankedOutputs', outputs)
        formData.append('normalize', normalize)

        return axios.post(process.env.VUE_APP_BACKEND + '/explanation/request',
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        ).then(function () {
            return true;
        }).catch(function () {
            console.error("Error while attempting to connect to explanation/request");
            return false;
        });
    }
}

export default new UploadService();
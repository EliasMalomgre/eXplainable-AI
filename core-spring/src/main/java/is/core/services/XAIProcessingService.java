package is.core.services;

import is.core.config.AddressConfig;
import is.core.domain.dtos.ModelInfoDto;
import is.core.domain.entities.files.Data;
import is.core.domain.entities.files.Model;
import is.core.domain.entities.taskparameters.ImagePlotParameters;
import is.core.domain.entities.taskparameters.Parameters;
import is.core.domain.enums.DataType;
import is.core.domain.enums.ExplainerType;
import is.core.domain.enums.PlotType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
public class XAIProcessingService {
    private final String uri;
    private final RestTemplate restTemplate;

    public XAIProcessingService(RestTemplate restTemplate, AddressConfig addressConfig) {
        this.restTemplate = restTemplate;
        this.uri = addressConfig.getPython();
    }

    /**
     * Transmits a model, images and images to explain to XAI project, to generate a shap image explanation
     *
     * @param model         A reference of the model
     * @param data          References of the data
     * @param dataToExplain References of the data to explain
     * @param taskId        The id of the task to which the result belongs
     * @param parameters    Contains task specific parameters
     * @param classLabels   The labels for the outputs of the model
     * @param explainerType The type of explainer which has to be used
     * @param plotType      The type of plot which has to be used
     */
    public void startExplanation(Model model, List<Data> data, List<Data> dataToExplain, String taskId,
                                 List<String> classLabels, ExplainerType explainerType, PlotType plotType, Parameters parameters) throws ConnectException {
        log.info("XAIProcessing: Transmitting data to XAI processing unit for Task({})", taskId);

        // Create http headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create a multi value map of all the resources
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("taskId", taskId);
        body.add("model", model);
        data.forEach(d -> body.add("data", d));
        dataToExplain.forEach(te -> body.add("toexplain", te));
        body.add("classLabels", classLabels);
        body.add("explainerType", explainerType);
        body.add("plotType", plotType);
        body.add("parameters", parameters);


        // Send request
        ResponseEntity<String> response = null;
        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            response = restTemplate.postForEntity(
                    uri + "/explanation/startexplanation",
                    requestEntity,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("XAIProcessing: Error occurred with status code [{}] when starting an explanation!", response.getStatusCodeValue());
                throw new ConnectException(String.format("An error has occurred in the XAI processing server while starting explanation[%s]", taskId));
            }
        } catch (Exception e) {
            log.error("XAIProcessing: Error occurred with status code [{}] when starting an explanation!", response.getStatusCodeValue());
            throw new ConnectException(String.format("An error has occurred in the XAI processing server while starting explanation[%s]", taskId));
        }

    }

    /**
     * Transmits a model to Python, to request model input shape
     *
     * @param model A reference to the model in Azure file storage
     * @return The input shape of the model
     */
    public ModelInfoDto getModelInfo(Model model) throws ConnectException {

        log.info("XAIProcessing: Getting model information for model with path[{}]", model.getStoragePath());

        // Create http headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create a multi value map of all the resources
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", model);

        // Send request
        ResponseEntity<ModelInfoDto> response = null;
        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            response = restTemplate.postForEntity(
                    uri + "model/getmodelinfo",
                    requestEntity,
                    ModelInfoDto.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("XAIProcessing: Error occurred with status code [{}] when getting model information!", response.getStatusCodeValue());
                throw new ConnectException(String.format("An error has occurred in the XAI processing server while getting model information of model[%s]", model.getId()));
            }
        } catch (Exception e) {
            log.error("XAIProcessing: Error occurred with status code [{}] when getting model information!", response.getStatusCodeValue());
            throw new ConnectException(String.format("An error has occurred in the XAI processing server while getting model information of model[%s]", model.getId()));
        }

        return response.getBody();
    }

    /**
     * Checks if the xai processing service is available, if not returns false
     *
     * @return if the service is available or not
     */
    public boolean isXaiAlive() {
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            return true;
        } catch (Exception e) {
            log.warn("XAIProcessing: XAI project is not alive");
            return false;
        }
    }
}

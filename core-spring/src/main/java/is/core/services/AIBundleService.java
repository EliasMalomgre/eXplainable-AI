package is.core.services;

import com.opencsv.CSVReader;
import is.core.domain.dtos.ModelInfoDto;
import is.core.domain.dtos.UploadedModelAndDataDto;
import is.core.domain.entities.AIBundle;
import is.core.domain.entities.files.Data;
import is.core.domain.entities.files.Model;
import is.core.domain.entities.neuralstructure.DeserializedNeuralNet;
import is.core.domain.entities.neuralstructure.Layer;
import is.core.domain.enums.DataType;
import is.core.repositories.AIBundleRepository;
import is.core.repositories.DataRepository;
import is.core.repositories.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIBundleService {

    private final AzureStorageService storageService;
    private final AIBundleRepository aiBundleRepository;
    private final XAIProcessingService xaiService;
    private final ModelRepository modelRepository;
    private final DataRepository dataRepository;

    /**
     * Returns all uploads to create an overview with, so the user can pick one of them
     *
     * @return a list of all uploads
     */
    public List<AIBundle> getAllBundles() {
        return aiBundleRepository.findAll(Sort.by(Sort.Direction.DESC, "timeUpdated"))
                .stream().filter(aiBundle -> aiBundle.getTimeUploaded() != null).collect(Collectors.toList());
    }

    /**
     * This method aIBundles all data into Azure file storage and saves the paths in a Mongo database
     *
     * @param uploadDto All data that has to be uploaded
     * @return The Id of the aIBundle document in the MongoDb
     */
    public String uploadAIBundle(UploadedModelAndDataDto uploadDto) throws ConnectException {
        // Save an empty aiBundle in MongoDb
        AIBundle aiBundle = aiBundleRepository.save(new AIBundle());
        aiBundle.setName(uploadDto.getName());
        aiBundle.setDescription(uploadDto.getDescription());

        log.info("AIBundle({}): Combined aiBundle has been initialised in MongoDB with key: {}", aiBundle.getId(), aiBundle.getId());

        // Get current date to add as prefix to the folder
        String dateTime = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyMMdd-HHmm"));

        // The Id of the aiBundle will be the root folder in Azure file storage
        aiBundle.setStoragePath(String.format("%s-%s", dateTime, aiBundle.getId()));

        // AIBundle the model to Azure file storage in the models folder and create a new model object with the returned path
        Model model = new Model(storageService.upload(uploadDto.getModel(), aiBundle.getStoragePath() + "/models/"));
        log.info("AIBundle({}): Model has been saved in Azure File Storage under: {}", aiBundle.getId(), aiBundle.getStoragePath() + "/models/");

        // Save the model in MongoBd and add it to the aiBundle object
        aiBundle.getModels().add(modelRepository.save(model));

        // Get model info from XAI project
        getModelInfo(aiBundle, uploadDto);

        // Storing the data
        saveData(aiBundle, uploadDto);

        // Finalize properties of aiBundle
        aiBundle.setTimeUploaded(LocalDateTime.now(ZoneOffset.UTC));
        aiBundle = updateBundle(aiBundle);

        log.info("AIBundle({}): AIBundle procedure was successful and has been finalised in MongoDB", aiBundle.getId());

        return aiBundle.getId();
    }

    /**
     * Saves data to MongoDb and Azure file stroge
     *
     * @param aiBundle      The newly created bundle
     * @param uploadDto     The upload data
     */
    public void saveData(AIBundle aiBundle, UploadedModelAndDataDto uploadDto) {
        aiBundle.setRequiredDataType(uploadDto.getDataType());

        String destination = aiBundle.getStoragePath() + "/data/";

        List<Data> savedData = new ArrayList<>();

        // Check the data type of the upload and use the correct upload method
        if (aiBundle.getRequiredDataType() == DataType.IMAGE) {
            savedData = uploadImages(
                    uploadDto.getData(),
                    destination,
                    DataType.IMAGE,
                    aiBundle.getInputShape()
            );
        }
        else if (aiBundle.getRequiredDataType() == DataType.CSV){
            savedData = uploadCSV(
                    uploadDto.getData().get(0),
                    destination,
                    DataType.CSV,
                    aiBundle.getInputShape()
            );
            if (savedData.isEmpty()) {
                log.warn("AIBundle({}) Invalid csv has been uploaded!", aiBundle.getId());
            }
        }

        if (savedData.isEmpty()) {
            aiBundleRepository.delete(aiBundle);
            storageService.deleteFile(aiBundle.getModels().get(0).getStoragePath());
            log.error("AIBundle({}): Uploaded data contains no valid files", aiBundle.getId());
            throw new IllegalArgumentException(String.format("AIBundle(%s): Uploaded data contains no valid files", aiBundle.getId()));
        }

        // Add all correctly saved data to aiBundle object
        aiBundle.getDataList().addAll(savedData);
        log.info("AIBundle({}): {} Data entries have been saved in Azure File Storage under: {}", aiBundle.getId(), savedData.size(), destination);
    }

    /**
     * Validates a single image
     *
     * @param imageBytes incoming bytes
     * @param inputShape the inputshape the image must remain conform to
     * @return if the given image is valid or not
     */
    public boolean validateImage(byte[] imageBytes, List<Integer> inputShape) {
        try {
            // Convert bytes to a buffered image
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            // Check for correct wight, height and color dimensions
            return image.getWidth() == inputShape.get(2)
                    && image.getHeight() == inputShape.get(1)
                    && image.getColorModel().getNumComponents() == inputShape.get(3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Validates incoming multipart files and saves them to the db
     *
     * @param dataList              the files we wish to save
     * @param destination           the path we want to save them to
     * @param type                  the file type
     * @param inputShape            the inputshape we're validating for
     * @param returnedInvalidImages an optional list we can add as parameter to retrieve failed files for error handling
     * @return a list of data objects, linked to successfully saved data
     * <p>
     */
    public List<Data> uploadImages(List<MultipartFile> dataList, String destination, DataType type, List<Integer> inputShape, List<MultipartFile> returnedInvalidImages) {
        List<Data> savedData = new ArrayList<>();

        // AIBundle all images to Azure file storage in the image folder and create a new image object with the returned path
        // and save the images in MongoDb and add them to the aIBundle object
        dataList.forEach(image -> {
            try {
                // If the image is valid, save it to the db's
                if (validateImage(image.getBytes(), inputShape)) {
                    Data data = new Data(storageService.upload(image, destination), type);
                    savedData.add(dataRepository.save(data));
                } else {
                    returnedInvalidImages.add(image);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        returnedInvalidImages.forEach(image -> log.warn("File {} is invalid", image.getOriginalFilename()));

        return savedData;
    }

    /**
     * Overload of validateImages, which doesn't require a returnedInvalidImages
     *
     * @param dataList    the files we wish to save
     * @param destination the path we want to save them to
     * @param type        the file type
     * @param inputShape  the inputshape we're validating for
     * @return a list of data objects, linked to successfully saved data
     */
    public List<Data> uploadImages(List<MultipartFile> dataList, String destination, DataType type, List<Integer> inputShape) {
        List<MultipartFile> invalidImages = new ArrayList<>();
        return uploadImages(dataList, destination, type, inputShape, invalidImages);
    }

    /**
     * Uploads an CSV to MongoDb and Azure file storage
     *
     * @param csv           The CSV to upload
     * @param destination   The destination path
     * @param type          The data type
     * @param inputShape    The input shape of the model
     * @return a list of saved data
     */
    public List<Data> uploadCSV(MultipartFile csv, String destination, DataType type, List<Integer> inputShape) {
        List<Data> savedData = new ArrayList<>();
        // If the CSV is valid, save it to the db's
        if (validateCSV(csv, inputShape)) {
            Data data = new Data(storageService.upload(csv, destination), type);
            savedData.add(dataRepository.save(data));
        }
        return savedData;
    }

    /**
     * Validates a CSV
     *
     * @param csv           The CSV to validate
     * @param inputShape    The input shape of the model
     * @return whether the CSV is valid
     */
    public boolean validateCSV(MultipartFile csv, List<Integer> inputShape){
        // Try to read the incoming csv
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(csv.getInputStream()));
            // Loop through all the lines an check for the correct length
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line.length != inputShape.get(1)) {
                    return false;
                }
            }
            csvReader.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Gets and processes all the model information pulled from XAI project
     *
     * @param aiBundle  The aiBundle used
     * @param uploadDto The uploaded data
     */
    private void getModelInfo(AIBundle aiBundle, UploadedModelAndDataDto uploadDto) throws ConnectException {
        // Checking the inputshape of the stored model
        ModelInfoDto modelInfoDto;
        // Try to make connection with XAI project
        try {
            modelInfoDto = xaiService.getModelInfo(aiBundle.getModels().get(0));
        } catch (Exception e) {
            aiBundleRepository.delete(aiBundle);
            storageService.deleteFile(aiBundle.getModels().get(0).getStoragePath());
            log.error("Model information request to XAI processing service failed");
            throw new ConnectException("No connection could be made to the XAI processing service, try again later");
        }

        aiBundle.setInputShape(Arrays.stream(modelInfoDto.getInputShape()).boxed().collect(Collectors.toList()));

        // Try to read the incoming csv
        try {
            if (uploadDto.getClassLabels() != null) {
                CSVReader csvReader = new CSVReader(new InputStreamReader(uploadDto.getClassLabels().getInputStream()));
                // Get(0) because all labels have to be on one line
                List<String> classLabels = Arrays.asList(csvReader.readAll().get(0));
                // If the csv is valid set aiundle class labels to the csv class labels
                if (modelInfoDto.getOutputShape()[1] == classLabels.size()) {
                    aiBundle.setClassLabels(classLabels);
                } else {
                    log.warn("AIBundle({}) Invalid csv has been uploaded!", aiBundle.getId());
                }
                csvReader.close();
            }
        } catch (IOException e) {
            log.warn("AIBundle({}) Could not read csv file!", aiBundle.getId());
        } catch (Exception e) {
            log.warn("AIBundle({}) Invalid csv has been uploaded!", aiBundle.getId());
        }

        // Parse incoming model information to DeserializedNeuralNet and Layer objects
        DeserializedNeuralNet neuralNet = new DeserializedNeuralNet();
        neuralNet.getLayers().addAll(modelInfoDto.getLayers().stream().map(dto -> new Layer(
                dto.getLayerPosition(), dto.getName(), dto.getClassName(),
                Arrays.stream(dto.getInputShape()).boxed().collect(Collectors.toList()),
                Arrays.stream(dto.getOutputShape()).boxed().collect(Collectors.toList())))
                .collect(Collectors.toList()));

        aiBundle.setDeserializedNeuralNet(neuralNet);
    }

    /**
     * Returns the requested aIBundle by given id, throws error if non existent
     *
     * @param aIBundleId id of the requested aggregate of model and data
     * @return the requested AIBundle object
     */
    public AIBundle getAIBundleById(String aIBundleId) {
        return aiBundleRepository.findById(aIBundleId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Given Id(%s) does not match an existing aggregate of models and data", aIBundleId)));
    }

    /**
     * Retrieves the bundle corresponding to the given id and updates its last activity/updated time
     * Stores the updated bundle
     *
     * @param bundleId the id corresponding to the bundle we wish to update
     * @return the updated bundle
     */
    public AIBundle updateBundle(String bundleId) {
        AIBundle bundle = getAIBundleById(bundleId);
        return updateBundle(bundle);
    }

    /**
     * Updates the bundle's last activity/updated time
     * Stores the updated bundle
     *
     * @param bundle the bundle we wish to update
     * @return the updated bundle
     */
    public AIBundle updateBundle(AIBundle bundle) {
        bundle.setTimeUpdated(LocalDateTime.now(ZoneOffset.UTC));
        return aiBundleRepository.save(bundle);
    }
}

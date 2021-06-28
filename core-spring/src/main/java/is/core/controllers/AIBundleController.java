package is.core.controllers;

import is.core.domain.dtos.AIBundleCardsDto;
import is.core.domain.dtos.AIBundlePageDto;
import is.core.domain.dtos.UploadedModelAndDataDto;
import is.core.domain.entities.AIBundle;
import is.core.services.AIBundleService;
import is.core.services.DeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.ConnectException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/aibundle")
public class AIBundleController {

    private final AIBundleService aiBundleService;
    private final DeletionService deletionService;

    @PostMapping(path = "/uploadModelAndData", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadModelAndData(UploadedModelAndDataDto uploadedModelAndDataDto) {
        log.debug("Received 'uploadModelAndData' POST");

        if (uploadedModelAndDataDto.getModel() == null || uploadedModelAndDataDto.getData() == null ||
                uploadedModelAndDataDto.getData().size() == 0 ||
                (uploadedModelAndDataDto.getName() != null && uploadedModelAndDataDto.getName().length() > 100) ||
                (uploadedModelAndDataDto.getDescription() != null && uploadedModelAndDataDto.getDescription().length() > 1500)) {
            log.warn("'uploadModelAndImages' resulted in a BAD REQUEST due to missing crucial information and has been aborted");
            return ResponseEntity.badRequest().body("POST was missing crucial information and has been aborted");
        }

        log.info("Starting the saving procedure for model({}) and {} data entries", uploadedModelAndDataDto.getModel().getOriginalFilename(), uploadedModelAndDataDto.getData().size());

        try {
            return ResponseEntity.ok().body(aiBundleService.uploadAIBundle(uploadedModelAndDataDto));
        } catch (ConnectException e) {
            return ResponseEntity.status(500).body("Validation service is temporarily down, please try again later");
        }
    }

    @GetMapping(path = "/getAllBundles")
    public ResponseEntity<List<AIBundleCardsDto>> getAllBundles() {
        log.debug("Received 'getAllBundles' GET");
        try {
            List<AIBundle> bundles = aiBundleService.getAllBundles();
            return ResponseEntity.ok().body(bundles.stream()
                    .map(upload -> new AIBundleCardsDto(upload.getId(), upload.getName(), upload.getTimeUploaded(), upload.getTimeUpdated()))
                    .collect(Collectors.toList()));
        }
        
        catch (Exception e) {
            log.error("Something went wrong getting bundles!");
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(path = "/getBundle")
    public ResponseEntity<AIBundlePageDto> getBundle(@RequestParam String bundleId) {
        log.debug("Received 'getBundle' GET for bundleId={}", bundleId);
        try {
            AIBundle bundle = aiBundleService.getAIBundleById(bundleId);
            return ResponseEntity.ok().body(new AIBundlePageDto(
                    bundle.getId(),
                    bundle.getName(),
                    bundle.getDescription(),
                    bundle.getRequiredDataType().toString().toLowerCase(),
                    bundle.getInputShape(),
                    bundle.getDataList(),
                    bundle.getClassLabels(),
                    bundle.getDeserializedNeuralNet(),
                    bundle.getTimeUploaded(),
                    bundle.getTimeUpdated()));
        }
        catch (Exception e) {
            log.error("AiBundle({}): Something went wrong getting bundle!", bundleId);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping(path = "/deleteBundle")
    public ResponseEntity<Boolean> deleteAIBundle(@RequestParam String bundleId) {
        log.debug("Received 'getBundle' Delete for bundleId={}", bundleId);
        try {
            deletionService.deleteBundleById(bundleId);
            return ResponseEntity.ok(true);
        }
        catch (Exception e){
            log.error("AiBundle({}): Something went wrong while deleting!", bundleId);
            return ResponseEntity.badRequest().body(false);
        }

    }
}

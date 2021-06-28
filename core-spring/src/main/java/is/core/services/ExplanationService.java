package is.core.services;

import is.core.domain.entities.ExplanationTask;
import is.core.domain.entities.AIBundle;
import is.core.repositories.AIBundleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExplanationService {
    private final XAIProcessingService xaiProcessingService;
    private final AIBundleRepository AIBundleRepository;

    /**
     * Starts the explanation process
     *
     * @param task  The task of the explanation
     */
    public void startExplanation(ExplanationTask task) throws ConnectException {
        AIBundle aiBundle = AIBundleRepository.findById(task.getAiBundleId()).orElseThrow( () ->
                new NoSuchElementException(String.format("AIBundle(%s) could not be found in the database when finishing task", task.getAiBundleId())));
      try {
            xaiProcessingService.startExplanation(aiBundle.getModels().get(0), aiBundle.getDataList(), task.getDataList(), task.getId(),
             aiBundle.getClassLabels(), task.getExplainerType(), task.getPlotType(), task.getParameters());
        } catch (Exception e) {
            log.error("Requested explanation was not accepted by the XAI processing service");
            throw e;
        }
    }
}

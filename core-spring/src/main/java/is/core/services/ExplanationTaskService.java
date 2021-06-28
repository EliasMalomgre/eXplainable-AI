package is.core.services;

import is.core.domain.dtos.RequestedTaskDto;
import is.core.domain.entities.ExplanationTask;
import is.core.domain.entities.files.Result;
import is.core.domain.entities.taskparameters.ImagePlotParameters;
import is.core.domain.entities.taskparameters.Parameters;
import is.core.domain.enums.DataType;
import is.core.repositories.ExplanationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExplanationTaskService {

    private final ResultService resultService;

    private final ExplanationTaskRepository taskRepository;

    /**
     * Requests all tasks sorted by their initial scheduled dateTime
     *
     * @return all tasks
     */
    public List<ExplanationTask> getAllTasks() {
        return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "timeScheduled"));
    }

    /**
     * Requests all tasks sorted by their initial scheduled dateTime, for a given upload
     *
     * @return all corresponding tasks
     */
    public List<ExplanationTask> getAllTasksForUpload(String bundleId) {
        return taskRepository.findAllByAiBundleId(bundleId, Sort.by(Sort.Direction.DESC, "timeScheduled"));
    }

    /**
     * Saves a result to MongoDb and Azure file storage
     *
     * @param task      The task to which the result belongs
     * @param results    The results which needs saving
     * @return the saved task containing all the results
     */
    public ExplanationTask saveResult(ExplanationTask task, List<MultipartFile> results) {
        for (MultipartFile result : results) {
            Result r = resultService.postResult(result, task.getStoragePath());
            task.getResults().add(r);
        }
        return taskRepository.save(task);
    }

    /**
     * Determines what kind of task is requested and what fields are important
     *
     * @param taskDto   The DTO containing all relevant information and parameters
     * @return  A subclass of Parameters, containing the relevant information
     */
    public Parameters extractParameters(RequestedTaskDto taskDto) {
        if(taskDto.getDataType().equals(DataType.IMAGE)){
            return new ImagePlotParameters(taskDto.getRankedOutputs(), taskDto.getNormalize());
        }
        return new Parameters();
    }
}

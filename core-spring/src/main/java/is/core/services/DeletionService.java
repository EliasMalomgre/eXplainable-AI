package is.core.services;

import is.core.domain.entities.AIBundle;
import is.core.domain.entities.ExplanationTask;
import is.core.domain.entities.files.Data;
import is.core.domain.entities.files.Model;
import is.core.domain.entities.files.Result;
import is.core.domain.enums.TaskStatus;
import is.core.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletionService {

    private final AzureStorageService storageService;
    private final ExplanationTaskService taskService;
    private final ExplanationTaskQueueService queueService;
    private final ResultService resultService;

    private final AIBundleRepository aiBundleRepository;
    private final DataRepository dataRepository;
    private final ModelRepository modelRepository;
    private final ExplanationTaskRepository taskRepository;
    private final ResultRepository resultRepository;

    /**
     * Deletes aiBundle from Azure file storage and MongoDb with all its contents: model, data, tasks and results
     *
     * @param bundleId The id of the aiBundle to delete
     */
    public void deleteBundleById(String bundleId) {
        AIBundle aiBundle = aiBundleRepository.findById(bundleId).orElseThrow();
        aiBundle.getDataList().forEach(this::deleteData);
        aiBundle.getModels().forEach(this::deleteModel);

        this.deleteTasksForBundleId(bundleId);

        aiBundleRepository.delete(aiBundle);
        log.info("AIBundle({}): bundle has been deleted", aiBundle.getId());
    }

    /**
     * Deletes data from Azure file storage and MongoDb
     *
     * @param data Data to delete
     */
    public void deleteData(Data data) {
        storageService.deleteFile(data.getStoragePath());
        dataRepository.delete(data);
        log.debug("Data({}): data has been deleted", data.getId());

    }

    /**
     * Deletes aiBundle from Azure file storage and MongoDb
     *
     * @param model Model to delete
     */
    public void deleteModel(Model model) {
        storageService.deleteFile(model.getStoragePath());
        modelRepository.delete(model);
        log.debug("Data({}): data has been deleted", model.getId());

    }

    /**
     * Deletes all tasks linked to an aiBundle from Azure file storage and MongoDb
     *
     * @param bundleId The id of the linked aiBundle
     */
    public void deleteTasksForBundleId(String bundleId) {
        taskService.getAllTasksForUpload(bundleId).forEach(this::deleteTask);
        log.info("AIBundle({}): tasks for bundle have been deleted", bundleId);
    }

    /**
     * Deletes task from Azure file storage and MongoDb with its result
     *
     * @param taskId Id of the task to delete
     */
    public void deleteTask(String taskId) {
        ExplanationTask task = taskRepository.findById(taskId).orElseThrow(() ->
                new NoSuchElementException(String.format("Task(%s) could not be found in the database", taskId)));
        deleteTask(task);
    }

    /**
     * Deletes task from Azure file storage and MongoDb with its result
     *
     * @param task Task to delete
     */
    public void deleteTask(ExplanationTask task) {
        // Cancels task when pending or inProgress
        if (task.getStatus() == TaskStatus.PENDING || task.getStatus() == TaskStatus.INPROGRESS) {
            queueService.cancelTask(task.getId());
        }

        task.getResults().forEach(this::deleteResult);
        taskRepository.delete(task);
        log.info("ExplanationTask({}): task has been deleted", task.getId());
    }

    /**
     * Deletes result from Azure file storage and MongoDb
     *
     * @param result Result to delete
     */
    public void deleteResult(Result result) {
        storageService.deleteFile(result.getStoragePath());
        resultRepository.delete(result);
        log.debug("Result({}): result has been deleted", result.getId());
    }
}

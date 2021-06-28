package is.core.controllers;

import is.core.domain.dtos.BundleTasksDto;
import is.core.domain.dtos.PostResultDto;
import is.core.domain.enums.ExplanationError;
import is.core.services.DeletionService;
import is.core.services.ExplanationTaskQueueService;
import is.core.services.ExplanationTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/task")
public class ExplanationTaskController {

    private final ExplanationTaskService taskService;
    private final ExplanationTaskQueueService queueService;
    private final DeletionService deletionService;

    @GetMapping(path = "getAllTasks")
    public ResponseEntity<BundleTasksDto> getAllTasks() {
        log.debug("Received 'getAllTasks'");
        try {
            return ResponseEntity.ok().body(new BundleTasksDto(taskService.getAllTasks()));
        } catch (Exception e) {
            log.error("Tasks: Something went wrong getting all tasks!");
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(path = "getTasksForBundle")
    public ResponseEntity<BundleTasksDto> getTasksForBundle(@RequestParam String bundleId) {
        log.debug("Received 'getTasksForBundle' for Bundle:{}", bundleId);
        try {
            return ResponseEntity.ok().body(new BundleTasksDto(taskService.getAllTasksForUpload(bundleId)));
        } catch (Exception e) {
            log.error("AiBundle({}): Something went wrong getting tasks for bundle!", bundleId);
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping(path = "deleteTasksForBundle")
    public ResponseEntity<Boolean> deleteTasksForBundle(@RequestParam String bundleId) {
        log.debug("Received 'deleteTasksForBundle' for Bundle:{}", bundleId);
        try {
            deletionService.deleteTasksForBundleId(bundleId);
            return ResponseEntity.ok().body(true);
        }
        catch (Exception e){
            log.error("AiBundle({}): Something went wrong while deleting tasks for bundle!", bundleId);
            return ResponseEntity.badRequest().body(false);
        }
    }

    @DeleteMapping(path = "deleteTask")
    public ResponseEntity<Boolean> deleteTask(@RequestParam String taskId) {
        log.debug("Received 'deleteTask' for Task:{}", taskId);
        try {
            deletionService.deleteTask(taskId);
            return ResponseEntity.ok().body(true);
        }
        catch (Exception e){
            log.error("ExplanationTask({}): Something went wrong while deleting task!", taskId);
            return ResponseEntity.badRequest().body(false);
        }
    }


    @PostMapping(path = "cancelTask")
    public ResponseEntity<Boolean> cancelTask(@RequestParam String taskId){
        log.debug("Received 'cancelTask' for Task:{}", taskId);
        try{
            queueService.cancelTask(taskId);
            return ResponseEntity.ok().body(true);
        } catch (Exception e) {
            log.error("ExplanationTask({}): Something went wrong while cancelling task!", taskId);
            return ResponseEntity.status(500).body(false);
        }
    }


    @PostMapping(path = "postExplanationError")
    public ResponseEntity<String> postExplanationError(@RequestParam String taskId, @RequestParam ExplanationError explanationError) {
        log.debug("Received 'postExplanationError' for Task:{}", taskId);
        try{
            queueService.postExplanationError(taskId, explanationError);
            return ResponseEntity.ok().build();
        } catch (Error e) {
            log.error("ExplanationTask({}): Something went wrong while posting an error!", taskId);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping(path = "finishtask", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Boolean> finishTask(PostResultDto resultDto) {
        log.debug("Received 'finishtask' for Task:{}", resultDto.getTaskId());
        try {
            queueService.finishTask(resultDto);
            return ResponseEntity.ok().body(true);
        } catch (Exception e) {
            log.error("ExplanationTask({}): Something went wrong while finishing task!", resultDto.getTaskId());
            return ResponseEntity.status(500).body(false);
        }
    }

}

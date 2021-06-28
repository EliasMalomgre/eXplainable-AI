package is.core.services;

import is.core.config.SchedulingConfig;
import is.core.domain.dtos.PostResultDto;
import is.core.domain.dtos.RequestedTaskDto;
import is.core.domain.entities.AIBundle;
import is.core.domain.entities.ExplanationTask;
import is.core.domain.entities.ExplanationTaskQueue;
import is.core.domain.entities.taskparameters.Parameters;
import is.core.domain.enums.DataType;
import is.core.domain.enums.ExplanationError;
import is.core.domain.enums.TaskStatus;
import is.core.repositories.ExplanationTaskQueueRepository;
import is.core.repositories.ExplanationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@EnableScheduling
@Service
@RequiredArgsConstructor
public class ExplanationTaskQueueService {
    private final XAIProcessingService xaiProcessingService;

    private final ExplanationService explanationService;
    private final AIBundleService aiBundleService;
    private final ExplanationTaskService taskService;

    private final ExplanationTaskRepository taskRepository;
    private final ExplanationTaskQueueRepository explanationTaskQueueRepository;

    private final SchedulingConfig schedulingConfig;

    /**
     * Returns all tasks currently in the queue
     *
     * @return the queue of planned tasks
     */
    public LinkedList<ExplanationTask> getQueuedTasks() {
        ExplanationTaskQueue queue = getQueueInitIfMissing();
        return queue.getTaskQueue();
    }

    /**
     * Returns the currently running task, if one is available
     *
     * @return the queue of planned tasks
     */
    public ExplanationTask getRunningTask() {
        ExplanationTaskQueue queue = getQueueInitIfMissing();
        ExplanationTask task = queue.getTaskQueue().getFirst();
        if (task == null) {
            log.debug("No currently running task available");
            return null;
        }

        if (!task.getStatus().equals(TaskStatus.INPROGRESS)) {
            log.debug("No tasks are running, attempting to start tasks");
            startNextTask();
            queue = getQueueInitIfMissing();
            task = queue.getTaskQueue().getFirst();
        }
        return task;
    }

    /**
     * Requests all tasks sorted by their initial scheduled dateTime
     *
     * @return all tasks
     */
    public List<ExplanationTask> getFinishedTasks() {
        return taskService.getAllTasks().stream()
                .filter(e -> e.getStatus().equals(TaskStatus.DONE))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the queue object from mongodb, initializes one if none are found
     *
     * @return the queue of pending and running tasks
     */
    private ExplanationTaskQueue getQueueInitIfMissing() {

        List<ExplanationTaskQueue> list = explanationTaskQueueRepository.findAll();

        // Check if there's a explanationTaskQueue available, if not create one
        if (list.isEmpty()) {
            log.warn("No ExplanationTaskQueue was found, creating one");
            explanationTaskQueueRepository.save(new ExplanationTaskQueue());
            list = explanationTaskQueueRepository.findAll();
        }

        // Warn user if multiple explanationTaskQueues are present
        if (list.size() > 1) {
            log.warn("Multiple({}) ExplanationTaskQueues have been detected in MongoDB, only one should be available", list.size());
        }

        ExplanationTaskQueue queue = list.get(0);
        log.debug("TaskQueue({}): retrieved, contains {} unfinished task(s)", queue.getId(), queue.getTaskQueue().size());

        return queue;
    }

    /**
     * Initializes a fresh task based on the given request
     *
     * @param taskDto required information to create the task, includes id of aggregate data, additional data and type
     */
    public void createTask(RequestedTaskDto taskDto) {

        // Throw error if aiBundleId is nonexistant
        AIBundle aiBundle = aiBundleService.getAIBundleById(taskDto.getAiBundleId());

        // Extract the necessary parameters for the outgoing request
        Parameters parameters = taskService.extractParameters(taskDto);

        // Initialize a Task object to save
        ExplanationTask task = new ExplanationTask(
                taskDto.getAiBundleId(),
                aiBundle.getName(),
                taskDto.getTaskType(),
                taskDto.getPlotType(),
                TaskStatus.PENDING,
                LocalDateTime.now(ZoneOffset.UTC),
                parameters
        );

        task = taskRepository.save(task);
        log.info("Task({}): Task on AIBundle({}) has been initialized with key: {}", task.getId(), task.getAiBundleId(), task.getId());

        // The destination folder
        String destination = aiBundle.getStoragePath() + "/tasks/" + task.getId();
        task.setStoragePath(destination);

        // Run validation
        if (aiBundle.getRequiredDataType().equals(taskDto.getDataType())) {
            switch (taskDto.getDataType()) {

                case IMAGE:
                    task.getDataList().addAll(
                            aiBundleService.uploadImages(
                                    taskDto.getData(),
                                    destination + "/toExplain/",
                                    DataType.IMAGE,
                                    aiBundle.getInputShape()));
                    break;

                case CSV:
                    task.getDataList().addAll(
                            aiBundleService.uploadCSV(
                                    taskDto.getData().get(0),
                                    destination + "/toExplain/",
                                    DataType.CSV,
                                    aiBundle.getInputShape()));
                    break;
            }

            // Cancel the task if no valid data was uploaded
            if (task.getDataList().isEmpty()) {
                task.setStatus(TaskStatus.CANCELLED);
                taskRepository.save(task);
                throw new IllegalArgumentException(String.format("Task(%s): No valid data was uploaded, cancelling task", task.getId()));
            }

        } else {
            // Cancelling task
            task.setStatus(TaskStatus.CANCELLED);
            taskRepository.save(task);

            throw new IllegalArgumentException(String.format("Task(%s): AIBundle(%s) required data of type %s, but received %s. Cancelling task", task.getId(), aiBundle.getId(),
                    aiBundle.getRequiredDataType(), taskDto.getDataType()));
        }

        log.info("Task({}): {} Data entries have been saved in Azure File Storage under: {}", task.getId(), task.getDataList().size(), destination + "/toExplain/");

        addTaskToQueue(taskRepository.save(task));
    }

    /**
     * Adds the created task to the queue and attempts to start it
     *
     * @param task the given task
     */
    public void addTaskToQueue(ExplanationTask task) {
        ExplanationTaskQueue queue = getQueueInitIfMissing();

        if (!task.getStatus().equals(TaskStatus.PENDING)) {
            log.error("Task({}): task was not pending, had status={}. Therefore has been cancelled", task.getId(), task.getStatus());

            task.setStatus(TaskStatus.CANCELLED);
            taskRepository.save(task);

            throw new IllegalArgumentException(String.format("Task(%s): task was not pending, had status=%s. Therefore has been cancelled", task.getId(), task.getStatus()));
        }

        queue.getTaskQueue().addLast(task);

        log.info("TaskQueue({}): Task({}) has been added to the back of the queue in position {}", queue.getId(), task.getId(), queue.getTaskQueue().size());

        // Save the updated queue, now that our task has been added
        explanationTaskQueueRepository.save(queue);
        aiBundleService.updateBundle(task.getAiBundleId());

        // Attempt to start the next task
        startNextTask();
    }

    /**
     * Attempts to start a next task if one is available, if one is still 'in progress' it will not start a new one
     */
    public void startNextTask() {
        ExplanationTaskQueue queue = getQueueInitIfMissing();

        if (queue.getTaskQueue().isEmpty()) {
            log.debug("TaskQueue({}): No tasks queued for execution", queue.getId());
            return;
        }

        ExplanationTask task = queue.getTaskQueue().getFirst();

        if (xaiProcessingService.isXaiAlive()) {

            if (task.getStatus().equals(TaskStatus.INPROGRESS)) {
                log.info("Task({}): task is already running, {} elements remaining in queue", task.getId(), queue.getTaskQueue().size() - 1);


            } else if (task.getStatus().equals(TaskStatus.PENDING)) {
                task.setStatus(TaskStatus.INPROGRESS);
                task.setTimeStarted(LocalDateTime.now(ZoneOffset.UTC));
                log.info("Task({}): task has started, {} elements remaining in queue", task.getId(), queue.getTaskQueue().size() - 1);
                task = taskRepository.save(task);
                explanationTaskQueueRepository.save(queue);
                aiBundleService.updateBundle(task.getAiBundleId());

                try {
                    explanationService.startExplanation(task);
                } catch (Exception e) {
                    cancelTask(task.getId());
                }
            } else {
                log.warn("Unforeseen error has occurred, rogue task in queue with status={}", task.getStatus());
                cancelTask(task.getId());
            }
        } else {
            if (task.getStatus().equals(TaskStatus.INPROGRESS)) {
                log.info("Task({}): XAI processing service is down, setting current task to pending", task.getId());
                task.setStatus(TaskStatus.PENDING);

                taskRepository.save(task);
                explanationTaskQueueRepository.save(queue);
                aiBundleService.updateBundle(task.getAiBundleId());
            }
        }
    }

    /**
     * Sets given task to done, attempts to start the next task
     */
    public void finishTask(PostResultDto resultDto) {
        // Save results for the task
        ExplanationTask task = taskRepository.findById(resultDto.getTaskId()).orElseThrow(() ->
                new NoSuchElementException(String.format("Task(%s) could not be found in the database when finishing task", resultDto.getTaskId())));

        task = taskService.saveResult(task, resultDto.getResults());

        // Retrieve the queue
        ExplanationTaskQueue queue = getQueueInitIfMissing();
        ExplanationTask peekedTask = queue.getTaskQueue().getFirst();

        if (peekedTask == null) {
            log.error("No task was found in the queue to be finished");

        } else {

            if (!task.getStatus().equals(TaskStatus.INPROGRESS)) {
                log.error("Given task({}) was not in progress, currently running task({})", task.getId(), peekedTask.getId());
            }

            if (task.getId().equals(peekedTask.getId())) {
                // Update the queue by removing the task from it
                queue.getTaskQueue().removeFirst();

                log.info("Finishing task({})", task.getId());
                task.setStatus(TaskStatus.DONE);
                task.setTimeDone(LocalDateTime.now(ZoneOffset.UTC));
                task.setProcessDuration(Duration.between(task.getTimeStarted(), task.getTimeDone()).toSeconds());

                taskRepository.save(task);
                explanationTaskQueueRepository.save(queue);
                aiBundleService.updateBundle(task.getAiBundleId());

                startNextTask();
            }
        }
    }

    /**
     * Cancel the task with the given id
     *
     * @param taskId The id of the task to cancel
     */
    public void cancelTask(String taskId) {
        ExplanationTaskQueue queue = getQueueInitIfMissing();
        ExplanationTask task = taskRepository.findById(taskId).orElseThrow(() ->
                new NoSuchElementException(String.format("Task(%s) could not be found in the database when canceling task", taskId)));


        if (task.getStatus().equals(TaskStatus.DONE) || task.getStatus().equals(TaskStatus.CANCELLED)) {
            log.error("Task({}): Task had already finished, can no longer be cancelled", taskId);
            return;
        }

        if (task.getStatus().equals(TaskStatus.INPROGRESS)) {
            log.warn("Task({}): Task was running, results may still show up", taskId);
        }

        queue.getTaskQueue().remove(task);
        task.setStatus(TaskStatus.CANCELLED);

        taskRepository.save(task);
        explanationTaskQueueRepository.save(queue);
        aiBundleService.updateBundle(task.getAiBundleId());

        log.info("Task({}): has been removed from the queue, {} tasks remaining", taskId, queue.getTaskQueue().size());
        startNextTask();
    }

    /**
     * Attempts to start the next task based on a cron schedule,
     * is meant as a failsafe in case a task would be left in limbo -> e.g. python stops while task in progress
     */
    @Scheduled(cron = "${scheduled.taskFrequency}")
    public void automatedTaskStartAttempt() {
        if (schedulingConfig.isEnabled()) {
            log.debug("Automated attempt to start next task");
            startNextTask();
        }
    }

    /**
     * Post an explanation error, describing what error or event occurred in XAI project
     *
     * @param taskId           The task that triggered an event
     * @param explanationError The type of error that occurred
     */
    public void postExplanationError(String taskId, ExplanationError explanationError) {
        ExplanationTask task = taskRepository.findById(taskId).orElseThrow(() ->
                new NoSuchElementException(String.format("Task %s could not be found in the database", taskId)));

        task.getErrors().add(explanationError);
        taskRepository.save(task);
        // When these errors happen the process is retried so the task must not be canceled
        if (explanationError != ExplanationError.CPU_SWITCH && explanationError != ExplanationError.LOW_BATCH_SIZE) {
            cancelTask(taskId);
        }
    }
}

package is.core.services;

import is.core.domain.dtos.PostResultDto;
import is.core.domain.dtos.RequestedTaskDto;
import is.core.domain.entities.AIBundle;
import is.core.domain.entities.ExplanationTask;
import is.core.domain.enums.DataType;
import is.core.domain.enums.ExplainerType;
import is.core.domain.enums.PlotType;
import is.core.domain.enums.TaskStatus;
import is.core.domain.entities.files.Data;
import is.core.repositories.AIBundleRepository;
import is.core.repositories.ExplanationTaskQueueRepository;
import is.core.repositories.ExplanationTaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExplanationTaskServiceTest {

    @Autowired
    ExplanationTaskService taskService;

    @Autowired
    ExplanationTaskQueueService queueService;

    @Autowired
    ExplanationTaskRepository taskRepository;

    @Autowired
    ExplanationTaskQueueRepository taskQueueRepository;

    @Autowired
    AIBundleRepository aiBundleRepository;

    @MockBean
    ExplanationService explanationService;

    @MockBean
    AIBundleService aiBundleService;

    @MockBean
    XAIProcessingService xaiProcessingService;


    AIBundle aiBundle1;
    AIBundle aiBundle;
    ExplanationTask t1;
    ExplanationTask t2;
    ExplanationTask t3;
    ExplanationTask t4done;

    @BeforeEach
    void setUp() throws ConnectException {
        // Clear test db
        taskRepository.deleteAll();
        taskQueueRepository.deleteAll();
        aiBundleRepository.deleteAll();

        // Seed data
        aiBundle1 = new AIBundle("destination/");
        aiBundle1.setRequiredDataType(DataType.IMAGE);
        aiBundle1 = aiBundleRepository.save(aiBundle1);


        t1 = taskRepository.save(
                new ExplanationTask(
                        "1000",
                        "Name",
                        ExplainerType.GRADIENT,
                        PlotType.IMAGE,
                        TaskStatus.PENDING,
                        LocalDateTime.now(ZoneOffset.UTC),
                        0,
                        true
                )
        );

        t2 = taskRepository.save(
                new ExplanationTask(
                        "1001",
                        "Name",
                        ExplainerType.GRADIENT,
                        PlotType.IMAGE,
                        TaskStatus.PENDING,
                        LocalDateTime.now(ZoneOffset.UTC),
                        0,
                        true
                )
        );

        t3 = taskRepository.save(
                new ExplanationTask(
                        "1002",
                        "Name",
                        ExplainerType.GRADIENT,
                        PlotType.IMAGE,
                        TaskStatus.PENDING,
                        LocalDateTime.now(ZoneOffset.UTC),
                        0,
                        true
                )
        );

        t4done = new ExplanationTask(
                "1003",
                "Name",
                ExplainerType.GRADIENT,
                PlotType.IMAGE,
                TaskStatus.DONE,
                LocalDateTime.now(ZoneOffset.UTC),
                0,
                true
        );

        List<Data> dataList = new ArrayList<>();
        dataList.add(new Data("path/", DataType.IMAGE));

        // Mocks to prevent non-required behaviour
        doNothing().when(explanationService).startExplanation(any());
        when(aiBundleService.getAIBundleById(anyString())).thenReturn(aiBundle1);
        when(aiBundleService.uploadImages(any(), anyString(), any(), any())).thenReturn(dataList);
        when(xaiProcessingService.isXaiAlive()).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void getQueuedTasksEmpty() {
        assertEquals(0, queueService.getQueuedTasks().size(), "The list is supposed to be empty at this stage");
    }

    @Test
    void getQueuedTasks() {
        when(xaiProcessingService.isXaiAlive()).thenReturn(true);

        queueService.addTaskToQueue(t1);
        queueService.addTaskToQueue(t2);
        queueService.addTaskToQueue(t3);

        LinkedList<ExplanationTask> queuedTasks = queueService.getQueuedTasks();

        assertEquals(3, queuedTasks.size(), "The list is supposed to contain 3 tasks at this stage");
        assertTrue(queuedTasks.stream().noneMatch(t -> t.getStatus().equals(TaskStatus.DONE)), "The DONE task should have been restarted");

        assertEquals(2, (int) queuedTasks.stream().filter(t -> t.getStatus().equals(TaskStatus.PENDING)).count(), "The list is supposed to contain 2 pending tasks at this stage");
        assertEquals(1, (int) queuedTasks.stream().filter(t -> t.getStatus().equals(TaskStatus.INPROGRESS)).count(), "The list is supposed to contain 1 running task at this stage");
    }

    @Test
    void getQueuedTasksXAIDown() {
        when(xaiProcessingService.isXaiAlive()).thenReturn(false);

        queueService.addTaskToQueue(t1);
        queueService.addTaskToQueue(t2);
        queueService.addTaskToQueue(t3);

        LinkedList<ExplanationTask> queuedTasks = queueService.getQueuedTasks();

        assertEquals(3, queuedTasks.size(), "The list is supposed to contain 3 tasks at this stage");
        assertTrue(queuedTasks.stream().noneMatch(t -> t.getStatus().equals(TaskStatus.DONE)), "The DONE task should have been restarted");

        assertEquals(3, (int) queuedTasks.stream().filter(t -> t.getStatus().equals(TaskStatus.PENDING)).count(), "If XAI Processing is down, there shouldn't be any running tasks afterwards");
        assertEquals(0, (int) queuedTasks.stream().filter(t -> t.getStatus().equals(TaskStatus.INPROGRESS)).count(), "If XAI Processing is down, the running task is supposed to have reverted to pending");
    }

    @Test
    void getFinishedTasks() {
        queueService.addTaskToQueue(t1);
        queueService.addTaskToQueue(t2);
        queueService.addTaskToQueue(t3);

        ExplanationTask t1started = queueService.getQueuedTasks().get(0);
        PostResultDto dto = new PostResultDto();
        dto.setTaskId(t1started.getId());
        dto.setResults(new ArrayList<>());
        queueService.finishTask(dto);

        LinkedList<ExplanationTask> queuedTasks = queueService.getQueuedTasks();

        assertEquals(2, queuedTasks.size(), "The list is supposed to contain 2 tasks at this stage");
        assertEquals(1, (int) queuedTasks.stream().filter(t -> t.getStatus().equals(TaskStatus.PENDING)).count(), "The list is supposed to contain 1 PENDING tasks");
        assertEquals(1, (int) queuedTasks.stream().filter(t -> t.getStatus().equals(TaskStatus.INPROGRESS)).count(), "The list is supposed to contain 1 INPROGRESS task");

        assertEquals(1, queueService.getFinishedTasks().size(), "One task is supposed to have finished");

        assertEquals(3, taskService.getAllTasks().size(), "In total there should be 3 tasks, of which 1 PENDING, 1 INPROGRESS and 1 DONE");
        assertEquals("1001", queueService.getRunningTask().getAiBundleId(), "the second item should have been set to INPROGRESS, the task meant for uploadid: 1001 ");
    }

    @Test
    void cancelTasks() {
        queueService.addTaskToQueue(t1);
        queueService.addTaskToQueue(t2);
        queueService.addTaskToQueue(t3);

        LinkedList<ExplanationTask> queuedTasks = queueService.getQueuedTasks();
        assertEquals(3, queuedTasks.size(), "The list is supposed to contain 3 tasks at this stage");

        ExplanationTask t2queued = queueService.getQueuedTasks().get(1);

        queueService.cancelTask(t2queued.getId());

        queuedTasks = queueService.getQueuedTasks();
        assertEquals(2, queuedTasks.size(), "The list is supposed to contain 2 tasks at this stage");
    }


    @Test
    void getAllTasksForUpload() {
        t2.setAiBundleId("1002");
        taskRepository.save(t2);

        assertEquals(2, taskService.getAllTasksForUpload("1002").size(), "Two tasks should be linked to upload with id 1002");
    }

    @Test
    void createTask() {
        when(xaiProcessingService.isXaiAlive()).thenReturn(true);

        List<MultipartFile> mockedMulti = new ArrayList<>();

        AIBundle upload = aiBundleRepository.save(aiBundle1);


        RequestedTaskDto dto = new RequestedTaskDto(upload.getId(), ExplainerType.GRADIENT, PlotType.IMAGE, DataType.IMAGE, mockedMulti, 0, true);

        queueService.createTask(dto);

        List<ExplanationTask> tasks = queueService.getQueuedTasks();

        assertEquals(1, tasks.size(), "One task should have been added to the queue");
        assertEquals(TaskStatus.INPROGRESS, tasks.get(0).getStatus(), "The task should have already been set to pending, seeing as it's the only task");

    }

    @Test
    void createTaskXAIDown() {
        when(xaiProcessingService.isXaiAlive()).thenReturn(false);

        List<MultipartFile> mockedMulti = new ArrayList<>();

        AIBundle upload = aiBundleRepository.save(aiBundle1);

        RequestedTaskDto dto = new RequestedTaskDto(upload.getId(), ExplainerType.GRADIENT, PlotType.IMAGE, DataType.IMAGE, mockedMulti,0 , true);

        queueService.createTask(dto);

        List<ExplanationTask> tasks = queueService.getQueuedTasks();

        assertEquals(1, tasks.size(), "One task should have been added to the queue");
        assertEquals(TaskStatus.PENDING, tasks.get(0).getStatus(), "If XAI is down, the task should stay on pending");
    }

    @Test
    void startNextTask() {
        assertThrows(IllegalArgumentException.class, () -> queueService.addTaskToQueue(t4done), "Done or Cancelled tasks can not be restarted");

        LinkedList<ExplanationTask> queuedTasks = queueService.getQueuedTasks();
        assertTrue(queuedTasks.stream().noneMatch(t -> t.getStatus().equals(TaskStatus.DONE)), "The list is should not contain DONE tasks");
    }
}
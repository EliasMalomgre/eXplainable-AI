package is.core.services;

import is.core.domain.entities.AIBundle;
import is.core.domain.entities.ExplanationTask;
import is.core.domain.entities.files.Data;
import is.core.domain.entities.files.Model;
import is.core.domain.entities.files.Result;
import is.core.domain.enums.DataType;
import is.core.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DeletionServiceTest {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private DeletionService deletionService;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private ExplanationTaskRepository taskRepository;

    @Autowired
    private AIBundleRepository aiBundleRepository;

    @MockBean
    private AzureStorageService storageService;

    @MockBean
    private ExplanationTaskService taskService;


    @Test
    public void testBundleDeletion() {
        when(storageService.deleteFile(anyString())).thenReturn(true);

        AIBundle bundle = new AIBundle();
        Model model1 = modelRepository.save(new Model("nets/HelloWorld1.h5"));
        Model model2 = modelRepository.save(new Model("nets/HelloWorld2.h5"));

        bundle.getModels().add(model1);
        bundle.getModels().add(model2);

        Data data1 = dataRepository.save(new Data("data/HelloWorld.png", DataType.IMAGE));
        Data data2 = dataRepository.save(new Data("data/HelloWorld.txt", DataType.RAWTEXT));
        Data data3 = dataRepository.save(new Data("data/HelloWorld.csv", DataType.CSV));

        bundle.getDataList().add(data1);
        bundle.getDataList().add(data2);
        bundle.getDataList().add(data3);


        Result result = resultRepository.save(new Result(""));

        ExplanationTask task = taskRepository.save(new ExplanationTask());
        task.setAiBundleId(bundle.getId());
        task.getResults().add(result);
        taskRepository.save(task);

        bundle = aiBundleRepository.save(bundle);

        when(taskService.getAllTasksForUpload(anyString())).thenReturn(Stream.of(task).collect(Collectors.toList()));

        deletionService.deleteBundleById(bundle.getId());

        System.out.println(taskRepository.findAll());
        AIBundle finalBundle = bundle;
        assertTrue(dataRepository.findAll().stream().noneMatch(data -> finalBundle.getDataList().contains(data)));
        assertTrue(modelRepository.findAll().stream().noneMatch(model -> finalBundle.getModels().contains(model)));
        assertTrue(resultRepository.findAll().stream().noneMatch(res -> res.equals(result)));
        assertTrue(taskRepository.findAll().stream().noneMatch(t -> t.equals(task)));
        assertTrue(aiBundleRepository.findAll().stream().noneMatch(aiBundle -> aiBundle.equals(finalBundle)));

    }
}

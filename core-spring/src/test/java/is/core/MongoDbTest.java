package is.core;

import is.core.domain.entities.AIBundle;
import is.core.domain.entities.ExplanationTask;
import is.core.domain.entities.ExplanationTaskQueue;
import is.core.domain.entities.files.Data;
import is.core.domain.entities.files.Model;
import is.core.domain.enums.DataType;
import is.core.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoDbTest {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ExplanationTaskQueueRepository taskQueueRepository;

    @Autowired
    private ExplanationTaskRepository taskRepository;

    @Autowired
    private AIBundleRepository AIBundleRepository;

    @Test
    public void TestMongo() {
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

        bundle = AIBundleRepository.save(bundle);
        AIBundle returned = AIBundleRepository.findById(bundle.getId()).orElse(null);
        assert returned != null;
        assertTrue(returned.getModels().containsAll(Stream.of(model1, model2).collect(Collectors.toList())));
        assertTrue(returned.getDataList().containsAll(Stream.of(data1, data2, data3).collect(Collectors.toList())));

    }

    @Test
    public void TestSavingTasks() {
        ExplanationTaskQueue queue = new ExplanationTaskQueue();

        Data data1 = dataRepository.save(new Data("data/HelloWorldToExplain.png", DataType.IMAGE));

        ExplanationTask task1 = new ExplanationTask();
        task1.setTimeScheduled(LocalDateTime.now(ZoneOffset.UTC));
        task1.getDataList().add(data1);

        task1 = taskRepository.save(task1);

        queue.getTaskQueue().add(task1);

        queue = taskQueueRepository.save(queue);
    }


}


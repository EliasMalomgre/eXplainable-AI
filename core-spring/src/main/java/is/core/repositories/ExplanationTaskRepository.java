package is.core.repositories;

import is.core.domain.entities.ExplanationTask;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExplanationTaskRepository extends MongoRepository<ExplanationTask, String> {

    List<ExplanationTask> findAllByAiBundleId(String aiBundleId, Sort timeScheduled);

}

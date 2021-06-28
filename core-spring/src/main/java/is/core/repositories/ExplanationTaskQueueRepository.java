package is.core.repositories;

import is.core.domain.entities.ExplanationTaskQueue;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExplanationTaskQueueRepository extends MongoRepository<ExplanationTaskQueue, String> {
}

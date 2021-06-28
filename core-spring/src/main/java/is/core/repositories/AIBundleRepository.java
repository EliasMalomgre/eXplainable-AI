package is.core.repositories;

import is.core.domain.entities.AIBundle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AIBundleRepository extends MongoRepository<AIBundle, String> {
}

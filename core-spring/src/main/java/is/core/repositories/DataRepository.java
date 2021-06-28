package is.core.repositories;

import is.core.domain.entities.files.Data;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataRepository extends MongoRepository<Data, String> {
}

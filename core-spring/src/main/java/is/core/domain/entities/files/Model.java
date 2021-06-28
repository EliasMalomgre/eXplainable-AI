package is.core.domain.entities.files;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("models")
public class Model extends PersistedFile {
    public Model(String storagePath) {
        super(storagePath);
    }
}


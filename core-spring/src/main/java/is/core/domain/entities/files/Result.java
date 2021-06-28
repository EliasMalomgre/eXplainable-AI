package is.core.domain.entities.files;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@EqualsAndHashCode
@Document("results")
public class Result extends PersistedFile {
    public Result(String storagePath) {
        super(storagePath);
    }
}

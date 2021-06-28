package is.core.domain.entities.files;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class PersistedFile {
    @Id
    private String id;
    protected String storagePath;

    public PersistedFile(String storagePath) {
        this.storagePath = storagePath;
    }
}

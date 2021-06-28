package is.core.domain.entities.files;

import is.core.domain.enums.DataType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Setter
@Getter
@Document("data")
public class Data extends PersistedFile {
    private DataType dataType;

    public Data(String storagePath, DataType dataType) {
        super(storagePath);
        this.dataType = dataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;
        if (!super.equals(o)) return false;
        Data data = (Data) o;
        return dataType == data.dataType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataType);
    }
}

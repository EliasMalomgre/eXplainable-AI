package is.core.domain.entities;

import is.core.domain.entities.neuralstructure.DeserializedNeuralNet;
import is.core.domain.enums.DataType;
import is.core.domain.entities.files.Data;
import is.core.domain.entities.files.Model;
import is.core.domain.entities.files.PersistedFile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document("aibundles")
public class AIBundle extends PersistedFile {
    private List<Model> models;
    private List<Data> dataList;
    private DeserializedNeuralNet deserializedNeuralNet;
    private List<Integer> inputShape;
    private List<String> classLabels;
    private DataType requiredDataType;

    private String name;
    private String description;

    private LocalDateTime timeUploaded;
    private LocalDateTime timeUpdated;

    public AIBundle() {
        this(null);
    }

    public AIBundle(String storagePath) {
        super(storagePath);
        this.dataList = new ArrayList<>();
        this.models = new ArrayList<>();
        this.inputShape = new ArrayList<>();
        this.classLabels = new ArrayList<>();
    }
}

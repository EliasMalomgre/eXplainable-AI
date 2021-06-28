package is.core.domain.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import is.core.domain.entities.files.Data;
import is.core.domain.entities.neuralstructure.DeserializedNeuralNet;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class AIBundlePageDto {
    String id;
    String name;
    String description;

    String requiredDataType;
    List<Integer> inputShape;
    List<Data> dataList;
    List<String> classLabels;
    DeserializedNeuralNet deserializedNeuralNet;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime timeUploaded;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime lastActivity;
}
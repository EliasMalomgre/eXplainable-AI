package is.core.domain.entities.neuralstructure;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeserializedNeuralNet {

    private List<Layer> layers;

    public DeserializedNeuralNet() {
        this.layers = new ArrayList<>();
    }
}

package is.core.domain.entities.neuralstructure;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Layer {
    private int layerPosition;
    private String name;
    private String type;
    private List<Integer> inputShape;
    private List<Integer> outputShape;
}

package is.core.domain.dtos;

import lombok.Data;

@Data
public class LayerDto {
    int layerPosition;
    String name;
    String className;
    int[] inputShape;
    int[] outputShape;
}

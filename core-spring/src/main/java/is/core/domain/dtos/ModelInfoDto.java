package is.core.domain.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ModelInfoDto {
    int[] inputShape;
    int[] outputShape;
    List<LayerDto> layers;
}

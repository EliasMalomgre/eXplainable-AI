package is.core.domain.entities.taskparameters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImagePlotParameters extends Parameters {
    private int rankedOutputs;
    private boolean normalize;

    public ImagePlotParameters(int rankedOutputs, boolean normalize) {
        super(ParameterType.IMAGEPLOT);
        this.rankedOutputs = rankedOutputs;
        this.normalize = normalize;
    }
}

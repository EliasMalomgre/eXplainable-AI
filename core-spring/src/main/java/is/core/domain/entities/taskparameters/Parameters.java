package is.core.domain.entities.taskparameters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parameters {
    private ParameterType parameterType;

    public Parameters() {
        this.parameterType = ParameterType.GENERIC;
    }

    public Parameters(ParameterType parameterType) {
        this.parameterType = parameterType;
    }
}

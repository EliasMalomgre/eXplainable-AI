package is.core.domain.dtos;

import is.core.domain.entities.ExplanationTask;
import lombok.Value;

import java.util.List;

@Value
public class BundleTasksDto {
    List<ExplanationTask> tasks;
}

package is.core.domain.dtos;

import is.core.domain.enums.DataType;
import is.core.domain.enums.ExplainerType;
import is.core.domain.enums.PlotType;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Value
public class RequestedTaskDto {
    String aiBundleId;
    ExplainerType taskType;
    PlotType plotType;
    DataType dataType;
    List<MultipartFile> data;
    Integer rankedOutputs;
    Boolean normalize;
}

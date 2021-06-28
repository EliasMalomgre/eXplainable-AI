package is.core.domain.dtos;

import is.core.domain.enums.DataType;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Value
public class UploadedModelAndDataDto {
    MultipartFile model;
    List<MultipartFile> data;
    String name;
    String description;
    MultipartFile classLabels;
    DataType dataType;
}

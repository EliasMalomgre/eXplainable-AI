package is.core.domain.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostResultDto {
    String taskId;
    List<MultipartFile> results;
}


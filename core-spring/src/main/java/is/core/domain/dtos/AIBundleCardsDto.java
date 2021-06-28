package is.core.domain.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class AIBundleCardsDto {
    String id;
    String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime timeUploaded;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime lastActivity;
}
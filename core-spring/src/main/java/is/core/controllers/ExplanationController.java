package is.core.controllers;

import is.core.domain.dtos.RequestedTaskDto;
import is.core.services.ExplanationTaskQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/explanation")
public class ExplanationController {
    private final ExplanationTaskQueueService queueService;

    @PostMapping(path = "/request")
    public ResponseEntity<Boolean> requestExplanation(RequestedTaskDto taskDto) {
        log.debug("Received 'requestExplanation' POST");
        try {
            queueService.createTask(taskDto);
            return ResponseEntity.ok().body(true);
        }
        catch (Exception e) {
            log.error("ExplanationTask: Something went wrong starting up the explanation request");
            return ResponseEntity.badRequest().body(false);
        }
    }

}

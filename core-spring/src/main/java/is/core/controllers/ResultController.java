package is.core.controllers;

import is.core.services.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/result")
public class ResultController {

    private final ResultService resultService;

    @GetMapping(
            value = "getResult",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> getResult(@RequestParam String resultId) {
        log.debug("Received 'getResult' for Result:{}", resultId);
        try {
            return ResponseEntity.ok().body(resultService.getResult(resultId));
        }
        catch (Exception e) {
            log.error("Result({}): Something went wrong while getting result!", resultId);
            return ResponseEntity.badRequest().body(null);
        }

    }
}

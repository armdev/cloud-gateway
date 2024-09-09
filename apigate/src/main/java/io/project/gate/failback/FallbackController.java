package io.project.gate.failback;

import io.project.gate.responses.BaseResponse;
import io.project.gate.responses.BaseResult;
import io.project.gate.responses.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author Armen Arzumanyan
 */
@RestController
@Slf4j
public class FallbackController {

    @GetMapping("/failback")
    public BaseResponse failback() {
        log.error("Service is not available.");
        String message = "Service temporary unavailable, try again";
        return BaseResult.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, message);
    }

}

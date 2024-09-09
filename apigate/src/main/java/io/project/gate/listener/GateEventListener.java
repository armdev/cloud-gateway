package io.project.gate.listener;

import io.project.gate.models.RejectedRequest;
import io.project.gate.models.GateRequest;
import io.project.gate.models.GateResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GateEventListener {

    @EventListener
    @Async
    public void resquestListener(GateRequest message) {
        message.setRequestTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        log.info("Request: " + message.getRequestCorrelationId());
    }

    @EventListener
    @Async
    public void responseListener(GateResponse message) {
        message.setResponseTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        log.info("Response: " + message.getRequestCorrelationId());
    }

    @EventListener
    @Async
    public void rejectedRequestsListener(RejectedRequest message) {
        log.info("REJECTED: " + message);
    }

}

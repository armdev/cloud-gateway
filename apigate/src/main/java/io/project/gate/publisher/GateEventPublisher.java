package io.project.gate.publisher;

import io.project.gate.models.RejectedRequest;
import io.project.gate.models.GateRequest;
import io.project.gate.models.GateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GateEventPublisher {

    @Autowired
    private ApplicationEventPublisher publisher;

    public void requestPublisher(GateRequest message) {
        publisher.publishEvent(message);
    }

    public void responsePublisher(GateResponse message) {
        publisher.publishEvent(message);
    }

    public void publishRejectedRequests(RejectedRequest message) {
        publisher.publishEvent(message);
    }
}

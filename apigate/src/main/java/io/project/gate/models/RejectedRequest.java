
package io.project.gate.models;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class RejectedRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String route;

    private String key;

    private Map<String, String> requestHeaders;
    private Map<String, String> responseHeaders;
    private String requestCorrelationId;

    private long responseTime;

    public RejectedRequest(String route, String key, Map<String, String> requestHeaders, Map<String, String> responseHeaders, String requestCorrelationId, long responseTime) {
        this.route = route;
        this.key = key;
        this.requestHeaders = requestHeaders;
        this.responseHeaders = responseHeaders;
        this.responseTime = responseTime;
        this.requestCorrelationId = requestCorrelationId;
    }

}

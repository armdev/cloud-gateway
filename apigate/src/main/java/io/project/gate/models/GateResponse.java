package io.project.gate.models;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author armen
 */
@NoArgsConstructor
@Slf4j
@Data
public class GateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;

    private String requestMethod;

    private String requestUri;

    private String remoteAddress;

    private String requestPath;

    private String requestHeaders;

    private String responseBody;

    private String requestCorrelationId;

    private Map<String, String> headers;

    private long responseTime;

    public GateResponse(String requestId, String requestMethod, String requestUri,
            String remoteAddress, String requestPath, String requestHeaders,
            String responseBody, String requestCorrelationId, Map<String, String> headers) {
        this.requestId = requestId;
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.remoteAddress = remoteAddress;
        this.requestPath = requestPath;
        this.requestHeaders = requestHeaders;
        this.responseBody = responseBody;
        this.requestCorrelationId = requestCorrelationId;
        this.headers = headers;
    }

}

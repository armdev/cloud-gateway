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
@Slf4j
@Data
@NoArgsConstructor
public class GateRequest implements Serializable {

    private static final long serialVersionUID = 5632756659885642216L;

    private String requestId;

    private String requestMethod;

    private String requestUri;

    private String remoteAddress;

    private String requestPath;

    private String requestHeaders;

    private String requestBody;

    private Map<String, String> queryParams;

    private String requestCorrelationId;

    private Map<String, String> headers;
    
    private long requestTime;

    public GateRequest(String requestId, String requestMethod, String requestUri, String remoteAddress,
            String requestPath, String requestHeaders, String requestBody, 
            Map<String, String> queryParams,
            String requestCorrelationId, 
            Map<String, String> headers) {
        this.requestId = requestId;
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.remoteAddress = remoteAddress;
        this.requestPath = requestPath;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.queryParams = queryParams;
        this.requestCorrelationId = requestCorrelationId;
        this.headers = headers;
    }

}

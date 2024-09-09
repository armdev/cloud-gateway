
package io.project.gate.filters;


import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.HttpHeaders;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FilterUtility {

    public static final String CORRELATION_ID = "x-correlation-id";

    public static final String AUTH_HEADER_PREFIX = "Bearer ";

    public String extractAccessToken(HttpHeaders requestHeaders) {
        List<String> authorizationHeaders = requestHeaders.get(HttpHeaders.AUTHORIZATION);
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String authorizationHeader = authorizationHeaders.get(0);
            log.info("ACCESS TOKEN " + authorizationHeader.substring(AUTH_HEADER_PREFIX.length()));
            return authorizationHeader.substring(AUTH_HEADER_PREFIX.length());
        }
        log.error("Did not find Authorization token ");
        return null;
    }

    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID);
            return requestHeaderList.stream().findFirst().get();
        } else {
            return null;
        }
    }

    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

    public String generateCorrelationId() {
        // Generate a UUID
        String uuid = UuidCreator.getTimeOrderedEpoch().toString();

        // Get the current timestamp in milliseconds
        long timestamp = System.currentTimeMillis();

        // Combine UUID and timestamp to create a unique correlation ID
        String correlationId = uuid + "-" + timestamp;

        return correlationId;
    }
}
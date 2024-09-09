/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.project.gate.filters;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
@Slf4j
public class RequestTraceFilter implements GlobalFilter {

    @Autowired
    private FilterUtility filterUtility;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().toString();

        log.info("SECURITY: REQUEST PATH: {}", requestPath);

        // Define public URL patterns for readability
        if (isPublicUrl(requestPath)) {
            log.debug("SECURITY DEBUG: PUBLIC URL: {} ", requestPath);
            return chain.filter(exchange);
        }

        HttpHeaders requestHeaders = request.getHeaders();

        // Validate access token
        if (!extractAccessToken(requestHeaders)) {
            log.error("SECURITY: WARNING: Access Token does not exist, stopping request!");
            return unauthorizedResponse(exchange);
        }
        // Ensure Correlation ID exists
        if (!isCorrelationIdPresent(requestHeaders)) {
            log.debug("SECURITY: CorrelationId Not Present, generate new one");
            String correlationID = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationID);
        }
        log.debug("Easy run");
        return chain.filter(exchange);
    }

// Helper method to check if the request path is public
    private boolean isPublicUrl(String requestPath) {
        return requestPath.contains("/api/v2/profile/fetch")
                || requestPath.contains("/api/v2/profile/register")
                || requestPath.startsWith("/v3/api-docs/swagger-config")
                || requestPath.startsWith("/swagger");
    }

// Helper method to handle unauthorized response
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorMessage = "{\"error\": \"Access Token does not exist\"}";
        DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer)).then(response.setComplete());
    }

    private boolean extractAccessToken(HttpHeaders requestHeaders) {
        return filterUtility.extractAccessToken(requestHeaders) != null;
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        return filterUtility.getCorrelationId(requestHeaders) != null;
    }

    public String generateCorrelationId() {
        // Generate a UUID
        String uuid = UUID.randomUUID().toString();

        // Get the current timestamp in milliseconds
        long timestamp = System.currentTimeMillis();

        // Combine UUID and timestamp to create a unique correlation ID
        String correlationId = uuid + "-" + timestamp;

        return correlationId;
    }

}

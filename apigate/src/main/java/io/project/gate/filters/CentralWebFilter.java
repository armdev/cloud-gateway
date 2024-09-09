package io.project.gate.filters;

import static io.project.gate.filters.FilterUtility.CORRELATION_ID;
import io.project.gate.models.GateRequest;
import io.project.gate.models.GateResponse;
import io.project.gate.publisher.GateEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
@Slf4j
@AllArgsConstructor
public class CentralWebFilter implements WebFilter {

   
    private final FilterUtility filterUtility;    
    
    private final GateEventPublisher eventPublisher;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
      
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        exchange = filterUtility.setCorrelationId(exchange, filterUtility.generateCorrelationId());

        HttpStatusCode statusCode = exchange.getResponse().getStatusCode();

        log.info("STATUS CODE " + statusCode);

        // log the request body
        ServerHttpRequest decoratedRequest = getDecoratedRequest(request);

        // log the response body
        ServerHttpResponseDecorator decoratedResponse = getDecoratedResponse(response, request, dataBufferFactory);

        return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build());
    }

    private ServerHttpResponseDecorator getDecoratedResponse(ServerHttpResponse response, ServerHttpRequest request, DataBufferFactory dataBufferFactory) {
        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {

                if (body instanceof Flux) {

                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map((var dataBuffers) -> {

                        DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);

                        byte[] content = new byte[joinedBuffers.readableByteCount()];
                        joinedBuffers.read(content);

                        String responseBody = new String(content, StandardCharsets.UTF_8);

                        String correlationId = null;
                        if (request.getHeaders().get(CORRELATION_ID) != null) {
                            List<String> requestHeaderList = request.getHeaders().get(CORRELATION_ID);

                            if (requestHeaderList != null && !requestHeaderList.isEmpty()) {

                                correlationId = requestHeaderList.stream().findFirst().get();
                            }

                        }
                        log.info("correlationId " + correlationId);

                        Set<Map.Entry<String, List<String>>> entrySet = request.getHeaders().entrySet();
                        Map<String, String> allHeaders = parseHeaders(entrySet);

                        GateResponse gateResponse = new GateResponse(request.getId(), request.getMethod().toString(),
                                request.getURI().toString(), request.getRemoteAddress() != null ? request.getRemoteAddress().toString() : "none", request.getPath().toString(),
                                response.getHeaders().toString(), responseBody, correlationId, allHeaders);

                        log.info("ResponsePublisher " + gateResponse);
                         eventPublisher.responsePublisher(gateResponse);
                        return dataBufferFactory.wrap(responseBody.getBytes());
                    })
                            .switchIfEmpty(Flux.defer(() -> {

                                log.info("Write to database here");
                                return Flux.just();
                            }))
                    ).onErrorResume(err -> {
                        log.error("error while decorating Response: {}", err.getMessage());
                        return Mono.empty();
                    });

                } else {
                    log.debug("Ignore me");
                }              
                return super.writeWith(body);
            }
        };
    }

    private ServerHttpRequest getDecoratedRequest(ServerHttpRequest request) {
        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                Flux<DataBuffer> originalBody = super.getBody();

                return originalBody.flatMap(dataBuffer -> {
                    try {
                        byte[] requestBodyBytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(requestBodyBytes);
                        // Check if the request body is gzipped and decompress it if needed
                        HttpHeaders requestHeaders = request.getHeaders();

                        String requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);
                        log.debug("RequestBody " + requestBody);

                        String correlationId = request.getHeaders().getFirst(CORRELATION_ID);

                        Map<String, String> allHeaders = parseHeaders(requestHeaders.entrySet());
                        Map<String, String> queryParams = parseQueryParams(request.getQueryParams());

                        GateRequest gateRequest = new GateRequest(request.getId(), request.getMethod().name(),
                                request.getURI().toString(),
                                request.getRemoteAddress() != null ? request.getRemoteAddress().toString() : "none",
                                request.getPath().value(), request.getHeaders().toString(), requestBody,
                                queryParams, correlationId, allHeaders);
                        log.debug("Request requestPublisher " + gateRequest);
                        eventPublisher.requestPublisher(gateRequest);
                        // Recreate the request body with the original data
                        DataBuffer buffer = new DefaultDataBufferFactory().wrap(requestBodyBytes);

                        return Flux.just(buffer);
                    } catch (Exception e) {
                        log.error("Error processing request body: " + e.getMessage());
                        return Flux.empty();
                    }
                }).switchIfEmpty(Mono.fromRunnable(() -> {
                    String correlationId = request.getHeaders().getFirst(CORRELATION_ID);
                    log.debug("Request with no body");
                    Map<String, String> allHeaders = parseHeaders(request.getHeaders().entrySet());
                    Map<String, String> queryParams = parseQueryParams(request.getQueryParams());

                    GateRequest gateRequest = new GateRequest(request.getId(), request.getMethod().name(),
                            request.getURI().toString(),
                            request.getRemoteAddress() != null ? request.getRemoteAddress().toString() : "none",
                            request.getPath().value(), request.getHeaders().toString(), "", queryParams, correlationId, allHeaders);
                    log.debug("Request no body RequestPublisher " + gateRequest);
                  eventPublisher.requestPublisher(gateRequest);
                })).publishOn(Schedulers.boundedElastic());
            }
        };
    }

    public static Map<String, String> parseHeaders(Set<Entry<String, List<String>>> headerEntrySet) {
        Map<String, String> parsedHeaders = new HashMap<>();

        for (Entry<String, List<String>> entry : headerEntrySet) {
            String headerName = entry.getKey();
            List<String> headerValues = entry.getValue();

            // Combine header values into a comma-separated string
            String headerValue = String.join(", ", headerValues);

            // Add the header key-value pair to the parsedHeaders map
            //log.info(headerName + " - " + headerValue );
            parsedHeaders.put(headerName, headerValue);
        }

        return parsedHeaders;
    }

    public static Map<String, String> parseQueryParams(MultiValueMap<String, String> queryParams) {
        Map<String, String> parsedQueryParams = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String paramName = entry.getKey();
            List<String> paramValues = entry.getValue();

            // For simplicity, this example assumes a single value for each query parameter
            if (!paramValues.isEmpty()) {
                String paramValue = paramValues.get(0); // Take the first value
                parsedQueryParams.put(paramName, paramValue);
            }
        }

        return parsedQueryParams;
    }

    

}
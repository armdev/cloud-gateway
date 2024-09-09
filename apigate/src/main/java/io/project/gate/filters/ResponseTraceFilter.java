package io.project.gate.filters;

import static io.project.gate.filters.FilterUtility.CORRELATION_ID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
@AllArgsConstructor
public class ResponseTraceFilter {

    private final FilterUtility filterUtility;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
                /// log.error("statusCode - " + statusCode.toString());
                if (statusCode != null && statusCode.equals(HttpStatus.TOO_MANY_REQUESTS)) {
                    // The response status is 429, print response headers
                    exchange.getResponse().getHeaders().forEach((name, values) -> {
                        values.forEach(value -> {
                            log.error("429 Response Header - " + name + ": " + value);
                        });
                    });
                }

                if (statusCode != null && statusCode.equals(HttpStatus.BAD_REQUEST)) {
                    // The response status is 500, print response headers
                    exchange.getResponse().getHeaders().forEach((name, values) -> {
                        values.forEach(value -> {
                            log.error("500 Response Header - " + name + ": " + value);
                        });
                    });
                }
                String correlationId = filterUtility.getCorrelationId(requestHeaders);
                if (!(exchange.getResponse().getHeaders().containsKey(CORRELATION_ID))) {
                    log.debug("Updated the correlation id to the outbound headers: {}", correlationId);
                    exchange.getResponse().getHeaders().add(CORRELATION_ID, correlationId);
                }

            }));
        };
    }

}

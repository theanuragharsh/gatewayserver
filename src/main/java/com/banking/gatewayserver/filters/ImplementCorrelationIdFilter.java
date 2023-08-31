package com.banking.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class ImplementCorrelationIdFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(ImplementCorrelationIdFilter.class);
    public static final String CORRELATION_ID = "correlation-id";

//    This method generates random CORRELATION_ID for the request and response headers
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Order(1)
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return exchange.getRequest()
                .getHeaders()
                .getOrEmpty(CORRELATION_ID)
                .stream().findFirst()
                .map(id -> {
                    logger.debug("correlation-id found in tracing filter: {}. ", id);
                    return chain.filter(exchange);
                })
                .orElse(Mono.defer(() -> {
                    String correlationId = generateCorrelationId();
                    exchange.mutate() //     changing the original request and adding an extra header for correlation-id
                            .request(exchange.getRequest().mutate()
                                    .header(CORRELATION_ID, correlationId).build()
                            ).build();
                    logger.debug("correlation-id generated in tracing filter: {}.", correlationId);
                    return chain.filter(exchange);
                }));
    }

    @Bean
    public GlobalFilter postGlobalFilter() {

        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID);
                logger.debug("Updated the correlation id to the outbound headers. {}", correlationId);
                exchange.getResponse().getHeaders().add(CORRELATION_ID, correlationId);
            }));
        };
    }

}

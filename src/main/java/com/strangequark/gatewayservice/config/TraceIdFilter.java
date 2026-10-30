package com.strangequark.gatewayservice.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class TraceIdFilter implements WebFilter {
    private final Tracer tracer;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    public TraceIdFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> {
            Span span = tracer.currentSpan();
            String traceId = span == null ? null : span.context().traceId();

            exchange.getResponse().beforeCommit(() -> {
                HttpHeaders headers = exchange.getResponse().getHeaders();
                if(traceId != null)
                    headers.set("X-Trace-ID", traceId);

                String origin = exchange.getRequest().getHeaders().getOrigin();
                if(origin != null && allowedOrigins.contains(origin)) {
                    headers.setAccessControlAllowOrigin(origin);
                    headers.setAccessControlAllowCredentials(true);
                    List<String> exposedHeaders = new ArrayList<>(headers.getAccessControlExposeHeaders());
                    if(!exposedHeaders.contains("X-Trace-ID"))
                        exposedHeaders.add("X-Trace-ID");
                    headers.setAccessControlExposeHeaders(exposedHeaders);
                    if(!headers.getVary().contains(HttpHeaders.ORIGIN))
                        headers.add(HttpHeaders.VARY, HttpHeaders.ORIGIN);
                }
                return Mono.empty();
            });

            return chain.filter(exchange);
        });
    }
}

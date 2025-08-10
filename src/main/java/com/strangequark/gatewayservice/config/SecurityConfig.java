package com.strangequark.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(allowedOrigins);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    // We must remove and re-add the CORS headers since both Gateway and downstream services must handle CORS policies
    @Bean
    public GlobalFilter cleanAndAddCorsHeaders() {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();

            // Remove downstream CORS headers first
            response.getHeaders().remove("Access-Control-Allow-Origin");
            response.getHeaders().remove("Access-Control-Allow-Credentials");
            response.getHeaders().remove("Access-Control-Allow-Headers");
            response.getHeaders().remove("Access-Control-Allow-Methods");
            response.getHeaders().remove("Access-Control-Expose-Headers");

            // Add gateway CORS headers once
            response.getHeaders().add("Access-Control-Allow-Origin", allowedOrigins.get(0));
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
            response.getHeaders().add("Access-Control-Allow-Headers", "*");
            response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        }));
    }
}

package com.strangequark.gatewayservice.filters;

import com.strangequark.gatewayservice.utility.AuthUtility;
import com.strangequark.gatewayservice.utility.CookieUtility;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secretKey;

    private final WebClient webClient;

    public JwtAuthenticationFilter() {
        super(Config.class);
        this.webClient = WebClient.builder().baseUrl("http://localhost:6001").build(); // JWT auth service URL
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String accessToken = headers.getFirst("Authorization");
            String refreshToken = CookieUtility.extractRefreshTokenFromCookies(exchange, "refresh_token");

            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                return unauthorized(exchange, "Missing or invalid access token");
            }

            accessToken = accessToken.substring(7);

            try {
                // Validate access token locally
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey.getBytes())
                        .build()
                        .parseClaimsJws(accessToken)
                        .getBody();

                // If the token is valid and not expired, proceed with the request
                return chain.filter(exchange);

            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                // Token expired, handle refresh token
                if (refreshToken == null || refreshToken.isEmpty()) {
                    return unauthorized(exchange, "Missing refresh token in cookies");
                }

                // Call JWT auth service to validate refresh token and get a new access token
                String newAccessToken = AuthUtility.requestNewAccessToken(refreshToken);

                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("Authorization", "Bearer " + newAccessToken)
                                .build())
                        .build();

                //Add the new access token back to the response headers so it can be sent back for the client to store
                exchange.getResponse().getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

                return chain.filter(mutatedExchange);
            } catch (SignatureException e) {
                return unauthorized(exchange, "Invalid access token signature");
            } catch (Exception e) {
                return unauthorized(exchange, "Invalid access token");
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Configuration properties if needed
    }
}

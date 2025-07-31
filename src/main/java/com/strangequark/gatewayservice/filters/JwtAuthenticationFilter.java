// Integration file: Auth
package com.strangequark.gatewayservice.filters;

import com.strangequark.gatewayservice.utility.AuthUtility;
import com.strangequark.gatewayservice.utility.CookieUtility;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${ACCESS_SECRET_KEY}")
    private String secretKey;

    private final WebClient webClient;
    private AuthUtility authUtility;
    private CookieUtility cookieUtility;

    public JwtAuthenticationFilter(AuthUtility authUtility, CookieUtility cookieUtility) {
        super(Config.class);
        this.authUtility = authUtility;
        this.cookieUtility = cookieUtility;
        this.webClient = WebClient.builder().baseUrl("http://auth-service:6001").build(); // JWT auth service URL
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String accessToken = headers.getFirst("Authorization");
            String refreshToken = cookieUtility.extractRefreshTokenFromCookies(exchange, "refresh_token");

            LOGGER.info("JWT filter to path: " + exchange.getRequest().getPath());

            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                LOGGER.error("Missing or invalid access token");
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
                LOGGER.info("Access token is valid");
                return chain.filter(exchange);

            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                LOGGER.info("Access token is expired");

                // Token expired, handle refresh token
                if (refreshToken == null || refreshToken.isEmpty()) {
                    LOGGER.error("Missing refresh token");
                    return unauthorized(exchange, "Missing refresh token in cookies");
                }

                // Call JWT auth service to validate refresh token and get a new access token
                String newAccessToken = authUtility.requestNewAccessToken(refreshToken);
                LOGGER.info("New access token created");

//                //Uncomment to return new token in cookie
//                // Set the new access token in an HttpOnly cookie
//                exchange.getResponse().addCookie(HttpCookie.builder("access_token", newAccessToken)
//                        .httpOnly(true)
//                        .secure(true)  // Ensure Secure flag is set (for HTTPS)
//                        .sameSite("Strict") // For CSRF protection
//                        .path("/")  // Path for the cookie
//                        .build());

                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("Authorization", "Bearer " + newAccessToken)
                                .build())
                        .build();

                //Add the new access token back to the response headers so it can be sent back for the client to store
                exchange.getResponse().getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

                LOGGER.info("Return new access token");
                return chain.filter(mutatedExchange);
            } catch (SignatureException e) {
                LOGGER.error("Invalid access token signature");
                LOGGER.error(e.getMessage());
                return unauthorized(exchange, "Invalid access token signature");
            } catch (Exception e) {
                LOGGER.error("Invalid access token");
                LOGGER.error(e.getMessage());
                return unauthorized(exchange, "Invalid access token");
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        LOGGER.error("Unauthorized request");
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Configuration properties if needed
    }
}

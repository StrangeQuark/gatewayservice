// Integration file: Auth
package com.strangequark.gatewayservice.filtertests;

import com.strangequark.gatewayservice.filters.JwtAuthenticationFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class JwtAuthenticationFilterTest {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void applyTest() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/some-path").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilter gatewayFilter = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config());

        GatewayFilterChain mockChain = mock(GatewayFilterChain.class);
        gatewayFilter.filter(exchange, mockChain).block();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
}

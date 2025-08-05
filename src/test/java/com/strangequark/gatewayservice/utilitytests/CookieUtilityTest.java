package com.strangequark.gatewayservice.utilitytests;

import com.strangequark.gatewayservice.utility.CookieUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class CookieUtilityTest {

    @Autowired
    CookieUtility cookieUtility;

    @Test
    void extractRefreshTokenFromCookiesTest() {
        MockServerHttpRequest request = MockServerHttpRequest.get("")
                .cookie(new HttpCookie("refresh_token", "my-refresh-token"))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Assertions.assertEquals("my-refresh-token", cookieUtility.extractRefreshTokenFromCookies(exchange, "refresh_token"));
    }
}

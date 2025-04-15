package com.strangequark.gatewayservice.utility;

import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Service
public class CookieUtility {
    public String extractRefreshTokenFromCookies(ServerWebExchange exchange, String cookieName) {
        // Get all cookies from the exchange
        MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();

        // Check if the cookie map contains the specific cookie name
        if (cookies.containsKey(cookieName)) {
            // Retrieve the list of cookies with the specified name
            List<HttpCookie> matchingCookies = cookies.get(cookieName);

            // If the list is not empty, return the value of the first matching cookie
            if (matchingCookies != null && !matchingCookies.isEmpty()) {
                return matchingCookies.get(0).getValue();
            }
        }

        // Return null if the cookie is not found or has no value
        return null;
    }
}

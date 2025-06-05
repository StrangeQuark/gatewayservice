// Integration file: Auth
package com.strangequark.gatewayservice.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUtility.class);

    public String requestNewAccessToken(String refreshToken) {
        LOGGER.info("Requesting new access token");

        //Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", refreshToken);

        // Create the HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = Boolean.parseBoolean(System.getenv("DOCKER_DEPLOYMENT")) ?
                "http://auth-service:6001/access" : "http://localhost:6001/access";

        LOGGER.info("Attempting to make access token request");

        // Make the GET request with the headers
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(
                url,             // The URL of the GET endpoint
                HttpMethod.GET,  // HTTP method
                entity,          // The HttpEntity with headers
                String.class     // The response type
        );

        LOGGER.info("Response status: " + responseEntity.getStatusCode());

        // Get and return the response body
        return responseEntity.getBody();
    }
}

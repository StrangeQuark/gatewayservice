package com.strangequark.gatewayservice.routetests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GatewayRouteTest {

    @Autowired
    private WebTestClient webTestClient;

    // Integration function start: Auth
    @Test
    void testAuthRouteForwarding() {
        webTestClient.get()
                .uri("/api/auth/health")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Integration function end: Auth
    // Integration function start: Email
    @Test
    void testEmailRouteForwarding() {
        webTestClient.get()
                .uri("/api/email/health")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Integration function end: Email
    // Integration function start: File
    @Test
    void testFileRouteForwarding() {
        webTestClient.get()
                .uri("/api/file/health")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Integration function end: File
    // Integration function start: Vault
    @Test
    void testVaultRouteForwarding() {
        webTestClient.get()
                .uri("/api/vault/health")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Integration function end: Vault
    // Integration function start: React
    @Test
    void testReactRouteForwarding() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Integration function end: React
}

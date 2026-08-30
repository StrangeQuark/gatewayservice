package com.strangequark.gatewayservice.routetests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class GatewayRouteTest {
    private static final DisposableServer mockServer = HttpServer.create()
            .port(0)
            .handle((request, response) -> response
                    .header("X-Test-Route", request.uri())
                    .sendString(Mono.just(request.uri())))
            .bindNow();

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void serviceUrls(DynamicPropertyRegistry registry) {
        registry.add("service.jenkins.url", GatewayRouteTest::getMockServerUrl); // Integration line: Jenkins
        registry.add("service.auth.url", GatewayRouteTest::getMockServerUrl); // Integration line: Auth
        registry.add("service.email.url", GatewayRouteTest::getMockServerUrl); // Integration line: Email
        registry.add("service.file.url", GatewayRouteTest::getMockServerUrl); // Integration line: File
        registry.add("service.vault.url", GatewayRouteTest::getMockServerUrl); // Integration line: Vault
        registry.add("service.telemetry.url", GatewayRouteTest::getMockServerUrl); // Integration line: Telemetry
        registry.add("service.react.url", GatewayRouteTest::getMockServerUrl); // Integration line: React
    }

    @AfterAll
    static void shutdownMockServer() {
        mockServer.disposeNow();
    }

    // Integration function start: Jenkins
    @Test
    void testJenkinsRouteForwarding() {
        testRoute("/", "jenkins.test");
    }
    // Integration function end: Jenkins
    // Integration function start: Auth
    @Test
    void testAuthRouteForwarding() {
        testRoute("/api/auth/health", null);
    }
    // Integration function end: Auth
    // Integration function start: Email
    @Test
    void testEmailRouteForwarding() {
        testRoute("/api/email/health", null);
    }
    // Integration function end: Email
    // Integration function start: File
    @Test
    void testFileRouteForwarding() {
        testRoute("/api/file/health", null);
    }
    // Integration function end: File
    // Integration function start: Vault
    @Test
    void testVaultRouteForwarding() {
        testRoute("/api/vault/health", null);
    }
    // Integration function end: Vault
    // Integration function start: Telemetry
    @Test
    void testTelemetryRouteForwarding() {
        testRoute("/api/telemetry/health", null);
    }
    // Integration function end: Telemetry
    // Integration function start: React
    @Test
    void testReactRouteForwarding() {
        testRoute("/", null);
    }
    // Integration function end: React

    private static String getMockServerUrl() {
        return "http://localhost:" + mockServer.port();
    }

    private void testRoute(String path, String host) {
        WebTestClient.RequestHeadersSpec<?> request = webTestClient.get()
                .uri(path);

        if(host != null)
            request.header(HttpHeaders.HOST, host);

        request.exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Powered-By", "Gateway Service")
                .expectHeader().valueEquals("X-Test-Route", path)
                .expectBody(String.class).isEqualTo(path);
    }
}

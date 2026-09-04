package com.strangequark.gatewayservice;

import com.strangequark.gatewayservice.config.HostsConfig;
import com.strangequark.gatewayservice.config.RateLimitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserviceApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayserviceApplication.class);

	@Autowired
    private HostsConfig hostsConfig;
	@Autowired
	private RateLimitConfig rateLimitConfig;
	// Integration function start: Jenkins
	@Value("${service.jenkins.url}")
	private String jenkinsServiceUrl;
	// Integration function end: Jenkins
	// Integration function start: Auth
	@Value("${service.auth.url}")
	private String authServiceUrl;
	// Integration function end: Auth
	// Integration function start: Email
	@Value("${service.email.url}")
	private String emailServiceUrl;
	// Integration function end: Email
	// Integration function start: File
	@Value("${service.file.url}")
	private String fileServiceUrl;
	// Integration function end: File
	// Integration function start: Vault
	@Value("${service.vault.url}")
	private String vaultServiceUrl;
	// Integration function end: Vault
	// Integration function start: Telemetry
	@Value("${service.telemetry.url}")
	private String telemetryServiceUrl;
	// Integration function end: Telemetry
	// Integration function start: React
	@Value("${service.react.url}")
	private String reactServiceUrl;
	// Integration function end: React

	public static void main(String[] args) {
		LOGGER.info("Starting Gateway Service Application...");
		SpringApplication.run(GatewayserviceApplication.class, args);
		LOGGER.info("Gateway Service Application started.");
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// Integration function start: Jenkins
				.route("jenkins_route", r -> r.host(hostsConfig.getHosts().get("jenkins"))
						.and()
						.path("/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri(jenkinsServiceUrl)
				)
				// Integration function end: Jenkins
 				// Integration function start: Auth
				.route(r -> r.path("/api/auth/health")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri(authServiceUrl)
				)
				.route(r -> r.path("/api/auth/**")
						.filters(f -> f
								.requestRateLimiter(config -> config
										.setRateLimiter(rateLimitConfig.authRateLimiter())
										.setKeyResolver(rateLimitConfig.clientIpKeyResolver()))
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri(authServiceUrl)
				)
                // Integration function end: Auth
				// Integration function start: Email
				.route(r -> r.path("/api/email/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri(emailServiceUrl)
				)
                // Integration function end: Email
				// Integration function start: File
				.route(r -> r.path("/api/file/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri(fileServiceUrl)
				)
                // Integration function end: File
				// Integration function start: Vault
				.route(r -> r.path("/api/vault/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri(vaultServiceUrl)
				)
                // Integration function end: Vault
                // Integration function start: Telemetry
                .route(r -> r.path("/api/telemetry/**")
                        .filters(f -> f
                                .addResponseHeader("X-Powered-By", "Gateway Service"))
                        .uri(telemetryServiceUrl)
                )
                // Integration function end: Telemetry
				// Integration function start: React
				.route(r -> r.path("/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri(reactServiceUrl))
                // Integration function end: React
                //Example route
//                .route(r -> r.path("/**")
//                        .filters(f -> f
//                                .addResponseHeader("X-Powered-By", "Gateway Service"))
//                        .uri("http://your-service"))
				.build();
	}
}

package com.strangequark.gatewayservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserviceApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayserviceApplication.class);

	public static void main(String[] args) {
		LOGGER.info("Starting Gateway Service Application...");
		SpringApplication.run(GatewayserviceApplication.class, args);
		LOGGER.info("Gateway Service Application started.");
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
 				// Integration function start: Auth
				.route(r -> r.path("/api/auth/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://auth-service:6001")
				)// Integration function end: Auth

				// Integration function start: Email
				.route(r -> r.path("/api/email/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://email-service:6005")
				)// Integration function end: Email

				// Integration function start: File
				.route(r -> r.path("/api/file/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://file-service:6010")
				)// Integration function end: File

				// Integration function start: Vault
				.route(r -> r.path("/api/vault/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://vault-service:6020")
				)// Integration function end: Vault

				// Integration function start: React
				.route(r -> r.path("/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://react-service:6080"))// Integration function end: React

				.build();
	}
}

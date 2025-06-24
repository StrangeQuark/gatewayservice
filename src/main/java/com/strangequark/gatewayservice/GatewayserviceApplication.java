package com.strangequark.gatewayservice;

import com.strangequark.gatewayservice.filters.JwtAuthenticationFilter;
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

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public GatewayserviceApplication(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		LOGGER.info("GatewayserviceApplication object initialized");
	}

	public static void main(String[] args) {
		LOGGER.info("Starting Gateway Service Application...");
		SpringApplication.run(GatewayserviceApplication.class, args);
		LOGGER.info("Gateway Service Application started.");
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/auth/register", "/auth/authenticate")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://auth-service:6001")
				)
				.route(r -> r.path("/auth/access", "/auth/user/**")
						.filters(f -> f
								.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://auth-service:6001")
				)
				.route(r -> r.path("/email/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://email-service:6005")
				)
				.route(r -> r.path("/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://react-service:6000"))
				.build();
	}
}

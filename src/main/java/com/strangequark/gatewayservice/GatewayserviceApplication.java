package com.strangequark.gatewayservice;

import com.strangequark.gatewayservice.filters.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@SpringBootApplication
public class GatewayserviceApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayserviceApplication.class);

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Value("${cors.allowed-origins}")
	private List<String> allowedOrigins;

	public GatewayserviceApplication(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		LOGGER.info("GatewayserviceApplication object initialized");
	}

	public static void main(String[] args) {
		LOGGER.info("Starting Gateway Service Application...");
		SpringApplication.run(GatewayserviceApplication.class, args);
		LOGGER.info("Gateway Service Application started.");
	}

	//Configure the CORS policy, allow the ReactService through
	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOrigins(allowedOrigins);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return new CorsWebFilter(source);
	}


	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				//
				// Requests not requiring authentication
				//

 				// Integration function start: Auth
				.route(r -> r.path("/api/auth/register", "/api/auth/authenticate")
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

				// Integration function start: Vault
				.route(r -> r.path("/api/vault/health")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://vault-service:6020")
				)// Integration function end: Vault

				//
 				// Requests requiring authentication
 				//

				// Integration function start: Auth
				.route(r -> r.path("/api/auth/access", "/api/auth/user/**")
						.filters(f -> f
								.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://auth-service:6001")
				)
				.route(r -> r.path("/api/vault/**")
						.filters(f -> f
								.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://vault-service:6020")
				)// Integration function end: Auth

				// Integration function start: File
				.route(r -> r.path("/api/file/**")
						.filters(f -> f
								.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))// Integration line: Auth
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://file-service:6010")
				)// Integration function end: File

				//
 				// All other requests go to frontend
 				//

				// Integration function start: React
				.route(r -> r.path("/**")
						.filters(f -> f
								.addResponseHeader("X-Powered-By", "Gateway Service"))
						.uri("http://react-service:6080"))// Integration function end: React

				.build();
	}
}

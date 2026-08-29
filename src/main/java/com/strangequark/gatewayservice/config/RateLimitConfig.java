package com.strangequark.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {
    @Value("${rate-limit.login.max-requests}")
    private int loginMaxRequests;
    @Value("${rate-limit.login.window-minutes}")
    private int loginWindowMinutes;
    @Value("${rate-limit.register.max-requests}")
    private int registerMaxRequests;
    @Value("${rate-limit.register.window-minutes}")
    private int registerWindowMinutes;
    @Value("${rate-limit.password-reset.max-requests}")
    private int passwordResetMaxRequests;
    @Value("${rate-limit.password-reset.window-minutes}")
    private int passwordResetWindowMinutes;

    @Bean
    public KeyResolver clientIpKeyResolver() {
        return exchange -> {
            if(exchange.getRequest().getRemoteAddress() == null)
                return Mono.just("unknown");

            return Mono.just(exchange.getRequest().getRemoteAddress().getHostString());
        };
    }

    @Bean
    @Primary
    public RedisRateLimiter loginRateLimiter() {
        return createRateLimiter(loginMaxRequests, loginWindowMinutes);
    }

    @Bean
    public RedisRateLimiter registerRateLimiter() {
        return createRateLimiter(registerMaxRequests, registerWindowMinutes);
    }

    @Bean
    public RedisRateLimiter passwordResetRateLimiter() {
        return createRateLimiter(passwordResetMaxRequests, passwordResetWindowMinutes);
    }

    private RedisRateLimiter createRateLimiter(int maxRequests, int windowMinutes) {
        int windowSeconds = windowMinutes * 60;

        return new RedisRateLimiter(1, windowSeconds, windowSeconds / maxRequests);
    }
}

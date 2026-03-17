package com.example.ratelimiter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
@Data
public class RateLimitConfig {
    private int capacity;
    private int refillTokens;
    private int refillDurationSeconds;
}
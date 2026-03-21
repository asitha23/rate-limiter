package com.example.ratelimiter;

import com.example.ratelimiter.config.RateLimitConfig;
import com.example.ratelimiter.service.impl.TokenBucketRateLimiterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class TokenBucketRateLimiterServiceTest {

    @Mock
    private RateLimitConfig config;
    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private TokenBucketRateLimiterService service;

    @Test
    void shouldAllowRequestWhenTokensAvailable() {
        // mock Redis behavior
        ValueOperations<String, String> ops = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(ops);
        Mockito.when(config.getCapacity()).thenReturn(10);
        Mockito.when(config.getRefillTokens()).thenReturn(5);
        Mockito.when(config.getRefillDurationSeconds()).thenReturn(60);

        Mockito.when(ops.get(Mockito.anyString())).thenReturn("5");

        boolean allowed = service.allowRequest("user1");

        Assertions.assertTrue(allowed);
    }

    @Test
    void shouldRejectWhenNoTokens() {
        ValueOperations<String, String> ops = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(ops);
        Mockito.when(config.getCapacity()).thenReturn(0);
        Mockito.when(config.getRefillTokens()).thenReturn(1);
        Mockito.when(config.getRefillDurationSeconds()).thenReturn(60);

        Mockito.when(ops.get(Mockito.anyString())).thenReturn("0");
        boolean allowed = service.allowRequest("user1");

        Assertions.assertFalse(allowed);
    }
}
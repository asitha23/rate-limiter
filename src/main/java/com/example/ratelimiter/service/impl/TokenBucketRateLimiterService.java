package com.example.ratelimiter.service.impl;

import com.example.ratelimiter.config.RateLimitConfig;
import com.example.ratelimiter.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBucketRateLimiterService implements RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimitConfig config;

    @Override
    public boolean allowRequest(String key) {

        String tokensKey = "rate:tokens:" + key;
        String tsKey = "rate:ts:" + key;

        long now = System.currentTimeMillis();

        int tokens = getInt(tokensKey, config.getCapacity());
        long lastRefill = getLong(tsKey, now);

        // refill logic
        long elapsedMillis = now - lastRefill;
        long refillCount = (elapsedMillis / 1000) / config.getRefillDurationSeconds()
                * config.getRefillTokens();

        tokens = Math.min(config.getCapacity(), tokens + (int) refillCount);

        if (tokens > 0) {
            tokens--;
            save(tokensKey, tokens, tsKey, now);
            return true;
        }

        save(tokensKey, tokens, tsKey, lastRefill);
        return false;
    }

    private int getInt(String key, int defaultVal) {
        String val = redisTemplate.opsForValue().get(key);
        return val == null ? defaultVal : Integer.parseInt(val);
    }

    private long getLong(String key, long defaultVal) {
        String val = redisTemplate.opsForValue().get(key);
        return val == null ? defaultVal : Long.parseLong(val);
    }

    private void save(String tokensKey, int tokens, String tsKey, long ts) {
        redisTemplate.opsForValue().set(tokensKey, String.valueOf(tokens));
        redisTemplate.opsForValue().set(tsKey, String.valueOf(ts));
    }
}
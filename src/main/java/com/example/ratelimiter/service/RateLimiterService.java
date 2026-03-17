package com.example.ratelimiter.service;

public interface RateLimiterService {
    boolean allowRequest(String key);
}

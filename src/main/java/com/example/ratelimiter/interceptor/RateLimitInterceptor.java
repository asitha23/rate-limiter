package com.example.ratelimiter.interceptor;

import com.example.ratelimiter.service.RateLimiterService;
import com.example.ratelimiter.util.KeyResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final KeyResolver keyResolver;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String key = keyResolver.resolve(request);

        if (!rateLimiterService.allowRequest(key)) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            return false;
        }

        return true;
    }
}
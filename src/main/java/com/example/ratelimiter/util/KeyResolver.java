package com.example.ratelimiter.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class KeyResolver {

    public String resolve(HttpServletRequest request) {
        // Option 1: IP-based
        return request.getRemoteAddr();

        // Option 2 (recommended):
        // return extractUserIdFromJWT(request);
    }
}
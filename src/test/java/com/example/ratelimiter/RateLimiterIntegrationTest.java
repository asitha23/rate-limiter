package com.example.ratelimiter;

import com.example.ratelimiter.service.RateLimiterService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimiterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateLimiterService rateLimiterService;

    @Test
    void shouldAllowRequestsWithinLimit() throws Exception {

        // allow first 3 requests
        Mockito.when(rateLimiterService.allowRequest(Mockito.any()))
                .thenReturn(true);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/hello"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void shouldBlockRequestsWhenLimitExceeded() throws Exception {

        // first 2 allowed, then blocked
        Mockito.when(rateLimiterService.allowRequest(Mockito.any()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isTooManyRequests());
    }

    RequestBuilder get(String url) {
        return MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON);
    }
}
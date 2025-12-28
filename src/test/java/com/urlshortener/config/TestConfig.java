package com.urlshortener.config;

import com.urlshortener.services.interfaces.RateLimitService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.mockito.Mockito;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RateLimitService rateLimitService() {
        RateLimitService mock = Mockito.mock(RateLimitService.class);
        Mockito.when(mock.canRequest(Mockito.anyString())).thenReturn(true);
        Mockito.doNothing().when(mock).recordRequest(Mockito.anyString());
        return mock;
    }
}

package com.urlshortener.services.interfaces;

public interface RateLimitService {
    boolean canRequest(String ip);
    void recordRequest(String ip);
}
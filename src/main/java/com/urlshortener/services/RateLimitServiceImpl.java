package com.urlshortener.services;

import com.urlshortener.entities.RateLimit;
import com.urlshortener.repositories.RateLimitRepository;
import com.urlshortener.services.interfaces.RateLimitService;
import org.springframework.stereotype.Service;

@Service
public class RateLimitServiceImpl implements RateLimitService {
    private final RateLimitRepository repository;

    public RateLimitServiceImpl(RateLimitRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean canRequest(String ip) {
        return repository.countByIp(ip) < 5;
    }

    @Override
    public void recordRequest(String ip) {
        repository.save(new RateLimit(ip));
    }
}
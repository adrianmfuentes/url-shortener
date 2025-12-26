package com.urlshortener.repositories;

import com.urlshortener.entities.RateLimit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RateLimitRepository extends MongoRepository<RateLimit, String> {
    long countByIp(String ip);
}
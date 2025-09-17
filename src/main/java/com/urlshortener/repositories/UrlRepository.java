package com.urlshortener.repositories;

import com.urlshortener.entities.Url;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UrlRepository extends MongoRepository<Url, Long> {
    Optional<Url> findByShortUrl(String shortCode);
}
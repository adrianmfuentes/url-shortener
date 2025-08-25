package com.urlshortener.services;

import com.urlshortener.dtos.UrlDto;
import com.urlshortener.entities.Url;
import com.urlshortener.exceptions.ResourceNotFoundException;
import com.urlshortener.repositories.UrlRepository;
import com.urlshortener.services.interfaces.UrlService;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;

    // Constructor con inyección de dependencias
    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public UrlDto shortenUrl(String longUrl) {
        String shortCode = generateUniqueShortCode();
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl("http://short.url/" + shortCode);
        urlRepository.save(url);
        return convertToDto(url);
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        try {
            Url url = urlRepository.findByShortUrl(shortUrl)
                    .orElseThrow(() -> new ResourceNotFoundException("URL not found"));
            return url.getLongUrl();
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private UrlDto convertToDto(Url url) {
        UrlDto dto = new UrlDto();
        dto.setLongUrl(url.getLongUrl());
        dto.setShortUrl(url.getShortUrl());
        return dto;
    }

    private String generateUniqueShortCode() {
        String shortUrl = "http://short.url/";
        do {
            String shortCode = Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 6);
            shortUrl = shortUrl + shortCode;
            if (urlRepository.findByShortUrl(shortUrl).isEmpty()) return shortCode;
        } while (true);
    }
}
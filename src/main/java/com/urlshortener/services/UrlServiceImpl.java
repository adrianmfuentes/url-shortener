package com.urlshortener.services;

import com.urlshortener.dtos.UrlDto;
import com.urlshortener.entities.Url;
import com.urlshortener.repositories.UrlRepository;
import com.urlshortener.services.interfaces.UrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    private UrlDto convertToDto(Url url) {
        UrlDto dto = new UrlDto();
        dto.setLongUrl(url.getLongUrl());
        dto.setShortUrl(url.getShortUrl());
        return dto;
    }


    @Override
    public UrlDto shortenUrl(String longUrl) {
        try {
            String shortCode = generateUniqueShortCode();
            Url url = new Url();
            url.setShortCode(shortCode);
            url.setLongUrl(longUrl);
            url.setShortUrl(baseUrl + "/" + shortCode);

            Url savedUrl = urlRepository.save(url);

            return convertToDto(savedUrl);
        } catch (Exception e) {
            System.err.println("✗ Error guardando URL: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .map(Url::getLongUrl)
                .orElse(null);
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 6);
        } while (urlRepository.findByShortCode(shortCode).isPresent());
        return shortCode;
    }
}
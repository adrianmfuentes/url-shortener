package com.urlshortener.services.interfaces;

import com.urlshortener.dtos.UrlDto;

public interface UrlService {
    UrlDto shortenUrl(String longUrl);
    String getOriginalUrl(String shortUrl);
}
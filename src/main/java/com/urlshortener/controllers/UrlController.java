package com.urlshortener.controllers;

import com.urlshortener.services.interfaces.UrlService;
import com.urlshortener.validators.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UrlController {

    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlController(UrlService urlService, UrlValidator urlValidator) {
        this.urlService = urlService;
        this.urlValidator = urlValidator;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/shorten")
    public String shortenUrl(@RequestParam("longUrl") String longUrl, Model model) {
        urlValidator.validateUrl(longUrl);
        String shortUrl = urlService.shortenUrl(longUrl).getShortUrl();
        urlValidator.validateUrl(shortUrl);
        model.addAttribute("shortUrl", shortUrl);
        return "index";
    }

    @GetMapping("/{shortCode}")
    public String redirectToOriginalUrl(@PathVariable("shortCode") String shortCode) {
        String shortUrl = baseUrl + shortCode;
        urlValidator.validateUrl(shortUrl);
        String originalUrl = urlService.getOriginalUrl(shortUrl);
        return "redirect:" + originalUrl;
    }
}

package com.urlshortener.controllers;

import com.urlshortener.services.interfaces.RateLimitService;
import com.urlshortener.services.interfaces.UrlService;
import com.urlshortener.validators.UrlValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@Controller
public class UrlController {

    private final UrlService urlService;
    private final UrlValidator urlValidator;
    private final RateLimitService rateLimitService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UrlController(UrlService urlService, UrlValidator urlValidator,  RateLimitService rateLimitService) {
        this.urlService = urlService;
        this.urlValidator = urlValidator;
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/api/url/shorten")
    public String shortenUrl(@RequestParam("longUrl") String longUrl, HttpServletRequest request, Model model) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();

        if (!rateLimitService.canRequest(ip)) {
            model.addAttribute("error", "Límite diario alcanzado (5 URLs).");
            return "index";
        }

        urlValidator.validateUrl(longUrl);
        String shortUrl = urlService.shortenUrl(longUrl).getShortUrl();
        rateLimitService.recordRequest(ip);

        model.addAttribute("shortUrl", shortUrl);
        return "index";
    }

    @GetMapping(value = "/{shortCode:^(?!api|privacidad|terminos|error)[a-zA-Z0-9]+$}", produces = "text/html")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        if (originalUrl != null && !originalUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/privacidad")
    public String privacidad() {
        return "privacidad";
    }

    @GetMapping("/terminos")
    public String terminos() {
        return "terminos";
    }

}

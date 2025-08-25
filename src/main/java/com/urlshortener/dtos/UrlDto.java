package com.urlshortener.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UrlDto {
    @NotBlank(message = "La URL no puede estar vacía")
    @Pattern(
            regexp = "^(https?:\\/\\/)?([\\w\\-]+(\\.[\\w\\-]+)*\\.\\w{2,})(:\\d+)?(\\/[\\w\\-\\.\\/?\\#@!\\$&'\\(\\)\\*\\+,;=]*)?$",
            message = "La URL debe ser válida"
    )
    private String longUrl;

    private String shortUrl;

    // Constructor
    public UrlDto() {}

    public UrlDto(String longUrl, String shortUrl) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
    }

    // Getters y setters
    public String getLongUrl() {
        return longUrl;
    }
    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
    public String getShortUrl() {
        return shortUrl;
    }
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
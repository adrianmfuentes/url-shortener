package com.urlshortener.components;

import com.urlshortener.validators.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Unit Tests - UrlValidator")
class UrlValidatorTest {

    private UrlValidator urlValidator;

    @BeforeEach
    void setUp() {
        urlValidator = new UrlValidator();
    }

    @Test
    @DisplayName("Debe validar URL correctamente con protocolo HTTP")
    void shouldValidateHttpUrlSuccessfully() {
        String validUrl = "http://www.example.com";
        assertDoesNotThrow(() -> urlValidator.validateUrl(validUrl));
    }

    @Test
    @DisplayName("Debe validar URL correctamente con protocolo HTTPS")
    void shouldValidateHttpsUrlSuccessfully() {
        String validUrl = "https://www.example.com";
        assertDoesNotThrow(() -> urlValidator.validateUrl(validUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://www.google.com",
            "http://example.com/path/to/resource",
            "https://sub.domain.example.com",
            "http://example.com:8080",
            "https://example.com/path?query=value&other=123"
    })
    @DisplayName("Debe validar URLs válidas correctamente")
    void shouldValidateValidUrls(String validUrl) {
        assertDoesNotThrow(() -> urlValidator.validateUrl(validUrl));
    }

    @Test
    @DisplayName("Debe rechazar URL nula")
    void shouldRejectNullUrl() {
        assertThatThrownBy(() -> urlValidator.validateUrl(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede estar vacía");
    }

    @Test
    @DisplayName("Debe rechazar URL vacía")
    void shouldRejectEmptyUrl() {
        assertThatThrownBy(() -> urlValidator.validateUrl(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede estar vacía");
    }

    @Test
    @DisplayName("Debe rechazar URL con solo espacios")
    void shouldRejectBlankUrl() {
        assertThatThrownBy(() -> urlValidator.validateUrl("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede estar vacía");
    }

    @Test
    @DisplayName("Debe rechazar URL sin protocolo HTTP/HTTPS")
    void shouldRejectUrlWithoutHttpProtocol() {
        assertThatThrownBy(() -> urlValidator.validateUrl("ftp://example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP o HTTPS");
    }

    @Test
    @DisplayName("Debe rechazar URL con formato inválido")
    void shouldRejectMalformedUrl() {
        assertThatThrownBy(() -> urlValidator.validateUrl("not-a-valid-url"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Debe rechazar URL que excede la longitud máxima")
    void shouldRejectUrlExceedingMaxLength() {
        String longUrl = "http://example.com/" + "a".repeat(2100);
        assertThatThrownBy(() -> urlValidator.validateUrl(longUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("excede la longitud máxima");
    }

    @Test
    @DisplayName("Debe rechazar URL sin host")
    void shouldRejectUrlWithoutHost() {
        assertThatThrownBy(() -> urlValidator.validateUrl("http://"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "javascript:alert('xss')",
            "data:text/html,<script>alert('xss')</script>",
            "file:///etc/passwd"
    })
    @DisplayName("Debe rechazar URLs con protocolos peligrosos")
    void shouldRejectDangerousProtocols(String dangerousUrl) {
        assertThatThrownBy(() -> urlValidator.validateUrl(dangerousUrl))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Debe validar URL con puerto específico")
    void shouldValidateUrlWithPort() {
        String urlWithPort = "http://example.com:3000/path";
        assertDoesNotThrow(() -> urlValidator.validateUrl(urlWithPort));
    }

    @Test
    @DisplayName("Debe validar URL con parámetros de consulta complejos")
    void shouldValidateUrlWithComplexQueryParams() {
        String complexUrl = "https://example.com/search?q=test&filter=active&sort=desc&page=1";
        assertDoesNotThrow(() -> urlValidator.validateUrl(complexUrl));
    }

    @Test
    @DisplayName("Debe validar URL con subdominios múltiples")
    void shouldValidateUrlWithMultipleSubdomains() {
        String subdomainUrl = "https://api.v2.staging.example.com";
        assertDoesNotThrow(() -> urlValidator.validateUrl(subdomainUrl));
    }
}


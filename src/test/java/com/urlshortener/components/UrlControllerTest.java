package com.urlshortener.components;

import com.urlshortener.controllers.UrlController;
import com.urlshortener.dtos.UrlDto;
import com.urlshortener.services.interfaces.RateLimitService;
import com.urlshortener.services.interfaces.UrlService;
import com.urlshortener.validators.UrlValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - UrlController")
class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private UrlController urlController;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String TEST_IP = "192.168.1.1";
    private static final String TEST_LONG_URL = "https://www.example.com";
    private static final String TEST_SHORT_CODE = "abc123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlController, "baseUrl", BASE_URL);
    }

    @Test
    @DisplayName("Debe retornar página principal")
    void shouldReturnHomePage() {
        // Act
        String result = urlController.home();

        // Assert
        assertThat(result).isEqualTo("index");
    }

    @Test
    @DisplayName("Debe acortar URL correctamente cuando no se alcanza el límite")
    void shouldShortenUrlWhenRateLimitNotReached() {
        // Arrange
        UrlDto urlDto = new UrlDto();
        urlDto.setLongUrl(TEST_LONG_URL);
        urlDto.setShortUrl(BASE_URL + "/" + TEST_SHORT_CODE);

        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(TEST_IP);
        when(rateLimitService.canRequest(TEST_IP)).thenReturn(true);
        when(urlService.shortenUrl(TEST_LONG_URL)).thenReturn(urlDto);
        doNothing().when(urlValidator).validateUrl(TEST_LONG_URL);
        doNothing().when(rateLimitService).recordRequest(TEST_IP);

        // Act
        String result = urlController.shortenUrl(TEST_LONG_URL, request, model);

        // Assert
        assertThat(result).isEqualTo("index");
        verify(urlValidator, times(1)).validateUrl(TEST_LONG_URL);
        verify(urlService, times(1)).shortenUrl(TEST_LONG_URL);
        verify(rateLimitService, times(1)).recordRequest(TEST_IP);
        verify(model, times(1)).addAttribute("shortUrl", urlDto.getShortUrl());
    }

    @Test
    @DisplayName("Debe rechazar cuando se alcanza el límite diario")
    void shouldRejectWhenRateLimitReached() {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(TEST_IP);
        when(rateLimitService.canRequest(TEST_IP)).thenReturn(false);

        // Act
        String result = urlController.shortenUrl(TEST_LONG_URL, request, model);

        // Assert
        assertThat(result).isEqualTo("index");
        verify(model, times(1)).addAttribute("error", "Límite diario alcanzado (5 URLs).");
        verify(urlValidator, never()).validateUrl(anyString());
        verify(urlService, never()).shortenUrl(anyString());
        verify(rateLimitService, never()).recordRequest(anyString());
    }

    @Test
    @DisplayName("Debe usar X-Forwarded-For cuando está disponible")
    void shouldUseXForwardedForWhenAvailable() {
        // Arrange
        String forwardedIp = "10.0.0.1";
        UrlDto urlDto = new UrlDto();
        urlDto.setShortUrl(BASE_URL + "/" + TEST_SHORT_CODE);

        when(request.getHeader("X-Forwarded-For")).thenReturn(forwardedIp);
        when(rateLimitService.canRequest(forwardedIp)).thenReturn(true);
        when(urlService.shortenUrl(TEST_LONG_URL)).thenReturn(urlDto);
        doNothing().when(urlValidator).validateUrl(TEST_LONG_URL);

        // Act
        urlController.shortenUrl(TEST_LONG_URL, request, model);

        // Assert
        verify(rateLimitService, times(1)).canRequest(forwardedIp);
        verify(rateLimitService, times(1)).recordRequest(forwardedIp);
    }

    @Test
    @DisplayName("Debe redirigir a URL original con código corto válido")
    void shouldRedirectToOriginalUrlWithValidShortCode() {
        // Arrange
        when(urlService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn(TEST_LONG_URL);

        // Act
        ResponseEntity<Void> response = urlController.redirectToOriginalUrl(TEST_SHORT_CODE);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();
        assertThat(location.toString()).isEqualTo(TEST_LONG_URL);
        verify(urlService, times(1)).getOriginalUrl(TEST_SHORT_CODE);
    }

    @Test
    @DisplayName("Debe retornar 404 cuando código corto no existe")
    void shouldReturn404WhenShortCodeNotFound() {
        // Arrange
        when(urlService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn(null);

        // Act
        ResponseEntity<Void> response = urlController.redirectToOriginalUrl(TEST_SHORT_CODE);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(urlService, times(1)).getOriginalUrl(TEST_SHORT_CODE);
    }

    @Test
    @DisplayName("Debe retornar 404 cuando URL original es vacía")
    void shouldReturn404WhenOriginalUrlIsEmpty() {
        // Arrange
        when(urlService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn("");

        // Act
        ResponseEntity<Void> response = urlController.redirectToOriginalUrl(TEST_SHORT_CODE);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Debe manejar errores de validación")
    void shouldHandleValidationErrors() {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(TEST_IP);
        when(rateLimitService.canRequest(TEST_IP)).thenReturn(true);
        doThrow(new IllegalArgumentException("URL inválida"))
                .when(urlValidator).validateUrl(TEST_LONG_URL);

        // Act & Assert
        try {
            urlController.shortenUrl(TEST_LONG_URL, request, model);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("URL inválida");
        }

        verify(urlService, never()).shortenUrl(anyString());
    }

    @Test
    @DisplayName("Debe usar IP remota cuando X-Forwarded-For es null")
    void shouldUseRemoteAddrWhenXForwardedForIsNull() {
        // Arrange
        UrlDto urlDto = new UrlDto();
        urlDto.setShortUrl(BASE_URL + "/" + TEST_SHORT_CODE);

        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(TEST_IP);
        when(rateLimitService.canRequest(TEST_IP)).thenReturn(true);
        when(urlService.shortenUrl(TEST_LONG_URL)).thenReturn(urlDto);
        doNothing().when(urlValidator).validateUrl(TEST_LONG_URL);

        // Act
        urlController.shortenUrl(TEST_LONG_URL, request, model);

        // Assert
        verify(request, times(1)).getRemoteAddr();
        verify(rateLimitService, times(1)).canRequest(TEST_IP);
    }
}


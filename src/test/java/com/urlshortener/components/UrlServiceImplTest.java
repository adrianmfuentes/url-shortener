package com.urlshortener.components;

import com.urlshortener.dtos.UrlDto;
import com.urlshortener.entities.Url;
import com.urlshortener.repositories.UrlRepository;
import com.urlshortener.services.UrlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - UrlServiceImpl")
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String TEST_LONG_URL = "https://www.example.com/very/long/path";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", BASE_URL);
    }

    @Test
    @DisplayName("Debe acortar URL correctamente")
    void shouldShortenUrlSuccessfully() {
        // Arrange
        String shortCode = "abc123";
        Url savedUrl = new Url();
        savedUrl.setId("1");
        savedUrl.setShortCode(shortCode);
        savedUrl.setLongUrl(TEST_LONG_URL);
        savedUrl.setShortUrl(BASE_URL + "/" + shortCode);

        when(urlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        // Act
        UrlDto result = urlService.shortenUrl(TEST_LONG_URL);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLongUrl()).isEqualTo(TEST_LONG_URL);
        assertThat(result.getShortUrl()).startsWith(BASE_URL);

        verify(urlRepository, atLeastOnce()).findByShortCode(anyString());
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    @DisplayName("Debe generar código corto único")
    void shouldGenerateUniqueShortCode() {
        // Arrange
        Url savedUrl = new Url();
        savedUrl.setShortCode("unique1");
        savedUrl.setLongUrl(TEST_LONG_URL);
        savedUrl.setShortUrl(BASE_URL + "/unique1");

        when(urlRepository.findByShortCode(anyString()))
                .thenReturn(Optional.of(new Url())) // Primera vez colisión
                .thenReturn(Optional.empty()); // Segunda vez único
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        // Act
        UrlDto result = urlService.shortenUrl(TEST_LONG_URL);

        // Assert
        assertThat(result).isNotNull();
        verify(urlRepository, atLeast(2)).findByShortCode(anyString());
    }

    @Test
    @DisplayName("Debe obtener URL original por código corto")
    void shouldGetOriginalUrlByShortCode() {
        // Arrange
        String shortCode = "abc123";
        Url url = new Url();
        url.setShortCode(shortCode);
        url.setLongUrl(TEST_LONG_URL);

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(url));

        // Act
        String result = urlService.getOriginalUrl(shortCode);

        // Assert
        assertThat(result).isEqualTo(TEST_LONG_URL);
        verify(urlRepository, times(1)).findByShortCode(shortCode);
    }

    @Test
    @DisplayName("Debe retornar null cuando no encuentra código corto")
    void shouldReturnNullWhenShortCodeNotFound() {
        // Arrange
        String shortCode = "notfound";
        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // Act
        String result = urlService.getOriginalUrl(shortCode);

        // Assert
        assertThat(result).isNull();
        verify(urlRepository, times(1)).findByShortCode(shortCode);
    }

    @Test
    @DisplayName("Debe manejar errores al guardar URL")
    void shouldHandleErrorWhenSavingUrl() {
        // Arrange
        when(urlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> urlService.shortenUrl(TEST_LONG_URL))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }

    @Test
    @DisplayName("Debe crear DTO correctamente desde entidad")
    void shouldConvertUrlToDtoCorrectly() {
        // Arrange
        Url savedUrl = new Url();
        savedUrl.setShortCode("test123");
        savedUrl.setLongUrl(TEST_LONG_URL);
        savedUrl.setShortUrl(BASE_URL + "/test123");

        when(urlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        // Act
        UrlDto result = urlService.shortenUrl(TEST_LONG_URL);

        // Assert
        assertThat(result.getLongUrl()).isEqualTo(savedUrl.getLongUrl());
        assertThat(result.getShortUrl()).isEqualTo(savedUrl.getShortUrl());
    }

    @Test
    @DisplayName("Debe generar código corto de 6 caracteres")
    void shouldGenerateShortCodeWithCorrectLength() {
        // Arrange
        Url savedUrl = new Url();
        savedUrl.setShortCode("abc123");
        savedUrl.setLongUrl(TEST_LONG_URL);
        savedUrl.setShortUrl(BASE_URL + "/abc123");

        when(urlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url arg = invocation.getArgument(0);
            assertThat(arg.getShortCode()).hasSize(6);
            return savedUrl;
        });

        // Act
        urlService.shortenUrl(TEST_LONG_URL);

        // Assert
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    @DisplayName("Debe construir shortUrl correctamente")
    void shouldBuildShortUrlCorrectly() {
        // Arrange
        String shortCode = "xyz789";
        Url savedUrl = new Url();
        savedUrl.setShortCode(shortCode);
        savedUrl.setLongUrl(TEST_LONG_URL);
        savedUrl.setShortUrl(BASE_URL + "/" + shortCode);

        when(urlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        // Act
        UrlDto result = urlService.shortenUrl(TEST_LONG_URL);

        // Assert
        assertThat(result.getShortUrl()).isEqualTo(BASE_URL + "/" + shortCode);
    }
}


package com.urlshortener.components;

import com.urlshortener.entities.RateLimit;
import com.urlshortener.repositories.RateLimitRepository;
import com.urlshortener.services.RateLimitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - RateLimitServiceImpl")
class RateLimitServiceImplTest {

    @Mock
    private RateLimitRepository rateLimitRepository;

    @InjectMocks
    private RateLimitServiceImpl rateLimitService;

    private static final String TEST_IP = "192.168.1.1";

    @BeforeEach
    void setUp() {
        // Setup común si es necesario
    }

    @Test
    @DisplayName("Debe permitir solicitud cuando el conteo es menor a 5")
    void shouldAllowRequestWhenCountIsLessThanFive() {
        // Arrange
        when(rateLimitRepository.countByIp(TEST_IP)).thenReturn(3L);

        // Act
        boolean canRequest = rateLimitService.canRequest(TEST_IP);

        // Assert
        assertThat(canRequest).isTrue();
        verify(rateLimitRepository, times(1)).countByIp(TEST_IP);
    }

    @Test
    @DisplayName("Debe permitir solicitud cuando el conteo es 0")
    void shouldAllowRequestWhenCountIsZero() {
        // Arrange
        when(rateLimitRepository.countByIp(TEST_IP)).thenReturn(0L);

        // Act
        boolean canRequest = rateLimitService.canRequest(TEST_IP);

        // Assert
        assertThat(canRequest).isTrue();
        verify(rateLimitRepository, times(1)).countByIp(TEST_IP);
    }

    @Test
    @DisplayName("Debe rechazar solicitud cuando el conteo es 5")
    void shouldRejectRequestWhenCountIsFive() {
        // Arrange
        when(rateLimitRepository.countByIp(TEST_IP)).thenReturn(5L);

        // Act
        boolean canRequest = rateLimitService.canRequest(TEST_IP);

        // Assert
        assertThat(canRequest).isFalse();
        verify(rateLimitRepository, times(1)).countByIp(TEST_IP);
    }

    @Test
    @DisplayName("Debe rechazar solicitud cuando el conteo excede 5")
    void shouldRejectRequestWhenCountExceedsFive() {
        // Arrange
        when(rateLimitRepository.countByIp(TEST_IP)).thenReturn(10L);

        // Act
        boolean canRequest = rateLimitService.canRequest(TEST_IP);

        // Assert
        assertThat(canRequest).isFalse();
        verify(rateLimitRepository, times(1)).countByIp(TEST_IP);
    }

    @Test
    @DisplayName("Debe permitir solicitud cuando el conteo es 4 (límite justo)")
    void shouldAllowRequestWhenCountIsFour() {
        // Arrange
        when(rateLimitRepository.countByIp(TEST_IP)).thenReturn(4L);

        // Act
        boolean canRequest = rateLimitService.canRequest(TEST_IP);

        // Assert
        assertThat(canRequest).isTrue();
        verify(rateLimitRepository, times(1)).countByIp(TEST_IP);
    }

    @Test
    @DisplayName("Debe registrar solicitud correctamente")
    void shouldRecordRequestSuccessfully() {
        // Arrange
        RateLimit rateLimit = new RateLimit(TEST_IP);
        when(rateLimitRepository.save(any(RateLimit.class))).thenReturn(rateLimit);

        // Act
        rateLimitService.recordRequest(TEST_IP);

        // Assert
        verify(rateLimitRepository, times(1)).save(any(RateLimit.class));
    }

    @Test
    @DisplayName("Debe crear RateLimit con IP correcta al registrar")
    void shouldCreateRateLimitWithCorrectIp() {
        // Arrange
        when(rateLimitRepository.save(any(RateLimit.class))).thenAnswer(invocation -> {
            RateLimit arg = invocation.getArgument(0);
            assertThat(arg.getIp()).isEqualTo(TEST_IP);
            return arg;
        });

        // Act
        rateLimitService.recordRequest(TEST_IP);

        // Assert
        verify(rateLimitRepository, times(1)).save(any(RateLimit.class));
    }

    @Test
    @DisplayName("Debe manejar múltiples IPs diferentes")
    void shouldHandleMultipleDifferentIps() {
        // Arrange
        String ip1 = "192.168.1.1";
        String ip2 = "192.168.1.2";
        String ip3 = "10.0.0.1";

        when(rateLimitRepository.countByIp(ip1)).thenReturn(2L);
        when(rateLimitRepository.countByIp(ip2)).thenReturn(5L);
        when(rateLimitRepository.countByIp(ip3)).thenReturn(0L);

        // Act & Assert
        assertThat(rateLimitService.canRequest(ip1)).isTrue();
        assertThat(rateLimitService.canRequest(ip2)).isFalse();
        assertThat(rateLimitService.canRequest(ip3)).isTrue();

        verify(rateLimitRepository, times(1)).countByIp(ip1);
        verify(rateLimitRepository, times(1)).countByIp(ip2);
        verify(rateLimitRepository, times(1)).countByIp(ip3);
    }

    @Test
    @DisplayName("Debe permitir solicitud con IP nula (caso edge)")
    void shouldHandleNullIp() {
        // Arrange
        when(rateLimitRepository.countByIp(null)).thenReturn(0L);

        // Act
        boolean canRequest = rateLimitService.canRequest(null);

        // Assert
        assertThat(canRequest).isTrue();
        verify(rateLimitRepository, times(1)).countByIp(null);
    }

    @Test
    @DisplayName("Debe propagar errores del repositorio")
    void shouldPropagateRepositoryErrors() {
        // Arrange
        when(rateLimitRepository.countByIp(anyString()))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            rateLimitService.canRequest(TEST_IP);
        });
    }
}


package com.urlshortener.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "spring.autoconfigure.exclude=de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration"
        })
@ActiveProfiles("test")
class UrlIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateUrlAndCheckHtmlResponse() {
        // 1. Datos de formulario
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("longUrl", "https://www.google.com");

        // 2. POST a la ruta correcta
        ResponseEntity<String> response = restTemplate.postForEntity("/api/url/shorten", map, String.class);

        // 3. Verificaciones
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verificamos que el HTML contiene la base del link acortado
        assertThat(response.getBody()).contains("http://localhost:8080/");

        // Verificar que el input del formulario sigue ahí
        assertThat(response.getBody()).contains("name=\"longUrl\"");
    }
}
package com.urlshortener.validators;

import org.springframework.stereotype.Component;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

@Component
public class UrlValidator {

    private static final int MAX_URL_LENGTH = 2048;

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
    );

    public void validateUrl(String url) {
        // Validación básica
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL no puede estar vacía");
        }

        // Validación de longitud
        if (url.length() > MAX_URL_LENGTH) {
            throw new IllegalArgumentException("La URL excede la longitud máxima permitida de " + MAX_URL_LENGTH + " caracteres");
        }

        try {
            // Validación de formato usando java.net.URL
            URL urlObj = new URL(url);
            String protocol = urlObj.getProtocol();

            // Validación de protocolo
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new IllegalArgumentException("La URL debe usar protocolo HTTP o HTTPS");
            }

            // Validación de patrón
            if (!URL_PATTERN.matcher(url).matches()) {
                throw new IllegalArgumentException("La URL contiene caracteres no permitidos");
            }

            // Validación del host
            if (urlObj.getHost().isEmpty()) {
                throw new IllegalArgumentException("La URL debe contener un host válido");
            }

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("El formato de la URL no es válido");
        }
    }
}
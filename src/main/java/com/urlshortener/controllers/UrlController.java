package com.urlshortener.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UrlController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/shorten")
    public String shortenUrl(@RequestParam("longUrl") String longUrl, Model model) {
        // Procesa la URL larga y genera una URL corta (lógica simplificada)
        model.addAttribute("shortUrl", "http://localhost:8080/abc123");
        return "index";
    }

    @GetMapping("/{shortCode")
    public String redirectToOriginalUrl(@RequestParam("shortCode") String shortCode) {
        // Redirecciona a la URL original. Aquí irá la lógica para buscar la URL original
        return "redirect:URL_ORIGINAL";
    }

    @GetMapping("/urls")
    public String listUrls(Model model) {
        // Lista todas las URLs acortadas (Para administración)
        return "urls";
    }
}

package com.urlshortener.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) != null) {
            return switch (code) {
                case 404 -> {
                    redirectAttributes.addFlashAttribute("error", "error.notFound");
                    yield "redirect:/home";
                }
                case 403 -> {
                    redirectAttributes.addFlashAttribute("error", "error.accessDenied");
                    yield "redirect:/home";
                }
                default -> {
                    redirectAttributes.addFlashAttribute("error", "error.unknown");
                    yield "redirect:/home";
                }
            };
        }
        return "redirect:/home";
    }
}
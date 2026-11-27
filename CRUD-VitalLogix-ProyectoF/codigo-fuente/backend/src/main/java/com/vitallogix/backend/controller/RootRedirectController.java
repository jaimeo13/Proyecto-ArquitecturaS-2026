package com.vitallogix.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de redirección de la raíz del backend hacia la documentación Swagger / API.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Controller
public class RootRedirectController {
    /**
     * Ejecuta la operación {@code redirectToSwagger} del componente.
     * @return resultado de la operación.
     */
    @GetMapping("/")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui.html";
    }
}

package com.vitallogix.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de monitoreo de estado (health check) del servidor backend.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@RestController
public class HealthController {

    /**
     * Ejecuta la operación {@code health} del componente.
     * @return resultado de la operación.
     */
    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}

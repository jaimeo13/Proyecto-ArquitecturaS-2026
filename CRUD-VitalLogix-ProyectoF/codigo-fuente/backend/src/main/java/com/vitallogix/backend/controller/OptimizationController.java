package com.vitallogix.backend.controller;

import com.vitallogix.backend.dto.ComboSuggestionRequest;
import com.vitallogix.backend.dto.ComboSuggestionResponse;
import com.vitallogix.backend.service.ComboSuggestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el motor de recomendaciones personalizadas de combos.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/optimization")
public class OptimizationController {

    private final ComboSuggestionService comboSuggestionService;

    public OptimizationController(ComboSuggestionService comboSuggestionService) {
        this.comboSuggestionService = comboSuggestionService;
    }

    /**
     * Ejecuta la operación {@code suggestCombo} del componente.
     * @param request parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    @PostMapping("/personalized-recommendations")
    public ComboSuggestionResponse suggestCombo(@Valid @RequestBody ComboSuggestionRequest request) {
        return comboSuggestionService.suggest(request);
    }
}

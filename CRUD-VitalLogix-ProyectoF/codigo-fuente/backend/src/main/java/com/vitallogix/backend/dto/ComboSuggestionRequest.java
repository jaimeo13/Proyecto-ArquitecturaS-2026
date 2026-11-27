package com.vitallogix.backend.dto;

import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de entrada con los productos priorizados para solicitar recomendaciones de combos.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ComboSuggestionRequest(
    List<Long> prioritizedProductIds,
    @Min(value = 1, message = "El maximo de recomendaciones debe ser mayor a 0") Integer maxRecommendations
) {
    public ComboSuggestionRequest {
        if (prioritizedProductIds == null) prioritizedProductIds = new ArrayList<>();
        if (maxRecommendations == null) maxRecommendations = 6;
    }

    // Legacy getters for compatibility
    /**
     * Obtiene el valor de {@code prioritizedProductIds}.
     * @return resultado de la operación.
     */
    public List<Long> getPrioritizedProductIds() { return prioritizedProductIds(); }
    /**
     * Obtiene el valor de {@code maxRecommendations}.
     * @return resultado de la operación.
     */
    public Integer getMaxRecommendations() { return maxRecommendations(); }
}

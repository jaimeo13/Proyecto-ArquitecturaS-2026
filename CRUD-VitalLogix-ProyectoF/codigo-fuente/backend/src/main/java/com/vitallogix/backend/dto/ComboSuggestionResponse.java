package com.vitallogix.backend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de salida que agrupa los productos sugeridos y el mensaje del motor de recomendación.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ComboSuggestionResponse(
    BigDecimal prioritizedCost,
    BigDecimal recommendedCost,
    BigDecimal totalCost,
    int totalScore,
    String message,
    List<ComboItem> prioritizedItems,
    List<ComboItem> recommendedItems
) {
    public static record ComboItem(Long id, String name, BigDecimal price, Integer stock, int score) {}
}

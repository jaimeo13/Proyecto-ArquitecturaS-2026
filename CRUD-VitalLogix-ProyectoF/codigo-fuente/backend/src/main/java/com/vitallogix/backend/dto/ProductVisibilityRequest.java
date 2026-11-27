package com.vitallogix.backend.dto;

/**
 * DTO de entrada para alternar la visibilidad de un producto en recomendaciones.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ProductVisibilityRequest(Boolean visibleInSuggestions) {}

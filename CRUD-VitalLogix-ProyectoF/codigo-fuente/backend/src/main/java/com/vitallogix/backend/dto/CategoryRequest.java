package com.vitallogix.backend.dto;

/**
 * DTO de entrada para crear o editar una categoría de productos.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record CategoryRequest(
    String name,
    String description,
    Boolean visibleInSuggestions
) {}

package com.vitallogix.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida que expone los metadatos y estado de aprobación de una categoría.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record CategoryResponse(
    Long id,
    String name,
    String description,
    String status,
    String type,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String approvedBy,
    LocalDateTime approvedAt,
    boolean visibleInSuggestions
) {}

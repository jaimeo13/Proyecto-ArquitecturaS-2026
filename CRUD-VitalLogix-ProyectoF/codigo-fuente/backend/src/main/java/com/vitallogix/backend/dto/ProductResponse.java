package com.vitallogix.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de salida que representa un registro de producto serializado para la tabla CRUD y catálogo.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ProductResponse(
    Long id,
    String name,
    String code,
    String description,
    String imageUrl,
    String category,
    BigDecimal price,
    Integer stock,
    boolean requiresPrescription,
    boolean visibleInSuggestions,
    String promotionType,
    Integer promoBuyQuantity,
    Integer promoPayQuantity,
    BigDecimal promoPercentDiscount,
    LocalDateTime createdAt,
    LocalDateTime expirationDate
) {}

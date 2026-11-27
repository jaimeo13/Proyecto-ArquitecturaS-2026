package com.vitallogix.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de salida con la información completa de una campaña y sus productos asociados.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record CampaignResponse(
    Long id,
    String name,
    String description,
    String promotionType,
    Integer promoBuyQuantity,
    Integer promoPayQuantity,
    BigDecimal promoPercentDiscount,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean isActive,
    LocalDateTime createdAt,
    Set<Long> productIds
) {}

package com.vitallogix.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de entrada validado para crear o actualizar una campaña promocional.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record CampaignRequest(
    String name,
    String description,
    String promotionType,
    Integer promoBuyQuantity,
    Integer promoPayQuantity,
    BigDecimal promoPercentDiscount,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean active,
    Set<Long> productIds
) {}

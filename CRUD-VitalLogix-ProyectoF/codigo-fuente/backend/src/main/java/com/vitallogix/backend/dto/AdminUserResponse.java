package com.vitallogix.backend.dto;

import com.vitallogix.backend.model.Role;

import java.util.Set;

/**
 * DTO de salida con el resumen de usuarios del sistema, roles y progreso de fidelidad.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record AdminUserResponse(
    Long id,
    String username,
    Set<Role> roles,
    String clienteAmigoNumber,
    boolean couponAvailable,
    int purchasesSinceCoupon,
    long totalPurchaseCount
) {}

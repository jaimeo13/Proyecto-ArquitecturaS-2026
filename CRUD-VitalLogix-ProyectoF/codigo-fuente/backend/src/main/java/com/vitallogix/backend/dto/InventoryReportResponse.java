package com.vitallogix.backend.dto;

/**
 * DTO de salida que representa una fila del reporte de inventario y caducidad.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record InventoryReportResponse(
    String productName,
    Integer stock,
    String category,
    String expiration
) {}

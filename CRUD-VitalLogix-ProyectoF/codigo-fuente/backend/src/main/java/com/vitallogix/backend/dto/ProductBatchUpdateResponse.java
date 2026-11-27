package com.vitallogix.backend.dto;

import java.util.List;

/**
 * DTO de salida que resume el resultado de una operación de modificación masiva de productos.
 *
 * @param updatedCount cantidad de productos actualizados satisfactoriamente.
 * @param updatedIds   lista de identificadores modificados en la base de datos MySQL.
 * @param message      mensaje descriptivo del resultado de la operación.
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ProductBatchUpdateResponse(
        int updatedCount,
        List<Long> updatedIds,
        String message
) {}

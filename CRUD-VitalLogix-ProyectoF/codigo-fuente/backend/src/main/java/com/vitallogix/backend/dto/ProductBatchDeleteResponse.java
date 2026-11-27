package com.vitallogix.backend.dto;

import java.util.List;

/**
 * DTO de salida que resume el resultado de una operación de eliminación masiva de productos.
 *
 * @param deletedCount cantidad de productos eliminados satisfactoriamente.
 * @param deletedIds   lista de identificadores eliminados de la base de datos MySQL.
 * @param message      mensaje descriptivo del resultado de la operación.
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ProductBatchDeleteResponse(
        int deletedCount,
        List<Long> deletedIds,
        String message
) {}

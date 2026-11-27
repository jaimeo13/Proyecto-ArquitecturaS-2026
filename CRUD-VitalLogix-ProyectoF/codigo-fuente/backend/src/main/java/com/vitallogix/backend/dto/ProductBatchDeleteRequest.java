package com.vitallogix.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO de entrada para la eliminación masiva (por lote) de registros de productos seleccionados en la tabla CRUD.
 *
 * @param ids lista no vacía de identificadores únicos de productos a eliminar.
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ProductBatchDeleteRequest(
        @NotEmpty(message = "Debe seleccionar al menos un producto para eliminar")
        List<Long> ids
) {}

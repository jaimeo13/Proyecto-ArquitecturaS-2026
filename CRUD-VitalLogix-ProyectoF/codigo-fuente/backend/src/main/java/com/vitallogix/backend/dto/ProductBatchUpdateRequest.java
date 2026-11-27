package com.vitallogix.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para la modificación masiva (por lote) de productos seleccionados en la vista administrativa CRUD.
 * Solo los campos distintos de {@code null} serán aplicados a los registros seleccionados.
 *
 * @param ids                  lista no vacía de identificadores de productos a modificar.
 * @param category             nueva categoría opcional para todos los productos seleccionados.
 * @param price                nuevo precio unitario fijo opcional (mayor o igual a 0.00).
 * @param stock                nuevo valor fijo de stock opcional (mayor o igual a 0).
 * @param stockDelta           ajuste relativo opcional a sumar o restar al stock actual de cada producto.
 * @param requiresPrescription bandera opcional para actualizar si requieren receta médica.
 * @param visibleInSuggestions bandera opcional para actualizar visibilidad en el motor de sugerencias.
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ProductBatchUpdateRequest(
        @NotEmpty(message = "Debe seleccionar al menos un producto para modificar")
        List<Long> ids,
        String category,
        @DecimalMin(value = "0.00", message = "El precio debe ser mayor o igual a 0")
        BigDecimal price,
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock,
        Integer stockDelta,
        Boolean requiresPrescription,
        Boolean visibleInSuggestions
) {}

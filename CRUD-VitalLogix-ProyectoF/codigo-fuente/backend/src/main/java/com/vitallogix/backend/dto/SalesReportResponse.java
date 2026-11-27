package com.vitallogix.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de salida que resume las ventas totales y número de transacciones por fecha.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record SalesReportResponse(
    LocalDate date,
    BigDecimal totalSales,
    Integer totalTransactions
) {}

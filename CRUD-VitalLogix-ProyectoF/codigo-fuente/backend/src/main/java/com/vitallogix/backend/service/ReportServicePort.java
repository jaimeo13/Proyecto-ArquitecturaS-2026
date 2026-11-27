package com.vitallogix.backend.service;

import com.vitallogix.backend.dto.InventoryReportResponse;
import com.vitallogix.backend.dto.SalesReportResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz de salida (DIP) que define los servicios de generación de reportes del sistema.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public interface ReportServicePort {
    List<SalesReportResponse> getSalesReport(LocalDate from, LocalDate to);
    List<InventoryReportResponse> getInventoryReport();
}

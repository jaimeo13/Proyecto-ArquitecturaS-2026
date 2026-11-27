package com.vitallogix.backend.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estrategia concreta (patrón Strategy) para descuentos porcentuales directos.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public class PercentagePromotionStrategy implements PromotionStrategy {
    /**
     * Calcula el resultado de negocio para la operación {@code calculateNet}.
     * @param unitPrice parámetro de entrada de la operación.
     * @param quantity parámetro de entrada de la operación.
     * @param buy parámetro de entrada de la operación.
     * @param pay parámetro de entrada de la operación.
     * @param percent parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    @Override
    public BigDecimal calculateNet(BigDecimal unitPrice, int quantity, Integer buy, Integer pay, BigDecimal percent) {
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
        if (percent != null && percent.compareTo(BigDecimal.ZERO) > 0 && percent.compareTo(BigDecimal.valueOf(100)) < 0) {
            BigDecimal multiplier = BigDecimal.ONE.subtract(percent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            return gross.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }
        return gross;
    }

    /**
     * Valida las reglas de negocio correspondientes (validate).
     * @param buy parámetro de entrada de la operación.
     * @param pay parámetro de entrada de la operación.
     * @param percent parámetro de entrada de la operación.
     */
    @Override
    public void validate(Integer buy, Integer pay, BigDecimal percent) {
        if (percent == null || percent.compareTo(BigDecimal.ZERO) <= 0 || percent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, 
                "Descuento porcentual invalido. Debe ser mayor a 0 y menor a 100."
            );
        }
    }
}

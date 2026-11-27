package com.vitallogix.backend.strategy;

import java.math.BigDecimal;

/**
 * Estrategia nula (patrón Strategy) aplicada cuando un producto no tiene promoción activa.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public class NoPromotionStrategy implements PromotionStrategy {
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
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Valida las reglas de negocio correspondientes (validate).
     * @param buy parámetro de entrada de la operación.
     * @param pay parámetro de entrada de la operación.
     * @param percent parámetro de entrada de la operación.
     */
    @Override
    public void validate(Integer buy, Integer pay, BigDecimal percent) {
        // No validation needed for NONE
    }
}

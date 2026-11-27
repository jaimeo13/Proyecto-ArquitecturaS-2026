package com.vitallogix.backend.strategy;

import java.math.BigDecimal;

/**
 * Interfaz del patrón Strategy que abstrae la validación y cálculo de descuentos promocionales.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public interface PromotionStrategy {
    BigDecimal calculateNet(BigDecimal unitPrice, int quantity, Integer buy, Integer pay, BigDecimal percent);
    void validate(Integer buy, Integer pay, BigDecimal percent);
}

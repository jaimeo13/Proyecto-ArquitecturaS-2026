package com.vitallogix.backend.strategy;

import java.math.BigDecimal;

/**
 * Estrategia concreta (patrón Strategy) para promociones de tipo Compra X y Paga Y (ej. 3x2).
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public class BuyXPayYPromotionStrategy implements PromotionStrategy {
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
        if (buy != null && pay != null && buy >= 2 && pay >= 1 && pay < buy && quantity >= buy) {
            int groups = quantity / buy;
            int remainder = quantity % buy;
            int payableUnits = (groups * pay) + remainder;
            return unitPrice.multiply(BigDecimal.valueOf(payableUnits));
        }
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
        if (buy == null || pay == null || buy < 2 || pay < 1 || pay >= buy) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, 
                "Oferta BUY_X_PAY_Y invalida. Usa buy>=2 y 1<=pay<buy."
            );
        }
    }
}

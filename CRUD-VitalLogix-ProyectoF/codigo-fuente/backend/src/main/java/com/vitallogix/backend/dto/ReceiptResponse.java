package com.vitallogix.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida con el desglose de artículos, totales y descuentos de un recibo de venta.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ReceiptResponse(
    Long saleId,
    LocalDateTime saleDate,
    String customerName,
    String customerAddress,
    String customerPhone,
    List<Item> items,
    BigDecimal totalAmount,
    BigDecimal discount,
    BigDecimal finalAmount,
    String loyaltyAwardedCode,
    Integer purchasesSinceCoupon,
    Integer purchasesToNextCoupon
) {
    public static record Item(
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        String campaignName,
        BigDecimal discountAmount
    ) {}
}

package com.vitallogix.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO de entrada validado para crear o actualizar un producto en la tabla MySQL products.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record ProductRequest(
    @NotBlank(message = "Name is required") String name,
    String code,
    String description,
    String imageUrl,
    String category,
    @NotNull(message = "Price is required") @DecimalMin(value = "0.00", message = "Price must be >= 0") BigDecimal price,
    @NotNull(message = "Stock is required") @Min(value = 0, message = "Stock must be >= 0") Integer stock,
    boolean requiresPrescription,
    boolean visibleInSuggestions,
    String promotionType,
    Integer promoBuyQuantity,
    Integer promoPayQuantity,
    BigDecimal promoPercentDiscount,
    java.time.LocalDateTime expirationDate
) {
    public ProductRequest {
        // default visibleInSuggestions true if not provided by binder (keep previous default behavior)
    }

    // Legacy getters for compatibility
    /**
     * Obtiene el valor de {@code name}.
     * @return resultado de la operación.
     */
    public String getName() { return name(); }
    /**
     * Obtiene el valor de {@code code}.
     * @return resultado de la operación.
     */
    public String getCode() { return code(); }
    /**
     * Obtiene el valor de {@code description}.
     * @return resultado de la operación.
     */
    public String getDescription() { return description(); }
    /**
     * Obtiene el valor de {@code imageUrl}.
     * @return resultado de la operación.
     */
    public String getImageUrl() { return imageUrl(); }
    /**
     * Obtiene el valor de {@code category}.
     * @return resultado de la operación.
     */
    public String getCategory() { return category(); }
    /**
     * Obtiene el valor de {@code price}.
     * @return resultado de la operación.
     */
    public BigDecimal getPrice() { return price(); }
    /**
     * Obtiene el valor de {@code stock}.
     * @return resultado de la operación.
     */
    public Integer getStock() { return stock(); }
    /**
     * Indica el estado booleano de {@code requiresPrescription}.
     * @return resultado de la operación.
     */
    public boolean isRequiresPrescription() { return requiresPrescription(); }
    /**
     * Indica el estado booleano de {@code visibleInSuggestions}.
     * @return resultado de la operación.
     */
    public boolean isVisibleInSuggestions() { return visibleInSuggestions(); }
    /**
     * Obtiene el valor de {@code promotionType}.
     * @return resultado de la operación.
     */
    public String getPromotionType() { return promotionType(); }
    /**
     * Obtiene el valor de {@code promoBuyQuantity}.
     * @return resultado de la operación.
     */
    public Integer getPromoBuyQuantity() { return promoBuyQuantity(); }
    /**
     * Obtiene el valor de {@code promoPayQuantity}.
     * @return resultado de la operación.
     */
    public Integer getPromoPayQuantity() { return promoPayQuantity(); }
    /**
     * Obtiene el valor de {@code promoPercentDiscount}.
     * @return resultado de la operación.
     */
    public BigDecimal getPromoPercentDiscount() { return promoPercentDiscount(); }
    /**
     * Obtiene el valor de {@code expirationDate}.
     * @return resultado de la operación.
     */
    public java.time.LocalDateTime getExpirationDate() { return expirationDate(); }
}

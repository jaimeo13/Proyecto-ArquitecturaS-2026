package com.vitallogix.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un producto farmacéutico almacenado en la tabla {@code products} de MySQL.
 * Cumple y excede los campos requeridos para el CRUD: identificador único ({@code id}), campos de texto
 * ({@code name}, {@code code}, {@code description}, {@code category}) y campos numéricos ({@code price}, {@code stock}).
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Entity
@Table(name = "products")
public class Product {

    /** Identificador único autoincremental del producto en MySQL. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre comercial o genérico del producto farmacéutico (obligatorio). */
    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    /** Código único de inventario o SKU del producto. */
    @Column(unique = true, length = 60)
    private String code;

    /** Descripción detallada o presentación del producto. */
    @Column(length = 500)
    private String description;

    /** URL o imagen codificada en Base64 (almacenada como LONGTEXT en MySQL). */
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    /** Nombre de la categoría terapéutica o comercial asignada al producto. */
    @Column(length = 100)
    private String category;

    /** Precio unitario de venta en moneda local (mayor o igual a 0.00). */
    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false)
    private BigDecimal price;

    /** Cantidad disponible de unidades en inventario (entero mayor o igual a 0). */
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    /** Indica si la venta del medicamento requiere receta médica. */
    @Column(nullable = false)
    private boolean requiresPrescription;

    /** Indica si el producto es visible en el catálogo público de usuarios. */
    @Column(nullable = true, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean visibleToUsers = true;

    /** Indica si el producto puede aparecer en el motor de sugerencias de combos. */
    @Column(nullable = true, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean visibleInSuggestions = true;

    /** Tipo de promoción directa aplicada al producto ({@code NONE}, {@code BUY_X_PAY_Y}, {@code PERCENTAGE}). */
    @Column(length = 30)
    private String promotionType;

    /** Cantidad a comprar en promociones tipo {@code BUY_X_PAY_Y}. */
    @Column
    private Integer promoBuyQuantity;

    /** Cantidad a pagar en promociones tipo {@code BUY_X_PAY_Y}. */
    @Column
    private Integer promoPayQuantity;

    /** Porcentaje de descuento en promociones tipo {@code PERCENTAGE}. */
    @Column(precision = 5, scale = 2)
    private BigDecimal promoPercentDiscount;

    /** Fecha y hora de creación del registro en base de datos. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fecha de caducidad del lote actual del producto. */
    @Column
    private LocalDateTime expirationDate;

    /**
     * Inicializa automáticamente la marca de tiempo de creación antes de persistir el registro.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /** @return identificador único del producto. */
    public Long getId() { return id; }

    /** @param id identificador único a establecer. */
    public void setId(Long id) { this.id = id; }

    /** @return nombre del producto. */
    public String getName() { return name; }

    /** @param name nombre del producto. */
    public void setName(String name) { this.name = name; }

    /** @return código único o SKU del producto. */
    public String getCode() { return code; }

    /** @param code código único o SKU del producto. */
    public void setCode(String code) { this.code = code; }

    /** @return descripción del producto. */
    public String getDescription() { return description; }

    /** @param description descripción del producto. */
    public void setDescription(String description) { this.description = description; }

    /** @return URL o contenido Base64 de la imagen del producto. */
    public String getImageUrl() { return imageUrl; }

    /** @param imageUrl URL o contenido Base64 de la imagen. */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    /** @return categoría del producto. */
    public String getCategory() { return category; }

    /** @param category categoría del producto. */
    public void setCategory(String category) { this.category = category; }

    /** @return precio unitario del producto. */
    public BigDecimal getPrice() { return price; }

    /** @param price precio unitario del producto. */
    public void setPrice(BigDecimal price) { this.price = price; }

    /** @return existencias disponibles en inventario. */
    public Integer getStock() { return stock; }

    /** @param stock existencias disponibles en inventario. */
    public void setStock(Integer stock) { this.stock = stock; }

    /** @return {@code true} si el producto requiere receta médica. */
    public boolean isRequiresPrescription() { return requiresPrescription; }

    /** @param requiresPrescription indica si requiere receta médica. */
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }

    /** @return {@code true} si el producto es visible para usuarios. */
    public boolean isVisibleToUsers() { return visibleToUsers; }

    /** @param visibleToUsers visibilidad hacia usuarios finales. */
    public void setVisibleToUsers(boolean visibleToUsers) { this.visibleToUsers = visibleToUsers; }

    /** @return {@code true} si el producto es elegible para recomendaciones. */
    public boolean isVisibleInSuggestions() { return visibleInSuggestions; }

    /** @param visibleInSuggestions visibilidad en el motor de sugerencias. */
    public void setVisibleInSuggestions(boolean visibleInSuggestions) { this.visibleInSuggestions = visibleInSuggestions; }

    /** @return tipo de promoción configurada. */
    public String getPromotionType() { return promotionType; }

    /** @param promotionType tipo de promoción. */
    public void setPromotionType(String promotionType) { this.promotionType = promotionType; }

    /** @return unidades a comprar en promoción X por Y. */
    public Integer getPromoBuyQuantity() { return promoBuyQuantity; }

    /** @param promoBuyQuantity unidades a comprar en promoción X por Y. */
    public void setPromoBuyQuantity(Integer promoBuyQuantity) { this.promoBuyQuantity = promoBuyQuantity; }

    /** @return unidades a pagar en promoción X por Y. */
    public Integer getPromoPayQuantity() { return promoPayQuantity; }

    /** @param promoPayQuantity unidades a pagar en promoción X por Y. */
    public void setPromoPayQuantity(Integer promoPayQuantity) { this.promoPayQuantity = promoPayQuantity; }

    /** @return porcentaje de descuento promocional. */
    public BigDecimal getPromoPercentDiscount() { return promoPercentDiscount; }

    /** @param promoPercentDiscount porcentaje de descuento promocional. */
    public void setPromoPercentDiscount(BigDecimal promoPercentDiscount) { this.promoPercentDiscount = promoPercentDiscount; }

    /** @return fecha y hora de creación del registro. */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @param createdAt fecha y hora de creación. */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** @return fecha de vencimiento del producto. */
    public LocalDateTime getExpirationDate() { return expirationDate; }

    /** @param expirationDate fecha de vencimiento del producto. */
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
}

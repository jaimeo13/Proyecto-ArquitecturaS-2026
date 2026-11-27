package com.vitallogix.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA almacenada en MySQL (tabla campaigns) que modela campañas promocionales vigentes.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 30)
    private String promotionType = "NONE";

    @Column
    private Integer promoBuyQuantity;

    @Column
    private Integer promoPayQuantity;

    @Column(precision = 5, scale = 2)
    private BigDecimal promoPercentDiscount;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "campaign_products",
        joinColumns = @JoinColumn(name = "campaign_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();

    /**
     * Ejecuta la operación {@code prePersist} del componente.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    /**
     * Obtiene el valor de {@code id}.
     * @return resultado de la operación.
     */
    public Long getId() { return id; }
    /**
     * Establece el valor de {@code id}.
     * @param id parámetro de entrada de la operación.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el valor de {@code name}.
     * @return resultado de la operación.
     */
    public String getName() { return name; }
    /**
     * Establece el valor de {@code name}.
     * @param name parámetro de entrada de la operación.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene el valor de {@code description}.
     * @return resultado de la operación.
     */
    public String getDescription() { return description; }
    /**
     * Establece el valor de {@code description}.
     * @param description parámetro de entrada de la operación.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Obtiene el valor de {@code promotionType}.
     * @return resultado de la operación.
     */
    public String getPromotionType() { return promotionType; }
    /**
     * Establece el valor de {@code promotionType}.
     * @param promotionType parámetro de entrada de la operación.
     */
    public void setPromotionType(String promotionType) { this.promotionType = promotionType; }

    /**
     * Obtiene el valor de {@code promoBuyQuantity}.
     * @return resultado de la operación.
     */
    public Integer getPromoBuyQuantity() { return promoBuyQuantity; }
    /**
     * Establece el valor de {@code promoBuyQuantity}.
     * @param promoBuyQuantity parámetro de entrada de la operación.
     */
    public void setPromoBuyQuantity(Integer promoBuyQuantity) { this.promoBuyQuantity = promoBuyQuantity; }

    /**
     * Obtiene el valor de {@code promoPayQuantity}.
     * @return resultado de la operación.
     */
    public Integer getPromoPayQuantity() { return promoPayQuantity; }
    /**
     * Establece el valor de {@code promoPayQuantity}.
     * @param promoPayQuantity parámetro de entrada de la operación.
     */
    public void setPromoPayQuantity(Integer promoPayQuantity) { this.promoPayQuantity = promoPayQuantity; }

    /**
     * Obtiene el valor de {@code promoPercentDiscount}.
     * @return resultado de la operación.
     */
    public BigDecimal getPromoPercentDiscount() { return promoPercentDiscount; }
    /**
     * Establece el valor de {@code promoPercentDiscount}.
     * @param promoPercentDiscount parámetro de entrada de la operación.
     */
    public void setPromoPercentDiscount(BigDecimal promoPercentDiscount) { this.promoPercentDiscount = promoPercentDiscount; }

    /**
     * Obtiene el valor de {@code startDate}.
     * @return resultado de la operación.
     */
    public LocalDateTime getStartDate() { return startDate; }
    /**
     * Establece el valor de {@code startDate}.
     * @param startDate parámetro de entrada de la operación.
     */
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    /**
     * Obtiene el valor de {@code endDate}.
     * @return resultado de la operación.
     */
    public LocalDateTime getEndDate() { return endDate; }
    /**
     * Establece el valor de {@code endDate}.
     * @param endDate parámetro de entrada de la operación.
     */
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    /**
     * Indica el estado booleano de {@code active}.
     * @return resultado de la operación.
     */
    public boolean isActive() { return isActive; }
    /**
     * Establece el valor de {@code active}.
     * @param active parámetro de entrada de la operación.
     */
    public void setActive(boolean active) { isActive = active; }

    /**
     * Obtiene el valor de {@code createdAt}.
     * @return resultado de la operación.
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * Establece el valor de {@code createdAt}.
     * @param createdAt parámetro de entrada de la operación.
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Obtiene el valor de {@code products}.
     * @return resultado de la operación.
     */
    public Set<Product> getProducts() { return products; }
    /**
     * Establece el valor de {@code products}.
     * @param products parámetro de entrada de la operación.
     */
    public void setProducts(Set<Product> products) { this.products = products; }
}

package com.vitallogix.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Entidad JPA almacenada en MySQL (tabla sale_items) que detalla cada producto vendido en una venta.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Entity
@Table(name = "sale_items")
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private BigDecimal unitPrice;
    private String campaignName;

    // Getters and setters
    /**
     * Obtiene el valor de {@code id}.
     * @return resultado de la operación.
     */
    public Long getId() { return id; }
    
    /**
     * Obtiene el valor de {@code sale}.
     * @return resultado de la operación.
     */
    public Sale getSale() { return sale; }
    /**
     * Establece el valor de {@code sale}.
     * @param sale parámetro de entrada de la operación.
     */
    public void setSale(Sale sale) { this.sale = sale; }

    /**
     * Obtiene el valor de {@code product}.
     * @return resultado de la operación.
     */
    public Product getProduct() { return product; }
    /**
     * Establece el valor de {@code product}.
     * @param product parámetro de entrada de la operación.
     */
    public void setProduct(Product product) { this.product = product; }

    /**
     * Obtiene el valor de {@code quantity}.
     * @return resultado de la operación.
     */
    public Integer getQuantity() { return quantity; }
    /**
     * Establece el valor de {@code quantity}.
     * @param quantity parámetro de entrada de la operación.
     */
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    /**
     * Obtiene el valor de {@code unitPrice}.
     * @return resultado de la operación.
     */
    public BigDecimal getUnitPrice() { return unitPrice; }
    /**
     * Establece el valor de {@code unitPrice}.
     * @param unitPrice parámetro de entrada de la operación.
     */
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    /**
     * Obtiene el valor de {@code campaignName}.
     * @return resultado de la operación.
     */
    public String getCampaignName() { return campaignName; }
    /**
     * Establece el valor de {@code campaignName}.
     * @param campaignName parámetro de entrada de la operación.
     */
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
}

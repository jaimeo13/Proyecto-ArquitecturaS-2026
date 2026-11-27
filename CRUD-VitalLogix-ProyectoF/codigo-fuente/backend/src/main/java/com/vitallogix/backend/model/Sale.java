package com.vitallogix.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad JPA almacenada en MySQL (tabla sales) que registra una transacción de venta completa.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime saleDate;

    private BigDecimal totalAmount;
    
    // 10% discount applied when customer has clienteamigo loyalty status.
    // Always set to ZERO if customer is not a friend (loyalty member).
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    // Original total amount before applying any discounts. Useful for audit trail and reporting.
    private BigDecimal originalAmount;

    @Column(name = "account_username", length = 100)
    private String accountUsername;

    @Column(name = "loyalty_awarded_code", length = 30)
    private String loyaltyAwardedCode;


    // Bidirectional relationship with SaleItem. Cascade ensures items are deleted when sale is deleted.
    // MappedBy indicates the reverse side of the relationship (SaleItem owns the foreign key).
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sale")
    private List<SaleItem> items;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * Obtiene el valor de {@code customer}.
     * @return resultado de la operación.
     */
    public Customer getCustomer() { return customer; }
    /**
     * Establece el valor de {@code customer}.
     * @param customer parámetro de entrada de la operación.
     */
    public void setCustomer(Customer customer) { this.customer = customer; }

    // Auto-set sale timestamp when record is first persisted to database.
    /**
     * Ejecuta la operación {@code onCreate} del componente.
     */
    @PrePersist
    protected void onCreate() {
        this.saleDate = LocalDateTime.now();
    }

    // Add item to sale and establish bidirectional relationship.
    // Initializes items list if null. Use this instead of directly modifying items list.
    /**
     * Ejecuta la operación {@code addItem} del componente.
     * @param item parámetro de entrada de la operación.
     */
    public void addItem(SaleItem item) {
        if (items == null) {
            this.items = new java.util.ArrayList<>();
        }
        items.add(item);
        item.setSale(this);
    }

    // Getters and setters 
    /**
     * Obtiene el valor de {@code id}.
     * @return resultado de la operación.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el valor de {@code id}.
     * @param id parámetro de entrada de la operación.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el valor de {@code saleDate}.
     * @return resultado de la operación.
     */
    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    /**
     * Establece el valor de {@code saleDate}.
     * @param saleDate parámetro de entrada de la operación.
     */
    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    /**
     * Obtiene el valor de {@code totalAmount}.
     * @return resultado de la operación.
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Establece el valor de {@code totalAmount}.
     * @param totalAmount parámetro de entrada de la operación.
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Returns discount amount applied to this sale.
    /**
     * Obtiene el valor de {@code discountAmount}.
     * @return resultado de la operación.
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Establece el valor de {@code discountAmount}.
     * @param discountAmount parámetro de entrada de la operación.
     */
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    // Returns amount before discount. Used for reporting and audit trail.
    /**
     * Obtiene el valor de {@code originalAmount}.
     * @return resultado de la operación.
     */
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    /**
     * Establece el valor de {@code originalAmount}.
     * @param originalAmount parámetro de entrada de la operación.
     */
    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    /**
     * Obtiene el valor de {@code accountUsername}.
     * @return resultado de la operación.
     */
    public String getAccountUsername() {
        return accountUsername;
    }

    /**
     * Establece el valor de {@code accountUsername}.
     * @param accountUsername parámetro de entrada de la operación.
     */
    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    /**
     * Obtiene el valor de {@code loyaltyAwardedCode}.
     * @return resultado de la operación.
     */
    public String getLoyaltyAwardedCode() {
        return loyaltyAwardedCode;
    }

    /**
     * Establece el valor de {@code loyaltyAwardedCode}.
     * @param loyaltyAwardedCode parámetro de entrada de la operación.
     */
    public void setLoyaltyAwardedCode(String loyaltyAwardedCode) {
        this.loyaltyAwardedCode = loyaltyAwardedCode;
    }

    /**
     * Obtiene el valor de {@code items}.
     * @return resultado de la operación.
     */
    public List<SaleItem> getItems() {
        return items;
    }

    /**
     * Establece el valor de {@code items}.
     * @param items parámetro de entrada de la operación.
     */
    public void setItems(List<SaleItem> items) {
        this.items = items;
    }
}

package com.vitallogix.backend.model;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Entidad JPA almacenada en MySQL (tabla users) que representa las credenciales y roles de acceso.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Column(name = "cliente_amigo_number", unique = true, length = 30)
    private String clienteAmigoNumber;

    @Column
    private Boolean friend = false;

    @Column(name = "purchases_since_coupon")
    private Integer purchasesSinceCoupon = 0;

    @Column(name = "coupon_used")
    private Boolean couponUsed = true;

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
     * Obtiene el valor de {@code username}.
     * @return resultado de la operación.
     */
    public String getUsername() { return username; }
    /**
     * Establece el valor de {@code username}.
     * @param username parámetro de entrada de la operación.
     */
    public void setUsername(String username) { this.username = username; }
    /**
     * Obtiene el valor de {@code password}.
     * @return resultado de la operación.
     */
    public String getPassword() { return password; }
    /**
     * Establece el valor de {@code password}.
     * @param password parámetro de entrada de la operación.
     */
    public void setPassword(String password) { this.password = password; }
    /**
     * Obtiene el valor de {@code roles}.
     * @return resultado de la operación.
     */
    public Set<Role> getRoles() { return roles; }
    /**
     * Establece el valor de {@code roles}.
     * @param roles parámetro de entrada de la operación.
     */
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    /**
     * Obtiene el valor de {@code clienteAmigoNumber}.
     * @return resultado de la operación.
     */
    public String getClienteAmigoNumber() { return clienteAmigoNumber; }
    /**
     * Establece el valor de {@code clienteAmigoNumber}.
     * @param clienteAmigoNumber parámetro de entrada de la operación.
     */
    public void setClienteAmigoNumber(String clienteAmigoNumber) { this.clienteAmigoNumber = clienteAmigoNumber; }
    /**
     * Indica el estado booleano de {@code friend}.
     * @return resultado de la operación.
     */
    public boolean isFriend() { return Boolean.TRUE.equals(friend); }
    /**
     * Establece el valor de {@code friend}.
     * @param friend parámetro de entrada de la operación.
     */
    public void setFriend(boolean friend) { this.friend = friend; }
    /**
     * Obtiene el valor de {@code purchasesSinceCoupon}.
     * @return resultado de la operación.
     */
    public Integer getPurchasesSinceCoupon() { return purchasesSinceCoupon; }
    /**
     * Establece el valor de {@code purchasesSinceCoupon}.
     * @param purchasesSinceCoupon parámetro de entrada de la operación.
     */
    public void setPurchasesSinceCoupon(Integer purchasesSinceCoupon) { this.purchasesSinceCoupon = purchasesSinceCoupon; }
    /**
     * Indica el estado booleano de {@code couponUsed}.
     * @return resultado de la operación.
     */
    public boolean isCouponUsed() { return Boolean.TRUE.equals(couponUsed); }
    /**
     * Establece el valor de {@code couponUsed}.
     * @param couponUsed parámetro de entrada de la operación.
     */
    public void setCouponUsed(boolean couponUsed) { this.couponUsed = couponUsed; }
}

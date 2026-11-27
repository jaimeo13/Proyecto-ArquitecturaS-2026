package com.vitallogix.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad JPA almacenada en MySQL (tabla customers) que representa a los clientes de la farmacia.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "cliente_amigo_number", unique = true, length = 30)
    private String clienteAmigoNumber;

    @Column(nullable = false)
    // Loyalty program flag. Set to true when customer reaches 5+ purchases (auto-promoted) or manually assigned.
    // Grants 10% discount on all sales. Customer gets unique CAM-XXXXXX code when promoted.
    private boolean friend;

    // Bidirectional relationship: one customer can have multiple sales.
    // JsonIgnore prevents infinite serialization loop in API responses.
    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Sale> sales;

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
     * Obtiene el valor de {@code address}.
     * @return resultado de la operación.
     */
    public String getAddress() { return address; }
    /**
     * Establece el valor de {@code address}.
     * @param address parámetro de entrada de la operación.
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * Obtiene el valor de {@code phone}.
     * @return resultado de la operación.
     */
    public String getPhone() { return phone; }
    /**
     * Establece el valor de {@code phone}.
     * @param phone parámetro de entrada de la operación.
     */
    public void setPhone(String phone) { this.phone = phone; }

    // Returns unique loyalty program identifier (CAM-XXXXXX format).
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

    // Returns true if customer is in loyalty program (entitled to 10% discount).
    /**
     * Indica el estado booleano de {@code friend}.
     * @return resultado de la operación.
     */
    public boolean isFriend() { return friend; }
    /**
     * Establece el valor de {@code friend}.
     * @param friend parámetro de entrada de la operación.
     */
    public void setFriend(boolean friend) { this.friend = friend; }

    // Returns customer's purchase history.
    /**
     * Obtiene el valor de {@code sales}.
     * @return resultado de la operación.
     */
    public List<Sale> getSales() { return sales; }
    /**
     * Establece el valor de {@code sales}.
     * @param sales parámetro de entrada de la operación.
     */
    public void setSales(List<Sale> sales) { this.sales = sales; }
}

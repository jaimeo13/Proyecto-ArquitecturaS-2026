package com.vitallogix.backend.dto;

/**
 * DTO de entrada para el registro o modificación de un cliente.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record CustomerRequest(
    String name,
    String address,
    String phone,
    String clienteAmigoNumber,
    boolean friend
) {
    // Legacy getters for compatibility
    /**
     * Obtiene el valor de {@code name}.
     * @return resultado de la operación.
     */
    public String getName() { return name(); }
    /**
     * Obtiene el valor de {@code address}.
     * @return resultado de la operación.
     */
    public String getAddress() { return address(); }
    /**
     * Obtiene el valor de {@code phone}.
     * @return resultado de la operación.
     */
    public String getPhone() { return phone(); }
    /**
     * Obtiene el valor de {@code clienteAmigoNumber}.
     * @return resultado de la operación.
     */
    public String getClienteAmigoNumber() { return clienteAmigoNumber(); }
    /**
     * Indica el estado booleano de {@code friend}.
     * @return resultado de la operación.
     */
    public boolean isFriend() { return friend(); }
}

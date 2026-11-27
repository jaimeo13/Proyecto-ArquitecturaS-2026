package com.vitallogix.backend.dto;

/**
 * DTO de salida con datos del cliente y conteo acumulado de compras.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record CustomerResponse(
    Long id,
    String name,
    String address,
    String phone,
    String clienteAmigoNumber,
    boolean friend,
    long purchaseCount
) {
    public CustomerResponse(Long id, String name, String address, String phone, String clienteAmigoNumber, boolean friend) {
        this(id, name, address, phone, clienteAmigoNumber, friend, 0);
    }
}

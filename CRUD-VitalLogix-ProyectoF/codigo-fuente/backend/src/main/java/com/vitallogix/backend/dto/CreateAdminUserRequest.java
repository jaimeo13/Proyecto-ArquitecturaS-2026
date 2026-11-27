package com.vitallogix.backend.dto;

/**
 * DTO de entrada para la creación de una cuenta con privilegios de administrador.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public record CreateAdminUserRequest(
    String username,
    String password
) {}

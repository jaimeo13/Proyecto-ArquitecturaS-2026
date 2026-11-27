package com.vitallogix.backend.exception;

/**
 * Excepción de dominio lanzada cuando se intenta registrar o actualizar una entidad
 * con un identificador único o código de negocio que ya existe en la base de datos MySQL.
 * Es manejada por {@link GlobalExceptionHandler} devolviendo HTTP 409 (Conflict).
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Crea una nueva excepción de recurso duplicado.
     *
     * @param message mensaje explicando qué campo único está en conflicto.
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}

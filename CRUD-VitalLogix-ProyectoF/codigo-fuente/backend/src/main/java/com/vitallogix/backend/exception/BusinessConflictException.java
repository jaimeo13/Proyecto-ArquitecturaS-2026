package com.vitallogix.backend.exception;

/**
 * Excepción de dominio lanzada cuando una operación viola una regla de negocio o de integridad referencial,
 * por ejemplo al intentar eliminar uno o varios productos que ya tienen ventas asociadas.
 * Es manejada por {@link GlobalExceptionHandler} devolviendo HTTP 409 (Conflict).
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public class BusinessConflictException extends RuntimeException {

    /**
     * Crea una nueva excepción de conflicto de negocio.
     *
     * @param message mensaje explicando la restricción de negocio incumplida.
     */
    public BusinessConflictException(String message) {
        super(message);
    }
}

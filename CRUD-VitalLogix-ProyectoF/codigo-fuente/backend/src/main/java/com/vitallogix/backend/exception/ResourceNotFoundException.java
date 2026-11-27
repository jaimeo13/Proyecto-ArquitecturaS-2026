package com.vitallogix.backend.exception;

/**
 * Excepción de dominio lanzada cuando un recurso solicitado no existe en la base de datos MySQL.
 * Es interceptada por {@link GlobalExceptionHandler} para devolver un código HTTP 404 (Not Found).
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crea una nueva excepción indicando el recurso que no fue encontrado.
     *
     * @param message mensaje descriptivo del recurso ausente.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

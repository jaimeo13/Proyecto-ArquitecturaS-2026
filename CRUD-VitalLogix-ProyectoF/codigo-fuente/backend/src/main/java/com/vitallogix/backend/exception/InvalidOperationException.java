package com.vitallogix.backend.exception;

/**
 * Excepción lanzada cuando los parámetros enviados en una petición CRUD son inválidos
 * o no cumplen las precondiciones de operación (por ejemplo, una lista vacía para borrado masivo).
 * Es manejada por {@link GlobalExceptionHandler} devolviendo HTTP 400 (Bad Request).
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public class InvalidOperationException extends RuntimeException {

    /**
     * Crea una nueva excepción de operación inválida.
     *
     * @param message mensaje explicando el motivo del rechazo.
     */
    public InvalidOperationException(String message) {
        super(message);
    }
}

package com.vitallogix.backend.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa una respuesta de error estructurada devuelta por la API REST de VitalLogix.
 * Encapsula la marca de tiempo, código HTTP, título del error, mensaje principal y lista detallada de mensajes.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public class ApiError {

    /** Fecha y hora en la que ocurrió el error. */
    private LocalDateTime timestamp;

    /** Código de estado HTTP asociado al error (por ejemplo, 400, 404, 409). */
    private int status;

    /** Descripción corta del tipo de error HTTP. */
    private String error;

    /** Lista de mensajes descriptivos que detallan la causa del error. */
    private List<String> messages;

    /**
     * Construye una nueva instancia de {@code ApiError} con la marca de tiempo actual.
     *
     * @param status   código de estado HTTP.
     * @param error    título o categoría del error.
     * @param messages lista de mensajes detallados de la falla.
     */
    public ApiError(int status, String error, List<String> messages) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.messages = messages;
    }

    /**
     * Obtiene la fecha y hora en la que se registró el error.
     *
     * @return marca de tiempo del error.
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Obtiene el código de estado HTTP.
     *
     * @return código de estado numérico HTTP.
     */
    public int getStatus() { return status; }

    /**
     * Obtiene el nombre o categoría del error HTTP.
     *
     * @return descripción breve del error.
     */
    public String getError() { return error; }

    /**
     * Obtiene la lista completa de mensajes de error.
     *
     * @return lista de mensajes descriptivos.
     */
    public List<String> getMessages() { return messages; }

    /**
     * Obtiene el primer mensaje de error como cadena única para conveniencia del cliente frontend.
     *
     * @return mensaje principal o cadena vacía si no hay mensajes.
     */
    public String getMessage() {
        return (messages != null && !messages.isEmpty()) ? String.join("; ", messages) : "";
    }
}

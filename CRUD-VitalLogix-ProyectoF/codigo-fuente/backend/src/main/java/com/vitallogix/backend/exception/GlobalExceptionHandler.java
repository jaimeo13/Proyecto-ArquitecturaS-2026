package com.vitallogix.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para todos los controladores REST de VitalLogix.
 * Intercepta excepciones de dominio, validación e integridad de datos de MySQL y las transforma
 * en respuestas JSON uniformes mediante {@link ApiError}.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de recurso no encontrado devolviendo HTTP 404.
     *
     * @param ex excepción lanzada cuando no existe el registro solicitado.
     * @return respuesta HTTP 404 con detalle en formato {@link ApiError}.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones por duplicidad de registros únicos devolviendo HTTP 409.
     *
     * @param ex excepción de duplicidad.
     * @return respuesta HTTP 409 con detalle en formato {@link ApiError}.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Maneja conflictos de negocio o de integridad referencial devolviendo HTTP 409.
     *
     * @param ex excepción de conflicto de negocio.
     * @return respuesta HTTP 409 con detalle en formato {@link ApiError}.
     */
    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ApiError> handleBusinessConflict(BusinessConflictException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Business Conflict",
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Maneja operaciones inválidas o parámetros incorrectos devolviendo HTTP 400.
     *
     * @param ex excepción de operación inválida.
     * @return respuesta HTTP 400 con detalle en formato {@link ApiError}.
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiError> handleInvalidOperation(InvalidOperationException ex) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores de validación Bean Validation ({@code @Valid}) devolviendo HTTP 400.
     *
     * @param ex excepción producida por campos inválidos en el cuerpo de la solicitud.
     * @return respuesta HTTP 400 con el listado de errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                messages
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja violaciones de integridad de datos lanzadas por MySQL/JPA devolviendo HTTP 409.
     *
     * @param ex excepción de integridad referencial o clave única.
     * @return respuesta HTTP 409 con mensaje amigable para el usuario.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Data Integrity Violation",
                List.of("No se pudo completar la operación porque viola una restricción de integridad en la base de datos.")
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones {@link ResponseStatusException} preservando el código HTTP original.
     *
     * @param ex excepción con estado HTTP explícito.
     * @return respuesta con el estado HTTP y mensaje especificado.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
        int statusCode = ex.getStatusCode().value();
        String reason = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        ApiError error = new ApiError(
                statusCode,
                ex.getStatusCode().toString(),
                List.of(reason)
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }
}

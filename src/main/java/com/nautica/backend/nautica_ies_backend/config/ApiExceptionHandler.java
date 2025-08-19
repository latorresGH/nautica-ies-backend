// src/main/java/com/nautica/backend/nautica_ies_backend/config/ApiExceptionHandler.java
package com.nautica.backend.nautica_ies_backend.config;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

/**
 * Manejador global de excepciones para la API REST.
 * <p>
 * Esta clase intercepta las excepciones lanzadas en los controladores y servicios,
 * devolviendo respuestas JSON estructuradas con información útil para el cliente.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Método base para construir la estructura común de los errores.
     *
     * @param status  Código HTTP.
     * @param message Mensaje de error.
     * @param path    Ruta del request que generó el error.
     * @return Mapa con detalles del error.
     */
    private Map<String, Object> baseBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        if (message != null) body.put("message", message);
        if (path != null) body.put("path", path);
        return body;
    }

    /**
     * Maneja la excepción {@link ResourceNotFoundException}.
     *
     * @param ex       Excepción lanzada.
     * @param exchange Contexto de la petición web.
     * @return Respuesta con código 404 y detalles del error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Map<String, Object> body = baseBody(status, ex.getMessage(), exchange.getRequest().getPath().value());
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Maneja la excepción {@link IllegalArgumentException}.
     *
     * @param ex       Excepción lanzada.
     * @param exchange Contexto de la petición web.
     * @return Respuesta con código 400 y detalles del error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = baseBody(status, ex.getMessage(), exchange.getRequest().getPath().value());
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Maneja errores por violaciones a restricciones de base de datos
     * como claves duplicadas (DNI, correo, etc.).
     *
     * @param ex       Excepción lanzada.
     * @param exchange Contexto de la petición web.
     * @return Respuesta con código 409 (conflicto) y mensaje personalizado.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(DataIntegrityViolationException ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.CONFLICT;
        Map<String, Object> body = baseBody(status, "Violación de restricción (¿duplicado de DNI/correo?)", exchange.getRequest().getPath().value());
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Maneja errores de validación en el cuerpo del request
     * (anotaciones como @NotBlank, @Email, etc.).
     *
     * @param ex       Excepción lanzada por Spring.
     * @param exchange Contexto de la petición web.
     * @return Respuesta con código 400 y lista de campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBeanValidation(MethodArgumentNotValidException ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = baseBody(status, "Validación fallida", exchange.getRequest().getPath().value());
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();
        body.put("details", errors);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Maneja errores de validación a nivel de parámetros (por ejemplo, en @RequestParam o @PathVariable).
     *
     * @param ex       Excepción lanzada.
     * @param exchange Contexto de la petición web.
     * @return Respuesta con código 400 y detalles de las violaciones.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = baseBody(status, "Parámetros inválidos", exchange.getRequest().getPath().value());
        List<Map<String, String>> errors = ex.getConstraintViolations().stream()
                .map(v -> Map.of("param", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList();
        body.put("details", errors);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Maneja cualquier otra excepción no contemplada específicamente.
     *
     * @param ex       Excepción genérica.
     * @param exchange Contexto de la petición web.
     * @return Respuesta con código 500 y mensaje genérico de error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> body = baseBody(status, "Ocurrió un error inesperado", exchange.getRequest().getPath().value());
        return ResponseEntity.status(status).body(body);
    }
}

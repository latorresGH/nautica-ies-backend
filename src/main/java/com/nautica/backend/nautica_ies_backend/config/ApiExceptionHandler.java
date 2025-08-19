// src/main/java/com/nautica/backend/nautica_ies_backend/config/ApiExceptionHandler.java
package com.nautica.backend.nautica_ies_backend.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;

import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    private Map<String, Object> baseBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        if (message != null) body.put("message", message);
        if (path != null) body.put("path", path);
        return body;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Map<String, Object> body = baseBody(status, ex.getMessage(), exchange.getRequest().getPath().value());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = baseBody(status, ex.getMessage(), exchange.getRequest().getPath().value());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(DataIntegrityViolationException ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.CONFLICT;
        Map<String, Object> body = baseBody(status, "Violación de restricción (¿duplicado de DNI/correo?)", exchange.getRequest().getPath().value());
        return ResponseEntity.status(status).body(body);
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, org.springframework.web.server.ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> body = baseBody(status, "Ocurrió un error inesperado", exchange.getRequest().getPath().value());
        return ResponseEntity.status(status).body(body);
    }
}

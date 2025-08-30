package com.nautica.backend.nautica_ies_backend.config;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex, ServletWebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "timestamp", OffsetDateTime.now().toString(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage(),
                        "path", req.getRequest().getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex, ServletWebRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", OffsetDateTime.now().toString(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage(),
                        "path", req.getRequest().getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, ServletWebRequest req) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", OffsetDateTime.now().toString(),
                        "status", 400,
                        "error", "Validation Failed",
                        "message", "Datos inválidos",
                        "errors", errors,
                        "path", req.getRequest().getRequestURI()));
    }

    // CAJA DE SEGURIDAD: captura todo lo no mapeado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(Exception ex, ServletWebRequest req) {
        ex.printStackTrace(); // imprime stacktrace completo en consola
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "timestamp", OffsetDateTime.now().toString(),
                        "status", 500,
                        "error", ex.getClass().getSimpleName(),
                        "message", ex.getMessage(),
                        "path", req.getRequest().getRequestURI()
                // Si querés incluir el stacktrace en la respuesta (SOLO DEV), podés agregarlo:
                // ,"trace", Arrays.toString(ex.getStackTrace())
                ));
    }
}

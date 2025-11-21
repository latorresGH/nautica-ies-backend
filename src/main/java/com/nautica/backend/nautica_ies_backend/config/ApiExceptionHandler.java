// src/main/java/com/nautica/backend/nautica_ies_backend/config/ApiExceptionHandler.java
package com.nautica.backend.nautica_ies_backend.config;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  private Map<String, Object> body(HttpServletRequest req, HttpStatus status, String message) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("timestamp", Instant.now().toString());
    map.put("status", status.value());
    map.put("error", status.getReasonPhrase());
    map.put("message", message);
    map.put("path", req.getRequestURI());
    map.put("method", req.getMethod());
    return map;
  }

  @ExceptionHandler(com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(
      com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException ex,
      jakarta.servlet.http.HttpServletRequest req) {
    var body = new java.util.LinkedHashMap<String, Object>();
    body.put("timestamp", java.time.Instant.now());
    body.put("status", 404);
    body.put("error", "Not Found");
    body.put("message", ex.getMessage());
    body.put("path", req.getRequestURI());
    return ResponseEntity.status(404).body(body);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrity(
      DataIntegrityViolationException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(body(req, HttpStatus.CONFLICT, "Violación de integridad de datos"));
  }

  @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
  public ResponseEntity<Map<String, Object>> handleValidation(Exception ex, HttpServletRequest req) {
    return ResponseEntity.badRequest()
        .body(body(req, HttpStatus.BAD_REQUEST, "Datos inválidos"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(
      Exception ex,
      jakarta.servlet.http.HttpServletRequest req) {

    // Si en realidad es una ResponseStatusException, delegamos
    if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
      return handleResponseStatus(rse, req);
    }

    var body = new java.util.LinkedHashMap<String, Object>();
    body.put("timestamp", java.time.Instant.now());
    body.put("status", 500);
    body.put("error", "Internal Server Error");
    body.put("message", "Ocurrió un error inesperado");
    body.put("path", req.getRequestURI());

    return ResponseEntity.status(500).body(body);
  }

  @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatus(
      org.springframework.web.server.ResponseStatusException ex,
      jakarta.servlet.http.HttpServletRequest req) {

    var status = ex.getStatusCode(); // HttpStatusCode
    var body = new java.util.LinkedHashMap<String, Object>();
    body.put("timestamp", java.time.Instant.now());
    body.put("status", status.value());
    // getReasonPhrase() no existe en HttpStatusCode -> usamos toString() o
    // resolvemos desde HttpStatus
    body.put("error", status.toString()); // ej. "409 CONFLICT"
    body.put("message", ex.getReason()); // <- acá va tu mensaje custom
    body.put("path", req.getRequestURI());

    return ResponseEntity.status(status).body(body);
  }

}

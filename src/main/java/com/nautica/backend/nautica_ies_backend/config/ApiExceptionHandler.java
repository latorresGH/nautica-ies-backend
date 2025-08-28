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

  private Map<String,Object> body(HttpServletRequest req, HttpStatus status, String message) {
    Map<String,Object> map = new LinkedHashMap<>();
    map.put("timestamp", Instant.now().toString());
    map.put("status", status.value());
    map.put("error", status.getReasonPhrase());
    map.put("message", message);
    map.put("path", req.getRequestURI());
    map.put("method", req.getMethod());
    return map;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String,Object>> handleDataIntegrity(
      DataIntegrityViolationException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(body(req, HttpStatus.CONFLICT, "Violación de integridad de datos"));
  }

  @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
  public ResponseEntity<Map<String,Object>> handleValidation(Exception ex, HttpServletRequest req) {
    return ResponseEntity.badRequest()
        .body(body(req, HttpStatus.BAD_REQUEST, "Datos inválidos"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(body(req, HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado"));
  }
}

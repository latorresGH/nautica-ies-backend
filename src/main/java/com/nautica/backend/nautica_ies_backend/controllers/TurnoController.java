package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nautica.backend.nautica_ies_backend.models.Turno;
import com.nautica.backend.nautica_ies_backend.services.TurnoService;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

  private final TurnoService service;

  public TurnoController(TurnoService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<Turno> crear(@RequestBody Turno turno) {
    var t = service.crear(turno);
    return ResponseEntity.status(201).body(t);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Turno> obtener(@PathVariable Long id) {
    return ResponseEntity.ok(service.obtener(id));
  }

  @GetMapping("/cliente/{idCliente}")
  public ResponseEntity<List<Turno>> porCliente(@PathVariable Long idCliente) {
    return ResponseEntity.ok(service.listarPorCliente(idCliente));
  }

  @GetMapping("/operario/{idOperario}")
  public ResponseEntity<List<Turno>> porOperarioEnRango(@PathVariable Long idOperario,
      @RequestParam LocalDate from,
      @RequestParam LocalDate to) {
    return ResponseEntity.ok(service.listarPorOperarioEnRango(idOperario, from, to));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Turno> actualizar(@PathVariable Long id, @RequestBody Turno turno) {
    return ResponseEntity.ok(service.actualizar(id, turno));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  
}

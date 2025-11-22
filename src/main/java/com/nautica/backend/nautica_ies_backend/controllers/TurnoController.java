// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/TurnoController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.models.Turno;
import com.nautica.backend.nautica_ies_backend.services.TurnoService;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cliente.Turnos.TurnoCliente;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cliente.Turnos.TurnoSolicitudRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    /* ==========================================
     * ===========   CRUD GENERAL   =============
     * (admin / operario, entidad Turno)
     * ========================================== */

    @PostMapping
    public ResponseEntity<Turno> crear(@RequestBody @Valid Turno turno,
                                       UriComponentsBuilder uriBuilder) {
        Turno creado = service.crear(turno);
        var location = uriBuilder.path("/api/turnos/{id}")
                .buildAndExpand(creado.getIdTurno())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

 @GetMapping(params = "fecha")
public ResponseEntity<List<TurnoCliente>> listarPorFecha(
        @RequestParam("fecha")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
    return ResponseEntity.ok(service.listarPorFechaDTO(fecha));
}

    /* ==========================================
     * ========  ENDPOINTS PARA CLIENTE  ========
     * (DTO TurnoCliente y TurnoSolicitudRequest)
     * ========================================== */

    // 🔹 Lista de turnos del cliente (DTO para front cliente)
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<TurnoCliente>> listarTurnosCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarTurnosClienteDTO(clienteId));
    }

    // 🔹 Solicitar turno desde el cliente (usa TurnoSolicitudRequest)
    @PostMapping("/cliente")
    public ResponseEntity<TurnoCliente> solicitarTurno(
            @RequestBody @Valid TurnoSolicitudRequest req,
            UriComponentsBuilder uriBuilder) {

        TurnoCliente dto = service.solicitarTurno(req);

        var location = uriBuilder.path("/api/turnos/{id}")
                .buildAndExpand(dto.id())
                .toUri();

        return ResponseEntity.created(location).body(dto);
    }

    // 🔹 Cancelar turno desde el cliente
    @PostMapping("/cliente/{idTurno}/cancelar")
    public ResponseEntity<TurnoCliente> cancelarTurno(@PathVariable Long idTurno) {
        TurnoCliente dto = service.cancelarTurno(idTurno);
        return ResponseEntity.ok(dto);
    }
}

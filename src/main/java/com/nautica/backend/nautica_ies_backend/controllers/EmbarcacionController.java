// EmbarcacionController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.UsuarioEmbarcacion;
import com.nautica.backend.nautica_ies_backend.models.enums.RolEnEmbarcacion;
import com.nautica.backend.nautica_ies_backend.services.EmbarcacionService;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/embarcaciones")
public class EmbarcacionController {

    private final EmbarcacionService service;

    public EmbarcacionController(EmbarcacionService service) {
        this.service = service;
    }

    // Listado paginado
    @GetMapping
    public ResponseEntity<Page<Embarcacion>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idEmbarcacion,asc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    // Obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<Embarcacion> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // Buscar por matrícula
    @GetMapping("/by-matricula")
    public ResponseEntity<Embarcacion> porMatricula(@RequestParam("valor") String numMatricula) {
        return ResponseEntity.ok(service.buscarPorMatricula(numMatricula));
    }

    // Crear
    @PostMapping
    public ResponseEntity<Embarcacion> crear(@RequestBody @Valid Embarcacion emb, UriComponentsBuilder uriBuilder) {
        Embarcacion creada = service.crear(emb);
        var location = uriBuilder.path("/api/embarcaciones/{id}")
                .buildAndExpand(creada.getIdEmbarcacion()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Embarcacion> actualizar(@PathVariable Long id, @RequestBody @Valid Embarcacion emb) {
        return ResponseEntity.ok(service.actualizar(id, emb));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --------- Asignaciones N:M (SIN DTOs) ---------

    // Listar relaciones usuario-embarcación (devuelve UsuarioEmbarcacion
    // directamente)
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<List<UsuarioEmbarcacion>> usuarios(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarUsuarios(id));
    }

    // Asignar usuario a embarcación usando query params
    // Ejemplo: POST
    // /api/embarcaciones/10/usuarios?usuarioId=5&rol=PROPIETARIO&desde=2025-08-28
    @PostMapping("/{id}/usuarios")
    public ResponseEntity<Void> asignarUsuario(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestParam RolEnEmbarcacion rol,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        service.asignarUsuario(id, usuarioId, rol, desde, hasta);
        return ResponseEntity.noContent().build();
    }

    // Desasignar usuario
    @DeleteMapping("/{id}/usuarios/{idUsuario}")
    public ResponseEntity<Void> desasignarUsuario(@PathVariable Long id, @PathVariable Long idUsuario) {
        service.desasignarUsuario(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}

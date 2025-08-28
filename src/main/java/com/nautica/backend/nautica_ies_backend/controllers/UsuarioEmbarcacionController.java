// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/UsuarioEmbarcacionController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.models.UsuarioEmbarcacion;
import com.nautica.backend.nautica_ies_backend.models.enums.RolEnEmbarcacion;
import com.nautica.backend.nautica_ies_backend.services.UsuarioEmbarcacionService;

@RestController
@RequestMapping("/api/usuario-embarcaciones")
public class UsuarioEmbarcacionController {

    private final UsuarioEmbarcacionService service;

    public UsuarioEmbarcacionController(UsuarioEmbarcacionService service) {
        this.service = service;
    }

    // Listado paginado
    @GetMapping
    public ResponseEntity<Page<UsuarioEmbarcacion>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    // Obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEmbarcacion> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // Listar por usuario
    @GetMapping("/by-usuario")
    public ResponseEntity<List<UsuarioEmbarcacion>> porUsuario(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    // Listar por embarcación
    @GetMapping("/by-embarcacion")
    public ResponseEntity<List<UsuarioEmbarcacion>> porEmbarcacion(@RequestParam Long embarcacionId) {
        return ResponseEntity.ok(service.listarPorEmbarcacion(embarcacionId));
    }

    // Crear (sin DTOs): recibe todo por query params
    // Ej: POST /api/usuario-embarcaciones?usuarioId=5&embarcacionId=10&rol=PROPIETARIO&desde=2025-08-28
    @PostMapping
    public ResponseEntity<UsuarioEmbarcacion> crear(
            @RequestParam Long usuarioId,
            @RequestParam Long embarcacionId,
            @RequestParam RolEnEmbarcacion rol,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            UriComponentsBuilder uriBuilder
    ) {
        UsuarioEmbarcacion creado = service.crear(usuarioId, embarcacionId, rol, desde, hasta);
        var location = uriBuilder.path("/api/usuario-embarcaciones/{id}")
                                 .buildAndExpand(creado.getId())
                                 .toUri();
        return ResponseEntity.created(location).body(creado); // 201 + Location
    }

    // Actualizar rol/fechas
    // Ej: PUT /api/usuario-embarcaciones/7?rol=AUTORIZADO&hasta=2025-10-01
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEmbarcacion> actualizar(
            @PathVariable Long id,
            @RequestParam(required = false) RolEnEmbarcacion rol,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta
    ) {
        return ResponseEntity.ok(service.actualizar(id, rol, desde, hasta));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

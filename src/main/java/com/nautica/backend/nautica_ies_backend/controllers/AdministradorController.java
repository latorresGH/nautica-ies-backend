package com.nautica.backend.nautica_ies_backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.models.Administrador;
import com.nautica.backend.nautica_ies_backend.services.AdministradorService;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    private final AdministradorService service;

    public AdministradorController(AdministradorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Administrador>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/codigo/{codigoAdmin}")
    public ResponseEntity<Administrador> getByCodigo(@PathVariable String codigoAdmin) {
        return ResponseEntity.ok(service.findByCodigoAdmin(codigoAdmin));
    }

    @PostMapping
    public ResponseEntity<Administrador> create(@RequestBody Administrador admin) {
        Administrador created = service.create(admin);
        return ResponseEntity
                .created(URI.create("/api/administradores/" + created.getIdUsuario()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrador> update(@PathVariable Long id, @RequestBody Administrador admin) {
        return ResponseEntity.ok(service.update(id, admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

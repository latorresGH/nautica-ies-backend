// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/OperarioController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.models.Operario;
import com.nautica.backend.nautica_ies_backend.services.OperarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/operarios")
public class OperarioController {

    private final OperarioService service;

    public OperarioController(OperarioService service) {
        this.service = service;
    }

    // Listado paginado (id heredado es idUsuario)
    @GetMapping
    public ResponseEntity<Page<Operario>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idUsuario,asc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    // Obtener por id (idUsuario del padre)
    @GetMapping("/{id}")
    public ResponseEntity<Operario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // Buscar por legajo
    @GetMapping("/by-legajo")
    public ResponseEntity<Operario> porLegajo(@RequestParam("valor") String legajo) {
        return ResponseEntity.ok(service.buscarPorLegajo(legajo));
    }

    // Crear
    @PostMapping
    public ResponseEntity<Operario> crear(@RequestBody @Valid Operario operario,
                                          UriComponentsBuilder uriBuilder) {
        Operario creado = service.crear(operario);
        var location = uriBuilder.path("/api/operarios/{id}")
                                 .buildAndExpand(creado.getIdUsuario())
                                 .toUri();
        return ResponseEntity.created(location).body(creado); // 201 + Location
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Operario> actualizar(@PathVariable Long id,
                                               @RequestBody @Valid Operario operario) {
        return ResponseEntity.ok(service.actualizar(id, operario));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}

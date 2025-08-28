// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/TareaController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.models.Tarea;
import com.nautica.backend.nautica_ies_backend.services.TareaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService service;

    public TareaController(TareaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<Tarea>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idTarea,asc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/by-numero")
    public ResponseEntity<Tarea> porNumero(@RequestParam("valor") Integer numero) {
        return ResponseEntity.ok(service.buscarPorNumero(numero));
    }

    @GetMapping("/by-operario")
    public ResponseEntity<List<Tarea>> porOperario(@RequestParam("idOperario") Long idOperario) {
        return ResponseEntity.ok(service.listarPorOperario(idOperario));
    }

    @PostMapping
    public ResponseEntity<Tarea> crear(@RequestBody @Valid Tarea tarea, UriComponentsBuilder uriBuilder) {
        Tarea creada = service.crear(tarea);
        var location = uriBuilder.path("/api/tareas/{id}")
                                 .buildAndExpand(creada.getIdTarea())
                                 .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizar(@PathVariable Long id, @RequestBody @Valid Tarea tarea) {
        return ResponseEntity.ok(service.actualizar(id, tarea));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

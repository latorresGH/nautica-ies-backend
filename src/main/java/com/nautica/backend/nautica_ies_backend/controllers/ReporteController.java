package com.nautica.backend.nautica_ies_backend.controllers;

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

import com.nautica.backend.nautica_ies_backend.models.Reporte;
import com.nautica.backend.nautica_ies_backend.services.ReporteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<Reporte>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idReporte,asc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return ResponseEntity.ok(service.listar(page, size, Sort.by(dir, s[0])));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Reporte> crear(@RequestBody @Valid Reporte reporte, UriComponentsBuilder uriBuilder) {
        Reporte creado = service.crear(reporte);
        var location = uriBuilder.path("/api/reportes/{id}").buildAndExpand(creado.getIdReporte()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reporte> actualizar(@PathVariable Long id, @RequestBody @Valid Reporte reporte) {
        return ResponseEntity.ok(service.actualizar(id, reporte));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

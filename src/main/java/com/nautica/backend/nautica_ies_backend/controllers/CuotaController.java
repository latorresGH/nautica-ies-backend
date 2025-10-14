package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.CuotaResumen;
import com.nautica.backend.nautica_ies_backend.models.Cuota;
import com.nautica.backend.nautica_ies_backend.services.CuotaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cuotas")
public class CuotaController {

    private final CuotaService service;

    public CuotaController(CuotaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<Cuota>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idCuota,asc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuota> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Cuota> crear(@RequestBody @Valid Cuota cuota, UriComponentsBuilder uriBuilder) {
        Cuota creada = service.crear(cuota);
        var location = uriBuilder.path("/api/cuotas/{id}").buildAndExpand(creada.getIdCuota()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuota> actualizar(@PathVariable Long id, @RequestBody @Valid Cuota cuota) {
        return ResponseEntity.ok(service.actualizar(id, cuota));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // @GetMapping("/by-cliente/actual")
    // public ResponseEntity<CuotaResumen> cuotaActual(@RequestParam Long clienteId) {
    //     return ResponseEntity.ok(service.cuotaActualPorCliente(clienteId));
    // }

    // GET /api/cuotas/by-cliente/actual?clienteId=1
@GetMapping("/by-cliente/actual")
public ResponseEntity<CuotaResumen> cuotaActual(@RequestParam Long clienteId,
                                                   @RequestParam(required = false) Long embarcacionId) {
    CuotaResumen dto = (embarcacionId == null)
        ? service.cuotaActualPorCliente(clienteId)
        : service.cuotaActualPorClienteYEmbarcacion(clienteId, embarcacionId);
    return ResponseEntity.ok(dto);
}
}

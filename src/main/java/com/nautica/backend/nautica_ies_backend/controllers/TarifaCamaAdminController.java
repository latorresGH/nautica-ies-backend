// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/admin/TarifaCamaAdminController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.controllers.dto.TarifasCamas.TarifaCamaDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.TarifasCamas.TarifaCamaRequest;
import com.nautica.backend.nautica_ies_backend.controllers.dto.TarifasCamas.TarifaCamaUpdatePrecioRequest;
import com.nautica.backend.nautica_ies_backend.services.TarifaCamaService;

@RestController
@RequestMapping("/api/admin/tarifas-cama")
public class TarifaCamaAdminController {

    private final TarifaCamaService service;

    public TarifaCamaAdminController(TarifaCamaService service) {
        this.service = service;
    }

    // GET /api/admin/tarifas-cama/mes?anio=2025&mes=11
    @GetMapping("/mes")
    public List<TarifaCamaDTO> listarPorMes(
            @RequestParam int anio,
            @RequestParam int mes
    ) {
        LocalDate fecha = LocalDate.of(anio, mes, 1);
        return service.listarPorMes(fecha);
    }

    // GET /api/admin/tarifas-cama/{id}
    @GetMapping("/{id}")
    public TarifaCamaDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    // POST /api/admin/tarifas-cama
    // Crea o actualiza una tarifa para tipo+mes (upsert)
    @PostMapping
    public TarifaCamaDTO crearOActualizar(@RequestBody TarifaCamaRequest req) {
        return service.crearOActualizar(req);
    }

    // PUT /api/admin/tarifas-cama/{id}/precio
    // Útil para la pantalla donde editás el número directamente
    @PutMapping("/{id}/precio")
    public TarifaCamaDTO actualizarPrecio(
            @PathVariable Long id,
            @RequestBody TarifaCamaUpdatePrecioRequest req
    ) {
        return service.actualizarPrecio(id, req.precio());
    }

    // DELETE /api/admin/tarifas-cama/{id}  (si la querés)
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}

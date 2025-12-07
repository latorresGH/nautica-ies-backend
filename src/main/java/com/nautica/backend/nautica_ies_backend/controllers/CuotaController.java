package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.CuotaResumen;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.CuotaAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.PagoCuotasRequest;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.PagoHistorialDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cuota.ResumenCuotaMesCliente;
import com.nautica.backend.nautica_ies_backend.models.Cuota;
import com.nautica.backend.nautica_ies_backend.services.CuotaService;
import com.nautica.backend.nautica_ies_backend.services.CuotaService.DeudaCliente;




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
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
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

    // GET /api/cuotas/by-cliente/actual?clienteId=1&embarcacionId=2?
    @GetMapping("/by-cliente/actual")
    public ResponseEntity<CuotaResumen> cuotaActual(
            @RequestParam Long clienteId,
            @RequestParam(required = false) Long embarcacionId) {

        CuotaResumen dto = (embarcacionId == null)
                ? service.cuotaActualPorCliente(clienteId)
                : service.cuotaActualPorClienteYEmbarcacion(clienteId, embarcacionId);

        return (dto == null)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(dto);
    }

    // historial de cuotas del cliente
    @GetMapping("/by-cliente")
    public ResponseEntity<List<CuotaResumen>> cuotasPorCliente(@RequestParam Long clienteId) {
        return ResponseEntity.ok(service.listarCuotasCliente(clienteId));
    }

    // resumen simple de deuda
    @GetMapping("/by-cliente/deuda")
    public ResponseEntity<DeudaCliente> deudaCliente(@RequestParam Long clienteId) {
        return ResponseEntity.ok(service.resumenDeudaCliente(clienteId));
    }

    // resumen del mes actual (vista cliente)
    @GetMapping("/by-cliente/mes-actual-resumen")
    public ResponseEntity<ResumenCuotaMesCliente> resumenMesActualCliente(
            @RequestParam Long clienteId) {

        LocalDate hoy = LocalDate.now();
        var dto = service.resumenCuotaMesCliente(clienteId, hoy);

        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    /* ===========================================================
     *  NUEVO: endpoints ADMIN para registrar pagos
     * =========================================================== */

    // GET /api/cuotas/admin/{clienteId}/impagas
    /*@GetMapping("/admin/{clienteId}/impagas")
    public ResponseEntity<List<CuotaAdminDTO>> cuotasImpagasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarCuotasImpagasPorCliente(clienteId));
    }*/

    // POST /api/cuotas/admin/pagar
    @PostMapping("/admin/pagar")
    public ResponseEntity<Void> registrarPagoCuotas(@RequestBody @Valid PagoCuotasRequest body) {
        service.registrarPagoCuotas(body);
        return ResponseEntity.noContent().build();
    }

        /**
     * Listado ADMIN: cuotas impagas (pendientes + vencidas) de un cliente,
     * con info de embarcación.
     *
     * GET /api/cuotas/admin/{clienteId}/impagas
     */
    @GetMapping("/admin/{clienteId}/impagas")
    public ResponseEntity<List<CuotaAdminDTO>> cuotasImpagasAdmin(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarCuotasImpagasPorCliente(clienteId));
    }

        /**
     * Historial de pagos (ADMIN) de un cliente.
     *
     * GET /api/cuotas/admin/{clienteId}/pagos
     */
    @GetMapping("/admin/{clienteId}/pagos")
    public ResponseEntity<java.util.List<PagoHistorialDTO>> historialPagosCliente(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(service.historialPagosPorCliente(clienteId));
    }


}

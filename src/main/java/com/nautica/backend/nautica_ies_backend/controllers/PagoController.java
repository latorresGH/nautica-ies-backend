package com.nautica.backend.nautica_ies_backend.controllers;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.*;
import com.nautica.backend.nautica_ies_backend.services.CuotaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final CuotaService service;

    public PagoController(CuotaService service) {
        this.service = service;
    }

    /**
     * Crear/registrar un pago sobre una cuota existente.
     * Body: { cuotaId, medio, fecha?, monto? }
     */
    @PostMapping
    public ResponseEntity<PagoDetail> crear(@RequestBody @Valid PagoCreateRequest body) {
        return ResponseEntity.ok(service.registrarPago(body));
    }

    /**
     * Listar pagos con filtros.
     * GET /api/pagos?clienteId=&desde=YYYY-MM-DD&hasta=YYYY-MM-DD&medio=&page=&size=
     */
    @GetMapping
    public ResponseEntity<Page<PagoSummary>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String medio,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        LocalDate d = (desde == null || desde.isBlank()) ? null : LocalDate.parse(desde);
        LocalDate h = (hasta == null || hasta.isBlank()) ? null : LocalDate.parse(hasta);
        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.listarPagos(clienteId, d, h, medio, pageable));
    }

    /**
     * Traer un pago por id (idCuota) — válido si la cuota está pagada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PagoDetail> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPago(id));
    }
}

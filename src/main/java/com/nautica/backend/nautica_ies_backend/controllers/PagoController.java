/*package com.nautica.backend.nautica_ies_backend.controllers;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.PagoCreateRequest;
import com.nautica.backend.nautica_ies_backend.services.CuotaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final CuotaService service;

    public PagoController(CuotaService service) {
        this.service = service;
    }

    /**
     * Registrar pago de varias cuotas de un cliente (panel admin).
     * URL: POST /api/pagos/admin/{clienteId}/cuotas
     * Body: { cuotasIds: number[], medio: 'efectivo' | 'transferencia' | 'tarjeta' | 'debito_automatico', fecha?: 'YYYY-MM-DD' }
     *//*
    @PostMapping("/admin/{clienteId}/cuotas")
    public ResponseEntity<Void> registrarPagoCuotas(
            @PathVariable Long clienteId,
            @RequestBody @Valid PagoCreateRequest body
    ) {
        service.registrarPagoCuotas(clienteId, body);
        // no hace falta devolver body; con 204 alcanza para que el front sepa que salió bien
        return ResponseEntity.noContent().build();
    }
} */

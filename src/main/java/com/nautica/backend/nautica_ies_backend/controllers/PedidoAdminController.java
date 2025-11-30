// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/PedidoAdminController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.PedidoResumenAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.PedidoDetalleAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.ActualizarEstadoPedidoRequest;
import com.nautica.backend.nautica_ies_backend.services.PedidoAdminService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/pedidos")
@CrossOrigin(origins = "*")
public class PedidoAdminController {

    private final PedidoAdminService pedidoAdminService;

    public PedidoAdminController(PedidoAdminService pedidoAdminService) {
        this.pedidoAdminService = pedidoAdminService;
    }

    // GET /api/admin/pedidos?page=0&size=20
    @GetMapping
    public Page<PedidoResumenAdminDTO> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return pedidoAdminService.listarPedidos(pageable);
    }

    // GET /api/admin/pedidos/{id}
    @GetMapping("/{id}")
    public PedidoDetalleAdminDTO detalle(@PathVariable Long id) {
        return pedidoAdminService.obtenerPedidoDetalle(id);
    }

    // PUT /api/admin/pedidos/{id}/estado
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoDetalleAdminDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestBody ActualizarEstadoPedidoRequest req
    ) {
        var dto = pedidoAdminService.actualizarEstado(id, req.estado());
        return ResponseEntity.ok(dto);
    }
}

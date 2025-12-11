// src/main/java/com/nautica/backend/nautica_ies_backend/services/PedidoAdminService.java
package com.nautica.backend.nautica_ies_backend.services;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.PedidoDetalleAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.PedidoItemDetalleDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.PedidoResumenAdminDTO;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Pedido;
import com.nautica.backend.nautica_ies_backend.models.PedidoProducto;
import com.nautica.backend.nautica_ies_backend.models.Producto;
import com.nautica.backend.nautica_ies_backend.repository.PedidoRepository;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;

@Service
@Transactional
public class PedidoAdminService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoAdminService(PedidoRepository pedidoRepository,
                              ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }


    private boolean esEstadoEntregado(String estado) {
    if (estado == null) return false;
    estado = estado.toLowerCase();
    return
            "entregado_y_pagado".equals(estado) ||
            "entregado_sin_pagar".equals(estado);
}

    /* ===========================================================
     *   1) LISTAR PEDIDOS (resumen para la tabla del admin)
     *      → PAGINADO
     * =========================================================== */
    @Transactional(readOnly = true)
    public Page<PedidoResumenAdminDTO> listarPedidos(Pageable pageable) {
        return pedidoRepository
                .findAllByOrderByFechaPedidoDesc(pageable)
                .map(this::toResumenDTO);
    }

    /* ===========================================================
     *   2) OBTENER DETALLE COMPLETO DEL PEDIDO
     * =========================================================== */
    @Transactional(readOnly = true)
    public PedidoDetalleAdminDTO obtenerPedidoDetalle(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        Cliente cliente = pedido.getCliente();

        return new PedidoDetalleAdminDTO(
                pedido.getId(),
                pedido.getNumeroPedido(),
                cliente != null ? cliente.getIdUsuario() : null,
                cliente != null ? (cliente.getNombre() + " " + cliente.getApellido()) : null,
                pedido.getFechaPedido(),
                pedido.getEstado(),
                pedido.getPrecioTotal(),
                pedido.getItems().stream()
                        .map(this::toItemDTO)
                        .collect(Collectors.toList())
        );
    }

    /* ===========================================================
     *   3) ACTUALIZAR ESTADO DEL PEDIDO
     *      → ahora maneja STOCK al cancelar
     * =========================================================== */
    public PedidoDetalleAdminDTO actualizarEstado(Long pedidoId, String nuevoEstadoRaw) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        String estadoAnterior = Optional.ofNullable(pedido.getEstado())
                .orElse("pendiente")
                .toLowerCase();

        String nuevoEstado = Optional.ofNullable(nuevoEstadoRaw)
                .orElse("pendiente")
                .toLowerCase();

        // Si no hay cambio real, devolvemos el detalle actual
        if (estadoAnterior.equals(nuevoEstado)) {
            return obtenerPedidoDetalle(pedidoId);
        }

        // Actualizar stock según el cambio de estado
        actualizarStockPorCambioEstado(pedido, estadoAnterior, nuevoEstado);

        // Actualizar estado
        pedido.setEstado(nuevoEstado);

        // No hace falta llamar a save explícito si la entidad está manejada y hay @Transactional,
        // pero lo podemos dejar para mayor claridad:
        pedidoRepository.save(pedido);

        // devolvemos el detalle ya actualizado
        return obtenerPedidoDetalle(pedidoId);
    }

    /**
     * REGLA NEGOCIO (según lo que me dijiste):
     * - El pedido se entrega en persona y se marca "entregado" en ese momento.
     * - Si pasa de (pendiente / preparando / enviado) → cancelado  => devolvemos stock.
     * - Si pasa de entregado → cancelado => NO devolvemos stock (ya lo tiene el cliente).
     * - Si pasa de cancelado → otro estado => por ahora NO re-descontamos stock (se podría
     *   agregar lógica extra si querés reactivar pedidos).
     */
    private void actualizarStockPorCambioEstado(Pedido pedido, String estadoAnterior, String nuevoEstado) {
    if (pedido.getItems() == null || pedido.getItems().isEmpty()) {
        return;
    }

    // Normalizamos por las dudas
    estadoAnterior = Optional.ofNullable(estadoAnterior).orElse("pendiente").toLowerCase();
    nuevoEstado   = Optional.ofNullable(nuevoEstado).orElse("pendiente").toLowerCase();

    // De NO cancelado → cancelado
    if (!"cancelado".equals(estadoAnterior) && "cancelado".equals(nuevoEstado)) {

        // Si antes ya estaba entregado (cualquiera de las variantes), no tocamos stock
        if (esEstadoEntregado(estadoAnterior)) {
            return;
        }

        // Antes NO era entregado → devolución de stock
        for (PedidoProducto item : pedido.getItems()) {
            Producto producto = item.getProducto();
            if (producto == null) continue;

            int stockActual = Optional.ofNullable(producto.getStock()).orElse(0);
            int cantidad = Optional.ofNullable(item.getCantidad()).orElse(0);

            producto.setStock(stockActual + cantidad);
        }
    }

    // Si algún día permitimos reactivar pedidos cancelados, acá va la lógica inversa
    // if ("cancelado".equals(estadoAnterior) && !"cancelado".equals(nuevoEstado)) { ... }
}


    /* ===========================================================
     *   4) MAPPERS
     * =========================================================== */

    private PedidoResumenAdminDTO toResumenDTO(Pedido p) {
        Cliente c = p.getCliente();
        String nombreCliente = (c != null)
                ? (c.getNombre() + " " + c.getApellido()).trim()
                : null;

        return new PedidoResumenAdminDTO(
                p.getId(),
                p.getNumeroPedido(),
                c != null ? c.getIdUsuario() : null,
                nombreCliente,
                p.getFechaPedido(),
                p.getEstado(),
                p.getPrecioTotal()
        );
    }

    private PedidoItemDetalleDTO toItemDTO(PedidoProducto item) {
        return new PedidoItemDetalleDTO(
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()
        );
    }
}

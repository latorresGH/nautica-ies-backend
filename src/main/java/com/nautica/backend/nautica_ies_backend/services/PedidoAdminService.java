// src/main/java/com/nautica/backend/nautica_ies_backend/services/PedidoAdminService.java
package com.nautica.backend.nautica_ies_backend.services;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.PedidoDetalleAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.PedidoItemDetalleDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.PedidoResumenAdminDTO;

import com.nautica.backend.nautica_ies_backend.models.Pedido;
import com.nautica.backend.nautica_ies_backend.models.PedidoProducto;
import com.nautica.backend.nautica_ies_backend.models.Cliente;

import com.nautica.backend.nautica_ies_backend.repository.PedidoRepository;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;

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
     *      → devolvemos el DETALLE actualizado
     * =========================================================== */
    public PedidoDetalleAdminDTO actualizarEstado(Long pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);

        // devolvemos el detalle ya actualizado
        return obtenerPedidoDetalle(pedidoId);
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

// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/Pedido/PedidoDetalleAdminDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoDetalleAdminDTO(
        Long idPedido,
        Integer numeroPedido,
        Long idCliente,
        String nombreCliente,
        LocalDate fechaPedido,
        String estado,
        BigDecimal total,
        List<PedidoItemDetalleDTO> items
) {}

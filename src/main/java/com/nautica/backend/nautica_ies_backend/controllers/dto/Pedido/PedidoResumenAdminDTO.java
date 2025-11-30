// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/Pedido/PedidoResumenAdminDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoResumenAdminDTO(
        Long idPedido,
        Integer numeroPedido,
        Long idCliente,
        String nombreCliente,
        LocalDate fechaPedido,
        String estado,
        BigDecimal total
) {}

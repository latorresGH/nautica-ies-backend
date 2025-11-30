// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/Pedido/PedidoItemDetalleDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido;

import java.math.BigDecimal;

public record PedidoItemDetalleDTO(
        Long idProducto,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}

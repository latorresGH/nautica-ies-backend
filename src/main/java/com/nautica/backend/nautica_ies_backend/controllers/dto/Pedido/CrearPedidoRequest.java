package com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido;

import java.util.List;

public record CrearPedidoRequest(
        Long idCliente,
        List<ItemPedidoRequest> items,
        String observaciones
) {
    public record ItemPedidoRequest(
            Long idProducto,
            Integer cantidad
    ) {}
}

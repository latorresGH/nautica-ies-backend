package com.nautica.backend.nautica_ies_backend.controllers.dto.Producto;

import java.math.BigDecimal;

public record ProductoCreateUpdateRequest(
        Integer numeroArticulo,
        String nombre,
        BigDecimal precioUnitario,
        String categoria,
        String descripcion,
        String codigoAlmacenamiento,
        String estado,   // "disponible", "no_disponible", etc.
        Integer stock
) {}

package com.nautica.backend.nautica_ies_backend.controllers.dto.Producto;

import java.math.BigDecimal;

public record ProductoAdminDTO(
        Long id,
        Integer numeroArticulo,
        String nombre,
        BigDecimal precioUnitario,
        String categoria,
        String descripcion,
        String codigoAlmacenamiento,
        String estado,
        Integer stock,
        String imagenUrl
) {}

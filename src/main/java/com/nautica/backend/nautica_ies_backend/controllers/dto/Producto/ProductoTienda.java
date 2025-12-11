
// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/Producto/ProductoTiendaDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Producto;

import java.math.BigDecimal;

public record ProductoTienda(
        Long id,
        Integer numeroArticulo,
        String nombre,
        BigDecimal precioUnitario,
        String categoria,
        String descripcion,
        Integer stock,
        String imagenUrl
) {}


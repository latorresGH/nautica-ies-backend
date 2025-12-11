// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/Cuota/ResumenCuotaMesCliente.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Cuota;

public record ResumenCuotaMesCliente(
    java.time.LocalDate numeroMes,          // ej: 2025-12-01
    String periodo,                         // ej: "2025-12"
    java.math.BigDecimal total,             // TOTAL ACTUAL (con recargo aplicado hoy)
    java.math.BigDecimal totalBase,         // TOTAL ORIGINAL (sin recargos)
    Integer porcentajeRecargo,              // 0, 10 o 20
    java.util.List<DetalleCuotaEmbarcacion> detalles
) {}

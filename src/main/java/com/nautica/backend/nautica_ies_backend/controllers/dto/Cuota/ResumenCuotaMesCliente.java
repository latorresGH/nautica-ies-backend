package com.nautica.backend.nautica_ies_backend.controllers.dto.Cuota;

public record ResumenCuotaMesCliente(
    java.time.LocalDate numeroMes,
    String periodo,
    java.math.BigDecimal total,
    java.util.List<DetalleCuotaEmbarcacion> detalles
) {}
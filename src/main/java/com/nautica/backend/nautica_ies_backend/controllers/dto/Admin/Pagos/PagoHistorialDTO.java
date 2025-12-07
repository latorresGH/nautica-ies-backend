package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoHistorialDTO(
        Long idCuota,
        String periodo,
        LocalDate numeroMes,
        LocalDate fechaPago,
        BigDecimal monto,
        Long idEmbarcacion,
        String nombreEmbarcacion,
        String matricula,
        String formaPago
) {}

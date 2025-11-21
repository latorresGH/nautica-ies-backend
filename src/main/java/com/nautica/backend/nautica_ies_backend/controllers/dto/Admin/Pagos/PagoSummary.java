package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoSummary(
        Long id,                 // idCuota
        Long clienteId,
        String clienteNombre,
        Long embarcacionId,
        LocalDate numeroMes,     // mes al que corresponde la cuota
        LocalDate fechaPago,
        BigDecimal monto,
        String medio             // formaPago en texto
) {}

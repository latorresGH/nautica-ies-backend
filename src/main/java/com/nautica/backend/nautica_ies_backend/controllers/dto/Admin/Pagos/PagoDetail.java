package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoDetail(
        Long id,                 // idCuota
        Integer numeroPago,
        Long clienteId,
        String clienteNombre,
        Long embarcacionId,
        LocalDate numeroMes,
        LocalDate fechaPago,
        BigDecimal monto,
        String medio,            // formaPago
        String estado            // "pagada"
) {}

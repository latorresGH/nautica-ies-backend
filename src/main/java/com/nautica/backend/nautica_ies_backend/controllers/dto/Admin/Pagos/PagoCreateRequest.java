package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Registrar pago sobre una cuota existente.
 * Elegimos operar por cuotaId para ser inequívocos.
 */
public record PagoCreateRequest(
        @NotNull Long cuotaId,
        @NotBlank String medio,          // "efectivo" | "transferencia" | "tarjeta" | "debito_automatico"
        LocalDate fecha,                 // opcional, default = hoy
        @Positive BigDecimal monto       // opcional, si se envía actualiza el monto de la cuota
) {}

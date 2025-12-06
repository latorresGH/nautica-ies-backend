// controllers/dto/Admin/Pagos/PagoCreateRequest.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import com.nautica.backend.nautica_ies_backend.models.enums.FormaPago;

/**
 * Registrar pago de VARIAS cuotas de un mismo cliente.
 * El monto se toma de la cuota; acá solo elegís cuotas + medio + fecha.
 */
public record PagoCreateRequest(
        @NotEmpty List<Long> cuotasIds,  // IDs de cuotas seleccionadas
        @NotNull FormaPago medio,       // TRANSFERENCIA, EFECTIVO, etc.
        LocalDate fecha                 // opcional, default = hoy
) {}

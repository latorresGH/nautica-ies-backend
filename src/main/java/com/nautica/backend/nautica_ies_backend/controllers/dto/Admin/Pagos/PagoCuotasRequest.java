package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PagoCuotasRequest(
        @NotNull Long clienteId,
        @NotEmpty List<Long> cuotasIds,
        @NotBlank String medio,   // "efectivo", "transferencia", etc.
        LocalDate fecha          // opcional, default = hoy
) {}

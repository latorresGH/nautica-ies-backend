package com.nautica.backend.nautica_ies_backend.controllers.dto.Cuota;

import com.nautica.backend.nautica_ies_backend.models.enums.TipoCama;

public record DetalleCuotaEmbarcacion(
    Long embarcacionId,
    String nombreEmbarcacion,
    TipoCama tipoCama,
    java.math.BigDecimal monto
) {}


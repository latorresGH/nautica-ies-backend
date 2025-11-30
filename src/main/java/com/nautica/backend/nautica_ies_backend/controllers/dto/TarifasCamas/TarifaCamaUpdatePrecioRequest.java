package com.nautica.backend.nautica_ies_backend.controllers.dto.TarifasCamas;

import java.math.BigDecimal;

public record TarifaCamaUpdatePrecioRequest(
        BigDecimal precio
) {}

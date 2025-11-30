package com.nautica.backend.nautica_ies_backend.controllers.dto.TarifasCamas;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TarifaCamaRequest(
        String tipoCama,   // enum en texto
        LocalDate numeroMes, // cualquier día del mes -> se normaliza al 1
        BigDecimal precio
) {}

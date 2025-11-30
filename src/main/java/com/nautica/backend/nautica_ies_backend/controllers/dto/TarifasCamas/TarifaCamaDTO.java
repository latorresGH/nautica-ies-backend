package com.nautica.backend.nautica_ies_backend.controllers.dto.TarifasCamas;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TarifaCamaDTO(
        Long idTarifa,
        String tipoCama,  
        LocalDate numeroMes,
        BigDecimal precio
) {}

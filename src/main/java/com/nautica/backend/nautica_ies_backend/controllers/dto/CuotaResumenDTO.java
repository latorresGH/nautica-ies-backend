package com.nautica.backend.nautica_ies_backend.controllers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaResumenDTO(
    LocalDate numeroMes,     
    BigDecimal monto,
    String estado            
) {}

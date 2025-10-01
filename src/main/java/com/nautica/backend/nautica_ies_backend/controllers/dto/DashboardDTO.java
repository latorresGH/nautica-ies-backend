// com.nautica.dashboard.dto.KpisDTO
package com.nautica.backend.nautica_ies_backend.controllers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardDTO(
    LocalDate fecha,
    long usuariosActivos,
    long tareasHoy,
    long tareasPendientes,
    long turnosHoy,
    long cuotasVencidas,
    BigDecimal ingresosMes,
    long clientesDebenMes,
    long clientesPagaronMes
) {}

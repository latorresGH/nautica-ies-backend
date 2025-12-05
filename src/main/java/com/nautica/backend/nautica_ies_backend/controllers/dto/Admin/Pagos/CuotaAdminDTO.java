// src/main/java/.../controllers/dto/Admin/Pagos/CuotaAdminDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;

public record CuotaAdminDTO(
        Long idCuota,
        LocalDate numeroMes,
        String periodo,
        BigDecimal monto,
        EstadoCuota estadoCuota,
        Long idEmbarcacion,
        String nombreEmbarcacion,
        String matriculaEmbarcacion
) {}

// ClienteEstadoCuentaAdminDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

import java.math.BigDecimal;

public record ClienteEstadoCuentaAdminDTO(
        String estadoCuotas,      // "AL_DIA" o "CON_DEUDA"
        long cuotasAdeudadas,
        BigDecimal montoAdeudado
) {}

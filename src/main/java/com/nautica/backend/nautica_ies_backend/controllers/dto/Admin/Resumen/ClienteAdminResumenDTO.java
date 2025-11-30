package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Resumen;

import java.util.List;

public record ClienteAdminResumenDTO(
        Long idCliente,
        String nombre,
        String apellido,
        String telefono,
        Boolean activo,                     
        List<EmbarcacionAdminDTO> embarcaciones,
        String estadoCuotas                 // "AL_DIA" | "CON_DEUDA"
) {}


package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCliente;

public record ClienteSummary(
        Long id,
        String nombre,
        String apellido,
        String correo,
        String telefono,
        EstadoCliente estado
) {}

package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

public record ClienteSummary(
        Long id,
        String nombre,
        String apellido,
        String correo,
        String telefono,
        Boolean activo
) {}

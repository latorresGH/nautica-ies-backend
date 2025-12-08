package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteAltaRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank @Email String correo,
        @NotBlank String dni,
        String telefono,
        String direccion,
        String localidad,
        String provincia,

        // puede ser "propietario" o "autorizado"
        String tipoCliente,

        // lista de embarcaciones a crear
        List<EmbarcacionAltaRequest> embarcaciones,

        // por ahora lo dejamos para más adelante
        List<Long> embarcacionesIdsAutorizado
) {}


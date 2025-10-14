package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCliente;

public record ClienteDetail(
        Long id,
        Integer numCliente,
        String tipoCliente,        // texto plano
        EstadoCliente estadoCliente,
        String nombre,
        String apellido,
        String dni,
        String correo,
        String telefono,
        String direccion,
        String localidad,
        String provincia,
        String fechaAlta,          // ISO yyyy-MM-dd
        Long idEmbarcacion
) {}
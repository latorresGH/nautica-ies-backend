package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

public record ClienteDetail(
        Long id,
        Integer numCliente,
        String tipoCliente,        // texto plano
        Boolean activo,       // <— reemplaza estadoCliente
        String nombre,
        String apellido,
        String dni,
        String correo,
        String telefono,
        String direccion,
        String localidad,
        String provincia,
        String fechaAlta          // ISO yyyy-MM-dd
        // Long idEmbarcacion
) {}
// ClienteAdminInfoDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;


import java.util.List;

public record ClienteInfoAdminDTO(
        Long idCliente,
        String nombre,
        String apellido,
        String dni,
        String correo,
        String telefono,
        String direccion,
        String localidad,
        String provincia,
        boolean activo,
        List<EmbarcacionClienteInfoDTO> embarcaciones,
        List<PersonaAutorizadaDTO> personasAutorizadas,
        ClienteEstadoCuentaAdminDTO estadoCuenta
) {}


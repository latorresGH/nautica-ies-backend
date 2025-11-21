package com.nautica.backend.nautica_ies_backend.controllers.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;

public record UsuarioCreateRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @Email @NotBlank String correo,
        @NotBlank String contrasena,
        @NotBlank String dni,

        // Acepta: numCliente | num_cliente | numeroCliente | numero_cliente
        @JsonAlias({"numCliente","num_cliente","numeroCliente","numero_cliente"})
        Integer numCliente,

        // "propietario" | "autorizado" (opcional; si no viene, default en el controller)
        String tipoCliente,

        String telefono,
        String direccion,
        String localidad,
        String provincia,

        // "admin" | "operario" | "cliente"
        @NotBlank String rol,

        // opcional; si viene null, lo seteamos a true en el controller o en el service
        Boolean activo,

        // Acepta: codigoAdmin | codigo_admin
        @JsonAlias({"codigoAdmin","codigo_admin"})
        String codigoAdmin,

        // Acepta: tipoAdmin | tipo_admin
        @JsonAlias({"tipoAdmin","tipo_admin"})
        String tipoAdmin,

        String legajo,
        String puesto
) {}

// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/UsuarioCreateRequest.java
package com.nautica.backend.nautica_ies_backend.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

public record UsuarioCreateRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @Email @NotBlank String correo,
        @NotBlank String contrasena,
        @NotBlank String dni,
        @JsonProperty("numero_cliente") Integer numCliente, // <-- viene como num_cliente en JSON
        String telefono,
        String direccion,
        String localidad,
        String provincia,
        @NotBlank String rol, // ej: "admin" | "operario" | "cliente"
        @NotNull Boolean activo,
        @JsonProperty("codigo_admin") String codigoAdmin,
        @JsonProperty("tipo_admin") String tipoAdmin, // "gerente" | "jefe" | "admin_usuarios"
        String legajo,
        String puesto
){
        public UsuarioCreateRequest {
                if (activo == null) activo = Boolean.TRUE; // default
    }
}

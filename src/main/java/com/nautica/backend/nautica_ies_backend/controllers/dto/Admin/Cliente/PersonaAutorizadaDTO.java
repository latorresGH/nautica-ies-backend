// AutorizadoInfoDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

public record PersonaAutorizadaDTO(
        Long idUsuario,
        String nombre,
        String apellido,
        String rolEnEmbarcacion,
        Long idEmbarcacion,
        String nombreEmbarcacion,
        String matricula
) {}


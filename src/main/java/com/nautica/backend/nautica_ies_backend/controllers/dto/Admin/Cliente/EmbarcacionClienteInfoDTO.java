// EmbarcacionClienteInfoDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

public record EmbarcacionClienteInfoDTO(
        Long id,
        String nombre,
        String matricula,
        String tipoCama
) {}

// EmbarcacionUpdateRequest.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

public record EmbarcacionUpdateRequest(
        Long id,           // null = nueva embarcación
        String nombre,
        String matricula,
        String marcaCasco,
        String modeloCasco,
        String marcaMotor,
        String modeloMotor,
        String numMotor,
        Integer potenciaMotor,
        String tipoCama    // "cama_grande", etc.
) {}


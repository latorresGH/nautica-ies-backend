// src/main/java/.../controllers/dto/Tareas/ResumenTareasOperarioDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas;

public record ResumenTareasOperarioDTO(
        long pendientesLavado,
        long pendientesBotado
) {}

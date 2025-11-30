// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/Tareas/TareaOperarioDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas;

public record TareaOperarioDTO(
        Long id,
        String tipo,            // "lavado"/"botado"
        String estado,          // "pendiente"/"realizado"
        String nombre,
        String apellido,
        String embarcacion,
        String telefono,
        String fecha,           // "2025-11-25" o lo que quieras
        String hora,            // "10:30"
        String notaOperario,     // puede ser null
        String matricula,
        String marca
) {}

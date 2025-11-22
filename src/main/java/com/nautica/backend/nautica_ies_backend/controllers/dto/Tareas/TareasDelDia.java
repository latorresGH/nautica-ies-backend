package com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas;

public record TareasDelDia(
        Long id,
        String nombre,       // cliente
        String apellido,     // cliente
        String embarcacion,  // nombre/barco
        String telefono,     // cliente
        String tarea,        // LAVADO / BOTADO
        String operario,     // "Juan Pérez"
        String horario       // "10:30"
) {}

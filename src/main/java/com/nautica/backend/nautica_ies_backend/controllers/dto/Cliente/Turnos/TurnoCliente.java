package com.nautica.backend.nautica_ies_backend.controllers.dto.Cliente.Turnos;

public record TurnoCliente(
        Long id,
        String fecha,
        String horaInicio,
        String horaFin,
        String estado,
        String tipo,
        Long idEmbarcacion
) {}
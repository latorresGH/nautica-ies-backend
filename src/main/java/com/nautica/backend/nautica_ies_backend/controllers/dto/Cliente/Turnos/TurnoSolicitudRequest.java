package com.nautica.backend.nautica_ies_backend.controllers.dto.Cliente.Turnos;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoSolicitudRequest(
        Long clienteId,
        Long idEmbarcacion,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String tipo,
        String notas
) {}

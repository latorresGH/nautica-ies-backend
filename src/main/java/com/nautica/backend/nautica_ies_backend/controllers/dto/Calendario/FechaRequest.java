package com.nautica.backend.nautica_ies_backend.controllers.dto.Calendario;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FechaRequest(@NotNull LocalDate fecha) {}

package com.nautica.backend.nautica_ies_backend.controllers.dto.Calendario;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record HorariosRequest(@NotNull LocalDate fecha, @NotNull List<Franja> franjas) {}

package com.nautica.backend.nautica_ies_backend.controllers.dto.Calendario;

import java.time.LocalDate;
import java.util.List;

public record DiaMes(LocalDate fecha, boolean disponible, List<Franja> franjas) {}

// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/Tareas/BarSemanaDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas;

import java.util.List;

public record BarSemana(
        List<String> labels,   // ["Lun", "Mar", ...]
        List<Long> realizadas  // [3, 5, 0, ...]
) {}

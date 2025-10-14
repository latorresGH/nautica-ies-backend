// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/operarios/OperarioEstadoPatch.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios;

import jakarta.validation.constraints.NotNull;

public class OperarioEstadoPatch {
    @NotNull public Boolean activo;
    public String motivo; // opcional
}

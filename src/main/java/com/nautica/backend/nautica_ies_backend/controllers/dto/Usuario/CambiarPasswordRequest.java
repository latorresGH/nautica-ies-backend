// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/CambiarPasswordRequest.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Usuario;

import jakarta.validation.constraints.NotBlank;

public record CambiarPasswordRequest(
    @NotBlank String actual,
    @NotBlank String nueva
) {}

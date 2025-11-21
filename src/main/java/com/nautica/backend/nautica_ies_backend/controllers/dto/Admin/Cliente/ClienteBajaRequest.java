package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;
import jakarta.validation.constraints.*;

public record ClienteBajaRequest(
        @NotBlank @Size(min=3, max=300) String motivo
) {}

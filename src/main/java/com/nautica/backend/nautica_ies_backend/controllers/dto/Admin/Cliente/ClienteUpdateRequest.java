package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;
import jakarta.validation.constraints.*;
import java.util.List;
public record ClienteUpdateRequest(
        @NotBlank @Size(min=2, max=80) String nombre,
        @NotBlank @Size(min=2, max=80) String apellido,
        @Email @NotBlank String correo,
        @Size(min=6, max=30) String telefono,
        @Size(max=120) String direccion,
        @Size(max=80) String localidad,
        @Size(max=80) String provincia,
        List<EmbarcacionUpdateRequest> embarcaciones        
) {}
// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/operarios/OperarioUpdateRequest.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OperarioUpdateRequest {
    @NotBlank public String nombre;
    public String apellido;
    @Email @NotBlank
    @JsonAlias({"correo","email"})  
    public String correo; 
    @NotBlank public String telefono;
    @NotBlank public String legajo;
    @NotBlank public String puesto;
    @NotNull  public Boolean activo;
    @NotBlank public String dni;
    // si viene no-blanca, se re-hashea
    public String contrasena;
}

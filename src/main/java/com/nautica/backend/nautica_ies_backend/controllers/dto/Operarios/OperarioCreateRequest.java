// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/operarios/OperarioCreateRequest.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OperarioCreateRequest {
    @NotBlank public String nombre;
    public String apellido;
    @Email @NotBlank
    @JsonAlias({"correo","email"})   // <- acepta "correo" desde el JSON
    public String correo;      // se mapeará a 'correo'
    @NotBlank public String telefono;
    @NotBlank public String legajo;
    @NotBlank public String puesto;
    @NotNull  public Boolean activo;
    @NotBlank public String dni;
    // opcional: si lo querés permitir, de lo contrario generamos una temporal
    public String contrasena;
}

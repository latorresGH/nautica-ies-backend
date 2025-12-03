// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/dto/operarios/OperarioResponse.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios;

public record OperarioResponse(
    Long id,
    String nombre,
    String apellido,
    String correo,
    String telefono,
    Boolean activo,
    String legajo,
    String puesto,
    String dni,
    String direccion,
    String localidad,
    String provincia
) {}
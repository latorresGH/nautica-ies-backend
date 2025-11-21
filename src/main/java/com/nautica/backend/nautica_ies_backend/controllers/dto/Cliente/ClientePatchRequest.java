package com.nautica.backend.nautica_ies_backend.controllers.dto.Cliente;

public record ClientePatchRequest(
    String nombre,
    String apellido,
    String correo,
    String telefono,
    String direccion,
    String localidad,
    String provincia
) {}

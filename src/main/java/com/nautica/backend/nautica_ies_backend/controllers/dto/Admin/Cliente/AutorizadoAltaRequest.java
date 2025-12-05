// PersonaAutorizadaAltaRequest.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente;

public record AutorizadoAltaRequest(
        String nombre,
        String apellido,
        String dni,
        String correo,
        String telefono,
        String direccion,
        String localidad,
        String provincia
        // Más adelante podemos agregar a qué embarcaciones aplica, etc.
) {}

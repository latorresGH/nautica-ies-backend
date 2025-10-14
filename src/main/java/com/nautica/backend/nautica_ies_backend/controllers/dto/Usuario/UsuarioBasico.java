// src/main/java/.../controllers/dto/UsuarioBasico.java
package com.nautica.backend.nautica_ies_backend.controllers.dto.Usuario;

import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.models.Cliente;

public record UsuarioBasico(
        Long idUsuario,
        String nombre,
        String apellido,
        String correo,
        String dni,
        String telefono,
        String direccion,
        String localidad,
        String provincia,
        String rol, // enum a String
        Boolean activo,
        Integer numCliente, // solo si es Cliente
        String tipoCliente // solo si es Cliente
) {
    public static UsuarioBasico from(Usuario u) {
        Integer num = null;
        String tipo = null;

        if (u instanceof Cliente c) {
            num = c.getNumCliente();
            tipo = (c.getTipoCliente() != null) ? c.getTipoCliente().name() : null;
        }

        return new UsuarioBasico(
                u.getIdUsuario(),
                u.getNombre(),
                u.getApellido(),
                u.getCorreo(),
                u.getDni(),
                u.getTelefono(),
                u.getDireccion(),
                u.getLocalidad(),
                u.getProvincia(),
                (u.getRol() != null ? u.getRol().name() : null),
                u.getActivo(),
                num,
                tipo);
    }
}

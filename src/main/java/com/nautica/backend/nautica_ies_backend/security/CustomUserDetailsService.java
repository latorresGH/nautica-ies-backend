package com.nautica.backend.nautica_ies_backend.security;

import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public CustomUserDetailsService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        var usuario = usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return User.withUsername(usuario.getCorreo())    // JWT subject será el correo
                .password(usuario.getContrasena())       // ojo: campo contrasena (encriptada)
                .authorities(usuario.getRol().name())    // tu enum Rol
                .accountLocked(false)
                .disabled(!usuario.getActivo())          // si usás flag "activo"
                .build();
    }
}

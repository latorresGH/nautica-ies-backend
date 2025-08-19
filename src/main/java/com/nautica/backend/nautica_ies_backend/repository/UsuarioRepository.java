package com.nautica.backend.nautica_ies_backend.repository;

import com.nautica.backend.nautica_ies_backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByDni(String dni);
}

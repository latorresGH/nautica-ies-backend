package com.nautica.backend.nautica_ies_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.Usuario;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 * <p>
 * Proporciona operaciones CRUD y consultas personalizadas para acceder a los
 * datos de usuarios.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param correo Correo del usuario.
     * @return Un {@link Optional} que contiene el usuario si fue encontrado, o
     *         vacío si no existe.
     */

    Optional<Usuario> findByCorreo(String correo);

    /**
     * Verifica si ya existe un usuario con el DNI proporcionado.
     *
     * @param dni DNI del usuario.
     * @return {@code true} si existe un usuario con ese DNI, {@code false} en caso
     *         contrario.
     */
    boolean existsByDni(String dni);

    long countByActivoTrue();
    
    boolean existsByCorreoIgnoreCase(String correo);
}

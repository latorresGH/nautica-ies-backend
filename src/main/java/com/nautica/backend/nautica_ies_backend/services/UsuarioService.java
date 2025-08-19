// src/main/java/com/nautica/backend/nautica_ies_backend/services/UsuarioService.java
package com.nautica.backend.nautica_ies_backend.services;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Usuario> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Usuario obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Usuario crear(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        try {
            return repo.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("DNI o correo ya existe.");
        }
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario u = obtener(id);
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            u.setContrasena(passwordEncoder.encode(datos.getContrasena()));
        }
        u.setNombre(datos.getNombre());
        u.setApellido(datos.getApellido());
        u.setCorreo(datos.getCorreo());
        u.setTelefono(datos.getTelefono());
        u.setDireccion(datos.getDireccion());
        u.setLocalidad(datos.getLocalidad());
        u.setProvincia(datos.getProvincia());
        u.setRol(datos.getRol());
        u.setActivo(datos.getActivo());
        try {
            return repo.save(u);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("DNI o correo ya existe.");
        }
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Usuario no encontrado");
        repo.deleteById(id);
    }

    public Usuario buscarPorCorreo(String correo) {
        return repo.findByCorreo(correo).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}

package com.nautica.backend.nautica_ies_backend.services;

import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario crear(Usuario usuario) {
        // TODO: encripta contraseñas antes de guardar (BCrypt)
        return repo.save(usuario);
    }

    public Usuario obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario usuario = obtener(id);
        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuario.setCorreo(datos.getCorreo());
        usuario.setTelefono(datos.getTelefono());
        usuario.setDireccion(datos.getDireccion());
        usuario.setLocalidad(datos.getLocalidad());
        usuario.setProvincia(datos.getProvincia());
        usuario.setRol(datos.getRol());
        usuario.setActivo(datos.getActivo());
        // TODO: si actualizás contraseña, encriptá
        return repo.save(usuario);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}

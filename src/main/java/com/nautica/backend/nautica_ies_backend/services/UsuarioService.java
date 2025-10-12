// src/main/java/com/nautica/backend/nautica_ies_backend/services/UsuarioService.java
package com.nautica.backend.nautica_ies_backend.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;


import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;

import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;

/**
 * Servicio que gestiona la lógica de negocio relacionada con la entidad
 * {@link Usuario}.
 * <p>
 * Se encarga de operaciones como listar usuarios paginados, crear, actualizar,
 * eliminar y buscar por correo.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final ClienteRepository clienteRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor que inyecta el repositorio de usuarios y el codificador de
     * contraseñas.
     *
     * @param repo            Repositorio de usuarios.
     * @param passwordEncoder Codificador de contraseñas.
     */
    public UsuarioService(UsuarioRepository repo, PasswordEncoder passwordEncoder, ClienteRepository clienteRepo) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.clienteRepo = clienteRepo;

    }

    /**
     * Retorna una lista paginada de usuarios con ordenamiento.
     *
     * @param page Número de página (0-index).
     * @param size Tamaño de página.
     * @param sort Objeto de ordenamiento (por campos como nombre, correo, etc.).
     * @return Página de usuarios.
     */
    public Page<Usuario> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Usuario encontrado.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    public Usuario obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    /**
     * Crea un nuevo usuario y codifica su contraseña antes de guardarlo.
     * 
     * @param usuario Usuario a crear.
     * @return Usuario creado.
     * @throws IllegalArgumentException si el DNI o correo ya existen.
     */
    public Usuario crear(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        try {
            return repo.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("DNI o correo ya existe.");
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id    ID del usuario a actualizar.
     * @param datos Datos nuevos a aplicar.
     * @return Usuario actualizado.
     * @throws IllegalArgumentException si el DNI o correo ya existen.
     */
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

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario a eliminar.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Usuario no encontrado");
        repo.deleteById(id);
    }

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param correo Correo a buscar.
     * @return Usuario correspondiente.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    public Usuario buscarPorCorreo(String correo) {
        return repo.findByCorreo(correo).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

        /** Devuelve idUsuario y clienteId a partir del correo. */
    public Map<String, Object> idsPorCorreo(String correo) {
        Usuario u = repo.findByCorreo(correo)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + correo));

        Long idUsuario = u.getIdUsuario();
        // En tu modelo Cliente comparte PK con Usuario, por eso usamos findById(idUsuario)
        var cliente = clienteRepo.findById(idUsuario).orElse(null);
        Long clienteId = (cliente != null ? cliente.getIdUsuario() : null);

        return Map.of(
            "idUsuario", idUsuario,
            "clienteId", clienteId,
            "correo", correo
        );
    }

    /**
     * Actualiza el teléfono de un usuario.
     * @param id       ID del usuario.
     * @param telefono Nuevo teléfono.
     */
    public Usuario actualizarTelefono(Long id, String telefono) {
    var u = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    u.setTelefono(telefono);
    return repo.save(u);
}
}

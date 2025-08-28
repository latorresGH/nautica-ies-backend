// src/main/java/com/nautica/backend/nautica_ies_backend/services/OperarioService.java
package com.nautica.backend.nautica_ies_backend.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.models.Operario;
import com.nautica.backend.nautica_ies_backend.models.enums.RolUsuario;
import com.nautica.backend.nautica_ies_backend.repository.OperarioRepository;

@Service
public class OperarioService {

    private final OperarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public OperarioService(OperarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Operario> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Operario obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Operario no encontrado"));
    }

    public Operario buscarPorLegajo(String legajo) {
        return repo.findByLegajo(legajo).orElseThrow(() -> new ResourceNotFoundException("Operario no encontrado"));
    }

    public Operario crear(Operario op) {
        // Forzar rol correcto y hashear contraseña (heredada de Usuario)
        op.setRol(RolUsuario.operario);
        op.setContrasena(passwordEncoder.encode(op.getContrasena()));
        try {
            return repo.save(op);
        } catch (DataIntegrityViolationException e) {
            // Puede ser legajo/correo/dni duplicado (únicos están en DB)
            throw new IllegalArgumentException("Datos duplicados: verifique legajo, correo o DNI");
        }
    }

    public Operario actualizar(Long id, Operario datos) {
        Operario op = obtener(id);

        // Campos comunes (heredados de Usuario)
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            op.setContrasena(passwordEncoder.encode(datos.getContrasena()));
        }
        op.setNombre(datos.getNombre());
        op.setApellido(datos.getApellido());
        op.setCorreo(datos.getCorreo());
        op.setTelefono(datos.getTelefono());
        op.setDireccion(datos.getDireccion());
        op.setLocalidad(datos.getLocalidad());
        op.setProvincia(datos.getProvincia());
        op.setActivo(datos.getActivo());
        // Forzar rol correcto (no permitir cambiar a otro)
        op.setRol(RolUsuario.operario);

        // Específicos de Operario
        op.setLegajo(datos.getLegajo());
        op.setPuesto(datos.getPuesto());

        try {
            return repo.save(op);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Datos duplicados: verifique legajo, correo o DNI");
        }
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Operario no encontrado");
        repo.deleteById(id);
    }
}

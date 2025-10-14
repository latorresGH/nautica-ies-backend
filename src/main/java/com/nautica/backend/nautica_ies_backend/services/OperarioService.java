// src/main/java/com/nautica/backend/nautica_ies_backend/services/OperarioService.java
package com.nautica.backend.nautica_ies_backend.services;

import java.security.SecureRandom;
import java.util.HexFormat;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios.OperarioCreateRequest;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios.OperarioResponse;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios.OperarioUpdateRequest;
import com.nautica.backend.nautica_ies_backend.models.Operario;
import com.nautica.backend.nautica_ies_backend.models.enums.RolUsuario;
import com.nautica.backend.nautica_ies_backend.repository.OperarioRepository;

@Service
public class OperarioService {

    private final OperarioRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom rng = new SecureRandom();

    public OperarioService(OperarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- Queries ----------
    public Page<Operario> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Page<Operario> listarConBusqueda(String q, int page, int size, Sort sort) {
        return repo.search(q, PageRequest.of(page, size, sort));
    }

    public Operario obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Operario no encontrado"));
    }

    // ---------- Commands ----------
    public Operario crearDesdeDto(OperarioCreateRequest req) {
        Operario op = new Operario();
        op.setRol(RolUsuario.operario);
        op.setNombre(req.nombre);
        op.setApellido(req.apellido);
        op.setDni(req.dni);
        op.setCorreo(req.correo);      // email -> correo
        op.setTelefono(req.telefono);
        op.setActivo(req.activo);
        op.setLegajo(req.legajo);
        op.setPuesto(req.puesto);

        String raw = (req.contrasena != null && !req.contrasena.isBlank())
                ? req.contrasena
                : generarPasswordTemporal();
        op.setContrasena(passwordEncoder.encode(raw));

        try {
            return repo.save(op);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Datos duplicados: verifique legajo, correo o DNI");
        }
    }

    public Operario actualizarDesdeDto(Long id, OperarioUpdateRequest req) {
        Operario op = obtener(id);

        op.setRol(RolUsuario.operario); // forzar
        op.setNombre(req.nombre);
        op.setApellido(req.apellido);
        op.setDni(req.dni);
        op.setCorreo(req.correo);
        op.setTelefono(req.telefono);
        op.setActivo(req.activo);
        op.setLegajo(req.legajo);
        op.setPuesto(req.puesto);

        if (req.contrasena != null && !req.contrasena.isBlank()) {
            op.setContrasena(passwordEncoder.encode(req.contrasena));
        }

        try {
            return repo.save(op);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Datos duplicados: verifique legajo, correo o DNI");
        }
    }

    public Operario cambiarEstado(Long id, boolean activo, String motivo) {
        Operario op = obtener(id);
        op.setActivo(activo);
        // TODO: si agregás auditoría, registrar motivo/usuario aquí
        return repo.save(op);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Operario no encontrado");
        repo.deleteById(id);
    }

    // ---------- Helpers internos (sin mapper) ----------
    private String generarPasswordTemporal() {
        return HexFormat.of().formatHex(rng.generateSeed(8)); // 8 bytes -> 16 hex
    }

    public OperarioResponse toResponse(Operario o) {
        return new OperarioResponse(
            o.getIdUsuario(),
            o.getNombre(),
            o.getApellido(),
            o.getCorreo(),
            o.getTelefono(),
            o.getActivo(),
            o.getLegajo(),
            o.getPuesto()
        );
    }

    public Page<OperarioResponse> toResponsePage(Page<Operario> page) {
        return page.map(this::toResponse);
    }
}

package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.models.Administrador;
import com.nautica.backend.nautica_ies_backend.repository.AdministradorRepository;

@Service
@Transactional
public class AdministradorService {

    private final AdministradorRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(AdministradorRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional(readOnly = true)
    public List<Administrador> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Administrador findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado (id=" + id + ")"));
    }

    @Transactional(readOnly = true)
    public Administrador findByCodigoAdmin(String codigoAdmin) {
        return repo.findByCodigoAdmin(codigoAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado (codigo=" + codigoAdmin + ")"));
    }

    public Administrador create(Administrador admin) {
        if (admin.getContrasena() != null && !admin.getContrasena().isBlank()) {
            admin.setContrasena(passwordEncoder.encode(admin.getContrasena()));
        }
        if (admin.getCodigoAdmin() == null || admin.getCodigoAdmin().isBlank()) {
            throw new IllegalArgumentException("codigoAdmin es obligatorio");
        }
        if (repo.existsByCodigoAdmin(admin.getCodigoAdmin())) {
            throw new IllegalArgumentException("Ya existe un administrador con ese codigoAdmin");
        }
        if (admin.getFechaAlta() == null) {
            admin.setFechaAlta(LocalDate.now());
        }
        return repo.save(admin);
    }

    public Administrador update(Long id, Administrador admin) {
        Administrador db = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado (id=" + id + ")"));

        if (admin.getCodigoAdmin() != null && !admin.getCodigoAdmin().isBlank()
                && !admin.getCodigoAdmin().equals(db.getCodigoAdmin())) {
            if (repo.existsByCodigoAdmin(admin.getCodigoAdmin())) {
                throw new IllegalArgumentException("Ya existe un administrador con ese codigoAdmin");
            }
            db.setCodigoAdmin(admin.getCodigoAdmin());
        }

        if (admin.getContrasena() != null && !admin.getContrasena().isBlank()) {
            db.setContrasena(passwordEncoder.encode(admin.getContrasena()));
        }

        if (admin.getTipoAdmin() != null) db.setTipoAdmin(admin.getTipoAdmin());
        if (admin.getFechaAlta() != null) db.setFechaAlta(admin.getFechaAlta());

        return repo.save(db);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Administrador no encontrado (id=" + id + ")");
        }
        repo.deleteById(id);
    }
}

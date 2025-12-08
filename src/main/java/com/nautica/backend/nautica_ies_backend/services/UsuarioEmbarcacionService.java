// src/main/java/com/nautica/backend/nautica_ies_backend/services/UsuarioEmbarcacionService.java
package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Resumen.EmbarcacionAdminDTO;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.models.UsuarioEmbarcacion;
import com.nautica.backend.nautica_ies_backend.models.enums.RolEnEmbarcacion;
import com.nautica.backend.nautica_ies_backend.repository.EmbarcacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioEmbarcacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;

@Service
public class UsuarioEmbarcacionService {

    private final UsuarioEmbarcacionRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final EmbarcacionRepository embarcacionRepo;

    public UsuarioEmbarcacionService(UsuarioEmbarcacionRepository repo,
            UsuarioRepository usuarioRepo,
            EmbarcacionRepository embarcacionRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.embarcacionRepo = embarcacionRepo;
    }

    public Page<UsuarioEmbarcacion> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public UsuarioEmbarcacion obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada"));
    }

    public List<UsuarioEmbarcacion> listarPorUsuario(Long idUsuario) {
        // valida existencia (opcional)
        usuarioRepo.findById(idUsuario).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return repo.findByUsuario_IdUsuario(idUsuario);
    }

    public List<UsuarioEmbarcacion> listarPorEmbarcacion(Long idEmbarcacion) {
        // valida existencia (opcional)
        embarcacionRepo.findById(idEmbarcacion)
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));
        return repo.findByEmbarcacion_IdEmbarcacion(idEmbarcacion);
    }

    @Transactional
    public UsuarioEmbarcacion crear(Long idUsuario, Long idEmbarcacion, RolEnEmbarcacion rol,
            LocalDate desde, LocalDate hasta) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Embarcacion embarcacion = embarcacionRepo.findById(idEmbarcacion)
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));

        // evitar duplicados
        repo.findByUsuario_IdUsuarioAndEmbarcacion_IdEmbarcacion(idUsuario, idEmbarcacion)
                .ifPresent(x -> {
                    throw new IllegalArgumentException("El usuario ya está asignado a esta embarcación");
                });

        // (opcional) regla: un solo PROPIETARIO activo por embarcación
        if (rol == RolEnEmbarcacion.propietario) {
            boolean yaHayPropietario = repo.findByEmbarcacion_IdEmbarcacion(idEmbarcacion).stream()
                    .anyMatch(x -> x.getRolEnEmbarcacion() == RolEnEmbarcacion.propietario && x.getHasta() == null);
            if (yaHayPropietario)
                throw new IllegalArgumentException("La embarcación ya tiene un propietario activo");
        }

        UsuarioEmbarcacion ue = new UsuarioEmbarcacion();
        ue.setUsuario(usuario);
        ue.setEmbarcacion(embarcacion);
        ue.setRolEnEmbarcacion(rol);
        ue.setDesde(desde != null ? desde : LocalDate.now());
        ue.setHasta(hasta);

        return repo.save(ue);
    }

    @Transactional
    public UsuarioEmbarcacion actualizar(Long id, RolEnEmbarcacion rol, LocalDate desde, LocalDate hasta) {
        UsuarioEmbarcacion ue = obtener(id);

        // Si cambian a PROPIETARIO, validar regla (solo 1 activo)
        if (rol == RolEnEmbarcacion.propietario && ue.getRolEnEmbarcacion() != RolEnEmbarcacion.propietario) {
            Long idEmbarcacion = ue.getEmbarcacion().getIdEmbarcacion();
            boolean yaHayPropietario = repo.findByEmbarcacion_IdEmbarcacion(idEmbarcacion).stream()
                    .anyMatch(x -> !x.getId().equals(id) &&
                            x.getRolEnEmbarcacion() == RolEnEmbarcacion.propietario &&
                            x.getHasta() == null);
            if (yaHayPropietario)
                throw new IllegalArgumentException("La embarcación ya tiene un propietario activo");
        }

        if (rol != null)
            ue.setRolEnEmbarcacion(rol);
        if (desde != null)
            ue.setDesde(desde);
        ue.setHasta(hasta); // permite setear null para “sin fin”

        return repo.save(ue);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Relación no encontrada");
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EmbarcacionAdminDTO> listarEmbarcacionesAdmin() {
        var relaciones = repo.findByRolEnEmbarcacionAndHastaIsNull(RolEnEmbarcacion.propietario);

        return relaciones.stream()
                .map(ue -> {
                    var emb = ue.getEmbarcacion();
                    var usuario = ue.getUsuario();
                    if (emb == null) return null;

                    return new EmbarcacionAdminDTO(
                            emb.getIdEmbarcacion(),
                            emb.getNombre(),
                            emb.getNumMatricula(),
                            usuario != null ? usuario.getIdUsuario() : null
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}

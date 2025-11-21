// EmbarcacionService.java
package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.EmbarcacionResumenDTO;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.models.UsuarioEmbarcacion;
import com.nautica.backend.nautica_ies_backend.models.enums.RolEnEmbarcacion;
import com.nautica.backend.nautica_ies_backend.repository.EmbarcacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioEmbarcacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;

@Service
public class EmbarcacionService {

    private final EmbarcacionRepository embarcacionRepo;
    private final UsuarioRepository usuarioRepo;
    private final UsuarioEmbarcacionRepository ueRepo;

    public EmbarcacionService(EmbarcacionRepository embarcacionRepo,
            UsuarioRepository usuarioRepo,
            UsuarioEmbarcacionRepository ueRepo) {
        this.embarcacionRepo = embarcacionRepo;
        this.usuarioRepo = usuarioRepo;
        this.ueRepo = ueRepo;
    }

    public Page<Embarcacion> listar(int page, int size, Sort sort) {
        return embarcacionRepo.findAll(PageRequest.of(page, size, sort));
    }

    public Embarcacion obtener(Long id) {
        return embarcacionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));
    }

    public Embarcacion buscarPorMatricula(String numMatricula) {
        return embarcacionRepo.findByNumMatricula(numMatricula)
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));
    }

    public Embarcacion crear(Embarcacion emb) {
        try {
            return embarcacionRepo.save(emb);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Violación de integridad (¿matrícula repetida?)");
        }
    }

    public Embarcacion actualizar(Long id, Embarcacion datos) {
        Embarcacion e = obtener(id);
        e.setNumMatricula(datos.getNumMatricula());
        e.setNombre(datos.getNombre());
        e.setMarcaCasco(datos.getMarcaCasco());
        e.setModeloCasco(datos.getModeloCasco());
        e.setDescripcion(datos.getDescripcion());
        e.setMarcaMotor(datos.getMarcaMotor());
        e.setModeloMotor(datos.getModeloMotor());
        e.setNumMotor(datos.getNumMotor());
        e.setPotenciaMotor(datos.getPotenciaMotor());
        e.setFechaAlta(datos.getFechaAlta());
        e.setFechaBaja(datos.getFechaBaja());
        try {
            return embarcacionRepo.save(e);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de integridad (¿matrícula repetida?)");
        }
    }

    public void eliminar(Long id) {
        if (!embarcacionRepo.existsById(id))
            throw new ResourceNotFoundException("Embarcación no encontrada");
        embarcacionRepo.deleteById(id);
    }

    // --------- N:M SIN DTOS ---------

    @Transactional
    public void asignarUsuario(Long idEmbarcacion, Long idUsuario, RolEnEmbarcacion rol,
            LocalDate desde, LocalDate hasta) {
        Embarcacion emb = obtener(idEmbarcacion);
        Usuario user = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ueRepo.findByUsuario_IdUsuarioAndEmbarcacion_IdEmbarcacion(idUsuario, idEmbarcacion)
                .ifPresent(x -> {
                    throw new IllegalArgumentException("El usuario ya está asignado a esta embarcación");
                });

        // (Opcional) regla: un solo PROPIETARIO activo por embarcación
        if (rol == RolEnEmbarcacion.propietario) {
            boolean yaHayPropietario = ueRepo.findByEmbarcacion_IdEmbarcacion(idEmbarcacion).stream()
                    .anyMatch(x -> x.getRolEnEmbarcacion() == RolEnEmbarcacion.propietario && x.getHasta() == null);
            if (yaHayPropietario)
                throw new IllegalArgumentException("La embarcación ya tiene un propietario activo");
        }

        UsuarioEmbarcacion ue = new UsuarioEmbarcacion();
        ue.setUsuario(user);
        ue.setEmbarcacion(emb);
        ue.setRolEnEmbarcacion(rol);
        ue.setDesde(desde != null ? desde : LocalDate.now());
        ue.setHasta(hasta);

        ueRepo.save(ue);
    }

    @Transactional
    public void desasignarUsuario(Long idEmbarcacion, Long idUsuario) {
        UsuarioEmbarcacion ue = ueRepo.findByUsuario_IdUsuarioAndEmbarcacion_IdEmbarcacion(idUsuario, idEmbarcacion)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación usuario-embarcación no encontrada"));
        ueRepo.delete(ue);
    }

    public List<UsuarioEmbarcacion> listarUsuarios(Long idEmbarcacion) {
        obtener(idEmbarcacion); // valida existencia
        return ueRepo.findByEmbarcacion_IdEmbarcacion(idEmbarcacion);
    }

    public List<EmbarcacionResumenDTO> listarPorUsuario(Long usuarioId) {
    var relaciones = ueRepo.findActivasByUsuario(usuarioId);
    return relaciones.stream()
        .map(UsuarioEmbarcacion::getEmbarcacion)
        .distinct()
        .map(e -> new EmbarcacionResumenDTO(
            e.getIdEmbarcacion(),
            e.getNombre(),
            e.getNumMatricula()
        ))
        .toList();
    }
}
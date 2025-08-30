// src/main/java/com/nautica/backend/nautica_ies_backend/services/TareaService.java
package com.nautica.backend.nautica_ies_backend.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.models.Operario;
import com.nautica.backend.nautica_ies_backend.models.Tarea;
import com.nautica.backend.nautica_ies_backend.repository.OperarioRepository;
import com.nautica.backend.nautica_ies_backend.repository.TareaRepository;

@Service
public class TareaService {

    private final TareaRepository repo;
    private final OperarioRepository operarioRepo;

    public TareaService(TareaRepository repo, OperarioRepository operarioRepo) {
        this.repo = repo;
        this.operarioRepo = operarioRepo;
    }

    public Page<Tarea> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Tarea obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
    }

    public Tarea buscarPorNumero(Integer numero) {
        return repo.findByNumeroTarea(numero).orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
    }

    public List<Tarea> listarPorOperario(Long idOperario) {
        return repo.findByOperario_IdUsuario(idOperario);
    }

    public Tarea crear(Tarea t) {
        if (t.getOperario() == null || t.getOperario().getIdUsuario() == null) {
            throw new IllegalArgumentException("Debe indicar el operario (idUsuario)");
        }
        Operario op = operarioRepo.findById(t.getOperario().getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Operario no encontrado"));
        t.setOperario(op);
        try {
            return repo.save(t);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Número de tarea duplicado");
        }
    }

    public Tarea actualizar(Long id, Tarea datos) {
        Tarea t = obtener(id);

        t.setNumeroTarea(datos.getNumeroTarea());
        t.setTipoTarea(datos.getTipoTarea());
        t.setFecha(datos.getFecha());
        t.setHora(datos.getHora());
        t.setEstado(datos.getEstado());

        if (datos.getOperario() != null && datos.getOperario().getIdUsuario() != null) {
            Operario op = operarioRepo.findById(datos.getOperario().getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Operario no encontrado"));
            t.setOperario(op);
        }

        try {
            return repo.save(t);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Número de tarea duplicado");
        }
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Tarea no encontrada");
        repo.deleteById(id);
    }
}

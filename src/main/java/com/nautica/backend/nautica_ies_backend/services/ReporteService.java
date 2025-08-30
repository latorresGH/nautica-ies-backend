package com.nautica.backend.nautica_ies_backend.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.models.Reporte;
import com.nautica.backend.nautica_ies_backend.repository.ReporteRepository;

@Service
public class ReporteService {

    private final ReporteRepository repo;

    public ReporteService(ReporteRepository repo) {
        this.repo = repo;
    }

    public Page<Reporte> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Reporte obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
    }

    public Reporte crear(Reporte reporte) {
        try {
            return repo.save(reporte);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error al crear reporte");
        }
    }

    public Reporte actualizar(Long id, Reporte datos) {
        Reporte r = obtener(id);
        r.setNombreReporte(datos.getNombreReporte());
        r.setAreaResponsable(datos.getAreaResponsable());
        r.setFormato(datos.getFormato());
        r.setFechaGeneracion(datos.getFechaGeneracion());
        r.setArchivoDireccion(datos.getArchivoDireccion());
        return repo.save(r);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Reporte no encontrado");
        repo.deleteById(id);
    }
}

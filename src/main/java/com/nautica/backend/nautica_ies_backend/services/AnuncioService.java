package com.nautica.backend.nautica_ies_backend.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.models.Anuncio;
import com.nautica.backend.nautica_ies_backend.repository.AnuncioRepository;

@Service
public class AnuncioService {

    private final AnuncioRepository repo;

    public AnuncioService(AnuncioRepository repo) {
        this.repo = repo;
    }

    public Page<Anuncio> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Anuncio obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));
    }

    public Anuncio crear(Anuncio anuncio) {
        try {
            return repo.save(anuncio);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error al crear anuncio");
        }
    }

    public Anuncio actualizar(Long id, Anuncio datos) {
        Anuncio a = obtener(id);
        a.setTitulo(datos.getTitulo());
        a.setMensaje(datos.getMensaje());
        a.setFechaPublicacion(datos.getFechaPublicacion());
        a.setFechaExpiracion(datos.getFechaExpiracion());
        a.setHoraPublicacion(datos.getHoraPublicacion());
        a.setDestinatarios(datos.getDestinatarios());
        return repo.save(a);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Anuncio no encontrado");
        repo.deleteById(id);
    }
}

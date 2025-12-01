package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio.AnuncioAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio.AnuncioAdminRequestDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio.AnuncioClienteDTO;
import com.nautica.backend.nautica_ies_backend.models.Anuncio;
import com.nautica.backend.nautica_ies_backend.repository.AnuncioRepository;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;

    public AnuncioService(AnuncioRepository anuncioRepository) {
        this.anuncioRepository = anuncioRepository;
    }

    @Transactional
    public AnuncioAdminDTO crearAnuncio(AnuncioAdminRequestDTO dto) {
        Anuncio a = new Anuncio();
        a.setTitulo(dto.getTitulo());
        a.setMensaje(dto.getMensaje());
        a.setFechaPublicacion(LocalDate.now());
        a.setHoraPublicacion(java.time.LocalTime.now());
        a.setFechaExpiracion(dto.getFechaExpiracion());
        // destinatarios: lo ignoramos
        Anuncio guardado = anuncioRepository.save(a);

        return mapToAdminDTO(guardado);
    }

    @Transactional
    public AnuncioAdminDTO actualizarAnuncio(Long id, AnuncioAdminRequestDTO dto) {
        Anuncio a = anuncioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        a.setTitulo(dto.getTitulo());
        a.setMensaje(dto.getMensaje());
        a.setFechaExpiracion(dto.getFechaExpiracion());

        Anuncio actualizado = anuncioRepository.save(a);
        return mapToAdminDTO(actualizado);
    }

    @Transactional
    public void eliminarAnuncio(Long id) {
        if (!anuncioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Anuncio no encontrado");
        }
        anuncioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AnuncioAdminDTO> listarTodosAdmin() {
        return anuncioRepository.findAll().stream()
                .sorted((a1, a2) -> {
                    // ordenar por fecha/hora desc
                    int cmpFecha = a2.getFechaPublicacion().compareTo(a1.getFechaPublicacion());
                    if (cmpFecha != 0) return cmpFecha;
                    return a2.getHoraPublicacion().compareTo(a1.getHoraPublicacion());
                })
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnuncioClienteDTO> listarActivosCliente() {
        LocalDate hoy = LocalDate.now();
        return anuncioRepository
                .findByFechaExpiracionIsNullOrFechaExpiracionGreaterThanEqualOrderByFechaPublicacionDescHoraPublicacionDesc(hoy)
                .stream()
                .map(this::mapToClienteDTO)
                .collect(Collectors.toList());
    }

    private AnuncioAdminDTO mapToAdminDTO(Anuncio a) {
        return new AnuncioAdminDTO(
                a.getIdAnuncio(),
                a.getTitulo(),
                a.getMensaje(),
                a.getFechaPublicacion(),
                a.getHoraPublicacion(),
                a.getFechaExpiracion()
        );
    }

    private AnuncioClienteDTO mapToClienteDTO(Anuncio a) {
        return new AnuncioClienteDTO(
                a.getIdAnuncio(),
                a.getTitulo(),
                a.getMensaje(),
                a.getFechaPublicacion(),
                a.getHoraPublicacion()
        );
    }
}

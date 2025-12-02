package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private final PushNotificationService pushNotificationService;

    public AnuncioService(
        AnuncioRepository anuncioRepository,
        PushNotificationService pushNotificationService
    ) {
        this.anuncioRepository = anuncioRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Transactional
    public AnuncioAdminDTO crearAnuncio(AnuncioAdminRequestDTO dto) {
        Anuncio anuncio = new Anuncio();
        anuncio.setTitulo(dto.getTitulo());
        anuncio.setMensaje(dto.getMensaje());
        anuncio.setFechaPublicacion(LocalDate.now());
        anuncio.setHoraPublicacion(LocalTime.now());
        anuncio.setFechaExpiracion(dto.getFechaExpiracion());

        Anuncio guardado = anuncioRepository.save(anuncio);

        try {
            pushNotificationService.enviarAnuncio(guardado);
        } catch (Exception e) {
            System.err.println("Error al enviar notificación push: " + e.getMessage());
        }

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

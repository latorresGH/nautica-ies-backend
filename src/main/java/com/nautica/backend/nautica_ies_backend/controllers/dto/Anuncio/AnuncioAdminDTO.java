package com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio;

import java.time.LocalDate;
import java.time.LocalTime;

// Admin
public class AnuncioAdminDTO {

    private Long idAnuncio;
    private String titulo;
    private String mensaje;
    private LocalDate fechaPublicacion;
    private LocalTime horaPublicacion;
    private LocalDate fechaExpiracion;

    public AnuncioAdminDTO(Long idAnuncio, String titulo, String mensaje,
                           LocalDate fechaPublicacion, LocalTime horaPublicacion,
                           LocalDate fechaExpiracion) {
        this.idAnuncio = idAnuncio;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaPublicacion = fechaPublicacion;
        this.horaPublicacion = horaPublicacion;
        this.fechaExpiracion = fechaExpiracion;
    }

    public Long getIdAnuncio() { return idAnuncio; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public LocalTime getHoraPublicacion() { return horaPublicacion; }
    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
}


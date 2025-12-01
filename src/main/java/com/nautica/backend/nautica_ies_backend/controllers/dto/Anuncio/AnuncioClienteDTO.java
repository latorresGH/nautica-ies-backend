package com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio;

import java.time.LocalDate;
import java.time.LocalTime;
// Cliente (campanita)
public class AnuncioClienteDTO {

    private Long idAnuncio;
    private String titulo;
    private String mensaje;
    private LocalDate fechaPublicacion;
    private LocalTime horaPublicacion;

    public AnuncioClienteDTO(Long idAnuncio, String titulo, String mensaje,
                             LocalDate fechaPublicacion, LocalTime horaPublicacion) {
        this.idAnuncio = idAnuncio;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaPublicacion = fechaPublicacion;
        this.horaPublicacion = horaPublicacion;
    }

    public Long getIdAnuncio() { return idAnuncio; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public LocalTime getHoraPublicacion() { return horaPublicacion; }
}


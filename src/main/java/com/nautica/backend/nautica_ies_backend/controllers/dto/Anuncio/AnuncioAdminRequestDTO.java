package com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio;

import java.time.LocalDate;

public class AnuncioAdminRequestDTO {

    private String titulo;
    private String mensaje;
    private LocalDate fechaExpiracion;

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }
    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
}

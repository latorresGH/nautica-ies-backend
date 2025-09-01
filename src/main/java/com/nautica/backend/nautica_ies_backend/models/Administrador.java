package com.nautica.backend.nautica_ies_backend.models;

import com.nautica.backend.nautica_ies_backend.models.enums.TipoAdministrador;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Entidad que representa a un Administrador del sistema.
 * Extiende de {@link Usuario}.
 */
@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "id_administrador") // PK = FK a usuarios.id_usuario
public class Administrador extends Usuario {

    @Column(name = "codigo_admin", nullable = false)
    private String codigoAdmin;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_admin", nullable = false)
    private TipoAdministrador tipoAdmin;

    // @Column(name = "area_responsable")
    // private String areaResponsable;

    // @Column(name = "nivel_acceso")
    // private Integer nivelAcceso;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta = LocalDate.now();

    // Getters y Setters
    public String getCodigoAdmin() {
        return codigoAdmin;
    }

    public void setCodigoAdmin(String codigoAdmin) {
        this.codigoAdmin = codigoAdmin;
    }

    public TipoAdministrador getTipoAdmin() {
        return tipoAdmin;
    }

    public void setTipoAdmin(TipoAdministrador tipoAdmin) {
        this.tipoAdmin = tipoAdmin;
    }

    // public String getAreaResponsable() {
    //     return areaResponsable;
    // }

    // public void setAreaResponsable(String areaResponsable) {
    //     this.areaResponsable = areaResponsable;
    // }

    // public Integer getNivelAcceso() {
    //     return nivelAcceso;
    // }

    // public void setNivelAcceso(Integer nivelAcceso) {
    //     this.nivelAcceso = nivelAcceso;
    // }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}

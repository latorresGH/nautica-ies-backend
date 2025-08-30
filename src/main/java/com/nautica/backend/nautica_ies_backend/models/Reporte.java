package com.nautica.backend.nautica_ies_backend.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long idReporte;

    @NotBlank
    @Column(name = "nombre_reporte", length = 100, nullable = false)
    private String nombreReporte;

    @NotBlank
    @Column(name = "area_responsable", length = 100, nullable = false)
    private String areaResponsable;

    @NotBlank
    @Column(name = "formato", length = 50, nullable = false)
    private String formato; // PDF, XLSX, etc.

    @Column(name = "fecha_generacion")
    private LocalDate fechaGeneracion = LocalDate.now();

    @Column(name = "archivo_direccion")
    private String archivoDireccion;

    // Getters y Setters
    public Long getIdReporte() {
        return idReporte;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public String getAreaResponsable() {
        return areaResponsable;
    }

    public void setAreaResponsable(String areaResponsable) {
        this.areaResponsable = areaResponsable;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getArchivoDireccion() {
        return archivoDireccion;
    }

    public void setArchivoDireccion(String archivoDireccion) {
        this.archivoDireccion = archivoDireccion;
    }
}

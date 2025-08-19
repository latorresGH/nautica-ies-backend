package com.nautica.backend.nautica_ies_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "embarcaciones")
public class Embarcacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_embarcacion")
    private Long idEmbarcacion;

    @Column(name = "num_matricula")
    @NotBlank
    private String numMatricula;

    @NotBlank
    private String nombre;
    @Column(name = "marca_casco")
    private String marcaCasco;
    @Column(name = "modelo_casco")
    private String modeloCasco;

    private String descripcion;

    @Column(name = "marca_motor")
    private String marcaMotor;
    @Column(name = "modelo_motor")
    private String modeloMotor;
    @Column(name = "num_motor")
    private String numMotor;
    @Column(name = "potencia_motor")
    private Integer potenciaMotor;

    @Column(name = "fecha_alta")
    @NotBlank
    private LocalDate fechaAlta = LocalDate.now();

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    // Relación 1:1 con Cliente (lado NO dueño de la FK)
    @OneToOne(mappedBy = "embarcacion")
    private Cliente cliente;

    // getters/setters
    public Long getIdEmbarcacion() {
        return idEmbarcacion;
    }

    public void setIdEmbarcacion(Long idEmbarcacion) {
        this.idEmbarcacion = idEmbarcacion;
    }

    public String getNumMatricula() {
        return numMatricula;
    }

    public void setNumMatricula(String numMatricula) {
        this.numMatricula = numMatricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarcaCasco() {
        return marcaCasco;
    }

    public void setMarcaCasco(String marcaCasco) {
        this.marcaCasco = marcaCasco;
    }

    public String getModeloCasco() {
        return modeloCasco;
    }

    public void setModeloCasco(String modeloCasco) {
        this.modeloCasco = modeloCasco;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarcaMotor() {
        return marcaMotor;
    }

    public void setMarcaMotor(String marcaMotor) {
        this.marcaMotor = marcaMotor;
    }

    public String getModeloMotor() {
        return modeloMotor;
    }

    public void setModeloMotor(String modeloMotor) {
        this.modeloMotor = modeloMotor;
    }

    public String getNumMotor() {
        return numMotor;
    }

    public void setNumMotor(String numMotor) {
        this.numMotor = numMotor;
    }

    public Integer getPotenciaMotor() {
        return potenciaMotor;
    }

    public void setPotenciaMotor(Integer potenciaMotor) {
        this.potenciaMotor = potenciaMotor;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}

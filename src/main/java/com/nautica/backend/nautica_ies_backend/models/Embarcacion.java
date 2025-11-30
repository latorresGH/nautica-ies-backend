package com.nautica.backend.nautica_ies_backend.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nautica.backend.nautica_ies_backend.models.enums.TipoCama;

import jakarta.persistence.*;

@Entity
@Table(name = "embarcaciones", indexes = {
    @Index(name = "idx_embarcaciones_num_matricula", columnList = "num_matricula")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Embarcacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_embarcacion")
  private Long idEmbarcacion;

  @Column(name = "matricula", length = 30)
  private String numMatricula;

  @Column(name = "nombre", length = 50, nullable = false)
  private String nombre;

  @Column(name = "marca_casco", length = 50)
  private String marcaCasco;

  @Column(name = "modelo_casco", length = 50)
  private String modeloCasco;

  @Column(name = "descripcion")
  private String descripcion;

  @Column(name = "marca_motor", length = 50)
  private String marcaMotor;

  @Column(name = "modelo_motor", length = 50)
  private String modeloMotor;

  @Column(name = "num_motor", length = 50)
  private String numMotor;

  @Column(name = "potencia_motor")
  private Integer potenciaMotor;

  @Column(name = "fecha_alta")
  private LocalDate fechaAlta = LocalDate.now();

  @Column(name = "fecha_baja")
  private LocalDate fechaBaja;
  
  @Column(name = "tipo_cama")
@Enumerated(EnumType.STRING)
private TipoCama tipoCama;

  /**
   * Asociación N:M con Usuario a través de la entidad puente.
   */
  @OneToMany(mappedBy = "embarcacion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UsuarioEmbarcacion> usuarios = new ArrayList<>();

  // --- getters/setters ---
  public TipoCama getTipoCama() {
    return tipoCama;
  }

  public void setTipoCama(TipoCama tipoCama) {
    this.tipoCama = tipoCama;
  }

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

  public List<UsuarioEmbarcacion> getUsuarios() {
    return usuarios;
  }

  public void setUsuarios(List<UsuarioEmbarcacion> usuarios) {
    this.usuarios = usuarios;
  }
}

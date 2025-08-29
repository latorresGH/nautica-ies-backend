package com.nautica.backend.nautica_ies_backend.models;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "turnos",
    indexes = {
      @Index(name = "idx_turno_fecha", columnList = "fecha_turno"),
      @Index(name = "idx_turno_cliente", columnList = "id_cliente"),
      @Index(name = "idx_turno_operario", columnList = "id_operario")
    }
)
public class Turno {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_turno")
  private Long id;

  @NotNull
  @Column(name = "fecha_turno", nullable = false)
  private LocalDate fecha;

  @NotNull
  @Column(name = "hora_inicio", nullable = false)
  private LocalTime horaInicio;

  @NotNull
  @Column(name = "hora_fin", nullable = false)
  private LocalTime horaFin;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_cliente", nullable = false)
  private Cliente cliente;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_embarcacion", nullable = false)
  private Embarcacion embarcacion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_operario", nullable = false)
  private Operario operario;

  // getters/setters
  public Long getIdTurno() { return id; }
  public void setId(Long id) { this.id = id; }

  public LocalDate getFecha() { return fecha; }
  public void setFecha(LocalDate fecha) { this.fecha = fecha; }

  public LocalTime getHoraInicio() { return horaInicio; }
  public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

  public LocalTime getHoraFin() { return horaFin; }
  public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

  public Cliente getCliente() { return cliente; }
  public void setCliente(Cliente cliente) { this.cliente = cliente; }

  public Embarcacion getEmbarcacion() { return embarcacion; }
  public void setEmbarcacion(Embarcacion embarcacion) { this.embarcacion = embarcacion; }

  public Operario getOperario() { return operario; }
  public void setOperario(Operario operario) { this.operario = operario; }
}

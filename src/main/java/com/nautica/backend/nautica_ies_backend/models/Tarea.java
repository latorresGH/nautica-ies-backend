// src/main/java/com/nautica/backend/nautica_ies_backend/models/Tarea.java
package com.nautica.backend.nautica_ies_backend.models;

import java.time.LocalDate;
import java.time.LocalTime;

import com.nautica.backend.nautica_ies_backend.models.enums.EstadoTarea;
import com.nautica.backend.nautica_ies_backend.models.enums.TipoTarea;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tarea", // si la tabla es "Tarea" con mayúscula, Postgres la crea en minúsculas salvo
                       // comillas; usa el nombre real
        uniqueConstraints = @UniqueConstraint(name = "uk_tarea_numero", columnNames = "numero_tarea"))
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Long idTarea;

    @NotNull
    @Column(name = "numero_tarea", nullable = false, unique = true)
    private Integer numeroTarea;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarea", nullable = false)
    private TipoTarea tipoTarea; // LAVADO | BOTADO

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_operario", referencedColumnName = "id_operario", nullable = false)
    private Operario operario;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoTarea estado;

    // En Tarea
    @ManyToOne(optional = true)
    @JoinColumn(name = "id_turno")
    private Turno turno;

    // getters/setters
    public Long getIdTarea() {
        return idTarea;
    }

    public Integer getNumeroTarea() {
        return numeroTarea;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public void setNumeroTarea(Integer numeroTarea) {
        this.numeroTarea = numeroTarea;
    }

    public TipoTarea getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(TipoTarea tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public Operario getOperario() {
        return operario;
    }

    public void setOperario(Operario operario) {
        this.operario = operario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }
}

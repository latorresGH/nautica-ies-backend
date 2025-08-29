package com.nautica.backend.nautica_ies_backend.models;

import java.time.LocalDate;

import com.nautica.backend.nautica_ies_backend.models.enums.TipoCliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Entidad que representa al Cliente en el sistema.
 * Extiende de {@link Usuario}.
 */
@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id_cliente") // PK = FK a usuarios.id_usuario
public class Cliente extends Usuario {

    @Column(name = "num_cliente", unique = true, nullable = false)
    private Integer numCliente;

    @Column(name = "estado_cliente", nullable = false)
    private String estadoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false, length = 20)
    private TipoCliente tipoCliente;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta = LocalDate.now();

    /**
     * Relación 1:1 con Embarcacion (dueño de la FK).
     */
    @OneToOne
    @JoinColumn(name = "id_embarcacion", unique = true)
    private Embarcacion embarcacion;

    

    public Integer getNumCliente() {
        return numCliente;
    }

    public void setNumCliente(Integer numCliente) {
        this.numCliente = numCliente;
    }

    public String getEstadoCliente() {
        return estadoCliente;
    }

    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Embarcacion getEmbarcacion() {
        return embarcacion;
    }

    public void setEmbarcacion(Embarcacion embarcacion) {
        this.embarcacion = embarcacion;
    }
}

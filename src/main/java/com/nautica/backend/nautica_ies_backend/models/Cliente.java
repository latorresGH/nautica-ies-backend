package com.nautica.backend.nautica_ies_backend.models;

import java.time.LocalDate;

import com.nautica.backend.nautica_ies_backend.models.enums.TipoCliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
/**
 * Entidad que representa al Cliente en el sistema.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

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
     *Relación 1:1 con Embarcacion (dueño de la FK)
     */ 
    @OneToOne
    @JoinColumn(name = "id_embarcacion", unique = true)
    private Embarcacion embarcacion;

    /**
     * Obtener el ID único del cliente.
     * @return idCliente
     */
    public Long getIdCliente() {
        return idCliente;
    }
    /**
     * Establece el ID del cliente.
     * @param idCliente ID único a asignar al cliente.
     */
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    /**
     * Obtiene el número identificador del cliente.
     * @return numCliente.
     */
    public Integer getNumCliente() {
        return numCliente;
    }
    /**
     * Establece el número identificador del cliente.
     * @param numCliente Numero del cliente.
     */
    public void setNumCliente(Integer numCliente) {
        this.numCliente = numCliente;
    }
    /**
     * Obtiene el estado actual del cliente (example: activo, inactivo).
     * @return estadoCliente
     */
    public String getEstadoCliente() {
        return estadoCliente;
    }
    /**
     * Estable el estado actual del cliente.
     * @param estadoCliente Estado a asignar al cliente.
     */
    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }
    /**
     * Obtiene el tipo de cliente (particular, empresa, ect).
     * @return tipoCliente
     */
    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }
    /**
     * Establece el tipo de cliente
     * @param tipoCliente Tipo a asignar al cliente.
     */
    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
    /**
     * Obtiene la fecha de alta del cliente en el sistema.
     * @return fechaAlta
     */
    public LocalDate getFechaAlta() {
        return fechaAlta;
    }
    /**
     * Establece la fecha de alta del cliente.
     * @param fechaAlta Fecha en la que se dio de alta el cliente.
     */
    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
    /**
     * Obtiene la embarcación asociada al cliente.
     * @return
     */
    public Embarcacion getEmbarcacion() {
        return embarcacion;
    }
    /**
     * Asocia una embarcación al cliente.
     * @param embarcacion Embarcación a asociar.
     */
    public void setEmbarcacion(Embarcacion embarcacion) {
        this.embarcacion = embarcacion;
    }
}

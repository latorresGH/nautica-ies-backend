package com.nautica.backend.nautica_ies_backend.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
/**
 * Entidad que representa una embarcación registrada en el sistema.
 * 
 * Contiene información técnica y administrativa de la embarcación, incluyendo
 * datos de motor, casco, fechas de alta/baja y la relación con el cliente propietario.
 */
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

    /**
     * Relación 1:1 con Cliente (lado NO dueño de la FK)  
     */ 
    @OneToOne(mappedBy = "embarcacion")
    private Cliente cliente;

    /**
     * Obtiene el ID único de la embarcación.
     * @return idEmbarcacion.
     */
    public Long getIdEmbarcacion() {
        return idEmbarcacion;
    }
    /**
     * Establece el ID único de la embarcación
     * @param idEmbarcacion ID a asignar
     */
    public void setIdEmbarcacion(Long idEmbarcacion) {
        this.idEmbarcacion = idEmbarcacion;
    }
    /**
     * Obtiene el número de matrícula de la embarcación.
     * @return numMatricula.
     */
    public String getNumMatricula() {
        return numMatricula;
    }
    /**
     * Establece el número de matrícula de la embarcación.
     * @param numMatricula Matrícula a asignar.
     */
    public void setNumMatricula(String numMatricula) {
        this.numMatricula = numMatricula;
    }
    /**
     * Obtiene el nombre de la embarcación.
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Establece el nombre de la embarcación.
     * @param nombre Nombre a asignar.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /**
     * Obtiene la marca del casco de la embarcación.
     * @return marcaCasco
     */
    public String getMarcaCasco() {
        return marcaCasco;
    }
    /**
     * Establece la marca del casco de la embarcación.
     * @param marcaCasco Maraca del casco a asignar.
     */
    public void setMarcaCasco(String marcaCasco) {
        this.marcaCasco = marcaCasco;
    }
    /**
     * Obtiene el modelo del casco de la embarcación.
     * @return modeloCasco
     */
    public String getModeloCasco() {
        return modeloCasco;
    }
    /**
    * Establece el modelo del casco de la embarcación.
     * @param modeloCasco Modelo de casco a asignar.
     */
    public void setModeloCasco(String modeloCasco) {
        this.modeloCasco = modeloCasco;
    }
     /**
     * Obtiene la descripción general de la embarcación.
     * 
     * @return descripcion.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción general de la embarcación.
     * 
     * @param descripcion Descripción a asignar.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la marca del motor de la embarcación.
     * 
     * @return marcaMotor.
     */
    public String getMarcaMotor() {
        return marcaMotor;
    }

    /**
     * Establece la marca del motor de la embarcación.
     * 
     * @param marcaMotor Marca del motor a asignar.
     */
    public void setMarcaMotor(String marcaMotor) {
        this.marcaMotor = marcaMotor;
    }

    /**
     * Obtiene el modelo del motor de la embarcación.
     * 
     * @return modeloMotor.
     */
    public String getModeloMotor() {
        return modeloMotor;
    }

    /**
     * Establece el modelo del motor de la embarcación.
     * 
     * @param modeloMotor Modelo del motor a asignar.
     */
    public void setModeloMotor(String modeloMotor) {
        this.modeloMotor = modeloMotor;
    }

    /**
     * Obtiene el número de serie del motor de la embarcación.
     * 
     * @return numMotor.
     */
    public String getNumMotor() {
        return numMotor;
    }

    /**
     * Establece el número de serie del motor de la embarcación.
     * 
     * @param numMotor Número del motor a asignar.
     */
    public void setNumMotor(String numMotor) {
        this.numMotor = numMotor;
    }

    /**
     * Obtiene la potencia del motor (en CV o kW, según especificación).
     * 
     * @return potenciaMotor.
     */
    public Integer getPotenciaMotor() {
        return potenciaMotor;
    }

    /**
     * Establece la potencia del motor.
     * 
     * @param potenciaMotor Potencia a asignar.
     */
    public void setPotenciaMotor(Integer potenciaMotor) {
        this.potenciaMotor = potenciaMotor;
    }

    /**
     * Obtiene la fecha de alta de la embarcación en el sistema.
     * 
     * @return fechaAlta.
     */
    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    /**
     * Establece la fecha de alta de la embarcación en el sistema.
     * 
     * @param fechaAlta Fecha de alta a asignar.
     */
    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    /**
     * Obtiene la fecha de baja de la embarcación (si aplica).
     * 
     * @return fechaBaja.
     */
    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    /**
     * Establece la fecha de baja de la embarcación.
     * 
     * @param fechaBaja Fecha de baja a asignar.
     */
    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    /**
     * Obtiene el cliente asociado a la embarcación.
     * 
     * @return cliente.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Asocia un cliente a la embarcación.
     * 
     * @param cliente Cliente a asignar.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}

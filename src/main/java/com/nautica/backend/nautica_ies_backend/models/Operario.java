package com.nautica.backend.nautica_ies_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "operarios")
@PrimaryKeyJoinColumn(name = "id_operario") // PK = FK a usuarios.id_usuario
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Operario extends Usuario {

    @NotBlank
    @Column(name = "legajo", nullable = false, unique = true)
    private String legajo;

    @NotBlank
    @Column(name = "puesto", nullable = false)
    private String puesto;

    // getters/setters
    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }
}

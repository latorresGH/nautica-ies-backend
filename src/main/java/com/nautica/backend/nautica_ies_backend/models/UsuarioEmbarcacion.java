package com.nautica.backend.nautica_ies_backend.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nautica.backend.nautica_ies_backend.models.enums.RolEnEmbarcacion;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario_embarcaciones", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_embarcacion", columnNames = { "id_usuario", "id_embarcacion" })
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UsuarioEmbarcacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK simple

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({ "embaracaciones" }) // Evita la recursión
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_embarcacion", nullable = false)
    @JsonIgnoreProperties({ "usuarios" }) // Evita la recursión
    private Embarcacion embarcacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_en_embarcacion", length = 20, nullable = false)
    private RolEnEmbarcacion rolEnEmbarcacion;

    @Column(name = "desde")
    private LocalDate desde;

    @Column(name = "hasta")
    private LocalDate hasta;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Embarcacion getEmbarcacion() {
        return embarcacion;
    }

    public void setEmbarcacion(Embarcacion embarcacion) {
        this.embarcacion = embarcacion;
    }

    public RolEnEmbarcacion getRolEnEmbarcacion() {
        return rolEnEmbarcacion;
    }

    public void setRolEnEmbarcacion(RolEnEmbarcacion rolEnEmbarcacion) {
        this.rolEnEmbarcacion = rolEnEmbarcacion;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public void setDesde(LocalDate desde) {
        this.desde = desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public void setHasta(LocalDate hasta) {
        this.hasta = hasta;
    }
}

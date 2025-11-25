// src/main/java/com/nautica/backend/nautica_ies_backend/models/TarifaCama.java
package com.nautica.backend.nautica_ies_backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nautica.backend.nautica_ies_backend.models.enums.TipoCama;

import jakarta.persistence.*;

@Entity
@Table(
    name = "tarifas_cama",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tarifa_tipo_mes", columnNames = { "tipo_cama", "numero_mes" })
    }
)
public class TarifaCama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private Long idTarifa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama", nullable = false, length = 20)
    private TipoCama tipoCama;

    @Column(name = "numero_mes", nullable = false)
    private LocalDate numeroMes;  // siempre normalizado al día 1

    @Column(name = "precio", nullable = false, precision = 14, scale = 2)
    private BigDecimal precio;

    public Long getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(Long idTarifa) {
        this.idTarifa = idTarifa;
    }

    public TipoCama getTipoCama() {
        return tipoCama;
    }

    public void setTipoCama(TipoCama tipoCama) {
        this.tipoCama = tipoCama;
    }

    public LocalDate getNumeroMes() {
        return numeroMes;
    }

    public void setNumeroMes(LocalDate numeroMes) {
        this.numeroMes = numeroMes;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}

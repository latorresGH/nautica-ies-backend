package com.nautica.backend.nautica_ies_backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;
import com.nautica.backend.nautica_ies_backend.models.enums.FormaPago;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "cuotas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cuota_cli_emb_mes", columnNames = { "id_cliente", "id_embarcacion", "numero_mes" })
}, indexes = {
        @Index(name = "ix_cuota_cli_mes", columnList = "id_cliente,numero_mes"),
        @Index(name = "ix_cuota_emb_mes", columnList = "id_embarcacion,numero_mes")
})
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Long idCuota;

    @NotNull
    @Column(name = "numero_pago", nullable = false)
    private Integer numeroPago; // correlativo por cliente+embarcación

    @NotNull
    @Column(name = "numero_mes", nullable = false)
    private LocalDate numeroMes; // usar día 1 del mes

    @Column(name = "fecha_pago")
    private LocalDate fechaPago; // null si pendiente

    @NotNull
    @Positive
    @Column(name = "monto", nullable = false, precision = 14, scale = 2)
    private BigDecimal monto;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_embarcacion", nullable = false)
    private Embarcacion embarcacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago", length = 50)
    private FormaPago formaPago; // puede ser null si aún no se pagó

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuota", nullable = false)
    private EstadoCuota estadoCuota; // PENDIENTE/PAGADA/VENCIDA

    @Column(nullable = false)
    private String periodo;


    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getPeriodo() {
        return periodo;
    }

    // Getters & setters
    public Long getIdCuota() {
        return idCuota;
    }

    public Integer getNumeroPago() {
        return numeroPago;
    }

    public void setNumeroPago(Integer numeroPago) {
        this.numeroPago = numeroPago;
    }

    public LocalDate getNumeroMes() {
        return numeroMes;
    }

    public void setNumeroMes(LocalDate numeroMes) {
        this.numeroMes = numeroMes;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Embarcacion getEmbarcacion() {
        return embarcacion;
    }

    public void setEmbarcacion(Embarcacion embarcacion) {
        this.embarcacion = embarcacion;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public EstadoCuota getEstadoCuota() {
        return estadoCuota;
    }

    public void setEstadoCuota(EstadoCuota estadoCuota) {
        this.estadoCuota = estadoCuota;
    }
}

package com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class ReportesAdminDTO {

    private LocalDate desde;
    private LocalDate hasta;

    private ClientesReportDTO clientes;
    private EmbarcacionesReportDTO embarcaciones;
    private TurnosReportDTO turnos;
    private EconomicoReportDTO economico;
    private CalendarioReportDTO calendario;
    private AnunciosReportDTO anuncios;
    private EcommerceReportDTO ecommerce;

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

    public ClientesReportDTO getClientes() {
        return clientes;
    }

    public void setClientes(ClientesReportDTO clientes) {
        this.clientes = clientes;
    }

    public EmbarcacionesReportDTO getEmbarcaciones() {
        return embarcaciones;
    }

    public void setEmbarcaciones(EmbarcacionesReportDTO embarcaciones) {
        this.embarcaciones = embarcaciones;
    }

    public TurnosReportDTO getTurnos() {
        return turnos;
    }

    public void setTurnos(TurnosReportDTO turnos) {
        this.turnos = turnos;
    }

    public EconomicoReportDTO getEconomico() {
        return economico;
    }

    public void setEconomico(EconomicoReportDTO economico) {
        this.economico = economico;
    }

    public CalendarioReportDTO getCalendario() {
        return calendario;
    }

    public void setCalendario(CalendarioReportDTO calendario) {
        this.calendario = calendario;
    }

    public AnunciosReportDTO getAnuncios() {
        return anuncios;
    }

    public void setAnuncios(AnunciosReportDTO anuncios) {
        this.anuncios = anuncios;
    }

    public EcommerceReportDTO getEcommerce() {
        return ecommerce;
    }

    public void setEcommerce(EcommerceReportDTO ecommerce) {
        this.ecommerce = ecommerce;
    }

    // ============================================================
    // SUB-DTOs
    // ============================================================

    // ---------- A. CLIENTES ----------
    public static class ClientesReportDTO {
        public long totalActivos;
        public long totalInactivos;
        public long totalClientes;

        public Map<YearMonth, Long> altasPorMes;

        public Map<YearMonth, BigDecimal> deudaTotalPorMes;
        public long clientesConCuotasVencidas;
        public List<ClienteDeudorDTO> topDeudores;
        public List<PagoLineaTiempoDTO> lineaPagosImpagos;

        public long clientesNuevosUltimos30Dias;
        public long clientesNuevosUltimoAnio;
        public Map<String, Long> clientesNuevosPorTemporada;

        public long clientesSinActividad;
    }

    public static class ClienteDeudorDTO {
        public Long idCliente;
        public String nombreCompleto;
        public BigDecimal deudaTotal;
    }

    public static class PagoLineaTiempoDTO {
        public YearMonth periodo;
        public BigDecimal montoPagado;
        public BigDecimal montoImpago;
    }

    // ---------- B. EMBARCACIONES / CAMAS ----------
    public static class EmbarcacionesReportDTO {
        public long totalEmbarcaciones;
        public Map<Long, Long> embarcacionesPorCliente;

        public Map<YearMonth, Long> ocupacionPorMes;
        public Map<String, Long> demandaPorTipoCama;
        public List<YearMonth> mesesMayorDemanda;
    }

    // ---------- C. TURNOS ----------
    public static class TurnosReportDTO {
        public Map<String, Long> turnosPorTipo;
        public Map<LocalDate, Long> turnosPorDia;

        public long totalMesActual;
        public long totalMesAnterior;
        public double variacionPorcentual;
        public List<LocalDate> diasMayorActividad;

        public long turnosCancelados;
        public double tasaCancelacion;
        public Map<String, Long> demandaPorFranjaHoraria;
    }

    // ---------- E. ECONÓMICO ----------
    public static class EconomicoReportDTO {
        public Map<YearMonth, BigDecimal> ingresosPorMes;
        public Map<String, BigDecimal> ingresosTrimestrales;
        public BigDecimal proyeccionIngresos;

        public long cuotasGeneradas;
        public long cuotasPagadas;
        public double tasaPago;
        public double porcentajeMorosidad;
    }

    // ---------- F. CALENDARIO ----------
    public static class CalendarioReportDTO {
        public Map<LocalDate, Boolean> diaHabilitado;
        public Map<LocalDate, Double> horasEfectivasPorDia;
        public Map<LocalDate, Long> turnosPorDia;
        public Map<LocalDate, Long> turnosPerdidosPorCierre;
    }

    // ---------- G. ANUNCIOS ----------
    public static class AnunciosReportDTO {
        public long totalAnuncios;
        public Map<LocalDate, Long> anunciosPorFecha;
    }

    // ---------- MINI E-COMMERCE ----------
    public static class EcommerceReportDTO {
        public Map<LocalDate, BigDecimal> ventasPorDia;
        public Map<YearMonth, BigDecimal> ventasPorMes;
        public BigDecimal totalPeriodo;
        public BigDecimal ticketPromedio;

        public Map<String, Long> pedidosPorEstado;
        public Map<YearMonth, Long> pedidosPorMes;

        public List<ProductoVentaDTO> topProductos;
        public List<ProductoVentaDTO> productosPocasVentas;
        public List<ProductoVentaDTO> productosConMasCancelaciones;

        public long clientesQueCompran;
        public long clientesQueNoCompran;
        public List<ClienteEcommerceDTO> rankingClientesGasto;
        public Map<String, Long> segmentosClientes;
    }

    public static class ProductoVentaDTO {
        public Long idProducto;
        public String nombre;
        public long cantidad;
        public BigDecimal totalFacturado;
    }

    public static class ClienteEcommerceDTO {
        public Long idCliente;
        public String nombreCompleto;
        public BigDecimal totalGastado;
    }
}

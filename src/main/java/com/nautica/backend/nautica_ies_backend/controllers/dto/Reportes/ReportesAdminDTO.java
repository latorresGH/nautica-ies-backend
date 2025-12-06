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

    // getters & setters
    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate desde) { this.desde = desde; }

    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }

    public ClientesReportDTO getClientes() { return clientes; }
    public void setClientes(ClientesReportDTO clientes) { this.clientes = clientes; }

    public EmbarcacionesReportDTO getEmbarcaciones() { return embarcaciones; }
    public void setEmbarcaciones(EmbarcacionesReportDTO embarcaciones) { this.embarcaciones = embarcaciones; }

    public TurnosReportDTO getTurnos() { return turnos; }
    public void setTurnos(TurnosReportDTO turnos) { this.turnos = turnos; }

    public EconomicoReportDTO getEconomico() { return economico; }
    public void setEconomico(EconomicoReportDTO economico) { this.economico = economico; }

    public CalendarioReportDTO getCalendario() { return calendario; }
    public void setCalendario(CalendarioReportDTO calendario) { this.calendario = calendario; }

    public AnunciosReportDTO getAnuncios() { return anuncios; }
    public void setAnuncios(AnunciosReportDTO anuncios) { this.anuncios = anuncios; }

    public EcommerceReportDTO getEcommerce() { return ecommerce; }
    public void setEcommerce(EcommerceReportDTO ecommerce) { this.ecommerce = ecommerce; }

    // ================== SUB-DTOs ==================

    // ---------- 1. CLIENTES ----------
    public static class ClientesReportDTO {
        public long totalActivos;
        public long totalInactivos;
        public long totalClientes;

        // "2025-01" -> altas
        public Map<YearMonth, Long> altasPorMes;

        // deudas
        public Map<YearMonth, BigDecimal> deudaTotalPorMes;
        public long clientesConCuotasVencidas;
        public List<ClienteDeudorDTO> topDeudores;
        public List<PagoLineaTiempoDTO> lineaPagosImpagos;

        // crecimiento
        public long clientesNuevosUltimos30Dias;
        public long clientesNuevosUltimoAnio;
        public Map<String, Long> clientesNuevosPorTemporada;

        // actividad
        public long clientesSinActividad; // sin turnos ni pagos en X meses
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

    // ---------- 2. EMBARCACIONES / CAMAS ----------
    public static class EmbarcacionesReportDTO {
        public long totalEmbarcaciones;
        // idCliente -> cantidad de embarcaciones (en tu modelo ahora es 0 o 1)
        public Map<Long, Long> embarcacionesPorCliente;

        // ocupación por mes (número de embarcaciones asignadas a camas, aprox)
        public Map<YearMonth, Long> ocupacionPorMes;
        public Map<String, Long> demandaPorTipoCama; // TipoCama.name() -> count
        public List<YearMonth> mesesMayorDemanda;
    }

    // ---------- 3. TURNOS ----------
    public static class TurnosReportDTO {
        public Map<String, Long> turnosPorTipo;    // "LAVADO"/"BOTADO"/"OTRO" (desde Tarea)
        public Map<LocalDate, Long> turnosPorDia;

        public long totalMesActual;
        public long totalMesAnterior;
        public double variacionPorcentual;
        public List<LocalDate> diasMayorActividad;

        public long turnosCancelados;
        public double tasaCancelacion;
        public Map<String, Long> demandaPorFranjaHoraria; // "08:00-10:00" -> count
    }

    // ---------- 5. ECONÓMICO / CUOTAS ----------
    public static class EconomicoReportDTO {
        public Map<YearMonth, BigDecimal> ingresosPorMes;    // por cuotas pagadas
        public Map<String, BigDecimal> ingresosTrimestrales; // "2025-Q1" -> total
        public BigDecimal proyeccionIngresos;

        public long cuotasGeneradas;
        public long cuotasPagadas;
        public double tasaPago;
        public double porcentajeMorosidad;
    }

    // ---------- 6. CALENDARIO ----------
    public static class CalendarioReportDTO {
        public Map<LocalDate, Boolean> diaHabilitado;          // true/false
        public Map<LocalDate, Double> horasEfectivasPorDia;
        public Map<LocalDate, Long> turnosPorDia;
        public Map<LocalDate, Long> turnosPerdidosPorCierre;   // impacto de días cerrados
    }

    // ---------- 7. ANUNCIOS ----------
    public static class AnunciosReportDTO {
        public long totalAnuncios;
        public Map<LocalDate, Long> anunciosPorFecha;
    }

    // ---------- 8. E-COMMERCE ----------
    public static class EcommerceReportDTO {
        public Map<LocalDate, BigDecimal> ventasPorDia;
        public Map<YearMonth, BigDecimal> ventasPorMes;
        public BigDecimal totalPeriodo;
        public BigDecimal ticketPromedio;

        public Map<String, Long> pedidosPorEstado;
        public Map<YearMonth, Long> pedidosPorMes;
        public Map<String, Long> mediosPagoUsados;    // si más adelante lo agregás
        public Map<String, Long> fallosPorMedioPago;  // idem

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

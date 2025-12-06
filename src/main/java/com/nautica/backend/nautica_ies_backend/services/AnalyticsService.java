package com.nautica.backend.nautica_ies_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.AnunciosReportDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.CalendarioReportDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.ClienteDeudorDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.ClienteEcommerceDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.ClientesReportDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.EcommerceReportDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.EconomicoReportDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.EmbarcacionesReportDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.PagoLineaTiempoDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.ProductoVentaDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO.TurnosReportDTO;
import com.nautica.backend.nautica_ies_backend.models.Anuncio;
import com.nautica.backend.nautica_ies_backend.models.CierreExcepcional;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Cuota;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.HorarioOperacion;
import com.nautica.backend.nautica_ies_backend.models.Pedido;
import com.nautica.backend.nautica_ies_backend.models.PedidoProducto;
import com.nautica.backend.nautica_ies_backend.models.Producto;
import com.nautica.backend.nautica_ies_backend.models.Tarea;
import com.nautica.backend.nautica_ies_backend.models.Turno;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoTarea;
import com.nautica.backend.nautica_ies_backend.repository.AnuncioRepository;
import com.nautica.backend.nautica_ies_backend.repository.CierreExcepcionalRepository;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import com.nautica.backend.nautica_ies_backend.repository.CuotaRepository;
import com.nautica.backend.nautica_ies_backend.repository.EmbarcacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.HorarioOperacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.PedidoProductoRepository;
import com.nautica.backend.nautica_ies_backend.repository.PedidoRepository;
import com.nautica.backend.nautica_ies_backend.repository.ProductoRepository;
import com.nautica.backend.nautica_ies_backend.repository.TareaRepository;
import com.nautica.backend.nautica_ies_backend.repository.TurnoRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;

@Service
public class AnalyticsService {

    private final ClienteRepository clienteRepo;
    private final UsuarioRepository usuarioRepo;
    private final EmbarcacionRepository embarcacionRepo;
    private final TurnoRepository turnoRepo;
    private final TareaRepository tareaRepo;
    private final CuotaRepository cuotaRepo;
    private final CierreExcepcionalRepository cierreRepo;
    private final HorarioOperacionRepository horarioRepo;
    private final AnuncioRepository anuncioRepo;
    private final PedidoRepository pedidoRepo;
    private final PedidoProductoRepository pedidoProductoRepo;
    private final ProductoRepository productoRepo;

    public AnalyticsService(
            ClienteRepository clienteRepo,
            UsuarioRepository usuarioRepo,
            EmbarcacionRepository embarcacionRepo,
            TurnoRepository turnoRepo,
            TareaRepository tareaRepo,
            CuotaRepository cuotaRepo,
            CierreExcepcionalRepository cierreRepo,
            HorarioOperacionRepository horarioRepo,
            AnuncioRepository anuncioRepo,
            PedidoRepository pedidoRepo,
            PedidoProductoRepository pedidoProductoRepo,
            ProductoRepository productoRepo) {

        this.clienteRepo = clienteRepo;
        this.usuarioRepo = usuarioRepo;
        this.embarcacionRepo = embarcacionRepo;
        this.turnoRepo = turnoRepo;
        this.tareaRepo = tareaRepo;
        this.cuotaRepo = cuotaRepo;
        this.cierreRepo = cierreRepo;
        this.horarioRepo = horarioRepo;
        this.anuncioRepo = anuncioRepo;
        this.pedidoRepo = pedidoRepo;
        this.pedidoProductoRepo = pedidoProductoRepo;
        this.productoRepo = productoRepo;
    }

    public ReportesAdminDTO buildAdminDashboard(LocalDate desde, LocalDate hasta) {
        ReportesAdminDTO dto = new ReportesAdminDTO();
        dto.setDesde(desde);
        dto.setHasta(hasta);

        dto.setClientes(buildClientesReport(desde, hasta));
        dto.setEmbarcaciones(buildEmbarcacionesReport(desde, hasta));
        dto.setTurnos(buildTurnosReport(desde, hasta));
        dto.setEconomico(buildEconomicoReport(desde, hasta));
        dto.setCalendario(buildCalendarioReport(desde, hasta));
        dto.setAnuncios(buildAnunciosReport(desde, hasta));
        dto.setEcommerce(buildEcommerceReport(desde, hasta));

        return dto;
    }

    // ============================================================
    // 1. CLIENTES
    // ============================================================
    private ClientesReportDTO buildClientesReport(LocalDate desde, LocalDate hasta) {
        ClientesReportDTO c = new ClientesReportDTO();

        List<Cliente> todos = clienteRepo.findAll();

        c.totalClientes = todos.size();
        long activos = todos.stream()
                .filter(cl -> Boolean.TRUE.equals(cl.getActivo()))
                .count();
        c.totalActivos = activos;
        c.totalInactivos = c.totalClientes - activos;

        // Altas por mes (según fechaAlta)
        Map<YearMonth, Long> altasPorMes = todos.stream()
                .filter(cl -> cl.getFechaAlta() != null
                        && !cl.getFechaAlta().isBefore(desde)
                        && !cl.getFechaAlta().isAfter(hasta))
                .collect(Collectors.groupingBy(
                        cl -> YearMonth.from(cl.getFechaAlta()),
                        Collectors.counting()
                ));
        c.altasPorMes = altasPorMes;

        // Deuda total por mes, clientes con cuotas vencidas, ranking de deudores
        List<Cuota> cuotas = cuotaRepo.findAll();

        // deuda total por mes (solo VENCIDA o PENDIENTE, según numeroMes en rango)
        Map<YearMonth, BigDecimal> deudaPorMes = cuotas.stream()
                .filter(q -> q.getNumeroMes() != null)
                .filter(q -> !q.getNumeroMes().isBefore(desde) && !q.getNumeroMes().isAfter(hasta))
                .filter(q -> q.getEstadoCuota() == EstadoCuota.vencida || q.getEstadoCuota() == EstadoCuota.pendiente)
                .collect(Collectors.groupingBy(
                        q -> YearMonth.from(q.getNumeroMes()),
                        Collectors.mapping(Cuota::getMonto,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        c.deudaTotalPorMes = deudaPorMes;

        // clientes con cuotas vencidas
        c.clientesConCuotasVencidas = cuotas.stream()
                .filter(q -> q.getEstadoCuota() == EstadoCuota.vencida)
                .map(q -> q.getCliente().getIdUsuario())
                .distinct()
                .count();

        // ranking de deudores (sum deuda x cliente)
        Map<Long, BigDecimal> deudaPorCliente = cuotas.stream()
                .filter(q -> q.getEstadoCuota() == EstadoCuota.vencida || q.getEstadoCuota() == EstadoCuota.pendiente)
                .collect(Collectors.groupingBy(
                        q -> q.getCliente().getIdUsuario(),
                        Collectors.mapping(Cuota::getMonto,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        c.topDeudores = deudaPorCliente.entrySet().stream()
                .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    ClienteDeudorDTO d = new ClienteDeudorDTO();
                    d.idCliente = e.getKey();
                    clienteRepo.findById(e.getKey()).ifPresent(cli -> {
                        d.nombreCompleto = cli.getNombre() + " " + cli.getApellido();
                    });
                    d.deudaTotal = e.getValue();
                    return d;
                })
                .collect(Collectors.toList());

        // línea pagos/impagos por periodo (YearMonth)
        Map<YearMonth, BigDecimal> pagosPorMes = cuotas.stream()
                .filter(q -> q.getEstadoCuota() == EstadoCuota.pagada)
                .filter(q -> q.getFechaPago() != null)
                .collect(Collectors.groupingBy(
                        q -> YearMonth.from(q.getFechaPago()),
                        Collectors.mapping(Cuota::getMonto,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        Map<YearMonth, BigDecimal> impagosPorMes = deudaPorMes; // ya lo calculamos arriba

        Set<YearMonth> periodos = new HashSet<>();
        periodos.addAll(pagosPorMes.keySet());
        periodos.addAll(impagosPorMes.keySet());

        c.lineaPagosImpagos = periodos.stream()
                .sorted()
                .map(ym -> {
                    PagoLineaTiempoDTO p = new PagoLineaTiempoDTO();
                    p.periodo = ym;
                    p.montoPagado = pagosPorMes.getOrDefault(ym, BigDecimal.ZERO);
                    p.montoImpago = impagosPorMes.getOrDefault(ym, BigDecimal.ZERO);
                    return p;
                })
                .collect(Collectors.toList());

        // crecimiento del padrón
        LocalDate hoy = LocalDate.now();
        LocalDate hace30 = hoy.minusDays(30);
        LocalDate hace1anio = hoy.minusYears(1);

        c.clientesNuevosUltimos30Dias = todos.stream()
                .filter(cl -> cl.getFechaAlta() != null && !cl.getFechaAlta().isBefore(hace30))
                .count();

        c.clientesNuevosUltimoAnio = todos.stream()
                .filter(cl -> cl.getFechaAlta() != null && !cl.getFechaAlta().isBefore(hace1anio))
                .count();

        // clientes sin actividad (sin turnos ni cuotas) en X meses (por ej. 6)
        int mesesInactividad = 6;
        LocalDate corteInactividad = hoy.minusMonths(mesesInactividad);

        List<Turno> turnos = turnoRepo.findAll();
        List<Cuota> cuotasUlt = cuotas; // mismo list

        Set<Long> clientesConActividad = new HashSet<>();
        turnos.stream()
                .filter(t -> !t.getFecha().isBefore(corteInactividad))
                .forEach(t -> clientesConActividad.add(t.getCliente().getIdUsuario()));
        cuotasUlt.stream()
                .filter(q -> q.getFechaPago() != null && !q.getFechaPago().isBefore(corteInactividad))
                .forEach(q -> clientesConActividad.add(q.getCliente().getIdUsuario()));

        c.clientesSinActividad = todos.stream()
                .filter(cl -> !clientesConActividad.contains(cl.getIdUsuario()))
                .count();

        // TODO: clientesNuevosPorTemporada (si querés después jugamos con verano/invierno)
        c.clientesNuevosPorTemporada = Collections.emptyMap();

        return c;
    }

    // ============================================================
    // 2. EMBARCACIONES / CAMAS
    // ============================================================
    private EmbarcacionesReportDTO buildEmbarcacionesReport(LocalDate desde, LocalDate hasta) {
        EmbarcacionesReportDTO e = new EmbarcacionesReportDTO();

        List<Embarcacion> embarcaciones = embarcacionRepo.findAll();
        e.totalEmbarcaciones = embarcaciones.size();

        // cantidad embarcaciones por cliente (en tu modelo actual: 0 o 1)
        Map<Long, Long> porCliente = embarcaciones.stream()
                .filter(em -> em.getFechaAlta() != null)
                .collect(Collectors.groupingBy(
                        em -> {
                            // en tu modelo Cliente tiene 1:1 con Embarcacion
                            // usamos el Cliente a través de Cuota para algo más realista, pero acá asumimos
                            // que el dueño principal está ligado por Cliente.embarcacion
                            return em.getUsuarios().stream()
                                    .findFirst()
                                    .map(ue -> ue.getUsuario().getIdUsuario())
                                    .orElse(null);
                        },
                        Collectors.counting()
                ));
        porCliente.remove(null);
        e.embarcacionesPorCliente = porCliente;

        // ocupación por mes: contamos embarcaciones activas (fechaAlta <= finMes y fechaBaja null o > finMes)
        Map<YearMonth, Long> ocupacion = new HashMap<>();
        YearMonth ymDesde = YearMonth.from(desde);
        YearMonth ymHasta = YearMonth.from(hasta);
        YearMonth ym = ymDesde;
        while (!ym.isAfter(ymHasta)) {
            LocalDate finMes = ym.atEndOfMonth();
            long count = embarcaciones.stream()
                    .filter(em -> em.getFechaAlta() != null && !em.getFechaAlta().isAfter(finMes))
                    .filter(em -> em.getFechaBaja() == null || em.getFechaBaja().isAfter(finMes))
                    .count();
            ocupacion.put(ym, count);
            ym = ym.plusMonths(1);
        }
        e.ocupacionPorMes = ocupacion;

        // demanda por tipo de cama
        Map<String, Long> demandaPorTipoCama = embarcaciones.stream()
                .filter(em -> em.getTipoCama() != null)
                .collect(Collectors.groupingBy(
                        em -> em.getTipoCama().name(),
                        Collectors.counting()
                ));
        e.demandaPorTipoCama = demandaPorTipoCama;

        // meses mayor demanda
        long max = ocupacion.values().stream().mapToLong(Long::longValue).max().orElse(0L);
        e.mesesMayorDemanda = ocupacion.entrySet().stream()
                .filter(en -> en.getValue() == max && max > 0)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

        return e;
    }

    // ============================================================
    // 3. TURNOS (usamos Turno + Tarea)
    // ============================================================
    private TurnosReportDTO buildTurnosReport(LocalDate desde, LocalDate hasta) {
        TurnosReportDTO t = new TurnosReportDTO();

        List<Turno> turnos = turnoRepo.findAll();
        List<Tarea> tareas = tareaRepo.findAll();

        // Turnos por día
        Map<LocalDate, Long> turnosPorDia = turnos.stream()
                .filter(tr -> !tr.getFecha().isBefore(desde) && !tr.getFecha().isAfter(hasta))
                .collect(Collectors.groupingBy(
                        Turno::getFecha,
                        Collectors.counting()
                ));
        t.turnosPorDia = turnosPorDia;

        // Turnos por tipo (lo interpretamos desde Tarea.tipoTarea)
        Map<String, Long> porTipo = tareas.stream()
                .filter(ta -> ta.getTipoTarea() != null)
                .filter(ta -> !ta.getFecha().isBefore(desde) && !ta.getFecha().isAfter(hasta))
                .collect(Collectors.groupingBy(
                        ta -> ta.getTipoTarea().name(),
                        Collectors.counting()
                ));
        t.turnosPorTipo = porTipo;

        // totales mes actual vs anterior
        LocalDate hoy = LocalDate.now();
        YearMonth ymActual = YearMonth.from(hoy);
        YearMonth ymAnterior = ymActual.minusMonths(1);

        long totalMesActual = turnos.stream()
                .filter(tr -> YearMonth.from(tr.getFecha()).equals(ymActual))
                .count();
        long totalMesAnterior = turnos.stream()
                .filter(tr -> YearMonth.from(tr.getFecha()).equals(ymAnterior))
                .count();

        t.totalMesActual = totalMesActual;
        t.totalMesAnterior = totalMesAnterior;
        if (totalMesAnterior == 0) {
            t.variacionPorcentual = 0;
        } else {
            t.variacionPorcentual = ((double) (totalMesActual - totalMesAnterior) / totalMesAnterior) * 100.0;
        }

        // días con mayor actividad
        long max = turnosPorDia.values().stream().mapToLong(Long::longValue).max().orElse(0L);
        t.diasMayorActividad = turnosPorDia.entrySet().stream()
                .filter(en -> en.getValue() == max && max > 0)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

        // cancelaciones: usamos Tarea.estado = cancelado
        long tareasCanceladas = tareas.stream()
                .filter(ta -> ta.getEstado() == EstadoTarea.cancelado)
                .count();
        long tareasTotales = tareas.size();
        t.turnosCancelados = tareasCanceladas;
        t.tasaCancelacion = tareasTotales == 0 ? 0 : (double) tareasCanceladas / tareasTotales;

        // demanda por franja horaria (ejemplo simple: 08-10, 10-12, 12-14, 14-16)
        Map<String, Long> demandaPorFranja = new HashMap<>();
        String[] franjas = { "08-10", "10-12", "12-14", "14-16", "16-18", "18-20" };

        for (String franja : franjas) {
            String[] partes = franja.split("-");
            int hIni = Integer.parseInt(partes[0]);
            int hFin = Integer.parseInt(partes[1]);
            LocalTime desdeH = LocalTime.of(hIni, 0);
            LocalTime hastaH = LocalTime.of(hFin, 0);

            long count = tareas.stream()
                    .filter(ta -> !ta.getFecha().isBefore(desde) && !ta.getFecha().isAfter(hasta))
                    .filter(ta -> ta.getHora() != null &&
                            !ta.getHora().isBefore(desdeH) &&
                            ta.getHora().isBefore(hastaH))
                    .count();
            demandaPorFranja.put(franja, count);
        }
        t.demandaPorFranjaHoraria = demandaPorFranja;

        return t;
    }

    // ============================================================
    // 5. ECONÓMICO / CUOTAS
    // ============================================================
    private EconomicoReportDTO buildEconomicoReport(LocalDate desde, LocalDate hasta) {
        EconomicoReportDTO e = new EconomicoReportDTO();

        List<Cuota> cuotas = cuotaRepo.findAll();

        // ingresos por mes (solo pagadas, usando fechaPago)
        Map<YearMonth, BigDecimal> ingresosPorMes = cuotas.stream()
                .filter(q -> q.getEstadoCuota() == EstadoCuota.pagada)
                .filter(q -> q.getFechaPago() != null)
                .filter(q -> !q.getFechaPago().isBefore(desde) && !q.getFechaPago().isAfter(hasta))
                .collect(Collectors.groupingBy(
                        q -> YearMonth.from(q.getFechaPago()),
                        Collectors.mapping(Cuota::getMonto,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        e.ingresosPorMes = ingresosPorMes;

        // comparativo trimestral (simple: agrupamos por "YYYY-Qx")
        Map<String, BigDecimal> ingresosTrimestrales = new HashMap<>();
        ingresosPorMes.forEach((ym, monto) -> {
            int trimestre = (ym.getMonthValue() - 1) / 3 + 1;
            String key = ym.getYear() + "-Q" + trimestre;
            ingresosTrimestrales.merge(key, monto, BigDecimal::add);
        });
        e.ingresosTrimestrales = ingresosTrimestrales;

        // proyección de ingresos (ejemplo simple: promedio de últimos 3 meses * 12/periodos)
        List<BigDecimal> ultimosMontos = ingresosPorMes.entrySet().stream()
                .sorted(Map.Entry.<YearMonth, BigDecimal>comparingByKey().reversed())
                .limit(3)
                .map(Map.Entry::getValue)
                .toList();
        if (ultimosMontos.isEmpty()) {
            e.proyeccionIngresos = BigDecimal.ZERO;
        } else {
            BigDecimal suma = ultimosMontos.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal promedio = suma.divide(BigDecimal.valueOf(ultimosMontos.size()), BigDecimal.ROUND_HALF_UP);
            e.proyeccionIngresos = promedio; // si querés lo multiplicás por 12 para anual
        }

        // morosidad
        e.cuotasGeneradas = cuotas.size();
        e.cuotasPagadas = cuotas.stream().filter(q -> q.getEstadoCuota() == EstadoCuota.pagada).count();
        e.tasaPago = e.cuotasGeneradas == 0 ? 0.0 : (double) e.cuotasPagadas / e.cuotasGeneradas;

        long cuotasImpagas = cuotas.stream()
                .filter(q -> q.getEstadoCuota() == EstadoCuota.pendiente || q.getEstadoCuota() == EstadoCuota.vencida)
                .count();
        e.porcentajeMorosidad = e.cuotasGeneradas == 0 ? 0.0 : (double) cuotasImpagas / e.cuotasGeneradas * 100.0;

        return e;
    }

    // ============================================================
    // 6. CALENDARIO / CONFIG
    // ============================================================
    private CalendarioReportDTO buildCalendarioReport(LocalDate desde, LocalDate hasta) {
        CalendarioReportDTO c = new CalendarioReportDTO();

        // días habilitados vs cerrados -> combinamos HorarioOperacion por díaSemana + CierreExcepcional
        Map<LocalDate, Boolean> diaHabilitado = new HashMap<>();
        Map<LocalDate, Long> turnosPorDia = turnoRepo.findAll().stream()
                .filter(t -> !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta))
                .collect(Collectors.groupingBy(
                        Turno::getFecha,
                        Collectors.counting()
                ));

        List<CierreExcepcional> cierres = cierreRepo.findByFechaBetween(desde, hasta);
        Map<LocalDate, Boolean> mapaCierres = cierres.stream()
                .collect(Collectors.toMap(
                        CierreExcepcional::getFecha,
                        CierreExcepcional::getAbierto
                ));

        LocalDate f = desde;
        while (!f.isAfter(hasta)) {
            Boolean abierto = mapaCierres.get(f);
            if (abierto == null) {
                // usamos HorarioOperacion por díaSemana
                int diaSemana = f.getDayOfWeek().getValue(); // 1..7
                boolean abiertoDefault = horarioRepo.findByDiaSemana(diaSemana)
                        .map(HorarioOperacion::getAbierto)
                        .orElse(Boolean.FALSE);
                diaHabilitado.put(f, abiertoDefault);
            } else {
                diaHabilitado.put(f, abierto);
            }
            f = f.plusDays(1);
        }
        c.diaHabilitado = diaHabilitado;
        c.turnosPorDia = turnosPorDia;

        // horas efectivas (si tenés bien puestos horarios, podés calcular apertura-cierre)
        Map<LocalDate, Double> horasEfectivasPorDia = new HashMap<>();
        f = desde;
        while (!f.isAfter(hasta)) {
            Boolean abierto = diaHabilitado.getOrDefault(f, false);
            double horas = 0.0;
            if (Boolean.TRUE.equals(abierto)) {
                int diaSemana = f.getDayOfWeek().getValue();
                var horarioOpt = horarioRepo.findByDiaSemana(diaSemana);
                if (horarioOpt.isPresent()) {
                    var h = horarioOpt.get();
                    if (h.getHoraApertura() != null && h.getHoraCierre() != null) {
                        horas = (h.getHoraCierre().toSecondOfDay() - h.getHoraApertura().toSecondOfDay()) / 3600.0;
                    }
                }
                // override si hay CierreExcepcional con horas específicas
                for (CierreExcepcional ce : cierres) {
                    if (ce.getFecha().equals(f) && Boolean.TRUE.equals(ce.getAbierto())
                            && ce.getHoraApertura() != null && ce.getHoraCierre() != null) {
                        horas = (ce.getHoraCierre().toSecondOfDay() - ce.getHoraApertura().toSecondOfDay()) / 3600.0;
                    }
                }
            }
            horasEfectivasPorDia.put(f, horas);
            f = f.plusDays(1);
        }
        c.horasEfectivasPorDia = horasEfectivasPorDia;

        // impacto de días cerrados en cantidad de turnos
        Map<LocalDate, Long> perdidos = new HashMap<>();
        diaHabilitado.forEach((fecha, abierto) -> {
            if (Boolean.FALSE.equals(abierto)) {
                long potencial = turnosPorDia.getOrDefault(fecha, 0L);
                // esto es un placeholder: si tenés lógica de cupo, acá podrías estimar más.
                perdidos.put(fecha, potencial);
            }
        });
        c.turnosPerdidosPorCierre = perdidos;

        return c;
    }

    // ============================================================
    // 7. ANUNCIOS
    // ============================================================
    private AnunciosReportDTO buildAnunciosReport(LocalDate desde, LocalDate hasta) {
        AnunciosReportDTO a = new AnunciosReportDTO();

        List<Anuncio> anuncios = anuncioRepo.findAll();
        a.totalAnuncios = anuncios.size();

        Map<LocalDate, Long> anunciosPorFecha = anuncios.stream()
                .filter(an -> an.getFechaPublicacion() != null)
                .filter(an -> !an.getFechaPublicacion().isBefore(desde)
                           && !an.getFechaPublicacion().isAfter(hasta))
                .collect(Collectors.groupingBy(
                        Anuncio::getFechaPublicacion,
                        Collectors.counting()
                ));
        a.anunciosPorFecha = anunciosPorFecha;

        return a;
    }

    // ============================================================
    // 8. E-COMMERCE
    // ============================================================
    private EcommerceReportDTO buildEcommerceReport(LocalDate desde, LocalDate hasta) {
        EcommerceReportDTO e = new EcommerceReportDTO();

        List<Pedido> pedidos = pedidoRepo.findAll();

        // filtramos por rango
        List<Pedido> pedidosRango = pedidos.stream()
                .filter(p -> !p.getFechaPedido().isBefore(desde) && !p.getFechaPedido().isAfter(hasta))
                .toList();

        // ventas totales por día (solo estado "pagado")
        List<Pedido> pedidosPagados = pedidosRango.stream()
                .filter(p -> "pagado".equalsIgnoreCase(p.getEstado()))
                .toList();

        Map<LocalDate, BigDecimal> ventasPorDia = pedidosPagados.stream()
                .collect(Collectors.groupingBy(
                        Pedido::getFechaPedido,
                        Collectors.mapping(Pedido::getPrecioTotal,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        e.ventasPorDia = ventasPorDia;

        Map<YearMonth, BigDecimal> ventasPorMes = pedidosPagados.stream()
                .collect(Collectors.groupingBy(
                        p -> YearMonth.from(p.getFechaPedido()),
                        Collectors.mapping(Pedido::getPrecioTotal,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        e.ventasPorMes = ventasPorMes;

        BigDecimal totalPeriodo = pedidosPagados.stream()
                .map(Pedido::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        e.totalPeriodo = totalPeriodo;

        long cantidadPedidosPagados = pedidosPagados.size();
        e.ticketPromedio = cantidadPedidosPagados == 0
                ? BigDecimal.ZERO
                : totalPeriodo.divide(BigDecimal.valueOf(cantidadPedidosPagados), BigDecimal.ROUND_HALF_UP);

        // pedidos por estado
        Map<String, Long> pedidosPorEstado = pedidosRango.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstado().toUpperCase(),
                        Collectors.counting()
                ));
        e.pedidosPorEstado = pedidosPorEstado;

        // pedidos por mes (todos los estados)
        Map<YearMonth, Long> pedidosPorMes = pedidosRango.stream()
                .collect(Collectors.groupingBy(
                        p -> YearMonth.from(p.getFechaPedido()),
                        Collectors.counting()
                ));
        e.pedidosPorMes = pedidosPorMes;

        // productos: top, pocas ventas, cancelados
        List<PedidoProducto> items = pedidoProductoRepo.findAll();

        Map<Long, ProductoVentaDTO> statsProducto = new HashMap<>();
        for (PedidoProducto it : items) {
            Long idProd = it.getProducto().getId();
            ProductoVentaDTO st = statsProducto.computeIfAbsent(idProd, id -> {
                ProductoVentaDTO x = new ProductoVentaDTO();
                x.idProducto = id;
                x.nombre = it.getProducto().getNombre();
                x.cantidad = 0;
                x.totalFacturado = BigDecimal.ZERO;
                return x;
            });
            st.cantidad += it.getCantidad();
            st.totalFacturado = st.totalFacturado.add(it.getSubtotal());
        }

        List<ProductoVentaDTO> listaProductos = new ArrayList<>(statsProducto.values());

        e.topProductos = listaProductos.stream()
                .sorted(Comparator.comparingLong((ProductoVentaDTO p) -> p.cantidad).reversed())
                .limit(10)
                .collect(Collectors.toList());

        e.productosPocasVentas = listaProductos.stream()
                .sorted(Comparator.comparingLong(p -> p.cantidad))
                .limit(10)
                .collect(Collectors.toList());

        // productos con más cancelaciones (si usás estado "cancelado" en Pedido)
        Map<Long, Long> cancelacionesPorProducto = items.stream()
                .filter(it -> "cancelado".equalsIgnoreCase(it.getPedido().getEstado()))
                .collect(Collectors.groupingBy(
                        it -> it.getProducto().getId(),
                        Collectors.summingLong(PedidoProducto::getCantidad)
                ));
        e.productosConMasCancelaciones = cancelacionesPorProducto.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(en -> {
                    Producto prod = productoRepo.findById(en.getKey()).orElse(null);
                    if (prod == null) return null;
                    ProductoVentaDTO pv = new ProductoVentaDTO();
                    pv.idProducto = prod.getId();
                    pv.nombre = prod.getNombre();
                    pv.cantidad = en.getValue();
                    pv.totalFacturado = BigDecimal.ZERO;
                    return pv;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // clientes que compran vs no
        Set<Long> clientesQueCompranSet = pedidos.stream()
                .map(p -> p.getCliente().getIdUsuario())
                .collect(Collectors.toSet());
        long totalClientes = clienteRepo.count();
        e.clientesQueCompran = clientesQueCompranSet.size();
        e.clientesQueNoCompran = totalClientes - e.clientesQueCompran;

        // ranking clientes por gasto
        Map<Long, BigDecimal> gastoPorCliente = pedidosPagados.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCliente().getIdUsuario(),
                        Collectors.mapping(Pedido::getPrecioTotal,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        e.rankingClientesGasto = gastoPorCliente.entrySet().stream()
                .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .map(en -> {
                    ClienteEcommerceDTO ce = new ClienteEcommerceDTO();
                    ce.idCliente = en.getKey();
                    clienteRepo.findById(en.getKey()).ifPresent(cli -> {
                        ce.nombreCompleto = cli.getNombre() + " " + cli.getApellido();
                    });
                    ce.totalGastado = en.getValue();
                    return ce;
                })
                .collect(Collectors.toList());

        // segmentos: solo cuotas / cuotas+tienda / solo tienda
        Map<String, Long> segmentos = new HashMap<>();
        Set<Long> clientesConCuotas = cuotaRepo.findAll().stream()
                .map(q -> q.getCliente().getIdUsuario())
                .collect(Collectors.toSet());

        long soloCuotas = clientesConCuotas.stream()
                .filter(id -> !clientesQueCompranSet.contains(id))
                .count();
        long cuotasMasTienda = clientesConCuotas.stream()
                .filter(clientesQueCompranSet::contains)
                .count();
        long soloTienda = clientesQueCompranSet.stream()
                .filter(id -> !clientesConCuotas.contains(id))
                .count();

        segmentos.put("solo_cuotas", soloCuotas);
        segmentos.put("cuotas_mas_tienda", cuotasMasTienda);
        segmentos.put("solo_tienda", soloTienda);
        e.segmentosClientes = segmentos;

        return e;
    }
}

package com.nautica.backend.nautica_ies_backend.services;

import com.nautica.backend.nautica_ies_backend.models.CierreExcepcional;
import com.nautica.backend.nautica_ies_backend.models.HorarioOperacion;
import com.nautica.backend.nautica_ies_backend.repository.CierreExcepcionalRepository;
import com.nautica.backend.nautica_ies_backend.repository.HorarioOperacionRepository;
import org.springframework.stereotype.Service;
import com.nautica.backend.nautica_ies_backend.services.TareaService;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Calendario.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class CalendarioService {

    private TareaService tareaService;
    private final HorarioOperacionRepository horarioRepo;
    private final CierreExcepcionalRepository excepcionRepo;

    public CalendarioService(HorarioOperacionRepository horarioRepo, CierreExcepcionalRepository excepcionRepo, TareaService tareaService) {
        this.horarioRepo = horarioRepo;
        this.excepcionRepo = excepcionRepo;
        this.tareaService = tareaService;
    }

    public static record DiaDTO(LocalDate fecha, boolean abierto, LocalTime horaApertura, LocalTime horaCierre,
            String motivoExcepcion) {
    }

    public List<DiaDTO> calendario(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde))
            throw new IllegalArgumentException("Rango inválido");

        Map<Integer, HorarioOperacion> horarios = new HashMap<>();
        horarioRepo.findAll().forEach(h -> horarios.put(h.getDiaSemana(), h));

        // excepciones en el rango
        Map<LocalDate, CierreExcepcional> exc = new HashMap<>();
        excepcionRepo.findByFechaBetween(desde, hasta).forEach(e -> exc.put(e.getFecha(), e));

        List<DiaDTO> out = new ArrayList<>();
        for (LocalDate f = desde; !f.isAfter(hasta); f = f.plusDays(1)) {
            CierreExcepcional e = exc.get(f);
            if (e != null) {
                // pisa todo
                out.add(new DiaDTO(
                        f,
                        e.getAbierto(),
                        e.getHoraApertura(),
                        e.getHoraCierre(),
                        e.getMotivo()));
            } else {
                int dow = mapTo1to7(f.getDayOfWeek());
                HorarioOperacion h = horarios.get(dow);
                if (h == null) {
                    // si no hay config para ese día, asumí cerrado
                    out.add(new DiaDTO(f, false, null, null, null));
                } else {
                    out.add(new DiaDTO(
                            f,
                            Boolean.TRUE.equals(h.getAbierto()),
                            h.getHoraApertura(),
                            h.getHoraCierre(),
                            null));
                }
            }
        }
        return out;
    }

    private int mapTo1to7(DayOfWeek dow) {
        // DayOfWeek.MONDAY=1 ... SUNDAY=7 -> ya coincide
        return dow.getValue();
    }

    public List<DiaMes> calendarioDelMes(YearMonth mes) {
        LocalDate from = mes.atDay(1);
        LocalDate to = mes.atEndOfMonth();
        // Reusamos tu calendario(from,to) y mapeamos a la forma solicitada
        var base = calendario(from, to);
        List<DiaMes> out = new ArrayList<>(base.size());
        for (var d : base) {
            out.add(mapDia(d));
        }
        return out;
    }

public void habilitarDia(LocalDate fecha) {
    // Día de la semana (1=Lunes ... 7=Domingo)
    int dow = mapTo1to7(fecha.getDayOfWeek());

    // Buscamos el horario base para ese día en la tabla de horarios semanales
    HorarioOperacion base = horarioRepo.findAll().stream()
            .filter(h -> h.getDiaSemana() == dow)
            .findFirst()
            .orElse(null);

    LocalTime apertura;
    LocalTime cierre;

    if (base != null && Boolean.TRUE.equals(base.getAbierto())) {
        // usamos las horas estándar configuradas
        apertura = base.getHoraApertura();
        cierre = base.getHoraCierre();
    } else {
        // fallback: 08–18 o 09–17 sábado
        boolean esSabado = dow == DayOfWeek.SATURDAY.getValue();
        apertura = esSabado ? LocalTime.of(9, 0) : LocalTime.of(8, 0);
        cierre = esSabado ? LocalTime.of(17, 0) : LocalTime.of(18, 0);
    }

    var ce = excepcionRepo.findByFecha(fecha).orElseGet(() -> {
        var n = new CierreExcepcional();
        n.setFecha(fecha);
        return n;
    });

    ce.setAbierto(true);
    ce.setHoraApertura(apertura);
    ce.setHoraCierre(cierre);
    ce.setMotivo(null);

    excepcionRepo.save(ce);
}


    public void deshabilitarDia(LocalDate fecha, String motivo) {
        var ce = excepcionRepo.findByFecha(fecha).orElseGet(() -> {
            var n = new CierreExcepcional();
            n.setFecha(fecha);
            return n;
        });
        ce.setAbierto(false);
        ce.setHoraApertura(null);
        ce.setHoraCierre(null);
        ce.setMotivo(motivo);
        excepcionRepo.save(ce);

        tareaService.cancelarTareasDeFecha(fecha, motivo);
    }

    public void cambiarHorarios(LocalDate fecha, List<Franja> franjas) {
        // soportamos 0 o 1 franja por límite de tu modelo
        if (franjas == null)
            throw new IllegalArgumentException("franjas requeridas");
        if (franjas.size() > 1)
            throw new IllegalArgumentException("Por ahora solo se admite 1 franja");
        if (franjas.size() == 0) {
            // sin franja => cerramos ese día explícitamente
            deshabilitarDia(fecha, null);
            return;
        }
        var f = franjas.get(0);
        if (f.desde() == null || f.hasta() == null || !f.desde().isBefore(f.hasta())) {
            throw new IllegalArgumentException("Franja inválida: desde < hasta");
        }

        var ce = excepcionRepo.findByFecha(fecha).orElseGet(() -> {
            var n = new CierreExcepcional();
            n.setFecha(fecha);
            return n;
        });
        ce.setAbierto(true);
        ce.setHoraApertura(f.desde());
        ce.setHoraCierre(f.hasta());
        ce.setMotivo(null);
        excepcionRepo.save(ce);
    }

    // ================== HELPERS PRIVADOS ==================

    private DiaMes mapDia(DiaDTO d) {
        boolean disponible = d.abierto();
        List<Franja> franjas = new ArrayList<>();
        if (disponible && d.horaApertura() != null && d.horaCierre() != null) {
            franjas.add(new Franja(d.horaApertura(), d.horaCierre()));
        }
        // Si está abierto pero sin horas en la excepción, tus horarios semanales ya
        // vinieron en d.horaApertura/horaCierre (el calendario(from,to) ya resolvió),
        // por lo que este mapping funciona igual.
        return new DiaMes(d.fecha(), disponible, franjas);
    }
}

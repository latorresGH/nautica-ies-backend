package com.nautica.backend.nautica_ies_backend.services;

import com.nautica.backend.nautica_ies_backend.models.CierreExcepcional;
import com.nautica.backend.nautica_ies_backend.models.HorarioOperacion;
import com.nautica.backend.nautica_ies_backend.repository.CierreExcepcionalRepository;
import com.nautica.backend.nautica_ies_backend.repository.HorarioOperacionRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class CalendarioService {

    private final HorarioOperacionRepository horarioRepo;
    private final CierreExcepcionalRepository excepcionRepo;

    public CalendarioService(HorarioOperacionRepository horarioRepo, CierreExcepcionalRepository excepcionRepo) {
        this.horarioRepo = horarioRepo;
        this.excepcionRepo = excepcionRepo;
    }

    public static record DiaDTO(LocalDate fecha, boolean abierto, LocalTime horaApertura, LocalTime horaCierre, String motivoExcepcion) {}

    public List<DiaDTO> calendario(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) throw new IllegalArgumentException("Rango inválido");

        
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
                        e.getMotivo()
                ));
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
                            null
                    ));
                }
            }
        }
        return out;
    }

    private int mapTo1to7(DayOfWeek dow) {
        // DayOfWeek.MONDAY=1 ... SUNDAY=7 -> ya coincide
        return dow.getValue();
    }
}
